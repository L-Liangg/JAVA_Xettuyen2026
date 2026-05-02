package com.xettuyen.service.imports;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.NguyenVong;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import static com.xettuyen.service.imports.ExcelImportService.getCellValue;

public class NguyenVongImportService {

    public ImportResult importFromExcel(File file, ImportProgressDialog dialog) {
        ImportResult result = new ImportResult();

        try (
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                Session session = HibernateUtil.getSessionFactory().openSession()
        ) {
            session.beginTransaction();

            Map<String, Integer> existingMap = new HashMap<>();
            session.createQuery("SELECT nv_keys, idnv FROM NguyenVong", Object[].class)
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

            if (!colIndex.containsKey("cccd") || !colIndex.containsKey("mã ngành") || !colIndex.containsKey("thứ tự nv")) {
                result.addError(0, "File thiếu cột khóa 'CCCD', 'Mã ngành' hoặc 'Thứ tự NV'");
                session.getTransaction().rollback();
                return result;
            }

            ArrayList<Pair<NguyenVong, Boolean>> validEntries = new ArrayList<>();
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
                        if (v != null && !v.isBlank()) {
                            isEmptyRow = false;
                            break;
                        }
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

                String thuTuNV = getCellValue(row.getCell(colIndex.get("thứ tự nv")));
                if (thuTuNV == null || thuTuNV.isBlank()) {
                    result.addError(i + 1, "Thiếu thứ tự NV");
                    continue;
                }

                String nvKeys = cccd + "_" + maNganh + "_" + thuTuNV;

                if (seenInFile.contains(nvKeys)) {
                    result.addError(i + 1, "Trùng khóa '" + nvKeys + "' trong file");
                    continue;
                }
                seenInFile.add(nvKeys);

                NguyenVong nv = new NguyenVong();
                boolean isNew;
                if (existingMap.containsKey(nvKeys)) {
                    nv.setIdnv(existingMap.get(nvKeys));
                    isNew = false;
                } else {
                    isNew = true;
                }
                nv.setNn_cccd(cccd);
                nv.setNv_manganh(maNganh);
                nv.setNv_tt(Integer.parseInt(thuTuNV));
                nv.setNv_keys(nvKeys);

                for (Map.Entry<String, String> entry : ExcelColumnMapping.NGUYEN_VONG.entrySet()) {
                    String excelCol = entry.getKey();
                    String fieldName = entry.getValue();
                    if (!colIndex.containsKey(excelCol)) continue;
                    String val = getCellValue(row.getCell(colIndex.get(excelCol)));
                    if (val == null) continue;
                    switch (fieldName) {
                        case "tt_phuongthuc" -> nv.setTt_phuongthuc(val);
                    }
                }

                validEntries.add(new Pair<>(nv, isNew));
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

                NguyenVong nv = validEntries.get(i).getFirst();
                boolean isNew = validEntries.get(i).getSecond();

                try {
                    if (isNew) session.persist(nv);
                    else session.merge(nv);

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