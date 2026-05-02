package com.xettuyen.service.imports;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.DiemCong;
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

    public ImportResult importFromExcel(File file, ImportProgressDialog dialog) {
        ImportResult result = new ImportResult();

        try (
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                Session session = HibernateUtil.getSessionFactory().openSession()
        ) {
            session.beginTransaction();

            Map<String, Integer> existingMap = new HashMap<>();
            session.createQuery("SELECT dc_keys, iddiemcong FROM DiemCong", Object[].class)
                    .list()
                    .forEach(row -> existingMap.put((String) row[0], (Integer) row[1]));

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();

            Row headerRow = sheet.getRow(0);
            Map<String, Integer> colIndex = new HashMap<>();
            for (Cell cell : headerRow) {
                String val = getCellValue(cell);
                if (val != null) colIndex.put(val.toLowerCase().trim(), cell.getColumnIndex());
            }

            if (!colIndex.containsKey("cccd") || !colIndex.containsKey("mã ngành") || !colIndex.containsKey("phương thức")) {
                result.addError(0, "File thiếu cột khóa 'CCCD', 'Mã ngành' hoặc 'Phương thức'");
                session.getTransaction().rollback();
                return result;
            }

            ArrayList<Pair<DiemCong, Boolean>> validEntries = new ArrayList<>();
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
                        case "diemCC"   -> dc.setDiemCC(new BigDecimal(val));
                        case "diemUtxt" -> dc.setDiemUtxt(new BigDecimal(val));
                        case "diemTong" -> dc.setDiemTong(new BigDecimal(val));
                        case "ghichu"   -> dc.setGhichu(val);
                    }
                }

                validEntries.add(new Pair<>(dc, isNew));
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