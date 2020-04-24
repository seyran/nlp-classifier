package com.seyrancom.nlp.model;

import com.seyrancom.nlp.term.Term;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@ToString(exclude = {"samples"})
@EqualsAndHashCode(exclude = {"samples"})
public class Category {
    private final int id;
    private final String path;
    private final List<Sample> samples;

    public Category(int id, String path, List<Sample> samples) {
        this.id = id;
        this.path = path;
        this.samples = samples;
    }
}
