package com.xettuyen.repository;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.BangQuyDoi;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class BangQuyDoiRepository extends BaseRepository<BangQuyDoi> {

    public BangQuyDoiRepository() {
        super(BangQuyDoi.class);
    }

    public List<BangQuyDoi> search(String keyword, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<BangQuyDoi> query = session.createQuery(
                    "FROM BangQuyDoi b WHERE b.d_phuongthuc LIKE :kw OR b.d_tohop LIKE :kw " +
                    "OR b.d_mon LIKE :kw OR b.d_maquydoi LIKE :kw",
                    BangQuyDoi.class);
            query.setParameter("kw", "%" + keyword + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countSearch(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(b) FROM BangQuyDoi b WHERE b.d_phuongthuc LIKE :kw OR b.d_tohop LIKE :kw " +
                            "OR b.d_mon LIKE :kw OR b.d_maquydoi LIKE :kw",
                            Long.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .uniqueResult();
        }
    }

    // AND search: phuongthuc AND tohop
    public List<BangQuyDoi> searchAnd(String phuongthuc, String tohop, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BangQuyDoi b WHERE (:phuongthuc = '' OR b.d_phuongthuc LIKE :phuongthucKw) AND (:tohop = '' OR b.d_tohop LIKE :tohopKw)";
            Query<BangQuyDoi> query = session.createQuery(hql, BangQuyDoi.class);
            query.setParameter("phuongthuc", phuongthuc);
            query.setParameter("phuongthucKw", "%" + phuongthuc + "%");
            query.setParameter("tohop", tohop);
            query.setParameter("tohopKw", "%" + tohop + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countSearchAnd(String phuongthuc, String tohop) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(b) FROM BangQuyDoi b WHERE (:phuongthuc = '' OR b.d_phuongthuc LIKE :phuongthucKw) AND (:tohop = '' OR b.d_tohop LIKE :tohopKw)",
                            Long.class)
                    .setParameter("phuongthuc", phuongthuc)
                    .setParameter("phuongthucKw", "%" + phuongthuc + "%")
                    .setParameter("tohop", tohop)
                    .setParameter("tohopKw", "%" + tohop + "%")
                    .uniqueResult();
        }
    }

    public BangQuyDoi findByMaquydoi(String maquydoi) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM BangQuyDoi b WHERE b.d_maquydoi = :maquydoi", BangQuyDoi.class)
                    .setParameter("maquydoi", maquydoi)
                    .uniqueResult();
        }
    }

    public List<BangQuyDoi> findByPhuongthucMon(String phuongthuc, String mon) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM BangQuyDoi b WHERE b.d_phuongthuc = :phuongthuc AND b.d_mon = :mon ORDER BY b.d_diema ASC",
                            BangQuyDoi.class)
                    .setParameter("phuongthuc", phuongthuc)
                    .setParameter("mon", mon)
                    .list();
        }
    }

    public List<BangQuyDoi> findByPhuongthucTohop(String phuongthuc, String tohop) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM BangQuyDoi b WHERE b.d_phuongthuc = :phuongthuc AND b.d_tohop = :tohop AND b.d_mon IS NULL ORDER BY b.d_diema ASC",
                            BangQuyDoi.class)
                    .setParameter("phuongthuc", phuongthuc)
                    .setParameter("tohop", tohop)
                    .list();
        }
    }
}