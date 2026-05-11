package org.example.server.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.java.Log;

import java.util.function.Consumer;
import java.util.function.Function;

@Log
public final class PersistenceManager {

    public static final String DEFAULT_PU = "blitzQuizPU";
    public static final String TEST_PU    = "blitzQuizTestPU";

    private static volatile EntityManagerFactory emf;
    private static String activeUnit;

    private PersistenceManager() {}

    public static synchronized void init(String persistenceUnit) {
        if (emf != null && persistenceUnit.equals(activeUnit)) return;
        if (emf != null) {
            close();
        }
        log.info("Initializing JPA persistence unit '" + persistenceUnit + "'");
        emf = Persistence.createEntityManagerFactory(persistenceUnit);
        activeUnit = persistenceUnit;
    }

    public static EntityManagerFactory factory() {
        if (emf == null) init(DEFAULT_PU);
        return emf;
    }

    public static EntityManager newEntityManager() {
        return factory().createEntityManager();
    }

    public static <T> T inTransaction(Function<EntityManager, T> work) {
        EntityManager em = newEntityManager();
        try {
            em.getTransaction().begin();
            T result = work.apply(em);
            em.getTransaction().commit();
            return result;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public static void runInTransaction(Consumer<EntityManager> work) {
        inTransaction(em -> {
            work.accept(em);
            return null;
        });
    }

    public static synchronized void close() {
        if (emf != null) {
            try {
                emf.close();
            } catch (Exception e) {
                log.warning("Error closing EntityManagerFactory: " + e.getMessage());
            }
            emf = null;
            activeUnit = null;
        }
    }
}
