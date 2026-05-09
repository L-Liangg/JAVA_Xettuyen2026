package com.xettuyen.service.imports;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.ToHopMon;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import static com.xettuyen.service.imports.ExcelImportService.getCellValue;

public class ToHopMonImportService {

    public ImportResult importFromExcel(File file, ImportProgressDialog dialog) {
        ImportResult result = new ImportResult();

        try (
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                Session session = HibernateUtil.getSessionFactory().openSession()
        ) {
            session.beginTransaction();

            Map<String, Integer> existingMap = new HashMap<>();
            session.createQuery("SELECT matohop, idtohop FROM ToHopMon", Object[].class)
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

            if (!colIndex.containsKey("mã tổ hợp")) {
                result.addError(0, "File thiếu cột khóa 'Mã tổ hợp'");
                session.getTransaction().rollback();
                return result;
            }

            if (!colIndex.containsKey("môn 1") || !colIndex.containsKey("môn 2") || !colIndex.containsKey("môn 3")) {
                result.addError(0, "File thiếu cột môn 1 / môn 2 / môn 3");
                session.getTransaction().rollback();
                return result;
            }

            ArrayList<Pair<ToHopMon, Boolean>> validEntries = new ArrayList<>();
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

                String maToHop = getCellValue(row.getCell(colIndex.get("mã tổ hợp")));
                if (maToHop == null || maToHop.isBlank()) {
                    boolean isEmptyRow = true;
                    for (Cell cell : row) {
                        String v = getCellValue(cell);
                        if (v != null && !v.isBlank()) {
                            isEmptyRow = false;
                            break;
                        }
                    }
                    if (isEmptyRow) continue;
                    result.addError(i + 1, "Thiếu mã tổ hợp");
                    continue;
                }

                if (seenInFile.contains(maToHop)) {
                    result.addError(i + 1, "Trùng mã tổ hợp '" + maToHop + "' trong file");
                    continue;
                }
                seenInFile.add(maToHop);

                String mon1 = getCellValue(row.getCell(colIndex.get("môn 1")));
                String mon2 = getCellValue(row.getCell(colIndex.get("môn 2")));
                String mon3 = getCellValue(row.getCell(colIndex.get("môn 3")));

                if (mon1 == null || mon1.isBlank()) {
                    result.addError(i + 1, "Thiếu môn 1");
                    continue;
                }
                if (mon2 == null || mon2.isBlank()) {
                    result.addError(i + 1, "Thiếu môn 2");
                    continue;
                }
                if (mon3 == null || mon3.isBlank()) {
                    result.addError(i + 1, "Thiếu môn 3");
                    continue;
                }

                ToHopMon toHopMon = new ToHopMon();
                boolean isNew;
                toHopMon.setMatohop(maToHop);
                if (existingMap.containsKey(maToHop)) {
                    toHopMon.setIdtohop(existingMap.get(maToHop));
                    isNew = false;
                } else {
                    isNew = true;
                }

                for (Map.Entry<String, String> entry : ExcelColumnMapping.TOHOP_MON.entrySet()) {
                    String excelCol = entry.getKey();
                    String fieldName = entry.getValue();
                    if (!colIndex.containsKey(excelCol)) continue;

                    String val = getCellValue(row.getCell(colIndex.get(excelCol)));
                    if (val == null) continue;

                    switch (fieldName) {
                        case "mon1" -> toHopMon.setMon1(val);
                        case "mon2" -> toHopMon.setMon2(val);
                        case "mon3" -> toHopMon.setMon3(val);
                        case "tentohop" -> toHopMon.setTentohop(val);
                    }
                }

                validEntries.add(new Pair<>(toHopMon, isNew));
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

                ToHopMon toHopMon = validEntries.get(i).getFirst();
                boolean isNew = validEntries.get(i).getSecond();

                try {
                    if (isNew) session.persist(toHopMon);
                    else session.merge(toHopMon);

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