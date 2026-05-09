package com.xettuyen.repository;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.NguyenVong;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class NguyenVongRepository extends BaseRepository<NguyenVong> {

    public NguyenVongRepository() {
        super(NguyenVong.class);
    }

    public List<NguyenVong> search(String keyword, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<NguyenVong> query = session.createQuery(
                    "FROM NguyenVong n WHERE n.nn_cccd LIKE :kw OR n.nv_manganh LIKE :kw " +
                    "OR n.tt_phuongthuc LIKE :kw OR n.nv_ketqua LIKE :kw OR n.nv_keys LIKE :kw",
                    NguyenVong.class);
            query.setParameter("kw", "%" + keyword + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countSearch(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(n) FROM NguyenVong n WHERE n.nn_cccd LIKE :kw OR n.nv_manganh LIKE :kw " +
                            "OR n.tt_phuongthuc LIKE :kw OR n.nv_ketqua LIKE :kw OR n.nv_keys LIKE :kw",
                            Long.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .uniqueResult();
        }
    }

    // AND search: cccd AND manganh
    public List<NguyenVong> searchAnd(String cccd, String manganh, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM NguyenVong n WHERE (:cccd = '' OR n.nn_cccd LIKE :cccdKw) AND (:manganh = '' OR n.nv_manganh LIKE :manganhKw)";
            Query<NguyenVong> query = session.createQuery(hql, NguyenVong.class);
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
                            "SELECT COUNT(n) FROM NguyenVong n WHERE (:cccd = '' OR n.nn_cccd LIKE :cccdKw) AND (:manganh = '' OR n.nv_manganh LIKE :manganhKw)",
                            Long.class)
                    .setParameter("cccd", cccd)
                    .setParameter("cccdKw", "%" + cccd + "%")
                    .setParameter("manganh", manganh)
                    .setParameter("manganhKw", "%" + manganh + "%")
                    .uniqueResult();
        }
    }

    public NguyenVong findByNvKeys(String nvKeys) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM NguyenVong n WHERE n.nv_keys = :nvKeys", NguyenVong.class)
                    .setParameter("nvKeys", nvKeys)
                    .uniqueResult();
        }
    }
}
