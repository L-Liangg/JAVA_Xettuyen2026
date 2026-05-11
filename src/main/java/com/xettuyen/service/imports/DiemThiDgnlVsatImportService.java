package com.xettuyen.service.imports;

import com.xettuyen.config.HibernateUtil;
import com.xettuyen.entity.DiemThiDgnlVsat;
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
import java.util.*;

import static com.xettuyen.service.imports.ExcelImportService.getCellValue;

public class DiemThiDgnlVsatImportService {

    public ImportResult importFromExcel(File file, ImportProgressDialog dialog) {
        ImportResult result = new ImportResult();

        try (
                FileInputStream fis = new FileInputStream(file);
                Workbook workbook = new XSSFWorkbook(fis);
                Session session = HibernateUtil.getSessionFactory().openSession()
        ) {
            session.beginTransaction();

            Map<String, Integer> existingMap = new HashMap<>();
            session.createQuery("SELECT dv_keys, id FROM DiemThiDgnlVsat", Object[].class)
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

            if (!colIndex.containsKey("cccd")
                    || !colIndex.containsKey("mã môn thi")
                    || !colIndex.containsKey("mã đợt thi")
                    || !colIndex.containsKey("đợt thi")) {
                result.addError(0, "File thiếu cột khóa 'CCCD', 'Mã môn thi','Đợt thi' hoặc 'Mã đợt thi'");
                session.getTransaction().rollback();
                return result;
            }

            ArrayList<Pair<DiemThiDgnlVsat, Boolean>> validEntries = new ArrayList<>();
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

                String maMon = getCellValue(row.getCell(colIndex.get("mã môn thi")));
                if (maMon == null || maMon.isBlank()) {
                    result.addError(i + 1, "Thiếu mã môn thi");
                    continue;
                }

                String maDotThi = getCellValue(row.getCell(colIndex.get("mã đợt thi")));
                if (maDotThi == null || maDotThi.isBlank()) {
                    result.addError(i + 1, "Thiếu mã đợt thi");
                    continue;
                }

                String dotThi = getCellValue(row.getCell(colIndex.get("đợt thi")));
                if (dotThi == null || dotThi.isBlank()) { result.addError(i + 1, "Thiếu đợt thi"); continue; }

                String dvKeys = cccd + "_" + maMon + "_" + maDotThi + "_" + dotThi;

                if (seenInFile.contains(dvKeys)) {
                    result.addError(i + 1, "Trùng khóa '" + dvKeys + "' trong file");
                    continue;
                }
                seenInFile.add(dvKeys);

                DiemThiDgnlVsat dt = new DiemThiDgnlVsat();
                boolean isNew;
                dt.setCccd(cccd);
                dt.setMa_mon(maMon);
                dt.setMa_dot_thi(maDotThi);
                dt.setDv_keys(dvKeys);
                if (existingMap.containsKey(dvKeys)) {
                    dt.setId(existingMap.get(dvKeys));
                    isNew = false;
                } else {
                    isNew = true;
                }

                for (Map.Entry<String, String> entry : ExcelColumnMapping.DIEM_THI_DGNL_VSAT.entrySet()) {
                    String excelCol = entry.getKey();
                    String fieldName = entry.getValue();
                    if (!colIndex.containsKey(excelCol)) continue;

                    String val = getCellValue(row.getCell(colIndex.get(excelCol)));
                    if (val == null) continue;

                    switch (fieldName) {
                        case "dot_thi" -> dt.setDot_thi(val);
                        case "ngay_thi" -> dt.setNgay_thi(val);
                        case "nam" -> dt.setNam(Integer.parseInt(val));
                        case "ten_mon" -> dt.setTen_mon(val);
                        case "diem" -> dt.setDiem(new BigDecimal(val));
                        case "thang_diem" -> dt.setThang_diem(val);
                        case "ma_dvtctdl" -> dt.setMa_dvtctdl(val);
                        case "ten_dvtctdl" -> dt.setTen_dvtctdl(val);
                    }
                }

                validEntries.add(new Pair<>(dt, isNew));
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

                DiemThiDgnlVsat dt = validEntries.get(i).getFirst();
                boolean isNew = validEntries.get(i).getSecond();

                try {
                    if (isNew) session.persist(dt);
                    else session.merge(dt);

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
