package com.xettuyen.service.imports;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.DiemCong;
import com.xettuyen.entity.NganhToHop;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.*;

import static com.xettuyen.service.imports.ExcelImportService.getCellValue;

public class DiemCongImportService {

    private enum ImportMode {
        BASE,
        ENGLISH_CERT,
        UU_TIEN
    }

    public ImportResult importFromExcel(File file, ImportProgressDialog dialog) {
        ImportResult result = new ImportResult();

        try (
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                Session session = HibernateUtil.getSessionFactory().openSession()
        ) {
            session.beginTransaction();
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();

            Row headerRow = sheet.getRow(0);
            Map<String, Integer> colIndex = new HashMap<>();
            for (Cell cell : headerRow) {
                String val = getCellValue(cell);
                if (val != null) colIndex.put(val.toLowerCase().trim(), cell.getColumnIndex());
            }

            ImportMode mode = detectMode(colIndex.keySet());
            switch (mode) {
                case ENGLISH_CERT -> importEnglishCert(sheet, colIndex, totalRows, session, dialog, result);
                case UU_TIEN -> importUuTien(sheet, colIndex, totalRows, session, dialog, result);
                default -> importBase(sheet, colIndex, totalRows, session, dialog, result);
            }

            if (result.hasErrors()) {
                session.getTransaction().rollback();
                return result;
            }

            session.getTransaction().commit();

        } catch (Exception e) {
            result.addError(0, "Lỗi đọc file: " + e.getMessage());
        }

        return result;
    }

    private static ImportMode detectMode(Set<String> headers) {
        if (headers.contains("chứng chỉ ngoại ngữ")) return ImportMode.ENGLISH_CERT;
        if (headers.contains("điểm cộng cho môn đạt giải") || headers.contains("điểm cộng cho thxt ko có môn đạt giải")) {
            return ImportMode.UU_TIEN;
        }
        return ImportMode.BASE;
    }

    private static void importBase(
            Sheet sheet,
            Map<String, Integer> colIndex,
            int totalRows,
            Session session,
            ImportProgressDialog dialog,
            ImportResult result
    ) {
        if (!colIndex.containsKey("cccd") || !colIndex.containsKey("mã ngành") || !colIndex.containsKey("phương thức")) {
            result.addError(0, "File thiếu cột khóa 'CCCD', 'Mã ngành' hoặc 'Phương thức'");
            return;
        }

        Map<String, Integer> existingMap = new HashMap<>();
        session.createQuery("SELECT dc_keys, iddiemcong FROM DiemCong", Object[].class)
                .list()
                .forEach(row -> existingMap.put((String) row[0], (Integer) row[1]));

        ArrayList<Pair<DiemCong, Boolean>> validEntries = new ArrayList<>();
        Set<String> seenInFile = new HashSet<>();

        for (int i = 1; i <= totalRows; i++) {

            if (dialog.isCancelled()) {
                result.addError(0, "Import bị hủy bởi người dùng");
                return;
            }

            int percent = (int) ((double) i / Math.max(1, totalRows) * 100);
            dialog.updateProgress(percent, "Đang duyệt dữ liệu " + i + " / " + totalRows);

            Row row = sheet.getRow(i);
            if (row == null) continue;

            String cccd = getCellValue(row.getCell(colIndex.get("cccd")));
            if (cccd == null || cccd.isBlank()) {
                boolean isEmptyRow = true;
                for (Cell cell : row) {
                    String v = getCellValue(cell);
                    if (v != null && !v.isBlank()) { isEmptyRow = false; break; }
                }
                if (isEmptyRow) continue;
                result.addError(i + 1, "Thiếu CCCD");
                continue;
            }

            String maNganh = getCellValue(row.getCell(colIndex.get("mã ngành")));
            if (maNganh == null || maNganh.isBlank()) { result.addError(i + 1, "Thiếu mã ngành"); continue; }

            String phuongThuc = getCellValue(row.getCell(colIndex.get("phương thức")));
            if (phuongThuc == null || phuongThuc.isBlank()) { result.addError(i + 1, "Thiếu phương thức"); continue; }

            String dcKeys = cccd + "_" + maNganh + "_" + phuongThuc;

            if (seenInFile.contains(dcKeys)) {
                result.addError(i + 1, "Trùng khóa '" + dcKeys + "' trong file");
                continue;
            }
            seenInFile.add(dcKeys);

            DiemCong dc = new DiemCong();
            boolean isNew;
            if (existingMap.containsKey(dcKeys)) {
                dc.setIddiemcong(existingMap.get(dcKeys));
                isNew = false;
            } else {
                isNew = true;
            }

            dc.setTs_cccd(cccd);
            dc.setManganh(maNganh);
            dc.setPhuongthuc(phuongThuc);
            dc.setDc_keys(dcKeys);

            for (Map.Entry<String, String> entry : ExcelColumnMapping.DIEM_CONG.entrySet()) {
                String excelCol = entry.getKey();
                String fieldName = entry.getValue();
                if (!colIndex.containsKey(excelCol)) continue;
                String val = getCellValue(row.getCell(colIndex.get(excelCol)));
                if (val == null) continue;
                switch (fieldName) {
                    case "matohop"  -> dc.setMatohop(val);
                    case "diemCC"   -> dc.setDiemCC(parseDecimal(val));
                    case "diemUtxt" -> dc.setDiemUtxt(parseDecimal(val));
                    case "diemTong" -> dc.setDiemTong(parseDecimal(val));
                    case "ghichu"   -> dc.setGhichu(val);
                }
            }

            dc.setDiemTong(computeDiemTong(dc.getDiemCC(), dc.getDiemUtxt()));

            validEntries.add(new Pair<>(dc, isNew));
        }

        if (result.hasErrors()) return;

        int validEntriesCount = validEntries.size();

        for (int i = 0; i < validEntriesCount; i++) {

            if (dialog.isCancelled()) {
                result.addError(0, "Import bị hủy bởi người dùng");
                return;
            }

            int percent = (int) ((double) (i + 1) / Math.max(1, validEntriesCount) * 100);
            dialog.updateProgress(percent, "Đang lưu dữ liệu " + (i + 1) + " / " + validEntriesCount);

            DiemCong dc = validEntries.get(i).getFirst();
            boolean isNew = validEntries.get(i).getSecond();

            try {
                if (isNew) session.persist(dc);
                else session.merge(dc);

                if (i % 50 == 0 && i > 0) {
                    session.flush();
                    session.clear();
                }
            } catch (Exception e) {
                result.addError(i + 1, "Hibernate error: " + e.getMessage());
                return;
            }
        }
    }

    private static void importEnglishCert(
            Sheet sheet,
            Map<String, Integer> colIndex,
            int totalRows,
            Session session,
            ImportProgressDialog dialog,
            ImportResult result
    ) {
        if (!colIndex.containsKey("cccd") || !colIndex.containsKey("điểm cộng")) {
            result.addError(0, "File thiếu cột 'CCCD' hoặc 'Điểm cộng'");
            return;
        }

        Map<String, BigDecimal> maxDiemCc = new HashMap<>();

        for (int i = 1; i <= totalRows; i++) {
            if (dialog.isCancelled()) {
                result.addError(0, "Import bị hủy bởi người dùng");
                return;
            }

            int percent = (int) ((double) i / Math.max(1, totalRows) * 100);
            dialog.updateProgress(percent, "Đang đọc dữ liệu " + i + " / " + totalRows);

            Row row = sheet.getRow(i);
            if (row == null) continue;

            String cccd = getCellValue(row.getCell(colIndex.get("cccd")));
            if (cccd == null || cccd.isBlank()) continue;

            String diemText = getCellValue(row.getCell(colIndex.get("điểm cộng")));
            BigDecimal diem = parseDecimal(diemText);
            if (diem == null) continue;

            maxDiemCc.merge(cccd, diem, (oldVal, newVal) -> newVal.compareTo(oldVal) > 0 ? newVal : oldVal);
        }

        int processed = 0;
        for (Map.Entry<String, BigDecimal> entry : maxDiemCc.entrySet()) {
            if (dialog.isCancelled()) {
                result.addError(0, "Import bị hủy bởi người dùng");
                return;
            }

            String cccd = entry.getKey();
            BigDecimal diemCC = entry.getValue();

            List<DiemCong> rows = session.createQuery(
                            "FROM DiemCong d WHERE d.ts_cccd = :cccd",
                            DiemCong.class)
                    .setParameter("cccd", cccd)
                    .list();

            for (DiemCong dc : rows) {
                dc.setDiemCC(diemCC);
                dc.setDiemTong(computeDiemTong(diemCC, dc.getDiemUtxt()));
                session.merge(dc);
            }

            processed++;
            int percent = (int) ((double) processed / Math.max(1, maxDiemCc.size()) * 100);
            dialog.updateProgress(percent, "Đang cập nhật điểm CC " + processed + " / " + maxDiemCc.size());

            if (processed % 50 == 0) {
                session.flush();
                session.clear();
            }
        }
    }

    private static void importUuTien(
            Sheet sheet,
            Map<String, Integer> colIndex,
            int totalRows,
            Session session,
            ImportProgressDialog dialog,
            ImportResult result
    ) {
        if (!colIndex.containsKey("cccd")
                || !colIndex.containsKey("mã môn")
                || !colIndex.containsKey("điểm cộng cho môn đạt giải")
                || !colIndex.containsKey("điểm cộng cho thxt ko có môn đạt giải")) {
            result.addError(0, "File thiếu cột bắt buộc cho ưu tiên xét tuyển");
            return;
        }

        Map<String, BigDecimal> maxNoMon = new HashMap<>();
        Map<String, Map<String, BigDecimal>> maxMonByCccd = new HashMap<>();

        for (int i = 1; i <= totalRows; i++) {
            if (dialog.isCancelled()) {
                result.addError(0, "Import bị hủy bởi người dùng");
                return;
            }

            int percent = (int) ((double) i / Math.max(1, totalRows) * 100);
            dialog.updateProgress(percent, "Đang đọc dữ liệu " + i + " / " + totalRows);

            Row row = sheet.getRow(i);
            if (row == null) continue;

            String cccd = getCellValue(row.getCell(colIndex.get("cccd")));
            if (cccd == null || cccd.isBlank()) continue;

            String mon = normalizeSubject(getCellValue(row.getCell(colIndex.get("mã môn"))));

            BigDecimal diemMon = parseDecimal(getCellValue(row.getCell(colIndex.get("điểm cộng cho môn đạt giải"))));
            BigDecimal diemNoMon = parseDecimal(getCellValue(row.getCell(colIndex.get("điểm cộng cho thxt ko có môn đạt giải"))));

            if (diemNoMon != null) {
                maxNoMon.merge(cccd, diemNoMon, (oldVal, newVal) -> newVal.compareTo(oldVal) > 0 ? newVal : oldVal);
            }

            if (mon != null && !mon.isBlank() && diemMon != null) {
                maxMonByCccd.computeIfAbsent(cccd, k -> new HashMap<>())
                        .merge(mon, diemMon, (oldVal, newVal) -> newVal.compareTo(oldVal) > 0 ? newVal : oldVal);
            }
        }

        Map<String, Set<String>> monByNganh = new HashMap<>();
        session.createQuery("FROM NganhToHop", NganhToHop.class)
                .list()
                .forEach(row -> {
                    String manganh = row.getManganh();
                    if (manganh == null) return;
                    monByNganh.computeIfAbsent(manganh, k -> new HashSet<>());
                    addMon(monByNganh.get(manganh), row.getTh_mon1());
                    addMon(monByNganh.get(manganh), row.getTh_mon2());
                    addMon(monByNganh.get(manganh), row.getTh_mon3());
                });

        int processed = 0;
        for (String cccd : unionKeys(maxNoMon.keySet(), maxMonByCccd.keySet())) {
            if (dialog.isCancelled()) {
                result.addError(0, "Import bị hủy bởi người dùng");
                return;
            }

            List<DiemCong> rows = session.createQuery(
                            "FROM DiemCong d WHERE d.ts_cccd = :cccd",
                            DiemCong.class)
                    .setParameter("cccd", cccd)
                    .list();

            Map<String, BigDecimal> monMap = maxMonByCccd.getOrDefault(cccd, Collections.emptyMap());
            BigDecimal fallback = maxNoMon.get(cccd);

            for (DiemCong dc : rows) {
                Set<String> subjects = monByNganh.getOrDefault(dc.getManganh(), Collections.emptySet());
                BigDecimal best = null;
                for (String sub : subjects) {
                    BigDecimal val = monMap.get(sub);
                    if (val != null && (best == null || val.compareTo(best) > 0)) best = val;
                }
                if (best == null) best = fallback;
                if (best == null) continue;

                dc.setDiemUtxt(best);
                dc.setDiemTong(computeDiemTong(dc.getDiemCC(), best));
                session.merge(dc);
            }

            processed++;
            int percent = (int) ((double) processed / Math.max(1, maxNoMon.size() + maxMonByCccd.size()) * 100);
            dialog.updateProgress(percent, "Đang cập nhật điểm ưu tiên " + processed);

            if (processed % 50 == 0) {
                session.flush();
                session.clear();
            }
        }
    }

    private static void addMon(Set<String> set, String mon) {
        String value = normalizeSubject(mon);
        if (value != null && !value.isBlank()) set.add(value);
    }

    private static String normalizeSubject(String mon) {
        if (mon == null) return null;
        return mon.trim().toUpperCase();
    }

    private static Set<String> unionKeys(Set<String> a, Set<String> b) {
        Set<String> out = new HashSet<>(a);
        out.addAll(b);
        return out;
    }

    private static BigDecimal parseDecimal(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        if (trimmed.isBlank()) return null;
        try {
            return new BigDecimal(trimmed.replace(",", "."));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static BigDecimal computeDiemTong(BigDecimal diemCC, BigDecimal diemUtxt) {
        BigDecimal sum = BigDecimal.ZERO;
        if (diemCC != null) sum = sum.add(diemCC);
        if (diemUtxt != null) sum = sum.add(diemUtxt);
        BigDecimal max = BigDecimal.valueOf(3);
        return sum.compareTo(max) > 0 ? max : sum;
    }
}