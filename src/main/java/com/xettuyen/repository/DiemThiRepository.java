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

    public DiemThi findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM DiemThi d WHERE d.cccd = :cccd", DiemThi.class)
                    .setParameter("cccd", cccd)
                    .uniqueResult();
        }
    }
}