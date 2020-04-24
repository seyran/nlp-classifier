package com.seyrancom.nlp.classifier;

import com.seyrancom.nlp.model.Category;
import com.seyrancom.nlp.model.Model;
import com.seyrancom.nlp.model.Sample;
import com.seyrancom.nlp.term.Term;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MultinomialNaiveBayes
 * <p>
 * <p>
 * https://en.wikipedia.org/wiki/Naive_Bayes_classifier
 * https://pdfs.semanticscholar.org/629d/60f8d7f408ac8814a2c0d85ca4f3375449de.pdf
 * https://www.cs.waikato.ac.nz/ml/publications/2004/kibriya_et_al_cr.pdf
 * additional topics
 * https://ac.els-cdn.com/S1877050917320872/1-s2.0-S1877050917320872-main.pdf?_tid=fe48bec3-128e-401d-a66d-bdc425784482&acdnat=1525089230_633191ed84c15f77df331a6bc2013421
 * https://towardsdatascience.com/multinomial-naive-bayes-classifier-for-text-analysis-python-8dd6825ece67
 * <p>
 * https://core.ac.uk/download/pdf/140678.pdf
 */
@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
@Slf4j
public class MNBClassifier implements Classifier {
    private Model model;
    private double[] termIDF;
    private double[][] termFrequencyMatrix;

    public MNBClassifier() {
    }

    @Override
    public void setModel(Model model) {
        this.model = model;
    }

    @Override
    public void train() {
        termFrequencyMatrix = new double[getModel().getCategories().size()][getModel().getTermCount()];

        getModel().getCategories().forEach(category -> termFrequencyMatrix[category.getId()] = calculateTermVector(category));

        termIDF = calculateTermIDF();
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
        final double[] result = {0D};
        final double categoryAllSampleTermRawFrequency = Arrays.stream(termFrequencyMatrix[category.getId()]).sum();

        // explicit
        evidence.getTermStats().forEach((term, rawFrequency) -> {
            double idf;
            double categorySumTermFrequency;
            if (getModel().isKnownTerm(term)) {
                // The sum of raw term frequencies of word Xi from all samples
                idf = termIDF[term.getId()];
                categorySumTermFrequency = termFrequencyMatrix[category.getId()][term.getId()];
            } else {
                // The sum of raw term frequencies of word Xi from all samples
                idf = Math.log(getModel().getSampleCount());
                categorySumTermFrequency = 0D;
            }
            final double categoryLikelihood = calculateLikelihood(categorySumTermFrequency, categoryAllSampleTermRawFrequency);
            final double tfIdf = Math.log1p(rawFrequency * idf);
            final double r = tfIdf + categoryLikelihood;
            result[0] += r;
        });
        return result[0];
    }

    private double calculateLikelihood(double termFrequency, double totalFrequency) {
        // Laplace smoothing = 1
        double likelihood = (1D + termFrequency) / (totalFrequency + getModel().getTermCount() + 1D);
        likelihood = Math.log(likelihood);
        return likelihood;
    }

    /**
     * inverseDocumentFrequency(t) = 1 + log (C /(1 + df(t)))
     */
    private double calculateIDF(int termId) {
        //Preconditions.checkArgument(sampleCount > 0);
        /**
         * Inverse document rawFrequency
         */
        final double result = Math.log(getModel().getSampleCount() / (1D + getModel().getTermUsageInSamples()[termId]));
        return result;
    }

    private double[] calculateTermVector(Category category) {
        final double[] vector = getModel().generateTermVector();
        for (Sample sample : category.getSamples()) {
            sample.getTermStats().forEach((term, rawFrequency) ->
                    vector[term.getId()] += rawFrequency
            );
        }
        return vector;
    }

    private double[] calculateTermIDF() {
        final double[] vector = new double[getModel().getTermCount()];
        for (int i = 0; i < getModel().getTermCount(); i++) {
            vector[i] += calculateIDF(i);
        }
        return vector;
    }
}
