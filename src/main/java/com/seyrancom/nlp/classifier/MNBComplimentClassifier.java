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
 * MultinomialNaiveBayes normalized
 * <p>
 * http://people.csail.mit.edu/jrennie/papers/icml03-nb.pdf
 */
@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
@Slf4j
public class MNBComplimentClassifier implements Classifier {
    private Model model;
    private double[] termIDF;
    private double[][] termFrequencyMatrix;

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
        final Category bestMatch = categoryMap.entrySet().stream().min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).get();
        return bestMatch;
    }

    /**
     * (prior * likelihood) / evidence
     *
     * @return
     */
    private double calculatePosterior(Category category, Sample evidence) {
        final double[] result = {0D};
        double tfIdfNorm = calculateTfIdfNorm(evidence);

        // explicit
        evidence.getTermStats().forEach((term, rawFrequency) -> {
            final double idf = getIdf(term);
            final double categoryLikelihood = calculateCategoryTermLikelihoodNorm(category, term);
            final double otherCategoriesLikelihood = calculateOtherCategoriesTermLikelihoodNorm(category, term);
            final double tfIdf = Math.log1p(rawFrequency) * idf / tfIdfNorm;
            final double r =  rawFrequency / tfIdfNorm* otherCategoriesLikelihood;
            result[0] += r;
        });
        return result[0];
    }

    private double calculateTfIdfNorm(Sample sample) {
        final double vectorNormAr[] = {0D};
        sample.getTermStats().forEach((term, rawFrequency) -> {
            final double idf = getIdf(term);
            vectorNormAr[0] += Math.pow(rawFrequency, 2);
        });
        final double vectorNorm = Math.sqrt(vectorNormAr[0]);
        return vectorNorm;
    }

    private double getIdf(Term term) {
        final double idf;
        if (getModel().isKnownTerm(term)) {
            idf = termIDF[term.getId()];
        } else {
            idf = Math.log(getModel().getSampleCount());
        }
        return idf;
    }

    private double calculateOtherCategoriesTermLikelihoodNorm(Category category, Term term) {

        final double categoryLikelihood = calculateOtherCategoriesTermLikelihood(category, term);
        final double likelihoodNorm = getModel().getCategories().stream().mapToDouble(c -> Math.abs(calculateOtherCategoriesTermLikelihood(c, term))).sum();
        final double result = categoryLikelihood / likelihoodNorm;
        return result;
    }

    private double calculateOtherCategoriesTermLikelihood(Category category, Term term) {
        double otherCategoriesAllSampleTermRawFrequency = 0;
        for (int i = 0; i < termFrequencyMatrix.length; i++) {
            if (i == category.getId()) {
                continue;
            }
            otherCategoriesAllSampleTermRawFrequency += Arrays.stream(termFrequencyMatrix[i]).sum();
        }
        double otherCategoriesSumTermFrequency = 0;
        for (int i = 0; i < termFrequencyMatrix.length; i++) {
            if (i == category.getId() || !getModel().isKnownTerm(term)) {
                continue;
            }
            otherCategoriesSumTermFrequency += termFrequencyMatrix[i][term.getId()];
        }

        final double categoryLikelihood = calculateLikelihood(otherCategoriesSumTermFrequency, otherCategoriesAllSampleTermRawFrequency);
        return categoryLikelihood;
    }

    private double calculateCategoryTermLikelihoodNorm(Category category, Term term) {
        final double categoryLikelihood = calculateCategoryTermLikelihood(category, term);
        final double likelihoodNorm = getModel().getCategories().stream().mapToDouble(c -> Math.abs(calculateCategoryTermLikelihood(c, term))).sum();
        final double result = categoryLikelihood / likelihoodNorm;
        return result;
    }

    private double calculateCategoryTermLikelihood(Category category, Term term) {
        final double categoryAllSampleTermRawFrequency = Arrays.stream(termFrequencyMatrix[category.getId()]).sum();
        double categorySumTermFrequency;
        if (getModel().isKnownTerm(term)) {
            // The sum of raw term frequencies of word Xi from all samples
            categorySumTermFrequency = termFrequencyMatrix[category.getId()][term.getId()];
        } else {
            categorySumTermFrequency = 0;
        }
        final double categoryLikelihood = calculateLikelihood(categorySumTermFrequency, categoryAllSampleTermRawFrequency);
        return categoryLikelihood;
    }

    private double calculateLikelihood(double termFrequency, double totalFrequency) {
        // Laplace smoothing = 1
        double likelihood = (1D + termFrequency) / (totalFrequency + getModel().getTermCount());
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
        final double[] vector = getModel().generateTermVector();
        for (int i = 0; i < getModel().getTermCount(); i++) {
            vector[i] = calculateIDF(i);
        }
        return vector;
    }
}
