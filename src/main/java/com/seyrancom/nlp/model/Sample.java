package com.seyrancom.nlp.model;

import com.seyrancom.nlp.term.Term;
import lombok.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter(AccessLevel.PRIVATE)
@ToString(exclude = {"termStats", "text"})
@EqualsAndHashCode(exclude = {"termStats", "text"})
public class Sample {
    private final int id;
    private final String text;
    private final Map<Term, Double> termStats;
    private final double sumTermRawFrequency;

    public Sample(int id, String text, Map<Term, Double> termStats) {
        this.id = id;
        this.text = text;
        this.termStats = termStats;
        sumTermRawFrequency = calculateSumTermRawFrequency();
    }

    private double calculateSumTermRawFrequency() {
        return getTermStats().values().stream().mapToDouble(Double::doubleValue).sum();
    }

    public boolean notEmpty() {
        return getSumTermRawFrequency() > 0;
    }
}
