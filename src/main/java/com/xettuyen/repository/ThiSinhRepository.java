package com.xettuyen.repository;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.ThiSinh;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ThiSinhRepository extends BaseRepository<ThiSinh> {

    public ThiSinhRepository() {
        super(ThiSinh.class);
    }

    public List<ThiSinh> search(String keyword, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ThiSinh> query = session.createQuery(
                    "FROM ThiSinh WHERE cccd LIKE :kw OR CONCAT(ho, ' ', ten) LIKE :kw OR sobaodanh LIKE :kw",
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
                            "SELECT COUNT(ts) FROM ThiSinh ts WHERE ts.cccd LIKE :kw OR CONCAT(ts.ho, ' ', ts.ten) LIKE :kw OR ts.sobaodanh LIKE :kw",
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

    public Map<String, Long> countByDoiTuong() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Map<String, Long> map = new LinkedHashMap<>();
            session.createQuery(
                            "SELECT ts.doi_tuong, COUNT(ts) FROM ThiSinh ts GROUP BY ts.doi_tuong ORDER BY ts.doi_tuong",
                            Object[].class)
                    .list()
                    .forEach(row -> map.merge(
                            (row[0] == null || ((String) row[0]).isBlank()) ? "(Trống)" : (String) row[0],
                            (Long) row[1],
                            Long::sum
                    ));
            return map;
        }
    }

    public Map<String, Long> countByKhuVuc() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Map<String, Long> map = new LinkedHashMap<>();
            session.createQuery(
                            "SELECT ts.khu_vuc, COUNT(ts) FROM ThiSinh ts GROUP BY ts.khu_vuc ORDER BY ts.khu_vuc",
                            Object[].class)
                    .list()
                    .forEach(row -> map.merge(
                            (row[0] == null || ((String) row[0]).isBlank()) ? "(Trống)" : (String) row[0],
                            (Long) row[1],
                            Long::sum
                    ));
            return map;
        }
    }
}

