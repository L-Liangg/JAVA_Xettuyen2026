package com.xettuyen.repository;

import com.xettuyen.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

import static com.xettuyen.config.AppConstants.PAGE_SIZE;

public class BaseRepository<T> {

    private final Class<T> entityClass;

    public BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public List<T> findAll(int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<T> query = session.createQuery("FROM " + entityClass.getSimpleName(), entityClass);
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM " + entityClass.getSimpleName(), entityClass).list();
        }
    }

    public long countAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(t) FROM " + entityClass.getSimpleName() + " t", Long.class)
                    .uniqueResult();
        }
    }

    public int getTotalPages() {
        return (int) Math.max(1, Math.ceil((double) countAll() / PAGE_SIZE));
    }

    public void save(T entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(entity);
            session.getTransaction().commit();
        }
    }

    public void update(T entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(entity);
            session.getTransaction().commit();
        }
    }

    public void delete(T entity) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(session.contains(entity) ? entity : session.merge(entity));
            session.getTransaction().commit();
        }
    }
}