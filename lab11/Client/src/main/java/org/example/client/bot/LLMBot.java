package org.example.client.bot;

import lombok.extern.java.Log;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Log
public class LLMBot implements BotStrategy {

    public enum Difficulty {
        EASY  (0.40, "claude-haiku-4-5"),
        MEDIUM(0.10, "claude-haiku-4-5"),
        HARD  (0.00, "claude-sonnet-4-6");

        final double noise;
        final String model;
        Difficulty(double noise, String model) {
            this.noise = noise;
            this.model = model;
        }
    }

    private static final String ANTHROPIC_ENDPOINT = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION  = "2023-06-01";

    private final Difficulty difficulty;
    private final String apiKey;
    private final HttpClient http;
    private final RandomBot fallback = new RandomBot();

    public LLMBot(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.apiKey = System.getenv("ANTHROPIC_API_KEY");
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        if (apiKey == null || apiKey.isBlank()) {
            log.warning("ANTHROPIC_API_KEY not set; LLMBot will use random fallback");
        }
    }

    @Override
    public int chooseAnswer(ClientQuestion question, long timeBudgetMs) throws InterruptedException {
        if (apiKey == null || apiKey.isBlank()) {
            return fallback.chooseAnswer(question, timeBudgetMs);
        }
        if (ThreadLocalRandom.current().nextDouble() < difficulty.noise) {
            return fallback.chooseAnswer(question, timeBudgetMs);
        }
        try {
            return callLlm(question, timeBudgetMs);
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            log.warning("LLM call failed (" + e.getClass().getSimpleName() + "): " + e.getMessage()
                    + "; falling back to random");
            return fallback.chooseAnswer(question, timeBudgetMs);
        }
    }

    private int callLlm(ClientQuestion question, long timeBudgetMs) throws Exception {
        String prompt = buildPrompt(question);
        String body  = buildBody(difficulty.model, prompt);

        long requestTimeoutMs = Math.max(2000, timeBudgetMs - 500);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(ANTHROPIC_ENDPOINT))
                .header("x-api-key", apiKey)
                .header("anthropic-version", ANTHROPIC_VERSION)
                .header("content-type", "application/json")
                .timeout(Duration.ofMillis(requestTimeoutMs))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() / 100 != 2) {
            log.warning("LLM HTTP " + resp.statusCode() + ": " + truncate(resp.body(), 200));
            return fallback.chooseAnswer(question, timeBudgetMs);
        }
        int parsed = extractAnswer(resp.body());
        if (parsed < 1 || parsed > 4) {
            return fallback.chooseAnswer(question, timeBudgetMs);
        }
        return parsed;
    }

    private static String buildPrompt(ClientQuestion q) {
        return "Quiz question. Reply with ONLY the option number (1, 2, 3, or 4). No prose.\n\n"
                + q.text() + "\n"
                + "1) " + q.options()[0] + "\n"
                + "2) " + q.options()[1] + "\n"
                + "3) " + q.options()[2] + "\n"
                + "4) " + q.options()[3];
    }

    private static String buildBody(String model, String prompt) {
        return "{\"model\":\"" + model + "\",\"max_tokens\":8,\"messages\":[{\"role\":\"user\",\"content\":"
                + jsonString(prompt) + "}]}";
    }

    private static String jsonString(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 8).append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default   -> {
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
                }
            }
        }
        return sb.append('"').toString();
    }

    private static int extractAnswer(String json) {
        int textKey = json.indexOf("\"text\"");
        if (textKey < 0) return 0;
        int colon = json.indexOf(':', textKey);
        if (colon < 0) return 0;
        int quote = json.indexOf('"', colon);
        if (quote < 0) return 0;
        int end = Math.min(json.length(), quote + 60);
        for (int i = quote + 1; i < end; i++) {
            char c = json.charAt(i);
            if (c >= '1' && c <= '4') return c - '0';
        }
        return 0;
    }

    private static String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }

    @Override
    public String label() {
        return "LLM-" + difficulty.name() + "(" + difficulty.model + ")";
    }
}
