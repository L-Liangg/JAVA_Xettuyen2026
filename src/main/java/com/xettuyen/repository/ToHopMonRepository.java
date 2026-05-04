package com.xettuyen.repository;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.ToHopMon;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ToHopMonRepository extends BaseRepository<ToHopMon> {

    public ToHopMonRepository() {
        super(ToHopMon.class);
    }

    public List<ToHopMon> search(String keyword, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ToHopMon> query = session.createQuery(
                    "FROM ToHopMon t WHERE t.matohop LIKE :kw OR t.tentohop LIKE :kw OR t.mon1 LIKE :kw OR t.mon2 LIKE :kw OR t.mon3 LIKE :kw",
                    ToHopMon.class);
            query.setParameter("kw", "%" + keyword + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countSearch(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(t) FROM ToHopMon t WHERE t.matohop LIKE :kw OR t.tentohop LIKE :kw OR t.mon1 LIKE :kw OR t.mon2 LIKE :kw OR t.mon3 LIKE :kw",
                            Long.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .uniqueResult();
        }
    }

    public ToHopMon findByMatohop(String matohop) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ToHopMon t WHERE t.matohop = :matohop", ToHopMon.class)
                    .setParameter("matohop", matohop)
                    .uniqueResult();
        }
    }
}