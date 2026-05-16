package com.xettuyen.service.imports;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.BangQuyDoi;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
=======
import java.util.*;
>>>>>>> 4e2abf2a3594ffbc505c1eb89b19f48c34e322f0

import static com.xettuyen.service.imports.ExcelImportService.getCellValue;

public class BangQuyDoiImportService {

    public ImportResult importFromExcel(File file, ImportProgressDialog dialog) {
        ImportResult result = new ImportResult();

        try (
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                Session session = HibernateUtil.getSessionFactory().openSession()
        ) {
            session.beginTransaction();

            Map<String, Integer> existingMap = new HashMap<>();
            session.createQuery("SELECT d_maquydoi, idqd FROM BangQuyDoi", Object[].class)
                    .list()
                    .forEach(row -> existingMap.put((String) row[0], (Integer) row[1]));

            Sheet sheet = workbook.getSheetAt(0);
<<<<<<< HEAD
            int totalRows = Math.max(1, sheet.getLastRowNum());

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                result.addError(0, "File Excel không có dòng tiêu đề");
                session.getTransaction().rollback();
                return result;
            }

=======
            int totalRows = sheet.getLastRowNum();

            Row headerRow = sheet.getRow(0);
>>>>>>> 4e2abf2a3594ffbc505c1eb89b19f48c34e322f0
            Map<String, Integer> colIndex = new HashMap<>();
            for (Cell cell : headerRow) {
                String val = getCellValue(cell);
                if (val != null) colIndex.put(val.toLowerCase().trim(), cell.getColumnIndex());
            }

            if (!colIndex.containsKey("mã quy đổi")) {
                result.addError(0, "File thiếu cột khóa 'Mã quy đổi'");
                session.getTransaction().rollback();
                return result;
            }

            ArrayList<Pair<BangQuyDoi, Boolean>> validEntries = new ArrayList<>();
            Set<String> seenInFile = new HashSet<>();

            for (int i = 1; i <= totalRows; i++) {
<<<<<<< HEAD
=======

>>>>>>> 4e2abf2a3594ffbc505c1eb89b19f48c34e322f0
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

                String maQuyDoi = getCellValue(row.getCell(colIndex.get("mã quy đổi")));
                if (maQuyDoi == null || maQuyDoi.isBlank()) {
                    boolean isEmptyRow = true;
                    for (Cell cell : row) {
                        String v = getCellValue(cell);
<<<<<<< HEAD
                        if (v != null && !v.isBlank()) {
                            isEmptyRow = false;
                            break;
                        }
=======
                        if (v != null && !v.isBlank()) { isEmptyRow = false; break; }
>>>>>>> 4e2abf2a3594ffbc505c1eb89b19f48c34e322f0
                    }
                    if (isEmptyRow) continue;
                    result.addError(i + 1, "Thiếu mã quy đổi");
                    continue;
                }

                if (seenInFile.contains(maQuyDoi)) {
                    result.addError(i + 1, "Trùng mã quy đổi '" + maQuyDoi + "' trong file");
                    continue;
                }
                seenInFile.add(maQuyDoi);

<<<<<<< HEAD
                BangQuyDoi bangQuyDoi = new BangQuyDoi();
                boolean isNew;
                bangQuyDoi.setD_maquydoi(maQuyDoi);
                if (existingMap.containsKey(maQuyDoi)) {
                    bangQuyDoi.setIdqd(existingMap.get(maQuyDoi));
=======
                BangQuyDoi bqd = new BangQuyDoi();
                boolean isNew;
                bqd.setD_maquydoi(maQuyDoi);
                if (existingMap.containsKey(maQuyDoi)) {
                    bqd.setIdqd(existingMap.get(maQuyDoi));
>>>>>>> 4e2abf2a3594ffbc505c1eb89b19f48c34e322f0
                    isNew = false;
                } else {
                    isNew = true;
                }

                for (Map.Entry<String, String> entry : ExcelColumnMapping.BANG_QUY_DOI.entrySet()) {
                    String excelCol = entry.getKey();
                    String fieldName = entry.getValue();
<<<<<<< HEAD
                    Integer index = colIndex.get(excelCol);
                    if (index == null) continue;

                    String val = getCellValue(row.getCell(index));
                    if (val == null || val.isBlank()) continue;

                    try {
                        switch (fieldName) {
                            case "d_phuongthuc" -> bangQuyDoi.setD_phuongthuc(val);
                            case "d_tohop" -> bangQuyDoi.setD_tohop(val);
                            case "d_mon" -> bangQuyDoi.setD_mon(val);
                            case "d_diema" -> bangQuyDoi.setD_diema(new BigDecimal(val));
                            case "d_diemb" -> bangQuyDoi.setD_diemb(new BigDecimal(val));
                            case "d_diemc" -> bangQuyDoi.setD_diemc(new BigDecimal(val));
                            case "d_diemd" -> bangQuyDoi.setD_diemd(new BigDecimal(val));
                            case "d_phanvi" -> bangQuyDoi.setD_phanvi(val);
                        }
                    } catch (NumberFormatException ex) {
                        result.addError(i + 1, "Giá trị số không hợp lệ ở cột '" + excelCol + "': " + val);
                    }
                }

                validEntries.add(new Pair<>(bangQuyDoi, isNew));
=======
                    if (!colIndex.containsKey(excelCol)) continue;

                    String val = getCellValue(row.getCell(colIndex.get(excelCol)));
                    if (val == null) continue;

                    switch (fieldName) {
                        case "d_phuongthuc" -> bqd.setD_phuongthuc(val);
                        case "d_tohop"      -> bqd.setD_tohop(val);
                        case "d_mon"        -> bqd.setD_mon(val);
                        case "d_diema"      -> bqd.setD_diema(new BigDecimal(val));
                        case "d_diemb"      -> bqd.setD_diemb(new BigDecimal(val));
                        case "d_diemc"      -> bqd.setD_diemc(new BigDecimal(val));
                        case "d_diemd"      -> bqd.setD_diemd(new BigDecimal(val));
                        case "d_phanvi"     -> bqd.setD_phanvi(val);
                    }
                }

                validEntries.add(new Pair<>(bqd, isNew));
>>>>>>> 4e2abf2a3594ffbc505c1eb89b19f48c34e322f0
            }

            if (result.hasErrors()) {
                session.getTransaction().rollback();
                return result;
            }

            int validEntriesCount = validEntries.size();

            for (int i = 0; i < validEntriesCount; i++) {
<<<<<<< HEAD
=======

>>>>>>> 4e2abf2a3594ffbc505c1eb89b19f48c34e322f0
                if (dialog.isCancelled()) {
                    session.getTransaction().rollback();
                    result = new ImportResult();
                    result.addError(0, "Import bị hủy bởi người dùng");
                    return result;
                }

                int percent = (int) ((double) (i + 1) / validEntriesCount * 100);
                dialog.updateProgress(percent, "Đang lưu dữ liệu " + (i + 1) + " / " + validEntriesCount);

<<<<<<< HEAD
                BangQuyDoi bangQuyDoi = validEntries.get(i).getFirst();
                boolean isNew = validEntries.get(i).getSecond();

                try {
                    if (isNew) session.persist(bangQuyDoi);
                    else session.merge(bangQuyDoi);
=======
                BangQuyDoi bqd = validEntries.get(i).getFirst();
                boolean isNew = validEntries.get(i).getSecond();

                try {
                    if (isNew) session.persist(bqd);
                    else session.merge(bqd);
>>>>>>> 4e2abf2a3594ffbc505c1eb89b19f48c34e322f0

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
<<<<<<< HEAD
}
=======
}
>>>>>>> 4e2abf2a3594ffbc505c1eb89b19f48c34e322f0
