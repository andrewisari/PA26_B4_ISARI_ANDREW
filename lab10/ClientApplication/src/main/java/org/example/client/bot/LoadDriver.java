package org.example.client.bot;

import lombok.extern.java.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Log
public class LoadDriver {

    public static void main(String[] args) throws InterruptedException {
        int bots    = args.length > 0 ? Integer.parseInt(args[0]) : 100;
        String host = args.length > 1 ? args[1] : "localhost";
        int port    = args.length > 2 ? Integer.parseInt(args[2]) : 5000;
        String mode = args.length > 3 ? args[3] : "virtual";

        if (bots % 2 != 0) {
            bots++;
            System.out.println("[load] Bot count must be even for pairing; using " + bots);
        }

        System.out.println("[load] Spawning " + bots + " RandomBots towards " + host + ":" + port
                + " using " + mode + " threads");

        ExecutorService exec = "platform".equalsIgnoreCase(mode)
                ? Executors.newCachedThreadPool()
                : Executors.newVirtualThreadPerTaskExecutor();

        AtomicInteger completed = new AtomicInteger();
        AtomicInteger failed    = new AtomicInteger();
        AtomicInteger wins      = new AtomicInteger();
        AtomicInteger draws     = new AtomicInteger();
        AtomicInteger losses    = new AtomicInteger();

        CountDownLatch latch = new CountDownLatch(bots);
        long startNs = System.nanoTime();

        for (int i = 0; i < bots; i++) {
            final int idx = i;
            exec.submit(() -> {
                try {
                    BotClient bot = new BotClient("loadbot-" + idx, new RandomBot());
                    BotClient.Result r = bot.play(host, port);
                    completed.incrementAndGet();
                    switch (r.getOutcome()) {
                        case "WIN"  -> wins.incrementAndGet();
                        case "DRAW" -> draws.incrementAndGet();
                        case "LOSE" -> losses.incrementAndGet();
                        default -> { }
                    }
                } catch (Exception e) {
                    failed.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long elapsedMs = (System.nanoTime() - startNs) / 1_000_000L;

        System.out.println("[load] Done in " + elapsedMs + " ms"
                + " | completed=" + completed.get()
                + " | failed=" + failed.get()
                + " | W=" + wins.get() + " D=" + draws.get() + " L=" + losses.get());

        exec.shutdown();
        if (!exec.awaitTermination(10, TimeUnit.SECONDS)) {
            exec.shutdownNow();
        }
    }
}
