package com.xettuyen.repository;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.Nganh;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NganhRepository extends BaseRepository<Nganh> {

    public NganhRepository() {
        super(Nganh.class);
    }

    public List<Nganh> search(String keyword, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Nganh> query = session.createQuery(
                    "FROM Nganh n WHERE n.manganh LIKE :kw OR n.tennganh LIKE :kw",
                    Nganh.class);
            query.setParameter("kw", "%" + keyword + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countSearch(String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(n) FROM Nganh n WHERE n.manganh LIKE :kw OR n.tennganh LIKE :kw",
                            Long.class)
                    .setParameter("kw", "%" + keyword + "%")
                    .uniqueResult();
        }
    }

    public List<Nganh> searchAnd(String manganh, String tennganh, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Nganh n WHERE (:manganh = '' OR n.manganh LIKE :manganhKw) AND (:tennganh = '' OR n.tennganh LIKE :tennganhKw)";
            Query<Nganh> query = session.createQuery(hql, Nganh.class);
            query.setParameter("manganh", manganh);
            query.setParameter("manganhKw", "%" + manganh + "%");
            query.setParameter("tennganh", tennganh);
            query.setParameter("tennganhKw", "%" + tennganh + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countSearchAnd(String manganh, String tennganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(n) FROM Nganh n WHERE (:manganh = '' OR n.manganh LIKE :manganhKw) AND (:tennganh = '' OR n.tennganh LIKE :tennganhKw)",
                            Long.class)
                    .setParameter("manganh", manganh)
                    .setParameter("manganhKw", "%" + manganh + "%")
                    .setParameter("tennganh", tennganh)
                    .setParameter("tennganhKw", "%" + tennganh + "%")
                    .uniqueResult();
        }
    }

    public Nganh findByManganh(String manganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Nganh n WHERE n.manganh = :manganh", Nganh.class)
                    .setParameter("manganh", manganh)
                    .uniqueResult();
        }
    }

    public void updateSlNguyenVong() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Map<String, Long> xttMap = new HashMap<>();
            Map<String, Long> dgnlMap = new HashMap<>();
            Map<String, Long> vsatMap = new HashMap<>();
            Map<String, Long> thptMap = new HashMap<>();

            session.createQuery(
                            "SELECT nv.nv_manganh, nv.tt_phuongthuc, COUNT(nv) FROM NguyenVong nv GROUP BY nv.nv_manganh, nv.tt_phuongthuc",
                            Object[].class)
                    .list()
                    .forEach(row -> {
                        String manganh = (String) row[0];
                        String phuongthuc = row[1] == null ? "" : ((String) row[1]).toLowerCase();
                        Long count = (Long) row[2];
                        switch (phuongthuc) {
                            case "xtt" -> xttMap.put(manganh, count);
                            case "dgnl" -> dgnlMap.put(manganh, count);
                            case "vsat" -> vsatMap.put(manganh, count);
                            case "thpt" -> thptMap.put(manganh, count);
                        }
                    });

            List<Nganh> list = session.createQuery("FROM Nganh", Nganh.class).list();
            for (Nganh n : list) {
                n.setSl_xtt(xttMap.getOrDefault(n.getManganh(), 0L).intValue());
                n.setSl_dgnl(dgnlMap.getOrDefault(n.getManganh(), 0L).intValue());
                n.setSl_vsat(vsatMap.getOrDefault(n.getManganh(), 0L).intValue());
                n.setSl_thpt(thptMap.getOrDefault(n.getManganh(), 0L).intValue());
                session.merge(n);
            }

            session.getTransaction().commit();
        }
    }
}