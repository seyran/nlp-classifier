package com.seyrancom.nlp.classifier;

import com.seyrancom.nlp.model.Category;
import com.seyrancom.nlp.model.Model;
import com.seyrancom.nlp.model.Sample;

public interface Classifier {
    void setModel(Model model);

    void train();

    Category testSample(Sample sample);
}
