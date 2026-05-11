package org.example.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@Log
@RequiredArgsConstructor
public class ServerListener implements Runnable {

    private final BufferedReader in;
    private final AtomicBoolean running;

    @Override
    public void run() {
        try {
            String line;
            while (running.get() && (line = in.readLine()) != null) {
                handle(line);
            }
        } catch (IOException e) {
            if (running.get()) log.warning("Listener error: " + e.getMessage());
        } finally {
            running.set(false);
            System.out.println();
            System.out.println("[client] Connection closed. Press ENTER to exit.");
        }
    }

    private void handle(String line) {
        String[] parts = line.split(Protocol.SPLIT_REGEX, -1);
        switch (parts[0]) {
            case Protocol.S_WELCOME -> {
                System.out.println();
                System.out.println("== " + safe(parts, 1) + " ==");
                System.out.print("Enter your name: ");
            }
            case Protocol.S_WAIT -> System.out.println("[server] " + safe(parts, 1));
            case Protocol.S_START -> {
                System.out.println();
                System.out.println("Match found! Opponent: " + safe(parts, 1));
                System.out.println("Questions: " + safe(parts, 2)
                        + " | Time per question: " + safe(parts, 3) + " ms");
                System.out.println("Reply with the option number (1-4). Good luck!");
            }
            case Protocol.S_QUESTION -> {
                System.out.println();
                System.out.println("Q" + safe(parts, 1) + "/" + safe(parts, 2) + ": " + safe(parts, 3));
                System.out.println("  1) " + safe(parts, 4));
                System.out.println("  2) " + safe(parts, 5));
                System.out.println("  3) " + safe(parts, 6));
                System.out.println("  4) " + safe(parts, 7));
                System.out.print("Your answer (1-4): ");
            }
            case Protocol.S_RESULT -> {
                String verdict = safe(parts, 1);
                String elapsed = safe(parts, 2);
                String correctIdx = safe(parts, 3);
                String correctText = safe(parts, 4);
                System.out.println();
                System.out.println("-> " + verdict + " in " + elapsed + " ms (correct: "
                        + correctIdx + ") " + correctText);
            }
            case Protocol.S_END -> {
                System.out.println();
                System.out.println("=== GAME OVER ===");
                System.out.println("Result: " + safe(parts, 1));
                System.out.println("You      : " + safe(parts, 2) + " correct | "
                        + safe(parts, 4) + " ms on correct answers");
                System.out.println("Opponent : " + safe(parts, 3) + " correct | "
                        + safe(parts, 5) + " ms on correct answers");
                running.set(false);
            }
            case Protocol.S_INFO -> System.out.println("[server] " + safe(parts, 1));
            case Protocol.S_ERROR -> System.out.println("[error]  " + safe(parts, 1));
            default -> System.out.println("[unknown] " + line);
        }
    }

    private static String safe(String[] parts, int i) {
        return i < parts.length ? parts[i] : "";
    }
}
