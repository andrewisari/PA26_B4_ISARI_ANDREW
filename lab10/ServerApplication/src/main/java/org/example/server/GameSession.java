package org.example.server;

import lombok.extern.java.Log;
import org.example.server.persistence.GameRecorder;
import org.example.server.persistence.entity.GameEntity;
import org.example.server.persistence.entity.PlayerEntity;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Log
public class GameSession implements Runnable {

    private final Player p1;
    private final Player p2;
    private final List<Question> questions;
    private final long timePerQuestionMs;
    private final ExecutorService executor;
    private final GameRecorder recorder;

    private PlayerEntity p1Entity;
    private PlayerEntity p2Entity;
    private GameEntity   gameEntity;

    public GameSession(Player p1, Player p2, List<Question> questions,
                       long timePerQuestionMs, ExecutorService executor,
                       GameRecorder recorder) {
        this.p1 = p1;
        this.p2 = p2;
        this.questions = questions;
        this.timePerQuestionMs = timePerQuestionMs;
        this.executor = executor;
        this.recorder = recorder;
    }

    public GameSession(Player p1, Player p2, List<Question> questions,
                       long timePerQuestionMs, ExecutorService executor) {
        this(p1, p2, questions, timePerQuestionMs, executor, null);
    }

    @Override
    public void run() {
        try {
            recordGameStart();
            announceStart();
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                int idx = i + 1;
                int total = questions.size();

                sendQuestion(p1, q, idx, total);
                sendQuestion(p2, q, idx, total);

                Future<Answer> f1 = executor.submit(awaitAnswerTask(p1));
                Future<Answer> f2 = executor.submit(awaitAnswerTask(p2));

                Answer a1 = takeAnswer(f1);
                Answer a2 = takeAnswer(f2);

                applyResult(p1, q, a1);
                applyResult(p2, q, a2);

                if (!p1.isConnected() || !p2.isConnected()) {
                    handleEarlyExit();
                    recordGameEnd(true);
                    return;
                }
            }
            announceEnd();
            recordGameEnd(false);
        } catch (Exception e) {
            log.warning("Session error: " + e.getMessage());
        } finally {
            p1.close();
            p2.close();
            log.info("Session ended: " + p1.getName() + " vs " + p2.getName());
        }
    }

    private void recordGameStart() {
        if (recorder == null) return;
        p1Entity = recorder.registerPlayer(p1.getName());
        p2Entity = recorder.registerPlayer(p2.getName());
        gameEntity = recorder.startGame(p1Entity, p2Entity, questions.size());
    }

    private void recordGameEnd(boolean earlyExit) {
        if (recorder == null || gameEntity == null) return;
        GameEntity.Outcome outcome;
        if (earlyExit) {
            boolean p1Out = !p1.isConnected();
            boolean p2Out = !p2.isConnected();
            if (p1Out && p2Out)      outcome = GameEntity.Outcome.BOTH_ABANDONED;
            else if (p1Out)          outcome = GameEntity.Outcome.P1_ABANDONED;
            else                     outcome = GameEntity.Outcome.P2_ABANDONED;
        } else {
            if (p1.getCorrectCount() > p2.getCorrectCount())      outcome = GameEntity.Outcome.P1_WIN;
            else if (p1.getCorrectCount() < p2.getCorrectCount()) outcome = GameEntity.Outcome.P2_WIN;
            else if (p1.getCorrectResponseMs() < p2.getCorrectResponseMs()) outcome = GameEntity.Outcome.P1_WIN;
            else if (p1.getCorrectResponseMs() > p2.getCorrectResponseMs()) outcome = GameEntity.Outcome.P2_WIN;
            else                                                  outcome = GameEntity.Outcome.DRAW;
        }
        recorder.recordResult(
                gameEntity,
                p1Entity, p1.getCorrectCount(), p1.getCorrectResponseMs(),
                p2Entity, p2.getCorrectCount(), p2.getCorrectResponseMs(),
                outcome);
    }

    private void announceStart() {
        String startMsg1 = Protocol.S_START + Protocol.SEPARATOR + p2.getName()
                + Protocol.SEPARATOR + questions.size()
                + Protocol.SEPARATOR + timePerQuestionMs;
        String startMsg2 = Protocol.S_START + Protocol.SEPARATOR + p1.getName()
                + Protocol.SEPARATOR + questions.size()
                + Protocol.SEPARATOR + timePerQuestionMs;
        p1.send(startMsg1);
        p2.send(startMsg2);
    }

    private void sendQuestion(Player p, Question q, int idx, int total) {
        if (!p.isConnected()) return;
        String msg = Protocol.S_QUESTION
                + Protocol.SEPARATOR + idx
                + Protocol.SEPARATOR + total
                + Protocol.SEPARATOR + q.getText()
                + Protocol.SEPARATOR + q.getOptions().get(0)
                + Protocol.SEPARATOR + q.getOptions().get(1)
                + Protocol.SEPARATOR + q.getOptions().get(2)
                + Protocol.SEPARATOR + q.getOptions().get(3);
        p.send(msg);
    }

    private Callable<Answer> awaitAnswerTask(Player p) {
        return () -> waitForAnswer(p);
    }

    private Answer waitForAnswer(Player p) {
        long deadlineNanos = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timePerQuestionMs);
        long startNanos = System.nanoTime();
        while (true) {
            long remainingNs = deadlineNanos - System.nanoTime();
            if (remainingNs <= 0) {
                return Answer.timeout(timePerQuestionMs);
            }
            try {
                String line = p.getInbox().poll(remainingNs, TimeUnit.NANOSECONDS);
                if (line == null) {
                    return Answer.timeout(timePerQuestionMs);
                }
                long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
                String[] parts = line.split(Protocol.SPLIT_REGEX, 2);
                if (Protocol.C_QUIT.equals(parts[0])) {
                    p.markDisconnected();
                    return Answer.quit(elapsedMs);
                }
                if (Protocol.C_ANS.equals(parts[0]) && parts.length >= 2) {
                    try {
                        int choice = Integer.parseInt(parts[1].trim());
                        if (choice < 1 || choice > 4) {
                            p.send(Protocol.S_ERROR + Protocol.SEPARATOR + "Answer must be 1-4");
                            continue;
                        }
                        return Answer.of(choice, elapsedMs);
                    } catch (NumberFormatException e) {
                        p.send(Protocol.S_ERROR + Protocol.SEPARATOR + "Invalid answer format");
                        continue;
                    }
                }
                p.send(Protocol.S_ERROR + Protocol.SEPARATOR + "Unexpected message: " + line);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return Answer.timeout(timePerQuestionMs);
            }
        }
    }

    private Answer takeAnswer(Future<Answer> f) {
        try {
            return f.get(timePerQuestionMs + 1000L, TimeUnit.MILLISECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException e) {
            f.cancel(true);
            return Answer.timeout(timePerQuestionMs);
        }
    }

    private void applyResult(Player p, Question q, Answer a) {
        if (!p.isConnected()) return;
        String verdict;
        if (a.isQuit()) {
            verdict = Protocol.R_TIMEOUT;
        } else if (a.isTimedOut()) {
            verdict = Protocol.R_TIMEOUT;
        } else if (q.isCorrect(a.getChoice())) {
            verdict = Protocol.R_CORRECT;
            p.recordCorrect(a.getElapsedMs());
        } else {
            verdict = Protocol.R_WRONG;
        }
        String msg = Protocol.S_RESULT + Protocol.SEPARATOR + verdict
                + Protocol.SEPARATOR + a.getElapsedMs()
                + Protocol.SEPARATOR + q.getCorrectIndex()
                + Protocol.SEPARATOR + q.correctOptionText();
        p.send(msg);
    }

    private void announceEnd() {
        String r1 = outcomeFor(p1, p2);
        String r2 = outcomeFor(p2, p1);
        send(p1, r1, p1, p2);
        send(p2, r2, p2, p1);
    }

    private void handleEarlyExit() {
        boolean p1Out = !p1.isConnected();
        boolean p2Out = !p2.isConnected();
        if (p1Out && p2Out) {
            return;
        }
        Player remaining = p1Out ? p2 : p1;
        Player gone     = p1Out ? p1 : p2;
        String msg = Protocol.S_END + Protocol.SEPARATOR + Protocol.E_OPPONENT_LEFT
                + Protocol.SEPARATOR + remaining.getCorrectCount()
                + Protocol.SEPARATOR + gone.getCorrectCount()
                + Protocol.SEPARATOR + remaining.getCorrectResponseMs()
                + Protocol.SEPARATOR + gone.getCorrectResponseMs();
        remaining.send(msg);
    }

    private String outcomeFor(Player me, Player them) {
        if (me.getCorrectCount() > them.getCorrectCount()) return Protocol.E_WIN;
        if (me.getCorrectCount() < them.getCorrectCount()) return Protocol.E_LOSE;
        if (me.getCorrectResponseMs() < them.getCorrectResponseMs()) return Protocol.E_WIN;
        if (me.getCorrectResponseMs() > them.getCorrectResponseMs()) return Protocol.E_LOSE;
        return Protocol.E_DRAW;
    }

    private void send(Player p, String outcome, Player self, Player opp) {
        String msg = Protocol.S_END + Protocol.SEPARATOR + outcome
                + Protocol.SEPARATOR + self.getCorrectCount()
                + Protocol.SEPARATOR + opp.getCorrectCount()
                + Protocol.SEPARATOR + self.getCorrectResponseMs()
                + Protocol.SEPARATOR + opp.getCorrectResponseMs();
        p.send(msg);
    }
}
