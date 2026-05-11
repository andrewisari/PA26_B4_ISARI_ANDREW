package org.example.server.persistence.repository;

import org.example.server.persistence.PersistenceManager;
import org.example.server.persistence.entity.GameEntity;
import org.example.server.persistence.entity.PlayerEntity;
import org.example.server.persistence.entity.ResultEntity;

import java.util.List;

public class ResultRepository {

    public ResultEntity record(GameEntity game, PlayerEntity player,
                               int correctCount, long correctResponseMs, boolean winner) {
        return PersistenceManager.inTransaction(em -> {
            ResultEntity r = new ResultEntity();
            r.setGame(em.merge(game));
            r.setPlayer(em.merge(player));
            r.setCorrectCount(correctCount);
            r.setCorrectResponseMs(correctResponseMs);
            r.setWinner(winner);
            em.persist(r);
            return r;
        });
    }

    public List<ResultEntity> findByPlayer(Long playerId) {
        return PersistenceManager.inTransaction(em ->
                em.createQuery(
                                "SELECT r FROM ResultEntity r WHERE r.player.id = :pid",
                                ResultEntity.class)
                        .setParameter("pid", playerId)
                        .getResultList());
    }

    public List<ResultEntity> findByGame(Long gameId) {
        return PersistenceManager.inTransaction(em ->
                em.createQuery(
                                "SELECT r FROM ResultEntity r WHERE r.game.id = :gid",
                                ResultEntity.class)
                        .setParameter("gid", gameId)
                        .getResultList());
    }

    public long count() {
        return PersistenceManager.inTransaction(em ->
                em.createQuery("SELECT COUNT(r) FROM ResultEntity r", Long.class)
                        .getSingleResult());
    }
}
