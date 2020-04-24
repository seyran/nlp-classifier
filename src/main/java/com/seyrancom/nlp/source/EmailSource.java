package com.seyrancom.nlp.source;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
public class EmailSource {

    private static final String DELIMITER = "\n\n\n";

    private static String readFile(String fileName) {
        try {
            //LOG.info(EmailSource.class.getResource(fileName).toURI());
            Path path = Paths.get(EmailSource.class.getResource(fileName).toURI());
            return String.join("\n", Files.readAllLines(path, StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOG.error("Exception occurred trying to read {}.", fileName);
            return null;
        }
    }

    public static List<String> loadEmailsForTraining(String path) {
        final List<String> strings = loadEmails(path);
        return strings.subList(0, (int) Math.round(strings.size() * 0.8));
    }

    public static List<String> loadEmailsForTest(String path) {
        final List<String> strings = loadEmails(path);
        return strings.subList((int) Math.round(strings.size() * 0.8), strings.size() - 1);
    }

    private static List<String> loadEmails(String path) {
        String content = readFile(path);
        return Arrays.asList(content.split(DELIMITER))
                .stream().filter(e -> !e.isEmpty())
                .collect(Collectors.toList());
    }
}
