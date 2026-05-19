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

        Map<Integer, Map<String, List<NguyenVong>>> byLevelAndNganh = new HashMap<>();
        int maxLevel = 0;
        for (NguyenVong nv : list) {
            nv.setNv_ketqua("0");
            Integer level = nv.getNv_tt();
            if (level == null) continue;
            maxLevel = Math.max(maxLevel, level);

            String manganh = nv.getNv_manganh();
            if (manganh == null || manganh.isBlank()) continue;

            byLevelAndNganh
                    .computeIfAbsent(level, k -> new HashMap<>())
                    .computeIfAbsent(manganh, k -> new ArrayList<>())
                    .add(nv);
        }

        Set<String> acceptedCccd = new HashSet<>();
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

        for (int level = 1; level <= maxLevel; level++) {
            Map<String, List<NguyenVong>> byNganh = byLevelAndNganh.get(level);
            if (byNganh == null) continue;

            for (Map.Entry<String, List<NguyenVong>> entry : byNganh.entrySet()) {
                String manganh = entry.getKey();
                int conLai = chiTieuByMa.getOrDefault(manganh, 0);
                if (conLai <= 0) continue;

                Nganh nganh = nganhByMa.get(manganh);
                BigDecimal diemSan = nganh != null ? nganh.getN_diemsan() : null;
                BigDecimal diemChuan = nganh != null ? nganh.getN_diemtrungtuyen() : null;

                List<NguyenVong> eligible = new ArrayList<>();
                for (NguyenVong nv : entry.getValue()) {
                    String cccd = nv.getNn_cccd();
                    if (cccd == null || acceptedCccd.contains(cccd)) continue;

                    BigDecimal diemXt = nv.getDiem_xettuyen();
                    if (diemXt == null) continue;
                    if (diemSan != null && diemXt.compareTo(diemSan) < 0) continue;
                    if (diemChuan != null && diemXt.compareTo(diemChuan) < 0) continue;

                    eligible.add(nv);
                }

                eligible.sort(byScoreDesc);

                for (NguyenVong nv : eligible) {
                    if (conLai <= 0) break;
                    nv.setNv_ketqua("1");
                    acceptedCccd.add(nv.getNn_cccd());
                    accepted++;
                    conLai--;
                }
                chiTieuByMa.put(manganh, conLai);
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
}
