package com.n26.challenge.transaction.service;

import com.google.common.collect.MinMaxPriorityQueue;
import com.n26.challenge.transaction.model.Aggregation;
import com.n26.challenge.transaction.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class TransactionService {

    private static ReentrantLock LOCK = new ReentrantLock();
    private static Aggregation AGGREGATION = new Aggregation();
    private static MinMaxPriorityQueue<Transaction> TRANSACTION_QUEUE = MinMaxPriorityQueue.orderedBy(Comparator.comparing(Transaction::getAmount)).create();

    public void addTransaction(Transaction transaction) throws Exception {
        if (hasTransactionExpired(transaction)) {
            RuntimeException runtimeException = new RuntimeException("Transaction is older than 60 seconds");
            log.error("Transaction can not be added", runtimeException);
            throw runtimeException;
        }

        addTransactionToList(transaction);
    }

    private void addTransactionToList(Transaction transaction) {
        AGGREGATION.getCount().incrementAndGet();
        AGGREGATION.getSum().addAndGet(transaction.getAmount());
        LOCK.lock();
        log.debug("Lock acquired: " + LOCK.toString());
        try {
            TRANSACTION_QUEUE.add(transaction);
            AGGREGATION.getMin().set(TRANSACTION_QUEUE.peekFirst().getAmount());
            AGGREGATION.getMax().set(TRANSACTION_QUEUE.peekLast().getAmount());
        } finally {
            LOCK.unlock();
            log.debug("Lock released: " + LOCK.toString());
        }
    }

    public Aggregation getStatistics() {
        LOCK.lock();
        log.debug("Lock acquired: " + LOCK.toString());
        try {
            return AGGREGATION;
        } finally {
            LOCK.unlock();
            log.debug("Lock released: " + LOCK.toString());
        }
    }

    @Scheduled(fixedRate = 1000)
    private void evictExpiredTransactions() {
        LOCK.lock();
        log.debug("Lock acquired: " + LOCK.toString());
        try {
            Iterator<Transaction> transactionIterator = TRANSACTION_QUEUE.iterator();
            while (transactionIterator.hasNext()) {
                Transaction transaction = transactionIterator.next();
                if (hasTransactionExpired(transaction)) {
                    transactionIterator.remove();
                    updateAggregation(transaction);
                }
            }
        } finally {
            LOCK.unlock();
            log.debug("Lock released: " + LOCK.toString());
        }
    }

    private void updateAggregation(Transaction transaction) {
        AGGREGATION.getCount().decrementAndGet();
        AGGREGATION.getSum().addAndGet(-transaction.getAmount());
        LOCK.lock();
        log.debug("Lock acquired: " + LOCK.toString());
        try {
            Transaction minTransaction = TRANSACTION_QUEUE.peekFirst();
            if (minTransaction != null) {
                AGGREGATION.getMin().set(minTransaction.getAmount());
            } else {
                AGGREGATION.getMin().set(0);
            }
            Transaction maxTransaction = TRANSACTION_QUEUE.peekLast();
            if (maxTransaction != null) {
                AGGREGATION.getMax().set(maxTransaction.getAmount());
            } else {
                AGGREGATION.getMax().set(0);
            }
        } finally {
            LOCK.unlock();
            log.debug("Lock released: " + LOCK.toString());
        }
    }

    private boolean hasTransactionExpired(Transaction transaction) {
        return transaction.getInstant().isBefore(Instant.now().minus(60, ChronoUnit.SECONDS));
    }
}
