package org.example.client.bot;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class KnowledgeBaseBot implements BotStrategy {

    private static final int REWARD_CORRECT = 2;
    private static final int PENALTY_WRONG = 1;

    private final Map<String, int[]> kb = new ConcurrentHashMap<>();

    @Override
    public int chooseAnswer(ClientQuestion question, long timeBudgetMs) throws InterruptedException {
        long delayCap = Math.max(20, Math.min(250, timeBudgetMs / 6));
        Thread.sleep(ThreadLocalRandom.current().nextLong(20, delayCap + 1));

        int[] votes = kb.get(question.text());
        if (votes == null) {
            return 1 + ThreadLocalRandom.current().nextInt(4);
        }
        int best = 0;
        int bestVote = votes[0];
        int ties = 1;
        for (int i = 1; i < 4; i++) {
            if (votes[i] > bestVote) {
                best = i;
                bestVote = votes[i];
                ties = 1;
            } else if (votes[i] == bestVote) {
                ties++;
                if (ThreadLocalRandom.current().nextInt(ties) == 0) {
                    best = i;
                }
            }
        }
        return best + 1;
    }

    @Override
    public void learn(ClientQuestion question, int chosen, int correct, boolean wasCorrect) {
        if (correct < 1 || correct > 4) return;
        int[] votes = kb.computeIfAbsent(question.text(), k -> new int[4]);
        synchronized (votes) {
            votes[correct - 1] += REWARD_CORRECT;
            if (!wasCorrect && chosen >= 1 && chosen <= 4) {
                votes[chosen - 1] -= PENALTY_WRONG;
            }
        }
    }

    public int knownQuestions() {
        return kb.size();
    }

    @Override
    public String label() {
        return "KB(" + kb.size() + ")";
    }
}
