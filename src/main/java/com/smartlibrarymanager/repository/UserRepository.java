package com.smartlibrarymanager.repository;

import com.smartlibrarymanager.model.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.concurrent.*;

public class UserRepository {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("SmartLibraryManagerPU");
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public Future<Void> saveAsync(User user) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            em.close();
            return null;
        });
    }

    public Future<User> findByIdAsync(int id) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            User user = em.find(User.class, id);
            em.close();
            return user;
        });
    }

    public Future<User> findByEmailAsync(String email) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            List<User> users = em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultList();
            em.close();
            return users.isEmpty() ? null : users.get(0);
        });
    }

    public Future<List<User>> findAllAsync() {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            List<User> users = em.createQuery("SELECT u FROM User u", User.class).getResultList();
            em.close();
            return users;
        });
    }

    public Future<Void> updateAsync(User user) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();
            em.close();
            return null;
        });
    }

    public Future<Void> clearAllTokensAsync() {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.createQuery("UPDATE User u SET u.loginToken = ''").executeUpdate();
            em.getTransaction().commit();
            em.close();
            return null;
        });
    }

    public Future<Void> updateTokenAsync(int userId, String token) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            User user = em.find(User.class, userId);
            if (user != null) {
                user.setLoginToken(token);
                em.merge(user);
            }
            em.getTransaction().commit();
            em.close();
            return null;
        });
    }

    // Synchronous methods for compatibility
    public void save(User user) {
        try {
            saveAsync(user).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User findById(int id) {
        try {
            return findByIdAsync(id).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public User findByEmail(String email) {
        try {
            return findByEmailAsync(email).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAll() {
        try {
            return findAllAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(User user) {
        try {
            updateAsync(user).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void clearAllTokens() {
        try {
            clearAllTokensAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateToken(int userId, String token) {
        try {
            updateTokenAsync(userId, token).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
