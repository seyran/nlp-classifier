package com.seyrancom.nlp.term;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString()
@EqualsAndHashCode(exclude = {"term"})
public class Term {
    private final int id;
    private final String term;

    public Term(int id, String term) {
        this.id = id;
        this.term = term.intern();
    }
}
