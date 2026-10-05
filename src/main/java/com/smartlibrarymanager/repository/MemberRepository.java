package com.smartlibrarymanager.repository;

import com.smartlibrarymanager.model.Member;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.concurrent.*;

public class MemberRepository {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("SmartLibraryManagerPU");
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public Future<Void> saveAsync(Member member) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(member);
            em.getTransaction().commit();
            em.close();
            return null;
        });
    }

    public Future<Member> findByIdAsync(int id) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            Member member = em.find(Member.class, id);
            em.close();
            return member;
        });
    }

    public Future<List<Member>> findAllAsync() {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            List<Member> members = em.createQuery("SELECT m FROM Member m", Member.class).getResultList();
            em.close();
            return members;
        });
    }

    public Future<Void> updateAsync(Member member) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.merge(member);
            em.getTransaction().commit();
            em.close();
            return null;
        });
    }

    public Future<Void> deleteAsync(int id) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            Member member = em.find(Member.class, id);
            if (member != null) {
                em.remove(member);
            }
            em.getTransaction().commit();
            em.close();
            return null;
        });
    }

    // Synchronous methods for compatibility
    public void save(Member member) {
        try {
            saveAsync(member).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Member findById(int id) {
        try {
            return findByIdAsync(id).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Member> findAll() {
        try {
            return findAllAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Member member) {
        try {
            updateAsync(member).get();
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
