package org.example.server;

import lombok.extern.java.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Log
public class Matchmaker implements Runnable {

    private final BlockingQueue<Player> waiting = new LinkedBlockingQueue<>();
    private final ExecutorService executor;
    private final QuestionBank bank;
    private final int questionsPerGame;
    private final long timePerQuestionMs;
    private volatile boolean running = true;

    public Matchmaker(ExecutorService executor, QuestionBank bank, int questionsPerGame, long timePerQuestionMs) {
        this.executor = executor;
        this.bank = bank;
        this.questionsPerGame = questionsPerGame;
        this.timePerQuestionMs = timePerQuestionMs;
    }

    public void enqueue(Player player) {
        player.send(Protocol.S_WAIT + Protocol.SEPARATOR + "Waiting for an opponent...");
        waiting.offer(player);
    }

    @Override
    public void run() {
        log.info("Matchmaker started; question bank size = " + bank.size());
        while (running) {
            try {
                Player p1 = waitForReady();
                if (p1 == null) continue;
                Player p2 = waitForReady();
                if (p2 == null) {
                    if (p1.isConnected()) waiting.offer(p1);
                    continue;
                }
                log.info("Pairing '" + p1.getName() + "' vs '" + p2.getName() + "'");
                GameSession session = new GameSession(
                        p1, p2, bank.sample(questionsPerGame), timePerQuestionMs, executor);
                executor.submit(session);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                log.warning("Matchmaker loop error: " + e.getMessage());
            }
        }
        drain();
        log.info("Matchmaker stopped");
    }

    private Player waitForReady() throws InterruptedException {
        while (running) {
            Player p = waiting.poll(500, TimeUnit.MILLISECONDS);
            if (p == null) return null;
            if (p.isConnected()) return p;
        }
        return null;
    }

    private void drain() {
        for (Player p : waiting) {
            try {
                p.send(Protocol.S_INFO + Protocol.SEPARATOR + "Server is shutting down. Try again later.");
            } catch (Exception ignored) {
            }
            p.close();
        }
        waiting.clear();
    }

    public void shutdown() {
        running = false;
    }
}
