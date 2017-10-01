package com.n26.challenge.transaction.controller;

import com.n26.challenge.transaction.model.Aggregation;
import com.n26.challenge.transaction.model.Transaction;
import com.n26.challenge.transaction.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/challenge")
@Slf4j
public class TransactionController {


    @Autowired
    TransactionService transactionService;

    @PostMapping("/transactions")
    @ResponseStatus(value = CREATED)
    public void addTransaction(@RequestBody Transaction transaction) throws Exception {
        transactionService.addTransaction(transaction);
    }

    @GetMapping("/statistics")
    public Aggregation getStatistics() {
        return transactionService.getStatistics();
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(value = NO_CONTENT)
    private void handleException(Exception exception) {
    }

}
