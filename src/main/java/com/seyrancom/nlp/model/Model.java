package com.seyrancom.nlp.model;

import com.seyrancom.nlp.processing.TermProcessor;
import com.seyrancom.nlp.source.EmailSource;
import com.seyrancom.nlp.term.Term;
import com.seyrancom.nlp.term.TermDictionary;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter(AccessLevel.PRIVATE)
@ToString(exclude = {"categories"})
@EqualsAndHashCode(exclude = {"categories"})
public class Model {
    public static final List<String> PATHS = Arrays.asList("/nlp-db/A.txt", "/nlp-db/B.txt", "/nlp-db/B1.txt",
            "/nlp-db/E34.txt", "/nlp-db/E125.txt", "/nlp-db/F.txt", "/nlp-db/W.txt");

    private final TermProcessor termProcessor;
    private final TermDictionary termDictionary;
    private final AtomicInteger categoryCounter = new AtomicInteger(0);
    private final AtomicInteger sampleCounter = new AtomicInteger(0);
    private List<Category> categories;
    private int sampleCount;
    private int termCount;
    private double[] termCountVector;
    private double[] termUsageInSamples;

    public Model() {
        termProcessor = new TermProcessor();
        termDictionary = new TermDictionary();
    }

    public void load() {
        categories = loadCategories();
        sampleCount = calculateSampleCount();
        termCount = getTermDictionary().getTermCount();
        calculateStatistics();
    }

    private int calculateSampleCount() {
        return categories.stream().mapToInt(c -> c.getSamples().size()).sum();
    }

    private void calculateStatistics() {
        termCountVector = generateTermVector();
        termUsageInSamples = generateTermVector();

        getCategories().forEach(category ->
                category.getSamples().forEach(sample -> {
                    sample.getTermStats().forEach((term, rawFrequency) -> {
                        termCountVector[term.getId()] += rawFrequency;
                        termUsageInSamples[term.getId()] += 1;
                    });
                }));
    }

    private List<Category> loadCategories() {
        return PATHS.stream().map(this::buildCategory).collect(Collectors.toList());
    }

    private Category buildCategory(String path) {
        final List<String> emails = EmailSource.loadEmailsForTraining(path);
        final List<Sample> samples = emails.stream()
                .map(this::createSample)
                .filter(s -> s.notEmpty())
                .collect(Collectors.toList());
        return new Category(categoryCounter.getAndIncrement(), path, samples);
    }

    public Sample createSample(String text) {
        final List<Term> terms = getTerms(text);
        final Map<Term, Double> termStats = new LinkedHashMap<>();
        terms.forEach(termString -> termStats.compute(termString, (key, rawFrequency) -> {
                    if (rawFrequency == null) {
                        rawFrequency = 0D;
                    }
                    rawFrequency++;
                    return rawFrequency;
                })
        );
        return new Sample(sampleCounter.getAndIncrement(), text, termStats);
    }

    private List<Term> getTerms(String source) {
        final List<String> termStrings = getTermProcessor().getTermStrings(source);
        final List<Term> terms = termStrings.stream()
                .filter(t -> !t.trim().isEmpty())
                .map(getTermDictionary()::getTerm)
                .collect(Collectors.toList());
        return terms;
    }

    public double[] generateTermVector() {
        return new double[getTermCount()];
    }

    public boolean isTermUsual(Term term) {
        return termCountVector[term.getId()] > 0;
        //return term.getCount() > (getTermCount() / sampleCount);
    }

    public boolean isKnownTerm(Term term) {
        return getTermCount() > term.getId();
    }
}
