package org.example.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Answer {

    private final int choice;
    private final long elapsedMs;
    private final boolean timedOut;
    private final boolean quit;

    public static Answer of(int choice, long elapsedMs) {
        return new Answer(choice, elapsedMs, false, false);
    }

    public static Answer timeout(long elapsedMs) {
        return new Answer(0, elapsedMs, true, false);
    }

    public static Answer quit(long elapsedMs) {
        return new Answer(0, elapsedMs, false, true);
    }
}
