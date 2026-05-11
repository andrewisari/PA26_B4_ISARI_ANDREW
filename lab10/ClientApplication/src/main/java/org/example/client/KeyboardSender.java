package org.example.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicBoolean;

@Log
@RequiredArgsConstructor
public class KeyboardSender implements Runnable {

    private final BufferedReader keyboard;
    private final PrintWriter out;
    private final AtomicBoolean running;

    @Override
    public void run() {
        boolean nameSent = false;
        try {
            String line;
            while (running.get() && (line = keyboard.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;
                if ("quit".equalsIgnoreCase(trimmed) || "exit".equalsIgnoreCase(trimmed)) {
                    safeSend(Protocol.C_QUIT);
                    running.set(false);
                    break;
                }
                if (!nameSent) {
                    safeSend(Protocol.C_NAME + Protocol.SEPARATOR + trimmed);
                    nameSent = true;
                    continue;
                }
                try {
                    int n = Integer.parseInt(trimmed);
                    if (n < 1 || n > 4) {
                        System.out.println("[client] Please enter 1-4 (or 'quit').");
                        continue;
                    }
                    safeSend(Protocol.C_ANS + Protocol.SEPARATOR + n);
                } catch (NumberFormatException e) {
                    System.out.println("[client] Please enter 1-4 (or 'quit').");
                }
            }
        } catch (IOException e) {
            log.warning("Keyboard error: " + e.getMessage());
        } finally {
            running.set(false);
        }
    }

    private void safeSend(String msg) {
        out.println(msg);
        out.flush();
    }
}
