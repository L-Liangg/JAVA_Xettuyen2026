package com.xettuyen.service.impl;

import com.xettuyen.entity.Nganh;
import com.xettuyen.entity.NguyenVong;
import com.xettuyen.repository.NganhRepository;
import com.xettuyen.repository.NguyenVongRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XetTuyenService {
    public static class Result {
        private final int total;
        private final int accepted;
        private final int rejected;

        public Result(int total, int accepted, int rejected) {
            this.total = total;
            this.accepted = accepted;
            this.rejected = rejected;
        }

        public int getTotal() {
            return total;
        }

        public int getAccepted() {
            return accepted;
        }

        public int getRejected() {
            return rejected;
        }
    }

    private final NguyenVongRepository nguyenVongRepository = new NguyenVongRepository();
    private final NganhRepository nganhRepository = new NganhRepository();

    public Result runXetTuyenAll() {
        List<NguyenVong> list = nguyenVongRepository.findAll();
        List<Nganh> nganhList = nganhRepository.findAll();

        Map<String, Nganh> nganhByMa = new HashMap<>();
        Map<String, Integer> chiTieuByMa = new HashMap<>();
        for (Nganh nganh : nganhList) {
            if (nganh.getManganh() == null) continue;
            nganhByMa.put(nganh.getManganh(), nganh);
            chiTieuByMa.put(nganh.getManganh(), safeInt(nganh.getN_chitieu()));
        }

        Map<String, List<NguyenVong>> byCccd = new HashMap<>();
        for (NguyenVong nv : list) {
            nv.setNv_ketqua("0");
            String cccd = nv.getNn_cccd();
            if (cccd == null || cccd.isBlank()) continue;
            byCccd.computeIfAbsent(cccd, k -> new ArrayList<>()).add(nv);
        }

        int accepted = 0;

        Comparator<NguyenVong> byScoreDesc = (a, b) -> {
            BigDecimal da = a.getDiem_xettuyen();
            BigDecimal db = b.getDiem_xettuyen();
            if (da == null && db == null) return 0;
            if (da == null) return 1;
            if (db == null) return -1;
            int cmp = db.compareTo(da);
            if (cmp != 0) return cmp;
            String ca = a.getNn_cccd() == null ? "" : a.getNn_cccd();
            String cb = b.getNn_cccd() == null ? "" : b.getNn_cccd();
            return ca.compareTo(cb);
        };

        List<String> sortedCccd = new ArrayList<>(byCccd.keySet());
        sortedCccd.sort((a, b) -> {
            BigDecimal maxA = maxDiemXetTuyen(byCccd.get(a));
            BigDecimal maxB = maxDiemXetTuyen(byCccd.get(b));
            if (maxA == null && maxB == null) return a.compareTo(b);
            if (maxA == null) return 1;
            if (maxB == null) return -1;
            int cmp = maxB.compareTo(maxA);
            if (cmp != 0) return cmp;
            return a.compareTo(b);
        });

        for (String cccd : sortedCccd) {
            List<NguyenVong> nvList = byCccd.getOrDefault(cccd, new ArrayList<>());
            nvList.sort((a, b) -> {
                Integer ta = a.getNv_tt();
                Integer tb = b.getNv_tt();
                if (ta == null && tb == null) return 0;
                if (ta == null) return 1;
                if (tb == null) return -1;
                int cmp = Integer.compare(ta, tb);
                if (cmp != 0) return cmp;
                return byScoreDesc.compare(a, b);
            });

            for (NguyenVong nv : nvList) {
                String manganh = nv.getNv_manganh();
                if (manganh == null || manganh.isBlank()) continue;
                int conLai = chiTieuByMa.getOrDefault(manganh, 0);
                if (conLai <= 0) continue;

                Nganh nganh = nganhByMa.get(manganh);
                BigDecimal diemSan = nganh != null ? nganh.getN_diemsan() : null;
                BigDecimal diemChuan = nganh != null ? nganh.getN_diemtrungtuyen() : null;

                BigDecimal diemXt = nv.getDiem_xettuyen();
                if (diemXt == null) continue;
                if (diemSan != null && diemXt.compareTo(diemSan) < 0) continue;
                if (diemChuan != null && diemXt.compareTo(diemChuan) < 0) continue;

                nv.setNv_ketqua("1");
                accepted++;
                chiTieuByMa.put(manganh, conLai - 1);
                break;
            }
        }

        nguyenVongRepository.updateAll(list);

        int total = list.size();
        int rejected = total - accepted;
        return new Result(total, accepted, rejected);
    }

    private static int safeInt(Integer value) {
        return value == null ? 0 : Math.max(0, value);
    }

    private static BigDecimal maxDiemXetTuyen(List<NguyenVong> list) {
        if (list == null || list.isEmpty()) return null;
        BigDecimal max = null;
        for (NguyenVong nv : list) {
            BigDecimal d = nv.getDiem_xettuyen();
            if (d == null) continue;
            if (max == null || d.compareTo(max) > 0) {
                max = d;
            }
        }
        return max;
    }
}
