package com.smartlibrarymanager.repository;

import com.smartlibrarymanager.model.Book;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.concurrent.*;

public class BookRepository {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("SmartLibraryManagerPU");
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public Future<Void> saveAsync(Book book) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(book);
            em.getTransaction().commit();
            em.close();
            return null;
        });
    }

    public Future<Book> findByIdAsync(int id) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            Book book = em.find(Book.class, id);
            em.close();
            return book;
        });
    }

    public Future<List<Book>> findAllAsync() {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            List<Book> books = em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
            em.close();
            return books;
        });
    }

    public Future<Void> updateAsync(Book book) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            em.merge(book);
            em.getTransaction().commit();
            em.close();
            return null;
        });
    }

    public Future<Void> deleteAsync(int id) {
        return executor.submit(() -> {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            Book book = em.find(Book.class, id);
            if (book != null) {
                em.remove(book);
            }
            em.getTransaction().commit();
            em.close();
            return null;
        });
    }

    // Synchronous methods for compatibility
    public void save(Book book) {
        try {
            saveAsync(book).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Book findById(int id) {
        try {
            return findByIdAsync(id).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Book> findAll() {
        try {
            return findAllAsync().get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Book book) {
        try {
            updateAsync(book).get();
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
