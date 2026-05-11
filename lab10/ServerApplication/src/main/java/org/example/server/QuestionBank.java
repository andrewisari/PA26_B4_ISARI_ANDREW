package org.example.server;

import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Log
public class QuestionBank {

    private final List<Question> questions = new ArrayList<>();

    public QuestionBank(String resourcePath) throws IOException {
        try (InputStream in = QuestionBank.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Question resource not found: " + resourcePath);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                int lineNo = 0;
                while ((line = reader.readLine()) != null) {
                    lineNo++;
                    String trimmed = line.trim();
                    if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;

                    String[] parts = trimmed.split(Protocol.SPLIT_REGEX);
                    if (parts.length != 7) {
                        log.warning("Skipping malformed question at line " + lineNo + ": " + trimmed);
                        continue;
                    }
                    int correct;
                    try {
                        correct = Integer.parseInt(parts[6].trim());
                    } catch (NumberFormatException e) {
                        log.warning("Skipping question with non-numeric correct index at line " + lineNo);
                        continue;
                    }
                    if (correct < 1 || correct > 4) {
                        log.warning("Skipping question with out-of-range correct index at line " + lineNo);
                        continue;
                    }
                    questions.add(new Question(
                            parts[0].trim(),
                            parts[1].trim(),
                            List.of(parts[2].trim(), parts[3].trim(), parts[4].trim(), parts[5].trim()),
                            correct));
                }
            }
        }
        log.info("Loaded " + questions.size() + " questions from " + resourcePath);
    }

    public List<Question> sample(int count) {
        if (questions.isEmpty()) {
            throw new IllegalStateException("Question bank is empty");
        }
        List<Question> copy = new ArrayList<>(questions);
        Collections.shuffle(copy);
        return new ArrayList<>(copy.subList(0, Math.min(count, copy.size())));
    }

    public int size() {
        return questions.size();
    }
}
