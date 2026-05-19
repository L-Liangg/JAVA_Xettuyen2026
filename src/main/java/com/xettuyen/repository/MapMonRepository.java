package com.xettuyen.repository;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.MapMon;
import org.hibernate.Session;

import java.util.List;

public class MapMonRepository extends BaseRepository<MapMon> {

    public MapMonRepository() {
        super(MapMon.class);
    }

    public List<MapMon> findByMonThpt(String monThpt) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM MapMon m WHERE m.mon_thpt = :monThpt", MapMon.class)
                    .setParameter("monThpt", monThpt)
                    .list();
        }
    }
}
