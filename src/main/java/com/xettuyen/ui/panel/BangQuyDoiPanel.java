package com.xettuyen.ui.panel;

import com.xettuyen.entity.BangQuyDoi;
import com.xettuyen.service.impl.BangQuyDoiService;
import com.xettuyen.service.imports.BangQuyDoiImportService;
import com.xettuyen.service.imports.ImportResult;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import com.xettuyen.ui.util.PaginationPanel;
import com.xettuyen.ui.util.PlaceholderTextField;
import com.xettuyen.ui.util.RoundedButton;
import com.xettuyen.ui.util.TableHeaders;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class BangQuyDoiPanel extends JPanel {

    private final BangQuyDoiService service = new BangQuyDoiService();
    private JTable table;
    private DefaultTableModel tableModel;
    private PaginationPanel paginationPanel;
    private int currentPage = 1;

    private PlaceholderTextField txtPhuongthucSearch;
    private PlaceholderTextField txtTohopSearch;

    public BangQuyDoiPanel() {
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

        JLabel title = new JLabel("Quản Lý Bảng Quy Đổi");
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

        txtPhuongthucSearch = new PlaceholderTextField("Phương thức", 13);
        txtTohopSearch = new PlaceholderTextField("Tổ hợp", 12);

        configureSearchField(txtPhuongthucSearch);
        configureSearchField(txtTohopSearch);

        RoundedButton btnSearch = createRoundedButton("Tìm kiếm", new Color(0, 122, 255));
        RoundedButton btnReset = createRoundedButton("Làm mới", new Color(108, 117, 125));

        btnSearch.addActionListener(e -> search());
        btnReset.addActionListener(e -> reset());
        txtPhuongthucSearch.addActionListener(e -> search());
        txtTohopSearch.addActionListener(e -> search());

        searchPanel.add(new JLabel("Phương thức:"));
        searchPanel.add(txtPhuongthucSearch);
        searchPanel.add(new JLabel("Tổ hợp:"));
        searchPanel.add(txtTohopSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReset);

        // Action Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        RoundedButton btnAdd = createRoundedButton("Thêm mới", new Color(34, 197, 151));
        RoundedButton btnEdit = createRoundedButton("Sửa", new Color(59, 130, 246));
        RoundedButton btnDelete = createRoundedButton("Xóa", new Color(239, 68, 68));
        RoundedButton btnImport = createRoundedButton("Import Excel", new Color(14, 165, 233));

        btnAdd.addActionListener(e -> addBangQuyDoi());
        btnEdit.addActionListener(e -> updateBangQuyDoi());
        btnDelete.addActionListener(e -> deleteBangQuyDoi());
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
        tableModel = new DefaultTableModel(TableHeaders.BANG_QUY_DOI, 0) {
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

        // ==================== PAGINATION ====================
        paginationPanel = new PaginationPanel();
        paginationPanel.setOnPageChange(() -> {
            currentPage = paginationPanel.getCurrentPage();
            loadData();
        });
        add(paginationPanel, BorderLayout.SOUTH);
    }

    private void configureSearchField(PlaceholderTextField field) {
        field.setPreferredSize(new Dimension(150, 38));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(new Color(30, 30, 30));
        field.setCaretColor(new Color(30, 30, 30));
        field.setPlaceholderColor(new Color(156, 163, 175));
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
            () -> new BangQuyDoiImportService().importFromExcel(file, progressDialog)
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
        String phuongthuc = txtPhuongthucSearch != null ? txtPhuongthucSearch.getText().trim() : "";
        String tohop = txtTohopSearch != null ? txtTohopSearch.getText().trim() : "";
        int totalPages = Math.max(1, service.getTotalPagesAnd(phuongthuc, tohop));
        if (currentPage > totalPages) currentPage = totalPages;

        List<BangQuyDoi> list = service.searchAnd(phuongthuc, tohop, currentPage);

        paginationPanel.update(currentPage, totalPages);

        tableModel.setRowCount(0);
        for (BangQuyDoi b : list) {
            tableModel.addRow(new Object[]{
                    b.getD_maquydoi(),
                    b.getD_phuongthuc(), b.getD_tohop(), b.getD_mon(),
                    b.getD_diema(), b.getD_diemb(), b.getD_diemc(),
                    b.getD_diemd(),  b.getD_phanvi()
            });
        }
    }

    private void search() {
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private void reset() {
        txtPhuongthucSearch.setText("");
        txtTohopSearch.setText("");
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private void addBangQuyDoi() {
        try {
            BangQuyDoi created = showForm(null);
            if (created == null) return;

            if (created.getD_maquydoi() == null || created.getD_maquydoi().isBlank()) {
                JOptionPane.showMessageDialog(this, "Mã quy đổi không được để trống.",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (service.findByMaquydoi(created.getD_maquydoi()) != null) {
                JOptionPane.showMessageDialog(this, "Mã quy đổi đã tồn tại.",
                        "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            service.save(created);
            JOptionPane.showMessageDialog(this, "Đã thêm bảng quy đổi thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reset();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi thêm bảng quy đổi:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBangQuyDoi() {
        try {
            String selectedMa = getSelectedMaquydoi();
            if (selectedMa == null) return;

            BangQuyDoi existing = service.findByMaquydoi(selectedMa);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi: " + selectedMa,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            BangQuyDoi updated = showForm(existing);
            if (updated == null) return;

            service.update(updated);
            JOptionPane.showMessageDialog(this, "Đã cập nhật bảng quy đổi thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi cập nhật bảng quy đổi:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBangQuyDoi() {
        try {
            String selectedMa = getSelectedMaquydoi();
            if (selectedMa == null) return;

            BangQuyDoi existing = service.findByMaquydoi(selectedMa);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi: " + selectedMa,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xóa bản ghi quy đổi: " + selectedMa + "?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            service.delete(existing);
            JOptionPane.showMessageDialog(this, "Đã xóa bảng quy đổi thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi xóa bảng quy đổi:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSelectedMaquydoi() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng trong bảng.",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        // Column 0 = d_maquydoi
        Object value = tableModel.getValueAt(modelRow, 0);
        String maquydoi = Objects.toString(value, "").trim();
        if (maquydoi.isBlank()) {
            JOptionPane.showMessageDialog(this, "Dòng đã chọn không có mã quy đổi hợp lệ.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return maquydoi;
    }

    private BangQuyDoi showForm(BangQuyDoi existing) {
        boolean isEdit = existing != null;

        JTextField txtPhuongThuc = new JTextField(20);
        JTextField txtToHop      = new JTextField(20);
        JTextField txtMon        = new JTextField(20);
        JTextField txtDiemA      = new JTextField(10);
        JTextField txtDiemB      = new JTextField(10);
        JTextField txtDiemC      = new JTextField(10);
        JTextField txtDiemD      = new JTextField(10);
        JTextField txtMaQuyDoi   = new JTextField(20);
        JTextField txtPhanVi     = new JTextField(20);

        if (isEdit) {
            txtPhuongThuc.setText(Objects.toString(existing.getD_phuongthuc(), ""));
            txtToHop.setText(Objects.toString(existing.getD_tohop(), ""));
            txtMon.setText(Objects.toString(existing.getD_mon(), ""));
            txtDiemA.setText(toText(existing.getD_diema()));
            txtDiemB.setText(toText(existing.getD_diemb()));
            txtDiemC.setText(toText(existing.getD_diemc()));
            txtDiemD.setText(toText(existing.getD_diemd()));
            txtMaQuyDoi.setText(Objects.toString(existing.getD_maquydoi(), ""));
            txtMaQuyDoi.setEditable(false);
            txtPhanVi.setText(Objects.toString(existing.getD_phanvi(), ""));
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Phương thức:"));
        form.add(txtPhuongThuc);
        form.add(new JLabel("Tổ hợp:"));
        form.add(txtToHop);
        form.add(new JLabel("Môn:"));
        form.add(txtMon);
        form.add(new JLabel("Điểm A:"));
        form.add(txtDiemA);
        form.add(new JLabel("Điểm B:"));
        form.add(txtDiemB);
        form.add(new JLabel("Điểm C:"));
        form.add(txtDiemC);
        form.add(new JLabel("Điểm D:"));
        form.add(txtDiemD);
        form.add(new JLabel("Mã quy đổi:"));
        form.add(txtMaQuyDoi);
        form.add(new JLabel("Phân vị:"));
        form.add(txtPhanVi);

        int option = JOptionPane.showConfirmDialog(this,
                form,
                isEdit ? "Sửa bảng quy đổi" : "Thêm bảng quy đổi",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return null;

        String maQuyDoi = txtMaQuyDoi.getText().trim();
        if (maQuyDoi.isBlank()) {
            JOptionPane.showMessageDialog(this, "Mã quy đổi không được để trống.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        BangQuyDoi b = isEdit ? existing : new BangQuyDoi();
        b.setD_phuongthuc(blankToNull(txtPhuongThuc.getText()));
        b.setD_tohop(blankToNull(txtToHop.getText()));
        b.setD_mon(blankToNull(txtMon.getText()));
        b.setD_diema(parseDecimalOrNull(txtDiemA.getText(), "Điểm A"));
        b.setD_diemb(parseDecimalOrNull(txtDiemB.getText(), "Điểm B"));
        b.setD_diemc(parseDecimalOrNull(txtDiemC.getText(), "Điểm C"));
        b.setD_diemd(parseDecimalOrNull(txtDiemD.getText(), "Điểm D"));
        b.setD_maquydoi(maQuyDoi);
        b.setD_phanvi(blankToNull(txtPhanVi.getText()));

        return b;
    }

    private static String blankToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private static BigDecimal parseDecimalOrNull(String value, String label) {
        String trimmed = blankToNull(value);
        if (trimmed == null) return null;
        try {
            return new BigDecimal(trimmed);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Giá trị không hợp lệ ở '" + label + "': " + trimmed,
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private static String toText(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }
}