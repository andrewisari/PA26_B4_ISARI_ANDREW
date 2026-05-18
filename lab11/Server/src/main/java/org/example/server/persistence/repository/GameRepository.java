package org.example.server.persistence.repository;

import org.example.server.persistence.PersistenceManager;
import org.example.server.persistence.entity.GameEntity;
import org.example.server.persistence.entity.PlayerEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class GameRepository {

    public GameEntity startGame(PlayerEntity p1, PlayerEntity p2, int questionsTotal) {
        return PersistenceManager.inTransaction(em -> {
            GameEntity g = new GameEntity();
            g.setPlayer1(em.merge(p1));
            g.setPlayer2(em.merge(p2));
            g.setStartedAt(Instant.now());
            g.setQuestionsTotal(questionsTotal);
            em.persist(g);
            return g;
        });
    }

    public GameEntity finish(Long gameId, GameEntity.Outcome outcome) {
        return PersistenceManager.inTransaction(em -> {
            GameEntity g = em.find(GameEntity.class, gameId);
            if (g == null) throw new IllegalArgumentException("Unknown game id: " + gameId);
            g.setEndedAt(Instant.now());
            g.setOutcome(outcome);
            return g;
        });
    }

    public Optional<GameEntity> findById(Long id) {
        return Optional.ofNullable(
                PersistenceManager.inTransaction(em -> em.find(GameEntity.class, id)));
    }

    public List<GameEntity> findAll() {
        return PersistenceManager.inTransaction(em ->
                em.createQuery("SELECT g FROM GameEntity g ORDER BY g.startedAt DESC", GameEntity.class)
                        .getResultList());
    }

    public List<GameEntity> findByPlayer(Long playerId) {
        return PersistenceManager.inTransaction(em ->
                em.createQuery(
                                "SELECT g FROM GameEntity g " +
                                        "WHERE g.player1.id = :pid OR g.player2.id = :pid " +
                                        "ORDER BY g.startedAt DESC",
                                GameEntity.class)
                        .setParameter("pid", playerId)
                        .getResultList());
    }

    public long count() {
        return PersistenceManager.inTransaction(em ->
                em.createQuery("SELECT COUNT(g) FROM GameEntity g", Long.class)
                        .getSingleResult());
    }
}
