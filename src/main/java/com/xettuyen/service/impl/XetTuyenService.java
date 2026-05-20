package com.xettuyen.service.impl;

import com.xettuyen.entity.Nganh;
import com.xettuyen.entity.NguyenVong;
import com.xettuyen.repository.NganhRepository;
import com.xettuyen.repository.NguyenVongRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class XetTuyenService {
    public interface ProgressListener {
        void onProgress(int percent, String status);
    }

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
        return runXetTuyenAll(null);
    }

    public Result runXetTuyenAll(ProgressListener listener) {
        updateProgress(listener, 0, "Đang chuẩn bị...");

        // Lay du lieu
        List<NguyenVong> allNguyenVong = nguyenVongRepository.findAll();
        List<Nganh> allNganh = nganhRepository.findAll();

        updateProgress(listener, 5, "Đang khởi tạo cấu trúc dữ liệu...");

        // map manganh -> Nganh để tra cứu nhanh
        Map<String, Nganh> nganhMap = allNganh.stream()
                .filter(n -> n.getManganh() != null && !n.getManganh().isBlank())
                .collect(Collectors.toMap(Nganh::getManganh, n -> n));

        // loc nguyen vong hop le: manganh ton tai, diem xet tuyen hop le, phuong thuc
        // hop le
        List<NguyenVong> nvHopLe = new ArrayList<>();
        for (NguyenVong nv : allNguyenVong) {
            nv.setNv_ketqua("0"); // Reset ban đầu

            String maNganh = nv.getNv_manganh();
            Nganh nganh = nganhMap.get(maNganh);

            if (nganh == null)
                continue;
            if (nv.getDiem_xettuyen() == null)
                continue;
            if (!isPhuongThucAllowed(nganh, nv.getTt_phuongthuc()))
                continue;

            BigDecimal diemSan = nganh.getN_diemsan();
            if (diemSan != null && nv.getDiem_xettuyen().compareTo(diemSan) < 0)
                continue;

            BigDecimal diemTrungTuyen = nganh.getN_diemtrungtuyen();
            if (diemTrungTuyen != null && nv.getDiem_xettuyen().compareTo(diemTrungTuyen) < 0)
                continue;

            nvHopLe.add(nv);
        }

        updateProgress(listener, 10, "Đang nhóm và sắp xếp theo điểm...");

        // nhom theo manganh de xet tuyen tung nganh, dong thoi sap xep theo diem xet
        // tuyen giam dan, neu bang diem thi uu tien NV nho hon
        Map<String, List<NguyenVong>> nvTheoNganh = new HashMap<>();
        for (NguyenVong nv : nvHopLe) {
            nvTheoNganh.computeIfAbsent(nv.getNv_manganh(), k -> new ArrayList<>()).add(nv);
        }

        // sap xep tung nganh theo diem xet tuyen giam dan, neu bang diem thi uu tien NV
        // nho hon
        for (List<NguyenVong> ds : nvTheoNganh.values()) {
            ds.sort((a, b) -> {
                int cmp = b.getDiem_xettuyen().compareTo(a.getDiem_xettuyen());
                if (cmp != 0)
                    return cmp;
                return Integer.compare(a.getNv_tt(), b.getNv_tt());
            });
        }

        updateProgress(listener, 20, "Bắt đầu lọc ảo...");

        // loc ao
        int soVongToiDa = 20;
        boolean changed = true;

        for (int vong = 1; vong <= soVongToiDa && changed; vong++) {
            System.out.println("bắt đầu vòng " + vong);
            changed = false;

            updateProgress(listener,20 + Math.min(vong * 5, 70),"Lọc ảo vòng " + vong);

            // reset
            for (NguyenVong nv : nvHopLe) {
                if (!"XND".equals(nv.getNv_ketqua())) {
                    nv.setNv_ketqua("0");
                }
            }

            // ngành lấy top
            for (Map.Entry<String, List<NguyenVong>> entry : nvTheoNganh.entrySet()) {

                List<NguyenVong> danhSach = entry.getValue();

                Nganh nganh = nganhMap.get(entry.getKey());

                int chiTieu = safeInt(nganh.getN_chitieu());

                int count = 0;

                for (NguyenVong nv : danhSach) {

                    if ("XND".equals(nv.getNv_ketqua()))
                        continue;

                    if (count < chiTieu) {
                        nv.setNv_ketqua("1");
                        count++;
                    } else {
                        break;
                    }
                }
            }

            // gom tạm đỗ
            Map<String, List<NguyenVong>> tamDoTheoThiSinh = new HashMap<>();

            for (NguyenVong nv : nvHopLe) {

                if ("1".equals(nv.getNv_ketqua())) {
                    tamDoTheoThiSinh.computeIfAbsent(nv.getNn_cccd(),k -> new ArrayList<>()).add(nv);
                }
            }

            // giữ NV cao nhất
            for (List<NguyenVong> dsTamDo : tamDoTheoThiSinh.values()) {

                if (dsTamDo.size() <= 1)
                    continue;

                dsTamDo.sort(Comparator.comparingInt(NguyenVong::getNv_tt));

                for (int i = 1; i < dsTamDo.size(); i++) {

                    NguyenVong nvBiHuy = dsTamDo.get(i);

                    if (!"XND".equals(nvBiHuy.getNv_ketqua())) {

                        nvBiHuy.setNv_ketqua("XND");

                        changed = true;
                    }
                }
            }

            System.out.println("kết thúc vòng " + vong);
            if(!changed) {
                System.out.println("đã ổn định, dừng vòng " + vong);
            }
        }

        // xac nhan ket qua - nhung nguyen vong tam dat "1" chinh thuc duoc chap nhan,
        // cac nguyen vong con lai (bao gom ca nhung nguyen vong bi huy "XND") deu la
        // truot "0"
        updateProgress(listener, 85, "Tổng hợp kết quả...");

        int accepted = 0;
        Set<String> tatCaThiSinh = new HashSet<>();

        for (NguyenVong nv : allNguyenVong) {
            String cccd = nv.getNn_cccd();
            if (cccd != null && !cccd.isBlank()) {
                tatCaThiSinh.add(cccd);
            }

            if ("XND".equals(nv.getNv_ketqua())) {
                nv.setNv_ketqua("0");
            } else if ("1".equals(nv.getNv_ketqua())) {
                accepted++; // Dem so luong thi sinh duoc chap nhan
            }
        }

        updateProgress(listener, 95, "Đang cập nhật dữ liệu...");
        nguyenVongRepository.updateAll(allNguyenVong);

        updateProgress(listener, 100, "Hoàn tất xét tuyển.");

        int total = tatCaThiSinh.size();
        return new Result(total, accepted, total - accepted);
    }

    private static int safeInt(Integer value) {
        return value == null ? 0 : Math.max(0, value);
    }

    private static void updateProgress(ProgressListener listener, int percent, String status) {
        if (listener == null)
            return;
        int safePercent = Math.max(0, Math.min(100, percent));
        listener.onProgress(safePercent, status);
    }

    private static boolean isPhuongThucAllowed(Nganh nganh, String phuongThuc) {
        if (nganh == null)
            return false;
        String normalized = normalize(phuongThuc);
        return switch (normalized) {
            case "THPT" -> isEnabled(nganh.getN_thpt());
            case "DGNL" -> isEnabled(nganh.getN_dgnl());
            case "VSAT" -> isEnabled(nganh.getN_vsat());
            default -> false;
        };
    }

    private static boolean isEnabled(String flag) {
        return "1".equals(normalize(flag));
    }

    private static String normalize(String value) {
        if (value == null)
            return "";
        return value.trim().toUpperCase();
    }
}