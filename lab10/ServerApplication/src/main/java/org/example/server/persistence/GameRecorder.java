package org.example.server.persistence;

import lombok.extern.java.Log;
import org.example.server.persistence.entity.GameEntity;
import org.example.server.persistence.entity.PlayerEntity;
import org.example.server.persistence.repository.GameRepository;
import org.example.server.persistence.repository.PlayerRepository;
import org.example.server.persistence.repository.ResultRepository;

@Log
public class GameRecorder {

    private final PlayerRepository players;
    private final GameRepository   games;
    private final ResultRepository results;

    public GameRecorder() {
        this(new PlayerRepository(), new GameRepository(), new ResultRepository());
    }

    public GameRecorder(PlayerRepository players, GameRepository games, ResultRepository results) {
        this.players = players;
        this.games   = games;
        this.results = results;
    }

    public PlayerEntity registerPlayer(String name) {
        try {
            return players.findOrCreate(name);
        } catch (RuntimeException e) {
            log.warning("Failed to register player '" + name + "': " + e.getMessage());
            return null;
        }
    }

    public GameEntity startGame(PlayerEntity p1, PlayerEntity p2, int questionsTotal) {
        if (p1 == null || p2 == null) return null;
        try {
            return games.startGame(p1, p2, questionsTotal);
        } catch (RuntimeException e) {
            log.warning("Failed to persist game start: " + e.getMessage());
            return null;
        }
    }

    public void recordResult(GameEntity game,
                             PlayerEntity p1, int p1Correct, long p1Ms,
                             PlayerEntity p2, int p2Correct, long p2Ms,
                             GameEntity.Outcome outcome) {
        if (game == null) return;
        try {
            boolean p1Winner = outcome == GameEntity.Outcome.P1_WIN;
            boolean p2Winner = outcome == GameEntity.Outcome.P2_WIN;
            results.record(game, p1, p1Correct, p1Ms, p1Winner);
            results.record(game, p2, p2Correct, p2Ms, p2Winner);

            updatePlayerStats(p1, p1Correct, outcome, true);
            updatePlayerStats(p2, p2Correct, outcome, false);

            games.finish(game.getId(), outcome);
        } catch (RuntimeException e) {
            log.warning("Failed to persist game result: " + e.getMessage());
        }
    }

    private void updatePlayerStats(PlayerEntity player, int correct,
                                   GameEntity.Outcome outcome, boolean isPlayer1) {
        if (player == null) return;
        PlayerEntity fresh = players.findById(player.getId()).orElse(null);
        if (fresh == null) return;
        fresh.setGamesPlayed(fresh.getGamesPlayed() + 1);
        fresh.setTotalCorrect(fresh.getTotalCorrect() + correct);
        switch (outcome) {
            case P1_WIN -> {
                if (isPlayer1) fresh.setWins(fresh.getWins() + 1);
                else fresh.setLosses(fresh.getLosses() + 1);
            }
            case P2_WIN -> {
                if (isPlayer1) fresh.setLosses(fresh.getLosses() + 1);
                else fresh.setWins(fresh.getWins() + 1);
            }
            case DRAW -> fresh.setDraws(fresh.getDraws() + 1);
            case P1_ABANDONED -> {
                if (isPlayer1) fresh.setLosses(fresh.getLosses() + 1);
                else fresh.setWins(fresh.getWins() + 1);
            }
            case P2_ABANDONED -> {
                if (isPlayer1) fresh.setWins(fresh.getWins() + 1);
                else fresh.setLosses(fresh.getLosses() + 1);
            }
            case BOTH_ABANDONED -> { /* no-op */ }
        }
        players.save(fresh);
    }
}
