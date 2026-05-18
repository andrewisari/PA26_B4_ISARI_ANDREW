package org.example.client.bot;

public interface BotStrategy {

    int chooseAnswer(ClientQuestion question, long timeBudgetMs) throws InterruptedException;

    default void learn(ClientQuestion question, int chosen, int correct, boolean wasCorrect) {
    }

    default String label() {
        return getClass().getSimpleName();
    }
}
