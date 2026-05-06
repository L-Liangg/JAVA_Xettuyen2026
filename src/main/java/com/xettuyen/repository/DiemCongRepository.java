package com.xettuyen.repository;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.DiemCong;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class DiemCongRepository extends BaseRepository<DiemCong> {

    public DiemCongRepository() {
        super(DiemCong.class);
    }

    public List<DiemCong> search(String keyword, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<DiemCong> query = session.createQuery(
                    "FROM DiemCong d WHERE d.ts_cccd LIKE :kw OR d.manganh LIKE :kw " +
                    "OR d.matohop LIKE :kw OR d.phuongthuc LIKE :kw OR d.dc_keys LIKE :kw",
                    DiemCong.class);
            query.setParameter("kw", "%" + keyword + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countSearch(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(d) FROM DiemCong d WHERE d.ts_cccd LIKE :kw OR d.manganh LIKE :kw " +
                            "OR d.matohop LIKE :kw OR d.phuongthuc LIKE :kw OR d.dc_keys LIKE :kw",
                            Long.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .uniqueResult();
        }
    }

    // AND search: cccd AND manganh
    public List<DiemCong> searchAnd(String cccd, String manganh, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM DiemCong d WHERE (:cccd = '' OR d.ts_cccd LIKE :cccdKw) AND (:manganh = '' OR d.manganh LIKE :manganhKw)";
            Query<DiemCong> query = session.createQuery(hql, DiemCong.class);
            query.setParameter("cccd", cccd);
            query.setParameter("cccdKw", "%" + cccd + "%");
            query.setParameter("manganh", manganh);
            query.setParameter("manganhKw", "%" + manganh + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countSearchAnd(String cccd, String manganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(d) FROM DiemCong d WHERE (:cccd = '' OR d.ts_cccd LIKE :cccdKw) AND (:manganh = '' OR d.manganh LIKE :manganhKw)",
                            Long.class)
                    .setParameter("cccd", cccd)
                    .setParameter("cccdKw", "%" + cccd + "%")
                    .setParameter("manganh", manganh)
                    .setParameter("manganhKw", "%" + manganh + "%")
                    .uniqueResult();
        }
    }

    public DiemCong findByDcKeys(String dcKeys) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM DiemCong d WHERE d.dc_keys = :dcKeys", DiemCong.class)
                    .setParameter("dcKeys", dcKeys)
                    .uniqueResult();
        }
    }

    public List<DiemCong> findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM DiemCong d WHERE d.ts_cccd = :cccd", DiemCong.class)
                    .setParameter("cccd", cccd)
                    .list();
        }
    }
}
