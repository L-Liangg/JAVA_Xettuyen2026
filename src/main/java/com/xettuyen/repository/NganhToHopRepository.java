package com.xettuyen.repository;

import com.xettuyen.entity.NganhToHop;
import com.xettuyen.config.HibernateUtil;
import org.hibernate.Session;

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
}