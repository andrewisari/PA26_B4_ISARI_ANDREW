package org.example.server.persistence;

import lombok.extern.java.Log;
import org.example.server.persistence.entity.QuestionEntity;
import org.example.server.persistence.repository.QuestionRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Log
public final class QuestionLoader {

    private static final String SPLIT_REGEX = "\\|";

    private QuestionLoader() {}

    public static int seedFromClasspath(String resourcePath, QuestionRepository repo) throws IOException {
        try (InputStream in = QuestionLoader.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Question resource not found: " + resourcePath);
            }
            List<QuestionEntity> parsed = parse(in);
            int inserted = repo.bulkInsertIfMissing(parsed);
            log.info("Seeded " + inserted + " new questions into DB (file had " + parsed.size() + ")");
            return inserted;
        }
    }

    private static List<QuestionEntity> parse(InputStream in) throws IOException {
        List<QuestionEntity> out = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            int lineNo = 0;
            while ((line = r.readLine()) != null) {
                lineNo++;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
                String[] parts = trimmed.split(SPLIT_REGEX);
                if (parts.length != 7) {
                    log.warning("Skipping malformed question at line " + lineNo);
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
                out.add(new QuestionEntity(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim(),
                        parts[4].trim(),
                        parts[5].trim(),
                        correct));
            }
        }
        return out;
    }
}
