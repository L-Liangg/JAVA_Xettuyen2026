package com.xettuyen.repository;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.ThiSinh;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ThiSinhRepository extends BaseRepository<ThiSinh> {

    public ThiSinhRepository() {
        super(ThiSinh.class);
    }

    public List<ThiSinh> search(String keyword, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ThiSinh> query = session.createQuery(
                    "FROM ThiSinh WHERE cccd LIKE :kw OR CONCAT(ho, ' ', ten) LIKE :kw",
                    ThiSinh.class);
            query.setParameter("kw", "%" + keyword + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countSearch(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(t) FROM ThiSinh t WHERE t.cccd LIKE :kw OR CONCAT(t.ho, ' ', t.ten) LIKE :kw",
                            Long.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .uniqueResult();
        }
    }

    // AND search: cccd AND sobaodanh
    public List<ThiSinh> searchAnd(String cccd, String sobaodanh, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM ThiSinh t WHERE (:cccd = '' OR t.cccd LIKE :cccdKw) AND (:sbd = '' OR t.sobaodanh LIKE :sbdKw)";
            Query<ThiSinh> query = session.createQuery(hql, ThiSinh.class);
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
            String hql = "SELECT COUNT(t) FROM ThiSinh t WHERE (:cccd = '' OR t.cccd LIKE :cccdKw) AND (:sbd = '' OR t.sobaodanh LIKE :sbdKw)";
            return session.createQuery(hql, Long.class)
                    .setParameter("cccd", cccd)
                    .setParameter("cccdKw", "%" + cccd + "%")
                    .setParameter("sbd", sobaodanh)
                    .setParameter("sbdKw", "%" + sobaodanh + "%")
                    .uniqueResult();
        }
    }

    public ThiSinh findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ThiSinh WHERE cccd = :cccd", ThiSinh.class)
                    .setParameter("cccd", cccd)
                    .uniqueResult();
        }
    }
}
