package com.seyrancom.nlp.processing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class TermProcessor {
    public static final String TERM_DELIMITER_PATTERN = "[ \\t\\r\\n\\v\\f]+";
    private final StanfordNLP StanfordNLP;

    public TermProcessor() {
        StanfordNLP = new StanfordNLP();
    }

    public List<String> getTermStrings(String source) {
        return getTermStringsStanfordCoreNLP(source);
    }

    private List<String> getTermStringsInternal(String source) {
        source = TermNormalization.of(source);
        return Arrays.asList(source.split(TERM_DELIMITER_PATTERN));
    }

    private List<String> getTermStringsStanfordCoreNLP(String source) {
/*        source = TermNormalization.of(source);
        if (source.isEmpty()) {
            return Collections.emptyList();
        }*/
        final List<String> strings = StanfordNLP.parse(TermNormalization.of(source));
        return strings;
    }
}
