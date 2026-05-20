package com.xettuyen.ui.panel;

import com.xettuyen.entity.ToHopMon;
import com.xettuyen.service.impl.ToHopMonService;
import com.xettuyen.service.imports.ImportResult;
import com.xettuyen.service.imports.ToHopMonImportService;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import com.xettuyen.ui.util.PaginationPanel;
import com.xettuyen.ui.util.PlaceholderTextField;
import com.xettuyen.ui.util.TableHeaders;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Objects;
import java.util.List;

import com.xettuyen.ui.util.RoundedButton;
import javax.swing.border.EmptyBorder;

public class ToHopMonPanel extends JPanel {

    private final ToHopMonService service = new ToHopMonService();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtMatohopSearch;
    private PaginationPanel paginationPanel;
    private int currentPage = 1;

    public ToHopMonPanel() {
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

    JLabel title = new JLabel("Quản Lý Tổ Hợp Môn");
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

    PlaceholderTextField matohopField = new PlaceholderTextField("Mã tổ hợp", 18);
    txtMatohopSearch = matohopField;
    configureSearchField(txtMatohopSearch);

    RoundedButton btnSearch = createRoundedButton("Tìm kiếm", new Color(0, 122, 255));
    RoundedButton btnReset = createRoundedButton("Làm mới", new Color(108, 117, 125));

    btnSearch.addActionListener(e -> search());
    btnReset.addActionListener(e -> reset());
    txtMatohopSearch.addActionListener(e -> search());

    searchPanel.add(new JLabel("Mã tổ hợp:"));
    searchPanel.add(txtMatohopSearch);
    searchPanel.add(btnSearch);
    searchPanel.add(btnReset);

    // Action Buttons
    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    btnPanel.setOpaque(false);

    RoundedButton btnAdd = createRoundedButton("Thêm mới", new Color(34, 197, 151));
    RoundedButton btnEdit = createRoundedButton("Sửa", new Color(59, 130, 246));
    RoundedButton btnDelete = createRoundedButton("Xóa", new Color(239, 68, 68));
    RoundedButton btnImport = createRoundedButton("Import Excel", new Color(14, 165, 233));

    btnAdd.addActionListener(e -> addToHopMon());
    btnEdit.addActionListener(e -> updateToHopMon());
    btnDelete.addActionListener(e -> deleteToHopMon());
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
    tableModel = new DefaultTableModel(TableHeaders.TOHOP_MON, 0) {
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

private void configureSearchField(JTextField txtMatohopSearch2) {
    txtMatohopSearch2.setPreferredSize(new Dimension(160, 38));
    txtMatohopSearch2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    txtMatohopSearch2.setBackground(Color.WHITE);
    txtMatohopSearch2.setForeground(new Color(30, 30, 30));
    txtMatohopSearch2.setCaretColor(new Color(30, 30, 30));
    ((PlaceholderTextField) txtMatohopSearch2).setPlaceholderColor(new Color(156, 163, 175));
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
                () -> new ToHopMonImportService().importFromExcel(file, progressDialog)
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
        String matohop = txtMatohopSearch != null ? txtMatohopSearch.getText().trim() : "";
        int totalPages = Math.max(1, service.getTotalPagesAnd(matohop));
        if (currentPage > totalPages) currentPage = totalPages;

        List<ToHopMon> list = service.searchAnd(matohop, currentPage);
        paginationPanel.update(currentPage, totalPages);

        tableModel.setRowCount(0);
        for (ToHopMon t : list) {
            tableModel.addRow(new Object[]{
                    t.getMatohop(), t.getMon1(), t.getMon2(),
                    t.getMon3(), t.getTentohop()
            });
        }
    }

    private void search() {
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private void reset() {
        txtMatohopSearch.setText("");
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private void addToHopMon() {
        try {
            ToHopMon created = showToHopMonForm(null);
            if (created == null) return;

            if (created.getMatohop() == null || created.getMatohop().isBlank()) {
                JOptionPane.showMessageDialog(this, "Mã tổ hợp không được để trống.",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (service.findByMatohop(created.getMatohop().trim()) != null) {
                JOptionPane.showMessageDialog(this, "Mã tổ hợp đã tồn tại.",
                        "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            service.save(created);
            JOptionPane.showMessageDialog(this, "Đã thêm tổ hợp môn thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            txtMatohopSearch.setText("");
            currentPage = 1;
            paginationPanel.reset();
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi thêm tổ hợp môn:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateToHopMon() {
        try {
            String selectedMatohop = getSelectedMatohop();
            if (selectedMatohop == null) return;

            ToHopMon existing = service.findByMatohop(selectedMatohop);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy tổ hợp theo mã: " + selectedMatohop,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ToHopMon updated = showToHopMonForm(existing);
            if (updated == null) return;

            service.update(updated);
            JOptionPane.showMessageDialog(this, "Đã cập nhật tổ hợp môn thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi cập nhật tổ hợp môn:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteToHopMon() {
        try {
            String selectedMatohop = getSelectedMatohop();
            if (selectedMatohop == null) return;

            ToHopMon existing = service.findByMatohop(selectedMatohop);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy tổ hợp theo mã: " + selectedMatohop,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xóa tổ hợp môn " + selectedMatohop + "?\nNếu đang được dùng ở bảng khác, hệ thống có thể không cho xóa.",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            service.delete(existing);
            JOptionPane.showMessageDialog(this, "Đã xóa tổ hợp môn thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi xóa tổ hợp môn:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSelectedMatohop() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 tổ hợp môn trong bảng.",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        Object value = tableModel.getValueAt(modelRow, 0);
        String matohop = Objects.toString(value, "").trim();
        if (matohop.isBlank()) {
            JOptionPane.showMessageDialog(this, "Dòng đã chọn không có mã tổ hợp hợp lệ.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return matohop;
    }

    private ToHopMon showToHopMonForm(ToHopMon existing) {
        boolean isEdit = existing != null;

        JTextField txtMa = new JTextField(20);
        JTextField txtMon1 = new JTextField(20);
        JTextField txtMon2 = new JTextField(20);
        JTextField txtMon3 = new JTextField(20);
        JTextField txtTen = new JTextField(20);

        if (isEdit) {
            txtMa.setText(existing.getMatohop());
            txtMon1.setText(existing.getMon1());
            txtMon2.setText(existing.getMon2());
            txtMon3.setText(existing.getMon3());
            txtTen.setText(existing.getTentohop());
            txtMa.setEnabled(false);
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel("Mã tổ hợp:"));
        panel.add(txtMa);
        panel.add(new JLabel("Môn 1:"));
        panel.add(txtMon1);
        panel.add(new JLabel("Môn 2:"));
        panel.add(txtMon2);
        panel.add(new JLabel("Môn 3:"));
        panel.add(txtMon3);
        panel.add(new JLabel("Tên tổ hợp:"));
        panel.add(txtTen);

        int result = JOptionPane.showConfirmDialog(this,
                panel,
                isEdit ? "Sửa tổ hợp môn" : "Thêm tổ hợp môn",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;

        String ma = txtMa.getText().trim();
        String mon1 = txtMon1.getText().trim();
        String mon2 = txtMon2.getText().trim();
        String mon3 = txtMon3.getText().trim();
        String ten = txtTen.getText().trim();

        ToHopMon t = new ToHopMon();
        if (isEdit) {
            t.setIdtohop(existing.getIdtohop());
            t.setMatohop(existing.getMatohop());
        } else {
            t.setMatohop(ma);
        }
        t.setMon1(mon1);
        t.setMon2(mon2);
        t.setMon3(mon3);
        t.setTentohop(ten);

        return t;
    }
}
