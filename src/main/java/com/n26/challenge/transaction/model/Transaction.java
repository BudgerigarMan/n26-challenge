package com.n26.challenge.transaction.model;

import lombok.Data;
import lombok.ToString;

import java.time.Instant;

@Data
@ToString
public class Transaction {

    private Double amount;
    private Long timestamp;

    public Instant getInstant() {
        return Instant.ofEpochMilli(timestamp);
    }
}
