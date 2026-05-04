package com.xettuyen.service.imports;

import com.xettuyen.config.HibernateUtil;
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
import java.util.Locale;

import static com.xettuyen.service.imports.ExcelImportService.getCellValue;

public class NganhToHopImportService {

    private static String normCode(String s) {
        return s == null ? "" : s.trim().toUpperCase(Locale.ROOT);
    }

    public ImportResult importFromExcel(File file, ImportProgressDialog dialog) {
        ImportResult result = new ImportResult();

        try (
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                Session session = HibernateUtil.getSessionFactory().openSession()
        ) {
            session.beginTransaction();

            Map<String, Integer> existingMap = new HashMap<>();
            session.createQuery("SELECT tb_keys, id FROM NganhToHop", Object[].class)
                    .list()
                    .forEach(row -> existingMap.put((String) row[0], (Integer) row[1]));

                // Preload parent keys to avoid FK constraint failures during persist/merge
                Map<String, String> nganhCanonicalByNorm = new HashMap<>();
                session.createQuery("SELECT manganh FROM Nganh", String.class)
                    .list()
                    .forEach(code -> {
                    if (code != null) nganhCanonicalByNorm.put(normCode(code), code.trim());
                    });

                Map<String, String> toHopCanonicalByNorm = new HashMap<>();
                session.createQuery("SELECT matohop FROM ToHopMon", String.class)
                    .list()
                    .forEach(code -> {
                    if (code != null) toHopCanonicalByNorm.put(normCode(code), code.trim());
                    });

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getLastRowNum();

            Row headerRow = sheet.getRow(0);

            Map<String, Integer> colIndex = new HashMap<>();
            for (Cell cell : headerRow) {
                String val = getCellValue(cell);
                if (val != null) colIndex.put(val.toLowerCase().trim(), cell.getColumnIndex());
            }

            if (!colIndex.containsKey("mã ngành") || !colIndex.containsKey("mã tổ hợp")) {
                result.addError(0, "File thiếu cột khóa 'Mã ngành' hoặc 'Mã tổ hợp'");
                session.getTransaction().rollback();
                return result;
            }

            ArrayList<Pair<NganhToHop, Boolean>> validEntries = new ArrayList<>();
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

                String maNganh = getCellValue(row.getCell(colIndex.get("mã ngành")));
                if (maNganh == null || maNganh.isBlank()) {
                    boolean isEmptyRow = true;
                    for (Cell cell : row) {
                        String v = getCellValue(cell);
                        if (v != null && !v.isBlank()) {
                            isEmptyRow = false;
                            break;
                        }
                    }
                    if (isEmptyRow) continue;
                    result.addError(i + 1, "Thiếu mã ngành");
                    continue;
                }

                String maNganhCanonical = nganhCanonicalByNorm.get(normCode(maNganh));
                if (maNganhCanonical == null || maNganhCanonical.isBlank()) {
                    result.addError(i + 1,
                            "Mã ngành '" + maNganh.trim() + "' không tồn tại trong hệ thống. " +
                                    "Hãy import/nhập Ngành trước rồi import Ngành–Tổ hợp.");
                    continue;
                }
                maNganh = maNganhCanonical;

                String maToHop = getCellValue(row.getCell(colIndex.get("mã tổ hợp")));
                if (maToHop == null || maToHop.isBlank()) {
                    result.addError(i + 1, "Thiếu mã tổ hợp");
                    continue;
                }

                String maToHopCanonical = toHopCanonicalByNorm.get(normCode(maToHop));
                if (maToHopCanonical == null || maToHopCanonical.isBlank()) {
                    result.addError(i + 1,
                            "Mã tổ hợp '" + maToHop.trim() + "' không tồn tại trong hệ thống. " +
                                    "Hãy import/nhập Tổ hợp môn trước rồi import Ngành–Tổ hợp.");
                    continue;
                }
                maToHop = maToHopCanonical;

                String tbKeys = maNganh + "_" + maToHop;

                if (seenInFile.contains(tbKeys)) {
                    result.addError(i + 1, "Trùng khóa '" + tbKeys + "' trong file");
                    continue;
                }
                seenInFile.add(tbKeys);

                NganhToHop nganhToHop = new NganhToHop();
                boolean isNew;
                if (existingMap.containsKey(tbKeys)) {
                    nganhToHop.setId(existingMap.get(tbKeys));
                    isNew = false;
                } else {
                    isNew = true;
                }

                nganhToHop.setManganh(maNganh);
                nganhToHop.setMatohop(maToHop);
                nganhToHop.setTb_keys(tbKeys);

                for (Map.Entry<String, String> entry : ExcelColumnMapping.NGANH_TOHOP.entrySet()) {
                    String excelCol = entry.getKey();
                    String fieldName = entry.getValue();
                    if (!colIndex.containsKey(excelCol)) continue;

                    String val = getCellValue(row.getCell(colIndex.get(excelCol)));
                    if (val == null) continue;

                    switch (fieldName) {
                        case "th_mon1" -> nganhToHop.setTh_mon1(val);
                        case "hsmon1" -> nganhToHop.setHsmon1(Byte.parseByte(val));
                        case "th_mon2" -> nganhToHop.setTh_mon2(val);
                        case "hsmon2" -> nganhToHop.setHsmon2(Byte.parseByte(val));
                        case "th_mon3" -> nganhToHop.setTh_mon3(val);
                        case "hsmon3" -> nganhToHop.setHsmon3(Byte.parseByte(val));
                        case "N1" -> nganhToHop.setN1(val.equals("1"));
                        case "TO" -> nganhToHop.setTO(val.equals("1"));
                        case "LI" -> nganhToHop.setLI(val.equals("1"));
                        case "HO" -> nganhToHop.setHO(val.equals("1"));
                        case "SI" -> nganhToHop.setSI(val.equals("1"));
                        case "VA" -> nganhToHop.setVA(val.equals("1"));
                        case "SU" -> nganhToHop.setSU(val.equals("1"));
                        case "DI" -> nganhToHop.setDI(val.equals("1"));
                        case "TI" -> nganhToHop.setTI(val.equals("1"));
                        case "KHAC" -> nganhToHop.setKHAC(val.equals("1"));
                        case "KTPL" -> nganhToHop.setKTPL(val.equals("1"));
                        case "dolech" -> nganhToHop.setDolech(new BigDecimal(val));
                    }
                }

                validEntries.add(new Pair<>(nganhToHop, isNew));
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

                NganhToHop nganhToHop = validEntries.get(i).getFirst();
                boolean isNew = validEntries.get(i).getSecond();

                try {
                    if (isNew) session.persist(nganhToHop);
                    else session.merge(nganhToHop);

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