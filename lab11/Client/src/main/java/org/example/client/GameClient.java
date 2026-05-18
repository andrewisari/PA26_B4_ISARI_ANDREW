package org.example.client;

import lombok.extern.java.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

@Log
public class GameClient {

    private static final String DEFAULT_HOST = "localhost";
    private static final int    DEFAULT_PORT = 5000;

    private final String host;
    private final int port;

    public GameClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        try (Socket socket = new Socket(host, port);
             BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter serverOut = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in))) {

            log.info("Connected to " + host + ":" + port);

            AtomicBoolean running = new AtomicBoolean(true);

            Thread listener = new Thread(new ServerListener(serverIn, running), "server-listener");
            listener.setDaemon(true);
            listener.start();

            new KeyboardSender(keyboard, serverOut, running).run();

            try { listener.join(500); } catch (InterruptedException ignored) {}
        } catch (IOException e) {
            log.severe("Client error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : DEFAULT_HOST;
        int    port = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_PORT;
        new GameClient(host, port).run();
    }
}
