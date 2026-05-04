package com.xettuyen.repository;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.Nganh;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class NganhRepository extends BaseRepository<Nganh> {

    public NganhRepository() {
        super(Nganh.class);
    }

    public List<Nganh> search(String keyword, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Nganh> query = session.createQuery(
                    "FROM Nganh n WHERE n.manganh LIKE :kw OR n.tennganh LIKE :kw",
                    Nganh.class);
            query.setParameter("kw", "%" + keyword + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countSearch(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(n) FROM Nganh n WHERE n.manganh LIKE :kw OR n.tennganh LIKE :kw",
                            Long.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .uniqueResult();
        }
    }

    public Nganh findByManganh(String manganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Nganh n WHERE n.manganh = :manganh", Nganh.class)
                    .setParameter("manganh", manganh)
                    .uniqueResult();
        }
    }
}