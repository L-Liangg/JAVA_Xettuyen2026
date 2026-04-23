package com.xettuyen.service.imports;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.Nganh;
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

public class NganhImportService {

    public ImportResult importFromExcel(File file, ImportProgressDialog dialog) {
        ImportResult result = new ImportResult();

        try (
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                Session session = HibernateUtil.getSessionFactory().openSession()
        ) {
            session.beginTransaction();

            Map<String, Integer> existingMap = new HashMap<>();
            session.createQuery("SELECT manganh, idnganh FROM Nganh", Object[].class)
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

            if (!colIndex.containsKey("mã ngành")) {
                result.addError(0, "File thiếu cột khóa 'Mã ngành'");
                session.getTransaction().rollback();
                return result;
            }

            ArrayList<Pair<Nganh, Boolean>> validEntries = new ArrayList<>();

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

                String maNganh = getCellValue(row.getCell(colIndex.get("mã ngành")));
                if (maNganh == null || maNganh.isBlank()) {
                    boolean isEmptyRow = true;
                    for (Cell cell : row) {
                        String v = getCellValue(cell);
                        if (v != null && !v.isBlank()) { isEmptyRow = false; break; }
                    }
                    if (isEmptyRow) continue;
                    result.addError(i + 1, "Thiếu mã ngành");
                    continue;
                }

                Nganh nganh = new Nganh();
                boolean isNew;
                nganh.setManganh(maNganh);
                if (existingMap.containsKey(maNganh)) {
                    nganh.setIdnganh(existingMap.get(maNganh));
                    isNew = false;
                } else {
                    isNew = true;
                }

                for (Map.Entry<String, String> entry : ExcelColumnMapping.NGANH.entrySet()) {
                    String excelCol = entry.getKey();
                    String fieldName = entry.getValue();
                    if (!colIndex.containsKey(excelCol)) continue;

                    String val = getCellValue(row.getCell(colIndex.get(excelCol)));
                    if (val == null) continue;

                    switch (fieldName) {
                        case "tennganh"     -> nganh.setTennganh(val);
                        case "n_tohopgoc"   -> nganh.setN_tohopgoc(val);
                        case "n_chitieu"    -> nganh.setN_chitieu(Integer.parseInt(val));
                        case "n_diemsan"    -> nganh.setN_diemsan(new BigDecimal(val));
                        case "n_tuyenthang" -> nganh.setN_tuyenthang(val);
                        case "n_dgnl"       -> nganh.setN_dgnl(val);
                        case "n_thpt"       -> nganh.setN_thpt(val);
                        case "n_vsat"       -> nganh.setN_vsat(val);
                    }
                }

                validEntries.add(new Pair<>(nganh, isNew));
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

                Nganh nganh = validEntries.get(i).getFirst();
                boolean isNew = validEntries.get(i).getSecond();

                try {
                    if (isNew) session.persist(nganh);
                    else session.merge(nganh);

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