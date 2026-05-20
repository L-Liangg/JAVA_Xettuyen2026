package com.xettuyen.ui.panel;

import com.xettuyen.entity.Nganh;
import com.xettuyen.service.impl.NganhService;
import com.xettuyen.service.imports.ImportResult;
import com.xettuyen.service.imports.NganhImportService;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import com.xettuyen.ui.util.PaginationPanel;
import com.xettuyen.ui.util.PlaceholderTextField;
import com.xettuyen.ui.util.TableHeaders;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.List;

import com.xettuyen.ui.util.RoundedButton;
import javax.swing.border.EmptyBorder;

public class NganhPanel extends JPanel {

    private final NganhService service = new NganhService();
    private JTable table;
    private DefaultTableModel tableModel;
    private PaginationPanel paginationPanel;
    private int currentPage = 1;
    private JTextField txtManganhSearch;
    private JTextField txtTennganhSearch;

    public NganhPanel() {
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

    JLabel title = new JLabel("Quản Lý Ngành Tuyển Sinh");
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

    PlaceholderTextField manganhField = new PlaceholderTextField("Mã ngành", 12);
    PlaceholderTextField tennganhField = new PlaceholderTextField("Tên ngành", 18);
    
    txtManganhSearch = manganhField;
    txtTennganhSearch = tennganhField;

    configureSearchField(txtManganhSearch);
    configureSearchField(txtTennganhSearch);

    RoundedButton btnSearch = createRoundedButton("Tìm kiếm", new Color(0, 122, 255));
    RoundedButton btnReset = createRoundedButton("Làm mới", new Color(108, 117, 125));

    btnSearch.addActionListener(e -> search());
    btnReset.addActionListener(e -> reset());
    txtManganhSearch.addActionListener(e -> search());
    txtTennganhSearch.addActionListener(e -> search());

    searchPanel.add(new JLabel("Mã ngành:"));
    searchPanel.add(txtManganhSearch);
    searchPanel.add(new JLabel("Tên ngành:"));
    searchPanel.add(txtTennganhSearch);
    searchPanel.add(btnSearch);
    searchPanel.add(btnReset);

    // Action Buttons
    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    btnPanel.setOpaque(false);

    RoundedButton btnAdd = createRoundedButton("Thêm mới", new Color(34, 197, 151));
    RoundedButton btnEdit = createRoundedButton("Sửa", new Color(59, 130, 246));
    RoundedButton btnDelete = createRoundedButton("Xóa", new Color(239, 68, 68));
    RoundedButton btnImport = createRoundedButton("Import Excel", new Color(14, 165, 233));

    btnAdd.addActionListener(e -> addNganh());
    btnEdit.addActionListener(e -> updateNganh());
    btnDelete.addActionListener(e -> deleteNganh());
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
    tableModel = new DefaultTableModel(TableHeaders.NGANH, 0) {
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

private void configureSearchField(JTextField txtTennganhSearch2) {
    txtTennganhSearch2.setPreferredSize(new Dimension(150, 38));
    txtTennganhSearch2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    txtTennganhSearch2.setBackground(Color.WHITE);
    txtTennganhSearch2.setForeground(new Color(30, 30, 30));
    txtTennganhSearch2.setCaretColor(new Color(30, 30, 30));
    ((PlaceholderTextField) txtTennganhSearch2).setPlaceholderColor(new Color(156, 163, 175));
}

private RoundedButton createRoundedButton(String text, Color bgColor) {
    RoundedButton btn = new RoundedButton(text);
    btn.setBackground(bgColor);
    btn.setForeground(Color.WHITE);
    btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    return btn;
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
                () -> new NganhImportService().importFromExcel(file, progressDialog)
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

    private void loadData() {
        service.updateSlNguyenVong();
        String manganh = txtManganhSearch != null ? txtManganhSearch.getText().trim() : "";
        String tennganh = txtTennganhSearch != null ? txtTennganhSearch.getText().trim() : "";
        int totalPages = service.getTotalPagesAnd(manganh, tennganh);
        if (currentPage > totalPages) currentPage = totalPages;

        List<Nganh> list = service.searchAnd(manganh, tennganh, currentPage);

        paginationPanel.update(currentPage, totalPages);

        tableModel.setRowCount(0);
        for (Nganh n : list) {
            tableModel.addRow(new Object[]{
                    n.getManganh(), n.getTennganh(), n.getN_tohopgoc(),
                    n.getN_chitieu(), n.getN_diemsan(), n.getN_diemtrungtuyen(),
                    n.getN_tuyenthang(), n.getN_dgnl(), n.getN_thpt(),
                    n.getN_vsat(), n.getSl_xtt(), n.getSl_dgnl(),
                    n.getSl_vsat(), n.getSl_thpt()
            });
        }
    }

    private void search() {
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private void reset() {
        txtManganhSearch.setText("");
        txtTennganhSearch.setText("");
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private void addNganh() {
        try {
            Nganh newNganh = showNganhForm(null);
            if (newNganh == null) return;

            if (newNganh.getManganh() == null || newNganh.getManganh().isBlank()) {
                JOptionPane.showMessageDialog(this, "Mã ngành không được để trống.",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (service.findByManganh(newNganh.getManganh()) != null) {
                JOptionPane.showMessageDialog(this, "Mã ngành đã tồn tại.",
                        "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            service.save(newNganh);
            JOptionPane.showMessageDialog(this, "Đã thêm ngành thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Reset để tránh đang lọc/trang khác
            txtManganhSearch.setText("");
            txtTennganhSearch.setText("");
            currentPage = 1;
            paginationPanel.reset();
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi thêm ngành:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateNganh() {
        try {
            String selectedMa = getSelectedManganh();
            if (selectedMa == null) return;

            Nganh existing = service.findByManganh(selectedMa);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy ngành theo mã: " + selectedMa,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Nganh updated = showNganhForm(existing);
            if (updated == null) return;

            service.update(updated);
            JOptionPane.showMessageDialog(this, "Đã cập nhật ngành thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi cập nhật ngành:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteNganh() {
        try {
            String selectedMa = getSelectedManganh();
            if (selectedMa == null) return;

            Nganh existing = service.findByManganh(selectedMa);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy ngành theo mã: " + selectedMa,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xóa ngành " + selectedMa + "?\nNếu ngành đang được dùng ở bảng khác, hệ thống có thể không cho xóa.",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            service.delete(existing);
            JOptionPane.showMessageDialog(this, "Đã xóa ngành thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi xóa ngành:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSelectedManganh() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 ngành trong bảng.",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        Object value = tableModel.getValueAt(modelRow, 0);
        String manganh = Objects.toString(value, "").trim();
        if (manganh.isBlank()) {
            JOptionPane.showMessageDialog(this, "Dòng đã chọn không có mã ngành hợp lệ.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return manganh;
    }

    private Nganh showNganhForm(Nganh existing) {
        boolean isEdit = existing != null;

        JTextField txtMa = new JTextField(20);
        JTextField txtTen = new JTextField(20);
        JTextField txtToHopGoc = new JTextField(20);
        JTextField txtChiTieu = new JTextField(20);
        JTextField txtDiemSan = new JTextField(20);
        JTextField txtDiemTrungTuyen = new JTextField(20);
        JTextField txtTuyenThang = new JTextField(20);
        JTextField txtDgnl = new JTextField(20);
        JTextField txtThpt = new JTextField(20);
        JTextField txtVsat = new JTextField(20);
        JTextField txtSlXtt = new JTextField(20);
        JTextField txtSlDgnl = new JTextField(20);
        JTextField txtSlVsat = new JTextField(20);
        JTextField txtSlThpt = new JTextField(20);

        txtSlXtt.setEditable(false);
        txtSlXtt.setBackground(Color.LIGHT_GRAY);
        txtSlDgnl.setEditable(false);
        txtSlDgnl.setBackground(Color.LIGHT_GRAY);
        txtSlVsat.setEditable(false);
        txtSlVsat.setBackground(Color.LIGHT_GRAY);
        txtSlThpt.setEditable(false);
        txtSlThpt.setBackground(Color.LIGHT_GRAY);

        if (isEdit) {
            txtMa.setText(existing.getManganh());
            txtTen.setText(existing.getTennganh());
            txtToHopGoc.setText(existing.getN_tohopgoc());
            txtChiTieu.setText(existing.getN_chitieu() == null ? "" : String.valueOf(existing.getN_chitieu()));
            txtDiemSan.setText(existing.getN_diemsan() == null ? "" : existing.getN_diemsan().toPlainString());
            txtDiemTrungTuyen.setText(existing.getN_diemtrungtuyen() == null ? "" : existing.getN_diemtrungtuyen().toPlainString());
            txtTuyenThang.setText(existing.getN_tuyenthang());
            txtDgnl.setText(existing.getN_dgnl());
            txtThpt.setText(existing.getN_thpt());
            txtVsat.setText(existing.getN_vsat());
            txtSlXtt.setText(existing.getSl_xtt() == null ? "" : String.valueOf(existing.getSl_xtt()));
            txtSlDgnl.setText(existing.getSl_dgnl() == null ? "" : String.valueOf(existing.getSl_dgnl()));
            txtSlVsat.setText(existing.getSl_vsat() == null ? "" : String.valueOf(existing.getSl_vsat()));

            txtSlThpt.setText(existing.getSl_thpt() == null ? "" : String.valueOf(existing.getSl_thpt()));

            txtMa.setEditable(false);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Mã ngành:")); form.add(txtMa);
        form.add(new JLabel("Tên ngành:")); form.add(txtTen);
        form.add(new JLabel("Tổ hợp gốc:")); form.add(txtToHopGoc);
        form.add(new JLabel("Chỉ tiêu:")); form.add(txtChiTieu);
        form.add(new JLabel("Điểm sàn:")); form.add(txtDiemSan);
        form.add(new JLabel("Điểm trúng tuyển:")); form.add(txtDiemTrungTuyen);
        form.add(new JLabel("Tuyển thẳng (0/1):")); form.add(txtTuyenThang);
        form.add(new JLabel("ĐGNL (0/1):")); form.add(txtDgnl);
        form.add(new JLabel("THPT (0/1):")); form.add(txtThpt);
        form.add(new JLabel("V-SAT (0/1):")); form.add(txtVsat);
        form.add(new JLabel("SL xét tuyển thẳng:")); form.add(txtSlXtt);
        form.add(new JLabel("SL ĐGNL:")); form.add(txtSlDgnl);
        form.add(new JLabel("SL V-SAT:")); form.add(txtSlVsat);
        form.add(new JLabel("SL THPT:")); form.add(txtSlThpt);

        int option = JOptionPane.showConfirmDialog(this,
                form,
                isEdit ? "Sửa ngành" : "Thêm ngành",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return null;

        String ma = txtMa.getText().trim();
        if (ma.isBlank()) {
            JOptionPane.showMessageDialog(this, "Mã ngành không được để trống.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Nganh n = isEdit ? existing : new Nganh();
        n.setManganh(ma);
        n.setTennganh(txtTen.getText().trim());
        n.setN_tohopgoc(blankToNull(txtToHopGoc.getText()));
        n.setN_chitieu(parseIntOrNull(txtChiTieu.getText()));
        n.setN_diemsan(parseDecimalOrNull(txtDiemSan.getText()));
        n.setN_diemtrungtuyen(parseDecimalOrNull(txtDiemTrungTuyen.getText()));
        n.setN_tuyenthang(blankToNull(txtTuyenThang.getText()));
        n.setN_dgnl(blankToNull(txtDgnl.getText()));
        n.setN_thpt(blankToNull(txtThpt.getText()));
        n.setN_vsat(blankToNull(txtVsat.getText()));
        n.setSl_xtt(parseIntOrNull(txtSlXtt.getText()));
        n.setSl_dgnl(parseIntOrNull(txtSlDgnl.getText()));
        n.setSl_vsat(parseIntOrNull(txtSlVsat.getText()));
        n.setSl_thpt(blankToNull(txtSlThpt.getText()));
        return n;
    }

    private static String blankToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private static Integer parseIntOrNull(String value) {
        String trimmed = blankToNull(value);
        if (trimmed == null) return null;
        return Integer.parseInt(trimmed);
    }

    private static BigDecimal parseDecimalOrNull(String value) {
        String trimmed = blankToNull(value);
        if (trimmed == null) return null;
        return new BigDecimal(trimmed);
    }
}