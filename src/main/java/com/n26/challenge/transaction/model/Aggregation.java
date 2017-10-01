package com.n26.challenge.transaction.model;

import com.google.common.util.concurrent.AtomicDouble;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicLong;

@Data
@ToString
public class Aggregation {

    private AtomicDouble sum = new AtomicDouble(0);
    @Getter(AccessLevel.NONE)
    private Double avg = new Double(0);
    private AtomicDouble max = new AtomicDouble(0);
    private AtomicDouble min = new AtomicDouble(0);
    private AtomicLong count = new AtomicLong(0);

    public Double getAvg() {
        return sum.get() / count.get();
    }
}
