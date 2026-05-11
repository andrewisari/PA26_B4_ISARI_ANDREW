package org.example.bunny.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.bunny.model.Cell;

public class GameState {

    @Getter
    @RequiredArgsConstructor
    public enum Outcome {
        ONGOING("game in progress"),
        BUNNY_ESCAPED("bunny reached the exit"),
        BUNNY_CAUGHT("a robot caught the bunny"),
        TIMEOUT("time limit exceeded"),
        STOPPED("stopped by user");

        private final String message;
    }

    @Getter
    private volatile Outcome outcome = Outcome.ONGOING;
    @Getter
    private volatile String winnerName;
    @Getter
    private volatile Cell endingCell;

    public boolean isOver() {
        return outcome != Outcome.ONGOING;
    }

    public synchronized boolean tryEnd(Outcome o, String winner, Cell where) {
        if (outcome != Outcome.ONGOING) return false;
        outcome = o;
        winnerName = winner;
        endingCell = where;
        return true;
    }
}
