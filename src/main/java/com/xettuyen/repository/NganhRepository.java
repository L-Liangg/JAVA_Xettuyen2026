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

    // AND search: manganh AND tennganh
    public List<Nganh> searchAnd(String manganh, String tennganh, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Nganh n WHERE (:manganh = '' OR n.manganh LIKE :manganhKw) AND (:tennganh = '' OR n.tennganh LIKE :tennganhKw)";
            Query<Nganh> query = session.createQuery(hql, Nganh.class);
            query.setParameter("manganh", manganh);
            query.setParameter("manganhKw", "%" + manganh + "%");
            query.setParameter("tennganh", tennganh);
            query.setParameter("tennganhKw", "%" + tennganh + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countSearchAnd(String manganh, String tennganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(n) FROM Nganh n WHERE (:manganh = '' OR n.manganh LIKE :manganhKw) AND (:tennganh = '' OR n.tennganh LIKE :tennganhKw)",
                            Long.class)
                    .setParameter("manganh", manganh)
                    .setParameter("manganhKw", "%" + manganh + "%")
                    .setParameter("tennganh", tennganh)
                    .setParameter("tennganhKw", "%" + tennganh + "%")
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
