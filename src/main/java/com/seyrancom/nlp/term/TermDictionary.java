package com.seyrancom.nlp.term;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter(value = AccessLevel.PRIVATE)
@Setter
public class TermDictionary {
    private final Map<String, Term> terms = new LinkedHashMap<>();

    public Term getTerm(@NonNull String termString) {
        //Preconditions.checkArgument(!termString.isEmpty());
        final String intern = termString.intern();
        final Term term = terms.computeIfAbsent(intern, t -> new Term(terms.size(), intern));
        return term;
    }

    public int getTermCount() {
        return getTerms().size();
    }
}
