package com.xettuyen.repository;

import com.xettuyen.entity.DiemThiDgnlVsat;
import com.xettuyen.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class DiemThiDgnlVsatRepository extends BaseRepository<DiemThiDgnlVsat> {

    public DiemThiDgnlVsatRepository() {
        super(DiemThiDgnlVsat.class);
    }

    public List<DiemThiDgnlVsat> search(String keyword, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<DiemThiDgnlVsat> query = session.createQuery(
                    "FROM DiemThiDgnlVsat d WHERE d.cccd LIKE :kw OR d.ma_mon LIKE :kw OR d.ten_mon LIKE :kw",
                    DiemThiDgnlVsat.class);
            query.setParameter("kw", "%" + keyword + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public List<DiemThiDgnlVsat> searchAll(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<DiemThiDgnlVsat> query = session.createQuery(
                    "FROM DiemThiDgnlVsat d WHERE d.cccd LIKE :kw OR d.ma_mon LIKE :kw OR d.ten_mon LIKE :kw",
                    DiemThiDgnlVsat.class);
            query.setParameter("kw", "%" + keyword + "%");
            return query.list();
        }
    }

    public long countSearch(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(d) FROM DiemThiDgnlVsat d WHERE d.cccd LIKE :kw OR d.ma_mon LIKE :kw OR d.ten_mon LIKE :kw",
                            Long.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .uniqueResult();
        }
    }

    public List<DiemThiDgnlVsat> searchAnd(String cccd, String maMon, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM DiemThiDgnlVsat d WHERE (:cccd = '' OR d.cccd LIKE :cccdKw) AND (:maMon = '' OR d.ma_mon LIKE :maMonKw)";
            Query<DiemThiDgnlVsat> query = session.createQuery(hql, DiemThiDgnlVsat.class);
            query.setParameter("cccd", cccd);
            query.setParameter("cccdKw", "%" + cccd + "%");
            query.setParameter("maMon", maMon);
            query.setParameter("maMonKw", "%" + maMon + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countSearchAnd(String cccd, String maMon) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(d) FROM DiemThiDgnlVsat d WHERE (:cccd = '' OR d.cccd LIKE :cccdKw) AND (:maMon = '' OR d.ma_mon LIKE :maMonKw)",
                            Long.class)
                    .setParameter("cccd", cccd)
                    .setParameter("cccdKw", "%" + cccd + "%")
                    .setParameter("maMon", maMon)
                    .setParameter("maMonKw", "%" + maMon + "%")
                    .uniqueResult();
        }
    }

    public DiemThiDgnlVsat findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM DiemThiDgnlVsat d WHERE d.cccd = :cccd", DiemThiDgnlVsat.class)
                    .setParameter("cccd", cccd)
                    .uniqueResult();
        }
    }

    public DiemThiDgnlVsat findByDvKeys(String dvKeys) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM DiemThiDgnlVsat d WHERE d.dv_keys = :dvKeys", DiemThiDgnlVsat.class)
                    .setParameter("dvKeys", dvKeys)
                    .uniqueResult();
        }
    }
}