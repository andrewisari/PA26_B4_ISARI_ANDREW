package org.example.server.persistence.repository;

import jakarta.persistence.NoResultException;
import org.example.server.persistence.PersistenceManager;
import org.example.server.persistence.entity.QuestionEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class QuestionRepository {

    public Optional<QuestionEntity> findByExternalId(String externalId) {
        return PersistenceManager.inTransaction(em -> {
            try {
                return Optional.of(em.createQuery(
                                "SELECT q FROM QuestionEntity q WHERE q.externalId = :eid", QuestionEntity.class)
                        .setParameter("eid", externalId)
                        .getSingleResult());
            } catch (NoResultException e) {
                return Optional.<QuestionEntity>empty();
            }
        });
    }

    public QuestionEntity save(QuestionEntity entity) {
        return PersistenceManager.inTransaction(em -> {
            if (entity.getId() == null) {
                em.persist(entity);
                return entity;
            }
            return em.merge(entity);
        });
    }

    public List<QuestionEntity> findAll() {
        return PersistenceManager.inTransaction(em ->
                em.createQuery("SELECT q FROM QuestionEntity q ORDER BY q.externalId", QuestionEntity.class)
                        .getResultList());
    }

    public List<QuestionEntity> sample(int count) {
        List<QuestionEntity> all = findAll();
        if (all.isEmpty()) return List.of();
        List<QuestionEntity> copy = new ArrayList<>(all);
        Collections.shuffle(copy);
        return new ArrayList<>(copy.subList(0, Math.min(count, copy.size())));
    }

    public long count() {
        return PersistenceManager.inTransaction(em ->
                em.createQuery("SELECT COUNT(q) FROM QuestionEntity q", Long.class)
                        .getSingleResult());
    }

    public int bulkInsertIfMissing(List<QuestionEntity> candidates) {
        return PersistenceManager.inTransaction(em -> {
            int inserted = 0;
            for (QuestionEntity q : candidates) {
                Long existing;
                try {
                    existing = em.createQuery(
                                    "SELECT q.id FROM QuestionEntity q WHERE q.externalId = :eid", Long.class)
                            .setParameter("eid", q.getExternalId())
                            .getSingleResult();
                } catch (NoResultException e) {
                    existing = null;
                }
                if (existing == null) {
                    em.persist(q);
                    inserted++;
                }
            }
            return inserted;
        });
    }
}
