package com.seyrancom.nlp;

import com.seyrancom.nlp.classifier.MNBClassifier;
import com.seyrancom.nlp.classifier.MNBComplimentClassifier;
import com.seyrancom.nlp.model.Model;
import com.seyrancom.nlp.model.Result;
import com.seyrancom.nlp.source.EmailSource;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit test for simple NLP.
 */
@Slf4j
public class NLPTestReport
        extends TestCase {

    public static final App app;

    static {
        app = new App();
    }


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public NLPTestReport(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(NLPTestReport.class);
    }

    public void testMass() {
        log.info("--------------------------------------------------");
        log.info("--------------------------------------------------");
        app.selectClassifier(new MNBClassifier());
        _testMass();
        log.info("--------------------------------------------------");
        log.info("--------------------------------------------------");
        app.selectClassifier(new MNBComplimentClassifier());
        _testMass();
        log.info("--------------------------------------------------");
    }

    private void _testMass() {
        double total = 0D;
        double truePositive = 0D;
        double falsePositive = 0D;
        double falseNegative = 0D;
        double trueNegative = 0D;
        Map<String, List<String>> map = new LinkedHashMap<>();
        for (String path : Model.PATHS) {
            //final List<String> emails = EmailSource.loadEmailsForTraining(path);
            final List<String> emails = EmailSource.loadEmailsForTest(path);
            map.put(path, emails);
        }
        for (String path : map.keySet()) {
            final List<String> emails = map.get(path);
            for (String email : emails) {
                total++;
                if (getReportNLP(path, email)) {
                    truePositive++;
                    falseNegative += Model.PATHS.size() - 1;
                } else {
                    falsePositive++;
                    trueNegative += Model.PATHS.size() - 1;
                }
            }
        }

        final double accuracy = (truePositive + trueNegative) / (truePositive + trueNegative + falsePositive + falseNegative) * 100;
        final double precision = (truePositive) / (truePositive + falsePositive) * 100;
        final double recall = (truePositive) / (truePositive + falseNegative) * 100;
        final double Fmeasure1 = (2 * precision * recall) / (precision + recall);
        final double Fmeasure05 = (1.25 * precision * recall) / (0.25 * precision + recall);
        //log.info("Sample Count: " + app.getModel().getSampleCount());
        //log.info("Term Count: " + app.getModel().getTermCount());
        //log.info("Total: " + total);
        log.info("Accuracy: " + String.format("%.3f", accuracy));
        log.info("Recall: " + String.format("%.3f", recall) + "%");
        log.info("Precision: " + String.format("%.3f", precision) + "%");
        log.info("F-measure1: " + String.format("%.3f", Fmeasure1));
        //log.info("F-measure05-Focus on Precision : " + String.format("%.3f", Fmeasure05));
    }

    private boolean getReportNLP(String path, String text) {
        final Result r = app.run(text);
        final boolean isOk = r.getMatchedCategory().getPath().equals(path);
        if (isOk) {
            //log.info("True - Correct: " + path + " Matched: " + r.getMatchedCategory());
        } else {
            //log.warn("False - Correct: " + path + " Matched: " + r.getMatchedCategory());
            //log.warn("text: " + text);
            //log.warn("sample: " + StringUtils.join(r.getSample().getTermStats().keySet().stream().map(term -> term.getTerm()).toArray(), " "));
        }
        return isOk;
    }
}
