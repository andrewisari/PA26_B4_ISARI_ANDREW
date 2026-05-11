package org.example.server;

import lombok.extern.java.Log;
import org.example.server.persistence.GameRecorder;
import org.example.server.persistence.PersistenceManager;
import org.example.server.persistence.QuestionLoader;
import org.example.server.persistence.repository.QuestionRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Log
public class GameServer {

    private static final int  DEFAULT_PORT             = 5000;
    private static final int  DEFAULT_POOL_SIZE        = 32;
    private static final int  DEFAULT_QUESTIONS        = 5;
    private static final long DEFAULT_TIME_PER_Q_MS    = 10_000L;
    private static final long SHUTDOWN_GRACE_SECONDS   = 15L;
    private static final String QUESTIONS_RESOURCE     = "/questions.txt";

    private final int  port;
    private final int  poolSize;
    private final int  questionsPerGame;
    private final long timePerQuestionMs;

    private ServerSocket    serverSocket;
    private ExecutorService executor;
    private Matchmaker      matchmaker;
    private QuestionBank    bank;
    private GameRecorder    recorder;
    private volatile boolean running;

    public GameServer(int port, int poolSize, int questionsPerGame, long timePerQuestionMs) {
        this.port = port;
        this.poolSize = poolSize;
        this.questionsPerGame = questionsPerGame;
        this.timePerQuestionMs = timePerQuestionMs;
    }

    public void start() {
        try {
            bank = new QuestionBank(QUESTIONS_RESOURCE);
        } catch (IOException e) {
            log.severe("Could not load questions: " + e.getMessage());
            return;
        }
        if (bank.size() == 0) {
            log.severe("Question bank is empty; aborting startup");
            return;
        }

        try {
            PersistenceManager.init(PersistenceManager.DEFAULT_PU);
            QuestionLoader.seedFromClasspath(QUESTIONS_RESOURCE, new QuestionRepository());
            recorder = new GameRecorder();
            log.info("Persistence layer initialized");
        } catch (Exception e) {
            log.warning("Persistence layer failed to initialize; game state will not be persisted: " + e.getMessage());
            recorder = null;
        }

        executor = (poolSize <= 0)
                ? Executors.newVirtualThreadPerTaskExecutor()
                : Executors.newFixedThreadPool(poolSize);
        matchmaker = new Matchmaker(executor, bank, questionsPerGame, timePerQuestionMs, recorder);
        executor.submit(matchmaker);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "server-shutdown-hook"));

        Thread console = new Thread(this::adminConsole, "admin-console");
        console.setDaemon(true);
        console.start();

        try {
            serverSocket = new ServerSocket(port);
            running = true;
            String poolDesc = (poolSize <= 0) ? "virtual-thread-per-task" : ("fixed=" + poolSize);
            log.info("GameServer listening on port " + port
                    + " | executor=" + poolDesc
                    + " | questions=" + questionsPerGame
                    + " | timePerQuestion=" + timePerQuestionMs + " ms"
                    + " | type 'stop' to shut down");

            acceptLoop();
        } catch (IOException e) {
            if (running) log.severe("Server error: " + e.getMessage());
        }
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                Player player = new Player(socket);
                executor.submit(new ClientHandler(player, matchmaker, executor));
            } catch (SocketException e) {
                if (running) log.warning("Socket exception: " + e.getMessage());
            } catch (IOException e) {
                if (running) log.warning("Accept failure: " + e.getMessage());
            }
        }
    }

    private void adminConsole() {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = r.readLine()) != null) {
                if ("stop".equalsIgnoreCase(line.trim()) || "shutdown".equalsIgnoreCase(line.trim())) {
                    log.info("Admin shutdown requested");
                    shutdown();
                    return;
                }
            }
        } catch (IOException ignored) {
        }
    }

    public synchronized void shutdown() {
        if (!running && serverSocket == null) return;
        if (!running) return;
        running = false;
        log.info("Initiating graceful shutdown");

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            log.warning("Error closing server socket: " + e.getMessage());
        }

        if (matchmaker != null) matchmaker.shutdown();

        if (executor != null) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(SHUTDOWN_GRACE_SECONDS, TimeUnit.SECONDS)) {
                    log.warning("Tasks did not finish within grace period; forcing shutdown");
                    executor.shutdownNow();
                    executor.awaitTermination(SHUTDOWN_GRACE_SECONDS, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        PersistenceManager.close();
        log.info("GameServer shutdown complete");
    }

    public static void main(String[] args) {
        int  port      = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        int  pool      = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_POOL_SIZE;
        int  qPerGame  = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_QUESTIONS;
        long timePerQ  = args.length > 3 ? Long.parseLong(args[3])  : DEFAULT_TIME_PER_Q_MS;
        new GameServer(port, pool, qPerGame, timePerQ).start();
    }
}
