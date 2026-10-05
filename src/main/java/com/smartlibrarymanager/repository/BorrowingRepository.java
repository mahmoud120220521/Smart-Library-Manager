package com.smartlibrarymanager.repository;

import com.smartlibrarymanager.model.Borrowing;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.concurrent.*;

public class BorrowingRepository {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("SmartLibraryManagerPU");
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public Future<Void> saveAsync(Borrowing borrowing) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(borrowing);
            em.getTransaction().commit();
            em.close();
            return null;
        });
    }

    public Future<Borrowing> findByIdAsync(int id) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            Borrowing borrowing = em.find(Borrowing.class, id);
            em.close();
            return borrowing;
        });
    }

    public Future<List<Borrowing>> findAllAsync() {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            List<Borrowing> borrowings = em.createQuery("SELECT b FROM Borrowing b", Borrowing.class).getResultList();
            em.close();
            return borrowings;
        });
    }

    public Future<Void> updateAsync(Borrowing borrowing) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.merge(borrowing);
            em.getTransaction().commit();
            em.close();
            return null;
        });
    }

    public Future<Void> deleteAsync(int id) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            Borrowing borrowing = em.find(Borrowing.class, id);
            if (borrowing != null) {
                em.remove(borrowing);
            }
            em.getTransaction().commit();
            em.close();
            return null;
        });
    }

    // Synchronous methods for compatibility
    public void save(Borrowing borrowing) {
        try {
            saveAsync(borrowing).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Borrowing findById(int id) {
        try {
            return findByIdAsync(id).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Borrowing> findAll() {
        try {
            return findAllAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Borrowing borrowing) {
        try {
            updateAsync(borrowing).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        try {
            deleteAsync(id).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
