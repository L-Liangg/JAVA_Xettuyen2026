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

    public ThiSinh findByCccd(String cccd) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ThiSinh WHERE cccd = :cccd", ThiSinh.class)
                    .setParameter("cccd", cccd)
                    .uniqueResult();
        }
    }
}