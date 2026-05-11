package org.example.client.bot;

import java.util.concurrent.ThreadLocalRandom;

public class RandomBot implements BotStrategy {

    @Override
    public int chooseAnswer(ClientQuestion question, long timeBudgetMs) throws InterruptedException {
        long delayCap = Math.max(10, Math.min(150, timeBudgetMs / 8));
        Thread.sleep(ThreadLocalRandom.current().nextLong(10, delayCap + 1));
        return 1 + ThreadLocalRandom.current().nextInt(4);
    }
}
