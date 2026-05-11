package org.example.bunny.game;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SpeedController {

    public static final long MIN_DELAY_MS = 20;
    public static final long MAX_DELAY_MS = 5_000;

    private static final class State {
        volatile long delayMs;
        volatile boolean paused;
        State(long d) { this.delayMs = d; }
    }

    private final ConcurrentMap<String, State> entities = new ConcurrentHashMap<>();
    private final Set<String> insertionOrder = Collections.synchronizedSet(new LinkedHashSet<>());

    public void register(String name, long initialDelayMs) {
        entities.computeIfAbsent(name, k -> {
            insertionOrder.add(k);
            return new State(clamp(initialDelayMs));
        });
    }

    public Set<String> names() {
        synchronized (insertionOrder) {
            return new LinkedHashSet<>(insertionOrder);
        }
    }

    public long getDelay(String name) {
        State s = entities.get(name);
        return s == null ? 200 : s.delayMs;
    }

    public boolean isPaused(String name) {
        State s = entities.get(name);
        return s != null && s.paused;
    }

    public boolean pause(String name)  { return apply(name, s -> s.paused = true); }
    public boolean resume(String name) { return apply(name, s -> s.paused = false); }
    public boolean faster(String name) { return apply(name, s -> s.delayMs = clamp(s.delayMs / 2)); }
    public boolean slower(String name) { return apply(name, s -> s.delayMs = clamp(Math.max(s.delayMs * 2, s.delayMs + 50))); }

    public void pauseAll()  { entities.values().forEach(s -> s.paused = true); }
    public void resumeAll() { entities.values().forEach(s -> s.paused = false); }
    public void fasterAll() { entities.values().forEach(s -> s.delayMs = clamp(s.delayMs / 2)); }
    public void slowerAll()
    {
        entities.values().forEach(state -> state.delayMs = clamp(Math.max(state.delayMs * 2, state.delayMs + 50)));
    }

    private boolean apply(String name, java.util.function.Consumer<State> action) {
        State s = entities.get(name);
        if (s == null) return false;
        action.accept(s);
        return true;
    }

    private static long clamp(long d) {
        return Math.max(MIN_DELAY_MS, Math.min(MAX_DELAY_MS, d));
    }
}
