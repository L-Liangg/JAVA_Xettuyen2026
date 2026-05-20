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

    public void updateAll(List<NguyenVong> list) {
        if (list == null || list.isEmpty()) return;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            for (NguyenVong nv : list) {
                session.merge(nv);
            }
            session.getTransaction().commit();
        }
    }

    public void batchUpdate(List<NguyenVong> list) {
    if (list == null || list.isEmpty()) return;
    
    String hql = "UPDATE NguyenVong n SET " +
                 "n.diem_thxt = :diemThxt, " +
                 "n.diem_utqd = :diemUtqd, " +
                 "n.diem_cong = :diemCong, " +
                 "n.diem_xettuyen = :diemXetTuyen, " +
                 "n.tt_thm = :ttThm, " +
                 "n.nv_ketqua = :nvKetqua, " +
                 "n.nv_keys = :nvKeys " +
                 "WHERE n.id = :id";
    
    Session session = null;
    try {
        session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        
        int batchSize = 100;
        int count = 0;
        
        for (NguyenVong nv : list) {
            // Sử dụng createMutationQuery cho UPDATE/DELETE (Hibernate 6+)
            var query = session.createMutationQuery(hql);
            query.setParameter("diemThxt", nv.getDiem_thxt());
            query.setParameter("diemUtqd", nv.getDiem_utqd());
            query.setParameter("diemCong", nv.getDiem_cong());
            query.setParameter("diemXetTuyen", nv.getDiem_xettuyen());
            query.setParameter("ttThm", nv.getTt_thm());
            query.setParameter("nvKetqua", nv.getNv_ketqua());
            query.setParameter("nvKeys", nv.getNv_keys());
            query.setParameter("id", nv.getIdnv());
            
            query.executeUpdate();
            count++;
            
            if (count % batchSize == 0) {
                session.flush();
                session.clear();
            }
        }
        
        session.getTransaction().commit();
        
    } catch (Exception e) {
        if (session != null && session.getTransaction() != null) {
            session.getTransaction().rollback();
        }
        e.printStackTrace();
        throw new RuntimeException("Batch update failed: " + e.getMessage(), e);
    } finally {
        if (session != null) {
            session.close();
        }
    }
}
}
