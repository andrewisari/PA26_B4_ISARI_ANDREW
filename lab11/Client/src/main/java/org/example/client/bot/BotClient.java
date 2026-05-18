package org.example.client.bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.client.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Log
@RequiredArgsConstructor
public class BotClient {

    private final String name;
    private final BotStrategy strategy;

    public Result play(String host, int port) throws IOException {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            return interact(in, out);
        }
    }

    private Result interact(BufferedReader in, PrintWriter out) throws IOException {
        long timePerQMs = 10_000L;
        ClientQuestion currentQ = null;
        int lastChoice = 0;
        int score = 0;
        int total = 0;
        String outcome = "DISCONNECTED";

        String line;
        while ((line = in.readLine()) != null) {
            String[] p = line.split(Protocol.SPLIT_REGEX, -1);
            switch (p[0]) {
                case Protocol.S_WELCOME -> out.println(Protocol.C_NAME + Protocol.SEPARATOR + name);
                case Protocol.S_START -> {
                    if (p.length > 3) {
                        try { timePerQMs = Long.parseLong(p[3]); }
                        catch (NumberFormatException ignored) {}
                    }
                }
                case Protocol.S_QUESTION -> {
                    currentQ = new ClientQuestion(
                            safe(p, 3),
                            new String[]{safe(p, 4), safe(p, 5), safe(p, 6), safe(p, 7)});
                    total++;
                    int choice;
                    try {
                        choice = strategy.chooseAnswer(currentQ, timePerQMs);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return new Result(name, "INTERRUPTED", score, total);
                    }
                    if (choice < 1 || choice > 4) choice = 1;
                    lastChoice = choice;
                    out.println(Protocol.C_ANS + Protocol.SEPARATOR + choice);
                }
                case Protocol.S_RESULT -> {
                    if (currentQ != null && p.length > 3) {
                        int correct = parseInt(p[3], 0);
                        boolean wasCorrect = Protocol.S_RESULT.equals(p[0]) && "CORRECT".equals(p[1]);
                        if (wasCorrect) score++;
                        strategy.learn(currentQ, lastChoice, correct, wasCorrect);
                    }
                }
                case Protocol.S_END -> {
                    outcome = safe(p, 1);
                    return new Result(name, outcome, score, total);
                }
                default -> { }
            }
        }
        return new Result(name, outcome, score, total);
    }

    private static String safe(String[] arr, int i) {
        return i < arr.length ? arr[i] : "";
    }

    private static int parseInt(String s, int fallback) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return fallback; }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Result {
        private final String name;
        private final String outcome;
        private final int score;
        private final int total;

        @Override
        public String toString() {
            return name + " [" + outcome + "] " + score + "/" + total;
        }
    }
}
