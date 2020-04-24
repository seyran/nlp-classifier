package com.seyrancom.nlp.classifier;

import com.seyrancom.nlp.model.Category;
import com.seyrancom.nlp.model.Model;
import com.seyrancom.nlp.model.Sample;
import com.seyrancom.nlp.term.Term;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * http://www.statsoft.com/Textbook/Support-Vector-Machines
 * http://www.saedsayad.com/support_vector_machine.htm
 * https://medium.com/machine-learning-101/chapter-2-svm-support-vector-machine-theory-f0812effc72
 * https://en.wikipedia.org/wiki/Support_vector_machine
 *
 * http://www.edvancer.in/logistic-regression-vs-decision-trees-vs-svm-part1/
 * https://www.edvancer.in/logistic-regression-vs-decision-trees-vs-svm-part2/
 */
@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
@Slf4j
public class SupportVectorMachinesClassifier implements Classifier {
    private Model model;
    private double[] averageVectorLength;
    private double[] termUsageInSamples;
    private double[][] termFrequencyMatrix;
    private int termVectorSize;

    public SupportVectorMachinesClassifier() {
    }

    @Override
    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public void train() {
    }

    @Override
    public Category testSample(Sample sample) {

        final Map<Category, Double> categoryMap = getModel().getCategories().stream()
                .collect(Collectors.toMap(c -> c, c -> calculatePosterior(c, sample)));
        final Category bestMatch = categoryMap.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).get();
        return bestMatch;
    }

    /**
     * (prior * likelihood) / evidence
     *
     * @return
     */
    private double calculatePosterior(Category category, Sample evidence) {

        final double categoryPriorLikelihood = Math.log((double) category.getSamples().size() / getModel().getSampleCount());
        final double[] result = {1D};


        return result[0];
    }

    private boolean isKnownTerm(Term term) {
        return termVectorSize > term.getId();
    }
}
