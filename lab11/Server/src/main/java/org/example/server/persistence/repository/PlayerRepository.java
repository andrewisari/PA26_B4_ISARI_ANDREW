package org.example.server.persistence.repository;

import jakarta.persistence.NoResultException;
import org.example.server.persistence.PersistenceManager;
import org.example.server.persistence.entity.PlayerEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class PlayerRepository {

    public Optional<PlayerEntity> findById(Long id) {
        return Optional.ofNullable(
                PersistenceManager.inTransaction(em -> em.find(PlayerEntity.class, id)));
    }

    public Optional<PlayerEntity> findByName(String name) {
        return PersistenceManager.inTransaction(em -> {
            try {
                return Optional.of(em.createQuery(
                                "SELECT p FROM PlayerEntity p WHERE p.name = :name", PlayerEntity.class)
                        .setParameter("name", name)
                        .getSingleResult());
            } catch (NoResultException e) {
                return Optional.<PlayerEntity>empty();
            }
        });
    }

    public PlayerEntity findOrCreate(String name) {
        return PersistenceManager.inTransaction(em -> {
            try {
                PlayerEntity existing = em.createQuery(
                                "SELECT p FROM PlayerEntity p WHERE p.name = :name", PlayerEntity.class)
                        .setParameter("name", name)
                        .getSingleResult();
                existing.setLastSeen(Instant.now());
                return existing;
            } catch (NoResultException e) {
                PlayerEntity created = new PlayerEntity(name);
                em.persist(created);
                return created;
            }
        });
    }

    public PlayerEntity save(PlayerEntity entity) {
        return PersistenceManager.inTransaction(em -> em.merge(entity));
    }

    public List<PlayerEntity> findAll() {
        return PersistenceManager.inTransaction(em ->
                em.createQuery("SELECT p FROM PlayerEntity p ORDER BY p.name", PlayerEntity.class)
                        .getResultList());
    }

    public List<PlayerEntity> topByWins(int limit) {
        return PersistenceManager.inTransaction(em ->
                em.createQuery(
                                "SELECT p FROM PlayerEntity p ORDER BY p.wins DESC, p.totalCorrect DESC",
                                PlayerEntity.class)
                        .setMaxResults(limit)
                        .getResultList());
    }

    public long count() {
        return PersistenceManager.inTransaction(em ->
                em.createQuery("SELECT COUNT(p) FROM PlayerEntity p", Long.class)
                        .getSingleResult());
    }
}
