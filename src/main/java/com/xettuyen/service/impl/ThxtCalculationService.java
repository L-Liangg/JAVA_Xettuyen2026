package com.xettuyen.service.impl;

import com.xettuyen.entity.BangQuyDoi;
import com.xettuyen.entity.DiemThi;
import com.xettuyen.entity.DiemThiDgnlVsat;
import com.xettuyen.entity.MapMon;
import com.xettuyen.entity.Nganh;
import com.xettuyen.entity.NganhToHop;
import com.xettuyen.entity.NguyenVong;
import com.xettuyen.entity.DiemCong;
import com.xettuyen.entity.ThiSinh;
import com.xettuyen.repository.BangQuyDoiRepository;
import com.xettuyen.repository.DiemCongRepository;
import com.xettuyen.repository.DiemThiDgnlVsatRepository;
import com.xettuyen.repository.DiemThiRepository;
import com.xettuyen.repository.MapMonRepository;
import com.xettuyen.repository.NganhRepository;
import com.xettuyen.repository.NganhToHopRepository;
import com.xettuyen.repository.NguyenVongRepository;
import com.xettuyen.repository.ThiSinhRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ThxtCalculationService {
    private static final BigDecimal THREE = BigDecimal.valueOf(3);
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final int SCALE_INTERNAL = 8;
    private static final int SCALE_OUTPUT = 5;

    private final NguyenVongRepository nguyenVongRepository = new NguyenVongRepository();
    private final NganhRepository nganhRepository = new NganhRepository();
    private final NganhToHopRepository nganhToHopRepository = new NganhToHopRepository();
    private final DiemThiRepository diemThiRepository = new DiemThiRepository();
    private final DiemThiDgnlVsatRepository diemThiDgnlVsatRepository = new DiemThiDgnlVsatRepository();
    private final MapMonRepository mapMonRepository = new MapMonRepository();
    private final BangQuyDoiRepository bangQuyDoiRepository = new BangQuyDoiRepository();
    private final ThiSinhRepository thiSinhRepository = new ThiSinhRepository();
    private final DiemCongRepository diemCongRepository = new DiemCongRepository();

    public List<String> recalculateAll() {
        List<String> warnings = new ArrayList<>();
        List<NguyenVong> list = nguyenVongRepository.findAll();

        Map<String, MapMon> mapMonByThpt = loadMapMonByThpt();
        Map<String, String> dgnlCodeByThpt = loadDgnlCodeByThpt(mapMonByThpt);
        Map<String, List<BangQuyDoi>> quyDoiCache = new HashMap<>();

        for (NguyenVong nv : list) {
            String cccd = normalize(nv.getNn_cccd());
            String manganh = normalize(nv.getNv_manganh());
            String phuongthuc = normalize(nv.getTt_phuongthuc());
            String nvKey = buildNvKey(nv);

            if (cccd.isEmpty()) {
                warnings.add(nvKey + ": thiếu CCCD.");
                continue;
            }
            if (manganh.isEmpty()) {
                warnings.add(nvKey + ": thiếu mã ngành.");
                continue;
            }
            if (phuongthuc.isEmpty()) {
                warnings.add(nvKey + ": thiếu phương thức.");
                continue;
            }

            Nganh nganh = nganhRepository.findByManganh(manganh);
            if (nganh == null) {
                warnings.add(nvKey + ": không tìm thấy ngành " + manganh + ".");
                continue;
            }

            List<NganhToHop> toHops = nganhToHopRepository.findAllByManganh(manganh);
            if (toHops == null || toHops.isEmpty()) {
                warnings.add(nvKey + ": ngành chưa có tổ hợp xét tuyển.");
                continue;
            }

            applyDiemCong(nv);

            DiemThi diemThi = null;
            Map<String, BigDecimal> dgnlVsatByMon = null;
            BigDecimal dgnlTotalRaw = null;

            if (isPhuongThuc(phuongthuc, "THPT")) {
                diemThi = diemThiRepository.findByCccd(cccd);
                if (diemThi == null) {
                    warnings.add(nvKey + ": không tìm thấy điểm THPT.");
                    continue;
                }
            } else if (isPhuongThuc(phuongthuc, "VSAT")) {
                List<DiemThiDgnlVsat> listDv = diemThiDgnlVsatRepository.findAllByCccd(cccd);
                if (listDv == null || listDv.isEmpty()) {
                    warnings.add(nvKey + ": không tìm thấy điểm " + phuongthuc + ".");
                    continue;
                }
                dgnlVsatByMon = indexDiemByMon(listDv);
            } else if (isPhuongThuc(phuongthuc, "DGNL")) {
                List<DiemThiDgnlVsat> listDv = diemThiDgnlVsatRepository.findAllByCccd(cccd);
                if (listDv == null || listDv.isEmpty()) {
                    warnings.add(nvKey + ": không tìm thấy điểm " + phuongthuc + ".");
                    continue;
                }
                dgnlTotalRaw = sumDgnlTotal(listDv, dgnlCodeByThpt);
                if (dgnlTotalRaw == null) {
                    warnings.add(nvKey + ": không xác định được tổng điểm DGNL.");
                    continue;
                }
            } else {
                warnings.add(nvKey + ": phương thức không hợp lệ: " + phuongthuc + ".");
                continue;
            }

            BigDecimal best = null;
            String bestToHop = null;
            BigDecimal bestThxtRaw = null;

            for (NganhToHop toHop : toHops) {
                BigDecimal thxt = computeThxtForToHop(
                        toHop,
                        phuongthuc,
                        diemThi,
                        dgnlVsatByMon,
                        dgnlTotalRaw,
                        mapMonByThpt,
                        quyDoiCache,
                        warnings,
                        nvKey
                );
                if (thxt == null) continue;

                BigDecimal quyDoi = applyDolech(thxt, toHop, nganh.getN_tohopgoc());
                if (best == null || quyDoi.compareTo(best) > 0) {
                    best = quyDoi;
                    bestToHop = toHop.getMatohop();
                    bestThxtRaw = thxt;
                }
            }

            if (best == null) {
                warnings.add(nvKey + ": không tính được điểm THXT cho bất kỳ tổ hợp nào.");
                nguyenVongRepository.update(nv);
                continue;
            }

            nv.setDiem_thxt(bestThxtRaw.setScale(SCALE_OUTPUT, RoundingMode.HALF_UP));
            nv.setTt_thm(bestToHop);
            applyDiemUtqd(nv, bestThxtRaw, warnings, nvKey);
            applyDiemXetTuyen(nv, best);
            nguyenVongRepository.update(nv);
        }

        return warnings;
    }

    private BigDecimal computeThxtForToHop(
            NganhToHop toHop,
            String phuongthuc,
            DiemThi diemThi,
            Map<String, BigDecimal> dgnlVsatByMon,
            BigDecimal dgnlTotalRaw,
            Map<String, MapMon> mapMonByThpt,
            Map<String, List<BangQuyDoi>> quyDoiCache,
            List<String> warnings,
            String nvKey
    ) {
        String mon1 = normalize(toHop.getTh_mon1());
        String mon2 = normalize(toHop.getTh_mon2());
        String mon3 = normalize(toHop.getTh_mon3());

        if (mon1.isEmpty() || mon2.isEmpty() || mon3.isEmpty()) {
            warnings.add(nvKey + ": tổ hợp " + toHop.getMatohop() + " thiếu môn thi.");
            return null;
        }

        int hs1 = toInt(toHop.getHsmon1());
        int hs2 = toInt(toHop.getHsmon2());
        int hs3 = toInt(toHop.getHsmon3());
        int hsSum = hs1 + hs2 + hs3;
        if (hsSum <= 0) {
            warnings.add(nvKey + ": tổ hợp " + toHop.getMatohop() + " có hệ số không hợp lệ.");
            return null;
        }

        if (isPhuongThuc(phuongthuc, "THPT")) {
            BigDecimal d1 = getDiemThptMon(diemThi, mon1);
            BigDecimal d2 = getDiemThptMon(diemThi, mon2);
            BigDecimal d3 = getDiemThptMon(diemThi, mon3);
            if (d1 == null || d2 == null || d3 == null) {
                warnings.add(nvKey + ": thiếu điểm THPT cho tổ hợp " + toHop.getMatohop() + ".");
                return null;
            }
            BigDecimal dt = weightedAverage(d1, d2, d3, hs1, hs2, hs3);
            return dt.multiply(THREE);
        }

        if (isPhuongThuc(phuongthuc, "VSAT")) {
            BigDecimal d1 = getDiemVsatMon(mon1, dgnlVsatByMon, mapMonByThpt, quyDoiCache, warnings, nvKey);
            BigDecimal d2 = getDiemVsatMon(mon2, dgnlVsatByMon, mapMonByThpt, quyDoiCache, warnings, nvKey);
            BigDecimal d3 = getDiemVsatMon(mon3, dgnlVsatByMon, mapMonByThpt, quyDoiCache, warnings, nvKey);
            if (d1 == null || d2 == null || d3 == null) return null;
            BigDecimal dt = weightedAverage(d1, d2, d3, hs1, hs2, hs3);
            return dt.multiply(THREE);
        }

        if (isPhuongThuc(phuongthuc, "DGNL")) {
            if (dgnlTotalRaw == null) {
                warnings.add(nvKey + ": thiếu tổng điểm DGNL.");
                return null;
            }
            return convertDgnlTotalByToHop(toHop.getMatohop(), dgnlTotalRaw, quyDoiCache, warnings, nvKey);
        }

        return null;
    }

    private BigDecimal getDiemVsatMon(
            String monThpt,
            Map<String, BigDecimal> dgnlVsatByMon,
            Map<String, MapMon> mapMonByThpt,
            Map<String, List<BangQuyDoi>> quyDoiCache,
            List<String> warnings,
            String nvKey
    ) {
        MapMon mapMon = mapMonByThpt.get(monThpt);
        if (mapMon == null || mapMon.getMon_vsat() == null) {
            warnings.add(nvKey + ": chưa khai báo mã VSAT cho môn " + monThpt + ".");
            return null;
        }
        BigDecimal raw = dgnlVsatByMon.get(mapMon.getMon_vsat());
        if (raw == null) {
            warnings.add(nvKey + ": thiếu điểm VSAT môn " + monThpt + ".");
            return null;
        }
        return convertByBangQuyDoi("VSAT", monThpt, raw, quyDoiCache, warnings, nvKey);
    }

    private BigDecimal convertDgnlTotalByToHop(
            String matohop,
            BigDecimal raw,
            Map<String, List<BangQuyDoi>> quyDoiCache,
            List<String> warnings,
            String nvKey
    ) {
        String tohop = normalize(matohop);
        if (tohop.isEmpty()) {
            warnings.add(nvKey + ": tổ hợp DGNL không hợp lệ.");
            return null;
        }
        String key = "DGNL|TOHOP|" + tohop;
        List<BangQuyDoi> rows = quyDoiCache.get(key);
        if (rows == null) {
            rows = bangQuyDoiRepository.findByPhuongthucTohop("DGNL", tohop);
            quyDoiCache.put(key, rows);
        }
        if (rows == null || rows.isEmpty()) {
            warnings.add(nvKey + ": thiếu bảng quy đổi DGNL cho tổ hợp " + tohop + ".");
            return null;
        }
        return convertRawByRows(raw, rows, warnings, nvKey, "DGNL tổng");
    }

    private BigDecimal convertByBangQuyDoi(
            String phuongthuc,
            String monThpt,
            BigDecimal raw,
            Map<String, List<BangQuyDoi>> quyDoiCache,
            List<String> warnings,
            String nvKey
    ) {
        String key = phuongthuc + "|" + monThpt;
        List<BangQuyDoi> rows = quyDoiCache.get(key);
        if (rows == null) {
            rows = bangQuyDoiRepository.findByPhuongthucMon(phuongthuc, monThpt);
            quyDoiCache.put(key, rows);
        }
        if (rows == null || rows.isEmpty()) {
            warnings.add(nvKey + ": thiếu bảng quy đổi " + phuongthuc + " cho môn " + monThpt + ".");
            return null;
        }
        return convertRawByRows(raw, rows, warnings, nvKey, monThpt);
    }

    private BigDecimal convertRawByRows(
            BigDecimal raw,
            List<BangQuyDoi> rows,
            List<String> warnings,
            String nvKey,
            String label
    ) {
        BangQuyDoi matched = null;
        for (BangQuyDoi row : rows) {
            if (row.getD_diema() == null || row.getD_diemb() == null
                    || row.getD_diemc() == null || row.getD_diemd() == null) {
                continue;
            }
            if (raw.compareTo(row.getD_diema()) > 0 && raw.compareTo(row.getD_diemb()) <= 0) {
                matched = row;
                break;
            }
        }

        if (matched == null) {
            BangQuyDoi first = rows.get(0);
            BangQuyDoi last = rows.get(rows.size() - 1);
            if (raw.compareTo(first.getD_diema()) <= 0) {
                matched = first;
            } else if (raw.compareTo(last.getD_diemb()) > 0) {
                matched = last;
            }
            if (matched == null) {
                warnings.add(nvKey + ": điểm " + label + " ngoài phạm vi bảng quy đổi.");
                return null;
            }
        }

        BigDecimal a = matched.getD_diema();
        BigDecimal b = matched.getD_diemb();
        BigDecimal c = matched.getD_diemc();
        BigDecimal d = matched.getD_diemd();

        if (a == null || b == null || c == null || d == null) {
            warnings.add(nvKey + ": bảng quy đổi thiếu dữ liệu cho " + label + ".");
            return null;
        }

        BigDecimal denom = b.subtract(a);
        if (denom.compareTo(ZERO) == 0) {
            return d;
        }

        BigDecimal ratio = raw.subtract(a).multiply(d.subtract(c))
                .divide(denom, SCALE_INTERNAL, RoundingMode.HALF_UP);
        return c.add(ratio);
    }

    private BigDecimal getDiemThptMon(DiemThi diemThi, String mon) {
        return switch (mon) {
            case "TO" -> diemThi.getTO();
            case "LI" -> diemThi.getLI();
            case "HO" -> diemThi.getHO();
            case "SI" -> diemThi.getSI();
            case "SU" -> diemThi.getSU();
            case "DI" -> diemThi.getDI();
            case "VA" -> diemThi.getVA();
            case "TI" -> diemThi.getTI();
            case "N1" -> diemThi.getN1_THI() != null ? diemThi.getN1_THI() : diemThi.getN1_CC();
            case "KTPL" -> diemThi.getKTPL();
            case "CNCN" -> diemThi.getCNCN();
            case "CNNN" -> diemThi.getCNNN();
            case "NL1" -> diemThi.getNL1();
            case "NK1" -> diemThi.getNK1();
            case "NK2" -> diemThi.getNK2();
            default -> null;
        };
    }

    private BigDecimal weightedAverage(
            BigDecimal d1,
            BigDecimal d2,
            BigDecimal d3,
            int hs1,
            int hs2,
            int hs3
    ) {
        BigDecimal sum = d1.multiply(BigDecimal.valueOf(hs1))
                .add(d2.multiply(BigDecimal.valueOf(hs2)))
                .add(d3.multiply(BigDecimal.valueOf(hs3)));
        BigDecimal denom = BigDecimal.valueOf(hs1 + hs2 + hs3);
        return sum.divide(denom, SCALE_INTERNAL, RoundingMode.HALF_UP);
    }

    private BigDecimal applyDolech(BigDecimal thxt, NganhToHop toHop, String toHopGoc) {
        String matohop = normalize(toHop.getMatohop());
        String goc = normalize(toHopGoc);
        if (goc.isEmpty()) return thxt;
        if (matohop.equals(goc)) return thxt;
        BigDecimal dolech = toHop.getDolech();
        if (dolech == null) dolech = ZERO;
        return thxt.subtract(dolech);
    }

    private void applyDiemCong(NguyenVong nv) {
        if (nv.getNn_cccd() == null || nv.getNn_cccd().trim().isEmpty()) return;
        List<DiemCong> list = diemCongRepository.findByCccd(nv.getNn_cccd().trim());
        if (list == null || list.isEmpty()) return;

        String manganh = normalize(nv.getNv_manganh());
        String phuongthuc = normalize(nv.getTt_phuongthuc());

        DiemCong best = null;
        for (DiemCong dc : list) {
            if (dc.getDiemTong() == null) continue;
            if (!manganh.isEmpty() && !manganh.equalsIgnoreCase(normalize(dc.getManganh()))) continue;
            if (!phuongthuc.isEmpty() && !phuongthuc.equalsIgnoreCase(normalize(dc.getPhuongthuc()))) continue;
            if (best == null || dc.getDiemTong().compareTo(best.getDiemTong()) > 0) {
                best = dc;
            }
        }

        if (best == null) {
            for (DiemCong dc : list) {
                if (dc.getDiemTong() == null) continue;
                if (best == null || dc.getDiemTong().compareTo(best.getDiemTong()) > 0) {
                    best = dc;
                }
            }
        }

        if (best != null) {
            nv.setDiem_cong(best.getDiemTong());
        }
    }

    private void applyDiemUtqd(NguyenVong nv, BigDecimal thxtRaw, List<String> warnings, String nvKey) {
        if (thxtRaw == null) return;
        if (nv.getNn_cccd() == null || nv.getNn_cccd().trim().isEmpty()) return;

        ThiSinh ts = thiSinhRepository.findByCccd(nv.getNn_cccd().trim());
        if (ts == null) {
            warnings.add(nvKey + ": khong tim thay thong tin thi sinh de tinh UTQD.");
            return;
        }

        BigDecimal diemCong = nv.getDiem_cong() == null ? ZERO : nv.getDiem_cong();
        BigDecimal mUuTien = getDiemDoiTuong(ts.getDoi_tuong()).add(getDiemKhuVuc(ts.getKhu_vuc()));
        BigDecimal thxtCong = thxtRaw.add(diemCong);

        BigDecimal utqd;
        if (thxtCong.compareTo(BigDecimal.valueOf(22.5)) < 0) {
            utqd = mUuTien;
        } else {
            BigDecimal numerator = BigDecimal.valueOf(30).subtract(thxtRaw).subtract(diemCong);
            BigDecimal ratio = numerator.divide(BigDecimal.valueOf(7.5), SCALE_INTERNAL, RoundingMode.HALF_UP);
            utqd = ratio.multiply(mUuTien);
        }

        if (utqd.compareTo(ZERO) < 0) utqd = ZERO;
        nv.setDiem_utqd(utqd.setScale(SCALE_OUTPUT, RoundingMode.HALF_UP));
    }

    private void applyDiemXetTuyen(NguyenVong nv, BigDecimal thgxt) {
        if (thgxt == null) return;
        BigDecimal diemCong = nv.getDiem_cong() == null ? ZERO : nv.getDiem_cong();
        BigDecimal diemUtqd = nv.getDiem_utqd() == null ? ZERO : nv.getDiem_utqd();
        BigDecimal sum = thgxt.add(diemCong).add(diemUtqd);
        nv.setDiem_xettuyen(sum.setScale(SCALE_OUTPUT, RoundingMode.HALF_UP));
    }

    private BigDecimal getDiemDoiTuong(String doiTuong) {
        if (doiTuong == null) return ZERO;
        String val = doiTuong.trim().toUpperCase(Locale.ROOT);
        if (val.isEmpty()) return ZERO;
        if (val.contains("01")) return BigDecimal.valueOf(2.0);
        if (val.contains("06A")) return BigDecimal.valueOf(1.0);
        return ZERO;
    }

    private BigDecimal getDiemKhuVuc(String khuVuc) {
        if (khuVuc == null) return ZERO;
        String val = khuVuc.trim().toUpperCase(Locale.ROOT).replace(" ", "");
        return switch (val) {
            case "KV1" -> BigDecimal.valueOf(0.75);
            case "KV2-NT", "KV2NT" -> BigDecimal.valueOf(0.5);
            case "KV2" -> BigDecimal.valueOf(0.25);
            default -> ZERO;
        };
    }

    private Map<String, BigDecimal> indexDiemByMon(List<DiemThiDgnlVsat> listDv) {
        Map<String, BigDecimal> map = new HashMap<>();
        for (DiemThiDgnlVsat dv : listDv) {
            if (dv.getMa_mon() == null || dv.getDiem() == null) continue;
            BigDecimal existing = map.get(dv.getMa_mon());
            if (existing == null || dv.getDiem().compareTo(existing) > 0) {
                map.put(dv.getMa_mon(), dv.getDiem());
            }
        }
        return map;
    }

    private Map<String, MapMon> loadMapMonByThpt() {
        Map<String, MapMon> map = new HashMap<>();
        for (MapMon mon : mapMonRepository.findAll()) {
            if (mon.getMon_thpt() != null) {
                map.put(mon.getMon_thpt().trim().toUpperCase(Locale.ROOT), mon);
            }
        }
        return map;
    }

    private Map<String, String> loadDgnlCodeByThpt(Map<String, MapMon> mapMonByThpt) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, MapMon> entry : mapMonByThpt.entrySet()) {
            String code = entry.getValue().getMon_dgnl();
            if (code != null && !code.trim().isEmpty()) {
                map.put(entry.getKey(), code.trim().toUpperCase(Locale.ROOT));
            }
        }
        return map;
    }

    private BigDecimal sumDgnlTotal(List<DiemThiDgnlVsat> listDv, Map<String, String> dgnlCodeByThpt) {
        if (listDv == null || listDv.isEmpty()) return null;
        Map<String, Boolean> dgnlCodes = new HashMap<>();
        for (String code : dgnlCodeByThpt.values()) {
            dgnlCodes.put(code, Boolean.TRUE);
        }

        BigDecimal sum = ZERO;
        boolean hasAny = false;
        for (DiemThiDgnlVsat dv : listDv) {
            if (dv.getMa_mon() == null || dv.getDiem() == null) continue;
            String code = dv.getMa_mon().trim().toUpperCase(Locale.ROOT);
            if (dgnlCodes.containsKey(code)) {
                sum = sum.add(dv.getDiem());
                hasAny = true;
            }
        }
        return hasAny ? sum : null;
    }

    private boolean isPhuongThuc(String value, String target) {
        return normalize(value).equalsIgnoreCase(target);
    }

    private int toInt(Byte value) {
        return value == null ? 1 : value.intValue();
    }

    private String normalize(String value) {
        if (value == null) return "";
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String buildNvKey(NguyenVong nv) {
        String cccd = nv.getNn_cccd() == null ? "" : nv.getNn_cccd().trim();
        String manganh = nv.getNv_manganh() == null ? "" : nv.getNv_manganh().trim();
        String thuTu = nv.getNv_tt() == null ? "" : String.valueOf(nv.getNv_tt());
        return "NV " + cccd + "-" + manganh + "-" + thuTu;
    }
}
