package com.xettuyen.ui.panel;

import com.xettuyen.entity.DiemThiDgnlVsat;
import com.xettuyen.service.impl.DiemThiDgnlVsatService;
import com.xettuyen.service.imports.DiemThiDgnlVsatImportService;
import com.xettuyen.service.imports.ImportResult;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import com.xettuyen.ui.util.PaginationPanel;
import com.xettuyen.ui.util.PlaceholderTextField;
import com.xettuyen.ui.util.TableHeaders;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import com.xettuyen.ui.util.RoundedButton;
import javax.swing.border.EmptyBorder;

public class DiemThiDgnlVsatPanel extends JPanel {

    private final DiemThiDgnlVsatService service = new DiemThiDgnlVsatService();
    private JTable table;
    private DefaultTableModel tableModel;
    private PaginationPanel paginationPanel;
    private int currentPage = 1;

    private JTextField txtCccdSearch;
    private JTextField txtMaMonSearch;

    public DiemThiDgnlVsatPanel() {
    setLayout(new BorderLayout(12, 12));
    setBorder(new EmptyBorder(20, 20, 20, 20));
    setBackground(new Color(250, 252, 255));

    initUI();
    loadData();
}

    private void initUI() {
    // ==================== TOP PANEL ====================
    JPanel topPanel = new JPanel(new BorderLayout(0, 12));
    topPanel.setOpaque(false);

    JLabel title = new JLabel("Quản Lý Điểm Thi ĐGNL - VSAT");
    title.setFont(new Font("Segoe UI", Font.BOLD, 24));
    title.setForeground(new Color(30, 30, 30));
    topPanel.add(title, BorderLayout.NORTH);

    // Toolbar
    JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
    toolBar.setBackground(Color.WHITE);
    toolBar.setBorder(new EmptyBorder(12, 0, 12, 0));

    // Search Panel
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    searchPanel.setOpaque(false);

    PlaceholderTextField cccdField = new PlaceholderTextField("CCCD", 14);
    PlaceholderTextField maMonField = new PlaceholderTextField("Mã môn thi", 14);
    
    txtCccdSearch = cccdField;
    txtMaMonSearch = maMonField;

    configureSearchField(txtCccdSearch);
    configureSearchField(txtMaMonSearch);

    RoundedButton btnSearch = createRoundedButton("Tìm kiếm", new Color(0, 122, 255));
    RoundedButton btnReset = createRoundedButton("Làm mới", new Color(108, 117, 125));

    btnSearch.addActionListener(e -> search());
    btnReset.addActionListener(e -> reset());
    txtCccdSearch.addActionListener(e -> search());
    txtMaMonSearch.addActionListener(e -> search());

    searchPanel.add(new JLabel("CCCD:"));
    searchPanel.add(txtCccdSearch);
    searchPanel.add(new JLabel("Mã môn:"));
    searchPanel.add(txtMaMonSearch);
    searchPanel.add(btnSearch);
    searchPanel.add(btnReset);

    // Action Buttons
    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    btnPanel.setOpaque(false);

    RoundedButton btnAdd = createRoundedButton("Thêm mới", new Color(34, 197, 151));
    RoundedButton btnEdit = createRoundedButton("Sửa", new Color(59, 130, 246));
    RoundedButton btnDelete = createRoundedButton("Xóa", new Color(239, 68, 68));
    RoundedButton btnImport = createRoundedButton("Import Excel", new Color(14, 165, 233));

    btnAdd.addActionListener(e -> addRecord());
    btnEdit.addActionListener(e -> updateRecord());
    btnDelete.addActionListener(e -> deleteRecord());
    btnImport.addActionListener(e -> importExcel());

    btnPanel.add(btnAdd);
    btnPanel.add(btnEdit);
    btnPanel.add(btnDelete);
    btnPanel.add(btnImport);

    toolBar.add(searchPanel);
    toolBar.add(Box.createHorizontalGlue());
    toolBar.add(btnPanel);

    topPanel.add(toolBar, BorderLayout.CENTER);
    add(topPanel, BorderLayout.NORTH);

    // ==================== TABLE ====================
    tableModel = new DefaultTableModel(TableHeaders.DIEM_THI_DGNL_VSAT, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    table = new JTable(tableModel);
    table.setRowHeight(36);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
    table.getTableHeader().setBackground(new Color(249, 250, 251));
    table.getTableHeader().setForeground(new Color(55, 65, 81));
    table.setBackground(Color.WHITE);
    table.setGridColor(new Color(241, 245, 249));
    table.setSelectionBackground(new Color(224, 242, 254));
    table.setSelectionForeground(Color.BLACK);
    table.getTableHeader().setReorderingAllowed(false);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
    add(scrollPane, BorderLayout.CENTER);

    // Pagination
    paginationPanel = new PaginationPanel();
    paginationPanel.setOnPageChange(() -> {
        currentPage = paginationPanel.getCurrentPage();
        loadData();
    });
    add(paginationPanel, BorderLayout.SOUTH);
}

    private void configureSearchField(JTextField txtMaMonSearch2) {
    txtMaMonSearch2.setPreferredSize(new Dimension(150, 38));
    txtMaMonSearch2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    txtMaMonSearch2.setBackground(Color.WHITE);
    txtMaMonSearch2.setForeground(new Color(30, 30, 30));
    txtMaMonSearch2.setCaretColor(new Color(30, 30, 30));
    ((PlaceholderTextField) txtMaMonSearch2).setPlaceholderColor(new Color(156, 163, 175));
}

private RoundedButton createRoundedButton(String text, Color bgColor) {
    RoundedButton btn = new RoundedButton(text);
    btn.setBackground(bgColor);
    btn.setForeground(Color.WHITE);
    btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    return btn;
}

    private void loadData() {
        String cccd = txtCccdSearch != null ? txtCccdSearch.getText().trim() : "";
        String maMon = txtMaMonSearch != null ? txtMaMonSearch.getText().trim() : "";
        int totalPages = service.getTotalPagesAnd(cccd, maMon);
        if (currentPage > totalPages) currentPage = totalPages;

        List<DiemThiDgnlVsat> list = service.searchAnd(cccd, maMon, currentPage);
        paginationPanel.update(currentPage, totalPages);

        tableModel.setRowCount(0);
        for (DiemThiDgnlVsat dt : list) {
            tableModel.addRow(new Object[]{
                    dt.getCccd(), dt.getDot_thi(), dt.getMa_dot_thi(),
                    dt.getNgay_thi(), dt.getNam(), dt.getMa_mon(),
                    dt.getTen_mon(), dt.getDiem(), dt.getThang_diem(),
                    dt.getMa_dvtctdl(), dt.getTen_dvtctdl()
            });
        }
    }

    private void importExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter("Excel files", "xlsx"));

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);

        ImportProgressDialog progressDialog = new ImportProgressDialog(parent);
        ImportResult result = progressDialog.startImport(
                () -> new DiemThiDgnlVsatImportService().importFromExcel(file, progressDialog)
        );

        if (result.hasErrors()) {
            JTextArea textArea = new JTextArea(String.join("\n", result.getErrors()));
            textArea.setEditable(false);
            textArea.setFont(new Font("Arial", Font.PLAIN, 12));
            JScrollPane scroll = new JScrollPane(textArea);
            scroll.setPreferredSize(new Dimension(400, 200));
            JOptionPane.showMessageDialog(this, scroll,
                    "Chi tiết lỗi", JOptionPane.WARNING_MESSAGE);
        }

        loadData();
    }

    private void search() {
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private void reset() {
        txtCccdSearch.setText("");
        txtMaMonSearch.setText("");
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private String getSelectedDvKeys() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng trong bảng.",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        // cccd=0, dot_thi=1, ma_dot_thi=2, ngay_thi=3, nam=4, ma_mon=5
        String cccd = Objects.toString(tableModel.getValueAt(modelRow, 0), "").trim();
        String dotThi = Objects.toString(tableModel.getValueAt(modelRow, 1), "").trim();
        String maDotThi = Objects.toString(tableModel.getValueAt(modelRow, 2), "").trim();
        String maMon = Objects.toString(tableModel.getValueAt(modelRow, 5), "").trim();
        return cccd + "_" + maMon + "_" + maDotThi + "_" + dotThi;
    }

    private void addRecord() {
        DiemThiDgnlVsat created = showForm(null);
        if (created == null) return;
        service.save(created);
        JOptionPane.showMessageDialog(this, "Đã thêm thành công.",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
        reset();
    }

    private void updateRecord() {
        String dvKeys = getSelectedDvKeys();
        if (dvKeys == null) return;
        DiemThiDgnlVsat existing = service.findByDvKeys(dvKeys);
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi.",
                    "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DiemThiDgnlVsat updated = showForm(existing);
        if (updated == null) return;
        service.update(updated);
        JOptionPane.showMessageDialog(this, "Đã cập nhật thành công.",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
        loadData();
    }

    private void deleteRecord() {
        String dvKeys = getSelectedDvKeys();
        if (dvKeys == null) return;
        DiemThiDgnlVsat existing = service.findByDvKeys(dvKeys);
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi.",
                    "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa bản ghi " + dvKeys + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        service.delete(existing);
        JOptionPane.showMessageDialog(this, "Đã xóa thành công.",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
        loadData();
    }

    private DiemThiDgnlVsat showForm(DiemThiDgnlVsat existing) {
        boolean isEdit = existing != null;

        JTextField txtCccd = new JTextField(20);
        JTextField txtMaMon = new JTextField(20);
        JTextField txtTenMon = new JTextField(20);
        JTextField txtDiem = new JTextField(10);
        JTextField txtThangDiem = new JTextField(10);
        JTextField txtNam = new JTextField(10);
        JTextField txtDotThi = new JTextField(20);
        JTextField txtMaDotThi = new JTextField(20);
        JTextField txtNgayThi = new JTextField(20);
        JTextField txtMaDv = new JTextField(20);
        JTextField txtTenDv = new JTextField(20);

        if (isEdit) {
            txtCccd.setText(existing.getCccd());
            txtCccd.setEditable(false);
            txtMaMon.setText(Objects.toString(existing.getMa_mon(), ""));
            txtMaMon.setEditable(false);
            txtTenMon.setText(Objects.toString(existing.getTen_mon(), ""));
            txtDiem.setText(existing.getDiem() != null ? existing.getDiem().toPlainString() : "");
            txtThangDiem.setText(Objects.toString(existing.getThang_diem(), ""));
            txtNam.setText(existing.getNam() != null ? String.valueOf(existing.getNam()) : "");
            txtDotThi.setText(Objects.toString(existing.getDot_thi(), ""));
            txtMaDotThi.setText(Objects.toString(existing.getMa_dot_thi(), ""));
            txtMaDotThi.setEditable(false);
            txtNgayThi.setText(Objects.toString(existing.getNgay_thi(), ""));
            txtMaDv.setText(Objects.toString(existing.getMa_dvtctdl(), ""));
            txtTenDv.setText(Objects.toString(existing.getTen_dvtctdl(), ""));
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("CCCD:")); form.add(txtCccd);
        form.add(new JLabel("Mã môn thi:")); form.add(txtMaMon);
        form.add(new JLabel("Tên môn thi:")); form.add(txtTenMon);
        form.add(new JLabel("Điểm:")); form.add(txtDiem);
        form.add(new JLabel("Thang điểm:")); form.add(txtThangDiem);
        form.add(new JLabel("Năm thi:")); form.add(txtNam);
        form.add(new JLabel("Đợt thi:")); form.add(txtDotThi);
        form.add(new JLabel("Mã đợt thi:")); form.add(txtMaDotThi);
        form.add(new JLabel("Ngày thi:")); form.add(txtNgayThi);
        form.add(new JLabel("Mã đơn vị:")); form.add(txtMaDv);
        form.add(new JLabel("Tên đơn vị:")); form.add(txtTenDv);

        int option = JOptionPane.showConfirmDialog(this, form,
                isEdit ? "Sửa điểm thi ĐGNL/VSAT" : "Thêm điểm thi ĐGNL/VSAT",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return null;

        String cccd = txtCccd.getText().trim();
        String maMon = txtMaMon.getText().trim();
        String maDotThi = txtMaDotThi.getText().trim();
        String dotThi = txtDotThi.getText().trim();

        if (cccd.isBlank() || maMon.isBlank() || maDotThi.isBlank() || dotThi.isBlank()) {
            JOptionPane.showMessageDialog(this, "CCCD, Mã môn thi, Mã đợt thi và Đợt thi không được để trống.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        DiemThiDgnlVsat dt = isEdit ? existing : new DiemThiDgnlVsat();
        dt.setCccd(cccd);
        dt.setMa_mon(maMon);
        dt.setMa_dot_thi(maDotThi);
        dt.setDot_thi(dotThi);
        dt.setDv_keys(cccd + "_" + maMon + "_" + maDotThi + "_" + dotThi);
        dt.setTen_mon(blankToNull(txtTenMon.getText()));
        dt.setThang_diem(blankToNull(txtThangDiem.getText()));
        dt.setNgay_thi(blankToNull(txtNgayThi.getText()));
        dt.setMa_dvtctdl(blankToNull(txtMaDv.getText()));
        dt.setTen_dvtctdl(blankToNull(txtTenDv.getText()));

        try {
            String diemStr = blankToNull(txtDiem.getText());
            dt.setDiem(diemStr != null ? new BigDecimal(diemStr) : null);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Điểm không hợp lệ.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        try {
            String namStr = blankToNull(txtNam.getText());
            dt.setNam(namStr != null ? Integer.parseInt(namStr) : null);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Năm thi không hợp lệ.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return dt;
    }

    private static String blankToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }
}