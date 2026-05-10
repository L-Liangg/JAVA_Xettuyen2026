package com.xettuyen.repository;

import com.xettuyen.entity.DiemThi;
import com.xettuyen.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class DiemThiRepository extends BaseRepository<DiemThi> {

    public DiemThiRepository() {
        super(DiemThi.class);
    }

    public List<DiemThi> search(String keyword, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<DiemThi> query = session.createQuery(
                    "FROM DiemThi d WHERE d.cccd LIKE :kw OR d.sobaodanh LIKE :kw OR d.d_phuongthuc LIKE :kw",
                    DiemThi.class);
            query.setParameter("kw", "%" + keyword + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public List<DiemThi> searchAll(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<DiemThi> query = session.createQuery(
                    "FROM DiemThi d WHERE d.cccd LIKE :kw OR d.sobaodanh LIKE :kw OR d.d_phuongthuc LIKE :kw",
                    DiemThi.class);
            query.setParameter("kw", "%" + keyword + "%");
            return query.list();
        }
    }

    public long countSearch(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(d) FROM DiemThi d WHERE d.cccd LIKE :kw OR d.sobaodanh LIKE :kw OR d.d_phuongthuc LIKE :kw",
                            Long.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .uniqueResult();
        }
    }

    // AND search: cccd AND sobaodanh
    public List<DiemThi> searchAnd(String cccd, String sobaodanh, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM DiemThi d WHERE (:cccd = '' OR d.cccd LIKE :cccdKw) AND (:sbd = '' OR d.sobaodanh LIKE :sbdKw)";
            Query<DiemThi> query = session.createQuery(hql, DiemThi.class);
            query.setParameter("cccd", cccd);
            query.setParameter("cccdKw", "%" + cccd + "%");
            query.setParameter("sbd", sobaodanh);
            query.setParameter("sbdKw", "%" + sobaodanh + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countSearchAnd(String cccd, String sobaodanh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(d) FROM DiemThi d WHERE (:cccd = '' OR d.cccd LIKE :cccdKw) AND (:sbd = '' OR d.sobaodanh LIKE :sbdKw)",
                            Long.class)
                    .setParameter("cccd", cccd)
                    .setParameter("cccdKw", "%" + cccd + "%")
                    .setParameter("sbd", sobaodanh)
                    .setParameter("sbdKw", "%" + sobaodanh + "%")
                    .uniqueResult();
        }
    }

    public DiemThi findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM DiemThi d WHERE d.cccd = :cccd", DiemThi.class)
                    .setParameter("cccd", cccd)
                    .uniqueResult();
        }
    }
}

