package com.seyrancom.nlp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class Result {
    private final Map<String, Boolean> categories;
    private final Category matchedCategory;
    private final Sample sample;

    @Override
    public String toString() {
        return "Result{" +
                "categories=" + categories +
                '}';
    }
}
