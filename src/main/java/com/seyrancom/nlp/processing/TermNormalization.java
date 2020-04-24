package com.seyrancom.nlp.processing;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class TermNormalization {
    public static final String EMPTY_TEMPLATE = "";
    public static final String SPACE_TEMPLATE = " ";
    private static final List<String> STOP_WORDS1 = Arrays.asList("mike", "hi", "hey", "hello", "and", "for", "ok",
            "it", "in", "at", "if", "on", "pm", "the", "th", "gmt", "cet", "est",
            "best regards", "thanks", "thx", "please", "to",
            "but", "etc", "ll", "so", "be", "sorry", "is", "with", "any", "that");
    private static final List<String> STOP_WORDS2 = Arrays.asList(
            "a", "about", "above", "across", "after", "afterwards",
            "again", "all", "almost", "alone", "along", "already", "also",
            "although", "always", "am", "among", "amongst", "amoungst", "amount", "an", "and", "another", "any", "anyhow", "anyone", "anything", "anyway", "anywhere", "are", "as", "at", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "behind", "being", "beside", "besides", "between", "beyond", "both", "but", "by", "can", "cannot", "cant", "could", "couldnt", "de", "describe", "do", "done", "each", "eg", "either", "else", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "find", "for", "found", "four", "from", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "i", "ie", "if", "in", "indeed", "is", "it", "its", "itself", "keep", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mine", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own", "part", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "she", "should", "since", "sincere", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "take", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they",
            "this", "those", "though", "through", "throughout",
            "thru", "thus", "to", "together", "too", "toward", "towards",
            "under", "until", "up", "upon", "us",
            "very", "was", "we", "well", "were", "what", "whatever", "when",
            "whence", "whenever", "where", "whereafter", "whereas", "whereby",
            "wherein", "whereupon", "wherever", "whether", "which", "while",
            "who", "whoever", "whom", "whose", "why", "will", "with",
            "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves");
    private static final List<String> STOP_WORDS3 = Arrays.asList("hi", "hey", "be", "thanks", "thank", "thx",
            "ok", "okay", "sorry", "please", "just", "the", "a", "an", "on", "as", "or",
            "best rgds", "best regards", "kind regards", "kind regard", "regards", "let's"
            //"monday", "friday", "tuesday", "wednesday", "thursday", "sunday", "june", "march", "november",
            //"gmt", "etc", "ect", "cet", "btc", "pm", "a.m.", "p.m."
    );
    private static final List<String> NAMES = Arrays.asList("mike", "dominic", "gabriel", "alex", "carl", "john");
    private static final List<String> STOP_WORDS4 = Arrays.asList("the");
    private final int SMALL_TERM_SIZE = 1;
    private final String PUNC_PATTERN = "[!\"\\#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]^_`{|}~]+";
    private final String NUMBER_PATTERN = "[0-9]+[a-zA-Z]*[\\.\\:\\|]*[0-9]*";
    private final String SYMBOL_PATTERN = "[^a-zA-Z0-9]+";
    private final String SMALL_TERM_PATTERN = String.format("(\\b\\w{1,%s}\\b)", SMALL_TERM_SIZE);
    private final String EXTRA_SPACE_PATTERN = "[\\p{Z}\\t\\r\\n\\v\\f]{1,}";
    private final Pattern UNION_TERM_PATTERN = Pattern.compile("(\\w+)(/)(\\w+)");
    private final String UNION_TERM_REPLACE = "$1 $2 $3";
    private final Pattern MISSPELLED_PATTERN = Pattern.compile("(\\w+)(') ");
    private final String MISSPELLED_REPLACE = "$1 ";

    private String term;

    public static String of(String term) {
        TermNormalization tx = new TermNormalization();
        tx.normalize(term);
        return tx.getTerm();
    }

    private void normalize(String term) {
        setTerm(term);
        caseConversion();
        removeNumbers();
        //removeNames();
        fixMisspelled();
        removeStopWords();
        //removePunctuation();
        //removeSpecialCharacters();
        removeSmallTerms();
        removeIncreaseRecognition();
        removeExtraSpace();
    }

    private void fixMisspelled() {
        Matcher m = MISSPELLED_PATTERN.matcher(term);
        if (m.find()) {
            term = m.replaceFirst(MISSPELLED_REPLACE);
        }
    }

    private void removeIncreaseRecognition() {
        Matcher m = UNION_TERM_PATTERN.matcher(term);
        if (m.find()) {
            term = m.replaceFirst(UNION_TERM_REPLACE);
        }
    }

    private void removeExtraSpace() {
        replaceAll(EXTRA_SPACE_PATTERN, SPACE_TEMPLATE);
        term = term.trim();
    }

    private void caseConversion() {
        term = term.toLowerCase();
    }

    private void stemming() {
    }

    private void removeSpecialCharacters() {
        /**
         * TODO implement
         */
        replaceAll(SYMBOL_PATTERN, SPACE_TEMPLATE);
    }

    private void removeStopWords() {
        STOP_WORDS3.forEach(s ->
                term = term.replaceAll(String.format("\\b(%s)\\b", s), EMPTY_TEMPLATE)
        );
    }

    private void removeNames() {
        NAMES.forEach(s ->
                term = term.replaceAll(String.format("\\b(%s)\\b", s), EMPTY_TEMPLATE)
        );
    }

    private void removePunctuation() {
        /**
         * TODO accounting '
         */
        replaceAll(PUNC_PATTERN, SPACE_TEMPLATE);
    }

    private void removeNumbers() {
        replaceAll(NUMBER_PATTERN, EMPTY_TEMPLATE);
    }

    private void removeSmallTerms() {
        replaceAll(SMALL_TERM_PATTERN, EMPTY_TEMPLATE);
    }

    private void replaceAll(String pattern, String replacement) {
        term = term.replaceAll(pattern, replacement);
    }
}
