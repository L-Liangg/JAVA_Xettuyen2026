package com.xettuyen.service.imports;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.ThiSinh;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.*;

import static com.xettuyen.service.imports.ExcelImportService.getCellValue;

public class ThiSinhImportService {

    public ImportResult importFromExcel(File file, ImportProgressDialog dialog) {
        ImportResult result = new ImportResult();

        try (
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                Session session = HibernateUtil.getSessionFactory().openSession()
        ) {

            session.beginTransaction();

                Map<String, Integer> existingIdByCccd = new HashMap<>();
                Map<String, String> existingPasswordByCccd = new HashMap<>();
                session.createQuery("SELECT cccd, idthisinh, password FROM ThiSinh", Object[].class)
                    .list()
                    .forEach(row -> {
                    String cccd = (String) row[0];
                    existingIdByCccd.put(cccd, (Integer) row[1]);
                    existingPasswordByCccd.put(cccd, (String) row[2]);
                    });

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();

            Row headerRow = sheet.getRow(0);
            Map<String, Integer> colIndex = new HashMap<>();
            for (Cell cell : headerRow) {
                String val = getCellValue(cell);
                if (val != null) colIndex.put(val.toLowerCase().trim(), cell.getColumnIndex());
            }

            if (!colIndex.containsKey("cccd")) {
                result.addError(0, "File thiếu cột khóa 'CCCD'");
                session.getTransaction().rollback();
                return result;
            }

            ArrayList<Pair<ThiSinh, Boolean>> validEntries = new ArrayList<>();
            Set<String> seenInFile = new HashSet<>();

            for (int i = 1; i <= totalRows; i++) {

                if (dialog.isCancelled()) {
                    session.getTransaction().rollback();
                    result = new ImportResult();
                    result.addError(0, "Import bị hủy bởi người dùng");
                    return result;
                }

                int percent = (int) ((double) i / totalRows * 100);
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

                if (seenInFile.contains(cccd)) {
                    result.addError(i + 1, "Trùng CCCD '" + cccd + "' trong file");
                    continue;
                }
                seenInFile.add(cccd);

                ThiSinh ts = new ThiSinh();

                boolean isNew;
                ts.setCccd(cccd);
                if (existingIdByCccd.containsKey(cccd)) {
                    ts.setIdthisinh(existingIdByCccd.get(cccd));
                    isNew = false;
                } else {
                    isNew = true;
                }

                for (Map.Entry<String, String> entry : ExcelColumnMapping.THI_SINH.entrySet()) {
                    String excelCol = entry.getKey();
                    String fieldName = entry.getValue();
                    if (!colIndex.containsKey(excelCol)) continue;

                    String val = getCellValue(row.getCell(colIndex.get(excelCol)));
                    if (val == null) continue;

                    switch (fieldName) {
                        case "sobaodanh" -> ts.setSobaodanh(val);
                        case "ho" -> ts.setHo(val);
                        case "ten" -> ts.setTen(val);
                        case "ngay_sinh" -> ts.setNgay_sinh(val);
                        case "dien_thoai" -> ts.setDien_thoai(val);
                        case "password" -> ts.setPassword(val);
                        case "gioi_tinh" -> ts.setGioi_tinh(val);
                        case "email" -> ts.setEmail(val);
                        case "noi_sinh" -> ts.setNoi_sinh(val);
                        case "doi_tuong" -> ts.setDoi_tuong(val);
                        case "khu_vuc" -> ts.setKhu_vuc(val);
                    }
                }

                // Mặc định password = CCCD nếu thiếu/để trống trong file.
                // Nếu bản ghi đã tồn tại thì ưu tiên giữ password cũ (tránh merge ghi null).
                String importedPassword = ts.getPassword();
                if (importedPassword == null || importedPassword.isBlank()) {
                    String existingPassword = existingPasswordByCccd.get(cccd);
                    if (existingPassword != null && !existingPassword.isBlank()) {
                        ts.setPassword(existingPassword);
                    } else {
                        ts.setPassword(cccd);
                    }
                }

                ts.setUpdated_at(LocalDate.now());
                validEntries.add(new Pair<>(ts, isNew));
            }

            if (result.hasErrors()) {
                session.getTransaction().rollback();
                return result;
            }

            int validEntriesCount = validEntries.size();

            for (int i = 0; i < validEntriesCount; i++) {

                if (dialog.isCancelled()) {
                    session.getTransaction().rollback();
                    result = new ImportResult();
                    result.addError(0, "Import bị hủy bởi người dùng");
                    return result;
                }

                int percent = (int) ((double) (i + 1) / validEntriesCount * 100);
                dialog.updateProgress(percent, "Đang lưu dữ liệu " + (i + 1) + " / " + validEntriesCount);

                ThiSinh ts = validEntries.get(i).getFirst();
                boolean isNew = validEntries.get(i).getSecond();

                try {
                    if (isNew) {
                        session.persist(ts);
                    } else {
                        session.merge(ts);
                    }

                    if (i % 50 == 0 && i > 0) {
                        session.flush();
                        session.clear();
                    }

                } catch (Exception e) {
                    session.getTransaction().rollback();
                    result.addError(i + 1, "Hibernate error: " + e.getMessage());
                    return result;
                }
            }

            session.getTransaction().commit();

        } catch (Exception e) {
            result.addError(0, "Lỗi đọc file: " + e.getMessage());
        }

        return result;
    }
}