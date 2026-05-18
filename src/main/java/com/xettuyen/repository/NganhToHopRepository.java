package com.xettuyen.repository;

import com.xettuyen.entity.NganhToHop;
import com.xettuyen.config.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

// import com.xettuyen.config.HibernateUtil;
// import org.hibernate.Session;
// import org.hibernate.query.Query;

// import java.util.List;

public class NganhToHopRepository extends BaseRepository<NganhToHop> {

    public NganhToHopRepository() {
        super(NganhToHop.class);
    }

    public NganhToHop findByTbKeys(String tbKeys) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM NganhToHop n WHERE n.tb_keys = :tbKeys", NganhToHop.class)
                    .setParameter("tbKeys", tbKeys)
                    .uniqueResult();
        }
    }

    // AND search: manganh AND matohop
    public List<NganhToHop> searchAnd(String manganh, String matohop, int page, int pageSize) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM NganhToHop n WHERE (:manganh = '' OR n.manganh LIKE :manganhKw) AND (:matohop = '' OR n.matohop LIKE :matohopKw)";
            Query<NganhToHop> query = session.createQuery(hql, NganhToHop.class);
            query.setParameter("manganh", manganh);
            query.setParameter("manganhKw", "%" + manganh + "%");
            query.setParameter("matohop", matohop);
            query.setParameter("matohopKw", "%" + matohop + "%");
            query.setFirstResult((page - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.list();
        }
    }

    public long countSearchAnd(String manganh, String matohop) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT COUNT(n) FROM NganhToHop n WHERE (:manganh = '' OR n.manganh LIKE :manganhKw) AND (:matohop = '' OR n.matohop LIKE :matohopKw)",
                            Long.class)
                    .setParameter("manganh", manganh)
                    .setParameter("manganhKw", "%" + manganh + "%")
                    .setParameter("matohop", matohop)
                    .setParameter("matohopKw", "%" + matohop + "%")
                    .uniqueResult();
        }
    }

    public List<NganhToHop> findAllByManganh(String manganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM NganhToHop n WHERE n.manganh = :manganh", NganhToHop.class)
                    .setParameter("manganh", manganh)
                    .list();
        }
    }
}
