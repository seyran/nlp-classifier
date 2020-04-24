package com.seyrancom.nlp.processing;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class StanfordNLP {
    private final StanfordCoreNLP pipeline;

    public StanfordNLP() {
        // set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        //props.setProperty("annotators", "tokenize,ssplit,pos,parse,lemma,ner");
        // set a property for an annotator, in this case the coref annotator is being set to use the neural algorithm
        props.setProperty("coref.algorithm", "neural");
        pipeline = new StanfordCoreNLP(props);
    }

    private List<String> performProcessing(String source) {
        CoreDocument document = new CoreDocument(source);
        pipeline.annotate(document);
        // https://www.winwaed.com/blog/2011/11/08/part-of-speech-tags/
        // https://web.stanford.edu/~jurafsky/slp3/10.pdf
        final List<String> result = new ArrayList<>();
        for (CoreSentence sentence : document.sentences()) {
            for (CoreLabel token : sentence.tokens()) {
/*                if (token.ner().equals("PERSON")
                        || token.ner().equals("ORGANIZATION")
                        || token.ner().equals("LOCATION")
                        || token.ner().equals("DATE")
                        || token.ner().equals("NUMBER")
                        ) {
                    continue;
                }*/
                if (token.tag().equals("SYM")
                        || token.tag().equals("POS")
                        //|| token.tag().equals("MD")
                        //|| token.tag().equals("RB")
                        || token.tag().equals("UH")
                        || token.tag().equals("LS")
                        || token.tag().equals("FW")
                        || token.tag().equals("EX")
                        || token.tag().equals("CC")
                        || token.tag().equals("CD")
                        || token.tag().equals("IN")
                        //|| token.tag().equals("DT")
                        || token.tag().equals("TO")
                        || token.tag().equals(".")
                        || token.tag().equals(",")
                        || token.tag().equals(":")
                        || token.tag().equals("''")
                        //|| token.tag().equals("WP")
                        //|| token.tag().equals("WDT")
                        || token.tag().equals("WRB")
                        || token.tag().equals("WP$")
                        //|| token.tag().equals("PRP")
                        //|| token.tag().equals("PRP$")
                        ) {
                    continue;
                }

                result.add(token.lemma());
            }
        }
        return result;
    }

    public List<String> parse(String source) {
        final List<String> result = performProcessing(source);
        return result;
    }
}
