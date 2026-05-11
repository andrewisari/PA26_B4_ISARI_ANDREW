package org.example.server;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
public class Player {

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final BlockingQueue<String> inbox = new LinkedBlockingQueue<>();

    private volatile String name;
    private volatile int correctCount = 0;
    private volatile long correctResponseMs = 0L;
    private volatile boolean connected = true;

    public Player(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void recordCorrect(long elapsedMs) {
        correctCount++;
        correctResponseMs += elapsedMs;
    }

    public synchronized void send(String message) {
        out.println(message);
        out.flush();
    }

    public String readLineDirect() throws IOException {
        return in.readLine();
    }

    public void markDisconnected() {
        connected = false;
    }

    public void close() {
        connected = false;
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    public String addressLabel() {
        return socket.getRemoteSocketAddress() == null ? "?" : socket.getRemoteSocketAddress().toString();
    }
}
