package com.seyrancom.nlp;

import com.seyrancom.nlp.classifier.Classifier;
import com.seyrancom.nlp.classifier.MNBClassifier;
import com.seyrancom.nlp.classifier.MNBComplimentClassifier;
import com.seyrancom.nlp.model.Category;
import com.seyrancom.nlp.model.Model;
import com.seyrancom.nlp.model.Result;
import com.seyrancom.nlp.model.Sample;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Slf4j
public class App {
    private final static List<Class> CLASSIFIER_CLASSES = Arrays.asList(MNBClassifier.class, MNBComplimentClassifier.class);
    private final Model model = new Model();
    private Classifier classifier;

    public App() {
        model.load();
    }

    public void selectClassifier(Classifier classifier) {
        try {
            LOG.info("Classifier: {}", classifier.getClass().getSimpleName());
            this.classifier = classifier;
            classifier.setModel(model);
            classifier.train();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public Result run(String text) {
        final Sample sample = getModel().createSample(text);
        final Category bestMatch = classifier.testSample(sample);
        return new Result(model.getCategories().stream()
                .collect(Collectors.toMap(c -> c.getPath(), c -> c.equals(bestMatch))), bestMatch, sample);
    }
}