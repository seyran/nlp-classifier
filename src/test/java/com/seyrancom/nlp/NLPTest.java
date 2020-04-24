package com.seyrancom.nlp;

import com.seyrancom.nlp.classifier.MNBClassifier;
import com.seyrancom.nlp.model.Result;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import lombok.extern.slf4j.Slf4j;

/**
 * Unit test for simple NLP.
 */
@Slf4j
public class NLPTest
        extends TestCase {

    public static final App app;
    static {
         app = new App();
         app.selectClassifier(new MNBClassifier());
    }

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public NLPTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(NLPTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void test1() {
        runNLP("/nlp-db/A.txt","Hi Mike,\n" +
                "next week on Friday morning we can talk.\n" +
                "I will be able from 9AM to 1PM. (GMT+1).\n" +
                "I will wait your scheadule to talk about BMW.\n" +
                "Best,");
    }

    /**
     * Rigourous Test :-)
     */
    public void test2() {
        runNLP("/nlp-db/W.txt","Hi Mike,\n" +
                "Much appreciate your demo earlier this week it was an interesting meeting. Please could you forward some more info on:\n" +
                " Pricing plans\n" +
                " Userability testing with Google.\n" +
                "Regards\n" +
                "Sebastian");
    }

    /**
     * Rigourous Test :-)
     */
    public void test3() {
        runNLP("/nlp-db/E34.txt", "Hi Dominic,\n" +
                "Thank you for your time today it was great speaking with you, having a few issues with my Skype for Business account at the moment so I am unable to send you a message.\n" +
                "Matt and I will speak with our COO and if all is agreed we will send over an NDA for your to sign and upon receipt of this we will arrange another call so that we can chat in more detail.\n" +
                "Kind Regards\n" +
                "Philip Heiner");
    }

    /**
     * Rigourous Test :-)
     */
    public void test4() {
        runNLP("/nlp-db/F.txt", "Hello Mike,\n" +
                "I'm sorry to hear you've been ill and hope you're feeling better now. I'm surrounded by people with bad colds at the moment but so far, helped I believe by Audi, I've managed to stay well.\n" +
                "Thanks for the information you've sent, which I will review later. If that's ok, I'd like to defer our conversation until after we've completed our current major release on 21st October. I detect a distinct lack of willingness on the part of our development team to think about anything other than completing the code at the moment!\n" +
                "We have planned a breathing space in our development plans for November which will be a better time for us to consider infrastructure requirements, which I see automated testing fitting into.\n" +
                "So could we plan a follow up call for early November instead please?\n" +
                "Thanks\n" +
                "Philip Heiner");
    }

    /**
     * Rigourous Test :-)
     */
    public void test5() {
        runNLP("/nlp-db/E125.txt", "Dear Anna,\n" +
                "It was pleasure meeting you last week, I do remember you and your coffee machine and the Italian man too.\n" +
                "I will contact you during next week after my meeting with the board of iTec.\n" +
                "Best regards,\n" +
                "Alexander");
    }

    /**
     * Rigourous Test :-)
     */
    public void test6() {
        runNLP("/nlp-db/B.txt", "Sorry, no, and I don't really have time at the moment to discuss. I'll leave it to the others to explain should they get in touch .\n" +
                "Cheers,\n" +
                "Ben.");
    }

    /**
     * Rigourous Test :-)
     */
    public void test7() {
        runNLP("/nlp-db/B1.txt", "Hi Bill,\n" +
                "After discussing this, we are not in a position to pursue this any further I'm afraid.\n" +
                "Thank you very much for your time,\n" +
                "Carl");
    }

    private void runNLP(String path, String text) {
        log.info("Correct category: " + path);
        final Result r = app.run(text);
        log.info("Matched category: " + r.getMatchedCategory());
        assertTrue(r.getMatchedCategory().getPath().equals(path));
    }
}
