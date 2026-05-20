package com.xettuyen.service.imports;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.DiemCong;
import com.xettuyen.entity.NganhToHop;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.xettuyen.service.imports.ExcelImportService.getCellValue;

public class DiemCongImportService {

    private enum ImportMode {
        BASE,
        ENGLISH_CERT,
        UU_TIEN
    }

    // Batch size tối ưu
    private static final int BATCH_SIZE = 500;

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
                case ENGLISH_CERT -> importEnglishCertOptimized(sheet, colIndex, totalRows, session, dialog, result);
                case UU_TIEN -> importUuTienOptimized(sheet, colIndex, totalRows, session, dialog, result);
                default -> importBaseOptimized(sheet, colIndex, totalRows, session, dialog, result);
            }

            if (result.hasErrors()) {
                session.getTransaction().rollback();
                return result;
            }

            session.getTransaction().commit();

        } catch (Exception e) {
            result.addError(0, "Lỗi đọc file: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    private static ImportMode detectMode(Set<String> headers) {
        if (headers.contains("chứng chỉ ngoại ngữ")) return ImportMode.ENGLISH_CERT;
        if (headers.contains("điểm cộng cho môn đạt giải") || 
            headers.contains("điểm cộng cho thxt ko có môn đạt giải")) {
            return ImportMode.UU_TIEN;
        }
        return ImportMode.BASE;
    }

    // ==================== BASE IMPORT TỐI ƯU ====================
    private static void importBaseOptimized(
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

        // 1. Load tất cả existing records vào map (CHỈ 1 QUERY)
        Map<String, DiemCong> existingMap = new HashMap<>();
        session.createQuery("FROM DiemCong", DiemCong.class)
                .list()
                .forEach(dc -> existingMap.put(dc.getDc_keys(), dc));

        // 2. Đọc và validate dữ liệu từ file
        List<DiemCong> toInsert = new ArrayList<>();
        List<DiemCong> toUpdate = new ArrayList<>();
        Set<String> seenInFile = new HashSet<>();

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
            if (maNganh == null || maNganh.isBlank()) { 
                result.addError(i + 1, "Thiếu mã ngành"); 
                continue; 
            }

            String phuongThuc = getCellValue(row.getCell(colIndex.get("phương thức")));
            if (phuongThuc == null || phuongThuc.isBlank()) { 
                result.addError(i + 1, "Thiếu phương thức"); 
                continue; 
            }

            String dcKeys = cccd + "_" + maNganh + "_" + phuongThuc;

            if (seenInFile.contains(dcKeys)) {
                result.addError(i + 1, "Trùng khóa '" + dcKeys + "' trong file");
                continue;
            }
            seenInFile.add(dcKeys);

            DiemCong dc = existingMap.get(dcKeys);
            boolean isNew = (dc == null);
            if (isNew) {
                dc = new DiemCong();
                dc.setDc_keys(dcKeys);
                toInsert.add(dc);
            } else {
                toUpdate.add(dc);
            }

            dc.setTs_cccd(cccd);
            dc.setManganh(maNganh);
            dc.setPhuongthuc(phuongThuc);

            // Set các field khác
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
        }

        if (result.hasErrors()) return;

        // 3. Batch insert/update
        batchSaveOrUpdate(session, toInsert, toUpdate, dialog, result);
    }

    // ==================== ENGLISH CERT TỐI ƯU ====================
    private static void importEnglishCertOptimized(
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

        // 1. Đọc file: lấy điểm cộng lớn nhất cho mỗi CCCD
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

        if (maxDiemCc.isEmpty()) return;

        // 2. Lấy TẤT CẢ DiemCong cần cập nhật trong 1 query (QUAN TRỌNG)
        Set<String> cccdSet = maxDiemCc.keySet();
        List<DiemCong> allDiemCong = session.createQuery(
                        "FROM DiemCong d WHERE d.ts_cccd IN (:cccds)", DiemCong.class)
                .setParameterList("cccds", cccdSet)
                .list();

        // 3. Map CCCD -> DiemCong để dễ xử lý
        Map<String, List<DiemCong>> diemCongByCccd = allDiemCong.stream()
                .collect(Collectors.groupingBy(DiemCong::getTs_cccd));

        // 4. Cập nhật trong memory
        List<DiemCong> toUpdate = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : maxDiemCc.entrySet()) {
            String cccd = entry.getKey();
            BigDecimal diemCC = entry.getValue();
            
            List<DiemCong> list = diemCongByCccd.get(cccd);
            if (list != null) {
                for (DiemCong dc : list) {
                    dc.setDiemCC(diemCC);
                    dc.setDiemTong(computeDiemTong(diemCC, dc.getDiemUtxt()));
                    toUpdate.add(dc);
                }
            }
        }

        // 5. Batch update
        batchUpdateOnly(session, toUpdate, dialog, maxDiemCc.size());
    }

    // ==================== UU TIEN TỐI ƯU ====================
    private static void importUuTienOptimized(
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

        // 1. Đọc file: lấy điểm ưu tiên cho mỗi CCCD và môn
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

        // 2. Lấy mapping môn học theo ngành (1 query)
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

        // 3. Lấy TẤT CẢ DiemCong cần cập nhật trong 1 query
        Set<String> allCccd = new HashSet<>();
        allCccd.addAll(maxNoMon.keySet());
        allCccd.addAll(maxMonByCccd.keySet());
        
        if (allCccd.isEmpty()) return;

        List<DiemCong> allDiemCong = session.createQuery(
                        "FROM DiemCong d WHERE d.ts_cccd IN (:cccds)", DiemCong.class)
                .setParameterList("cccds", allCccd)
                .list();

        // 4. Cập nhật trong memory
        List<DiemCong> toUpdate = new ArrayList<>();
        for (DiemCong dc : allDiemCong) {
            String cccd = dc.getTs_cccd();
            Set<String> subjects = monByNganh.getOrDefault(dc.getManganh(), Collections.emptySet());
            
            BigDecimal best = null;
            Map<String, BigDecimal> monMap = maxMonByCccd.getOrDefault(cccd, Collections.emptyMap());
            
            for (String sub : subjects) {
                BigDecimal val = monMap.get(sub);
                if (val != null && (best == null || val.compareTo(best) > 0)) {
                    best = val;
                }
            }
            
            if (best == null) best = maxNoMon.get(cccd);
            if (best == null) continue;

            dc.setDiemUtxt(best);
            dc.setDiemTong(computeDiemTong(dc.getDiemCC(), best));
            toUpdate.add(dc);
        }

        // 5. Batch update
        batchUpdateOnly(session, toUpdate, dialog, allCccd.size());
    }

    // ==================== BATCH UTILITIES ====================
    
    private static void batchSaveOrUpdate(Session session, List<DiemCong> toInsert, List<DiemCong> toUpdate, 
                                          ImportProgressDialog dialog, ImportResult result) {
        int total = toInsert.size() + toUpdate.size();
        int processed = 0;
        
        // Insert
        for (int i = 0; i < toInsert.size(); i++) {
            if (dialog.isCancelled()) {
                result.addError(0, "Import bị hủy bởi người dùng");
                return;
            }
            session.persist(toInsert.get(i));
            processed++;
            
            if (processed % BATCH_SIZE == 0) {
                session.flush();
                session.clear();
                int percent = (int) ((double) processed / total * 100);
                dialog.updateProgress(percent, "Đang lưu " + processed + " / " + total);
            }
        }
        
        // Update
        for (int i = 0; i < toUpdate.size(); i++) {
            if (dialog.isCancelled()) {
                result.addError(0, "Import bị hủy bởi người dùng");
                return;
            }
            session.merge(toUpdate.get(i));
            processed++;
            
            if (processed % BATCH_SIZE == 0) {
                session.flush();
                session.clear();
                int percent = (int) ((double) processed / total * 100);
                dialog.updateProgress(percent, "Đang lưu " + processed + " / " + total);
            }
        }
        
        // Final flush
        session.flush();
        session.clear();
    }
    
    private static void batchUpdateOnly(Session session, List<DiemCong> toUpdate, 
                                        ImportProgressDialog dialog, int totalEstimate) {
        int total = toUpdate.size();
        for (int i = 0; i < total; i++) {
            if (dialog.isCancelled()) return;
            
            session.merge(toUpdate.get(i));
            
            if ((i + 1) % BATCH_SIZE == 0) {
                session.flush();
                session.clear();
                int percent = (int) ((double) (i + 1) / totalEstimate * 100);
                dialog.updateProgress(percent, "Đang cập nhật " + (i + 1) + " / " + total);
            }
        }
        
        session.flush();
        session.clear();
    }

    // ==================== HELPER METHODS ====================
    
    private static void addMon(Set<String> set, String mon) {
        String value = normalizeSubject(mon);
        if (value != null && !value.isBlank()) set.add(value);
    }

    private static String normalizeSubject(String mon) {
        if (mon == null) return null;
        return mon.trim().toUpperCase();
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