package com.xettuyen.ui.panel;

import com.xettuyen.entity.DiemCong;
import com.xettuyen.service.impl.DiemCongService;
import com.xettuyen.service.imports.DiemCongImportService;
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

public class DiemCongPanel extends JPanel {

    private final DiemCongService service = new DiemCongService();
    private JTable table;
    private DefaultTableModel tableModel;
    private PaginationPanel paginationPanel;
    private int currentPage = 1;
    private JTextField txtCccdSearch;
    private JTextField txtManganhSearch;

    public DiemCongPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("QUẢN LÝ ĐIỂM CỘNG");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(title);

        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        PlaceholderTextField cccdField = new PlaceholderTextField("CCCD", 14);
        cccdField.setPlaceholderColor(Color.GRAY);
        txtCccdSearch = cccdField;

        PlaceholderTextField manganhField = new PlaceholderTextField("Mã ngành", 12);
        manganhField.setPlaceholderColor(Color.GRAY);
        txtManganhSearch = manganhField;

        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnReset  = new JButton("Làm mới");

        btnSearch.addActionListener(e -> search());
        btnReset.addActionListener(e -> reset());
        txtCccdSearch.addActionListener(e -> search());
        txtManganhSearch.addActionListener(e -> search());

        searchPanel.add(new JLabel("CCCD:"));
        searchPanel.add(txtCccdSearch);
        searchPanel.add(new JLabel("Mã ngành:"));
        searchPanel.add(txtManganhSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReset);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnAdd    = new JButton("Thêm mới");
        JButton btnEdit   = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnImport = new JButton("Import Excel");

        btnAdd.addActionListener(e -> addDiemCong());
        btnEdit.addActionListener(e -> updateDiemCong());
        btnDelete.addActionListener(e -> deleteDiemCong());
        btnImport.addActionListener(e -> importExcel());

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnImport);

        actionPanel.add(searchPanel, BorderLayout.WEST);
        actionPanel.add(btnPanel, BorderLayout.EAST);

        topPanel.add(actionPanel);

        add(topPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        tableModel = new DefaultTableModel(TableHeaders.DIEM_CONG, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        paginationPanel = new PaginationPanel();
        paginationPanel.setOnPageChange(() -> {
            currentPage = paginationPanel.getCurrentPage();
            loadData();
        });
        add(paginationPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        String cccd = txtCccdSearch != null ? txtCccdSearch.getText().trim() : "";
        String manganh = txtManganhSearch != null ? txtManganhSearch.getText().trim() : "";
        int totalPages = Math.max(1, service.getTotalPagesAnd(cccd, manganh));
        if (currentPage > totalPages) currentPage = totalPages;

        List<DiemCong> list = service.searchAnd(cccd, manganh, currentPage);
        paginationPanel.update(currentPage, totalPages);

        tableModel.setRowCount(0);
        for (DiemCong dc : list) {
            tableModel.addRow(new Object[]{
                    dc.getTs_cccd(), dc.getManganh(), dc.getMatohop(),
                    dc.getPhuongthuc(), dc.getDiemCC(),
                    dc.getDiemUtxt(), dc.getDiemTong(), dc.getGhichu(), dc.getDc_keys()
            });
        }
    }

    private void search() {
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private void reset() {
        txtCccdSearch.setText("");
        txtManganhSearch.setText("");
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private void addDiemCong() {
        try {
            DiemCong created = showForm(null);
            if (created == null) return;

            if (created.getDc_keys() == null || created.getDc_keys().isBlank()) {
                JOptionPane.showMessageDialog(this, "Khóa điểm cộng (dc_keys) không được để trống.",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (service.findByDcKeys(created.getDc_keys()) != null) {
                JOptionPane.showMessageDialog(this, "Bản ghi đã tồn tại với khóa: " + created.getDc_keys(),
                        "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            service.save(created);
            JOptionPane.showMessageDialog(this, "Đã thêm điểm cộng thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reset();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi thêm điểm cộng:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDiemCong() {
        try {
            String selectedDcKeys = getSelectedDcKeys();
            if (selectedDcKeys == null) return;

            DiemCong existing = service.findByDcKeys(selectedDcKeys);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi: " + selectedDcKeys,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            DiemCong updated = showForm(existing);
            if (updated == null) return;

            service.update(updated);
            JOptionPane.showMessageDialog(this, "Đã cập nhật điểm cộng thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi cập nhật điểm cộng:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDiemCong() {
        try {
            String selectedDcKeys = getSelectedDcKeys();
            if (selectedDcKeys == null) return;

            DiemCong existing = service.findByDcKeys(selectedDcKeys);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi: " + selectedDcKeys,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xóa điểm cộng của CCCD " + existing.getTs_cccd() + " ngành " + existing.getManganh() + "?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            service.delete(existing);
            JOptionPane.showMessageDialog(this, "Đã xóa điểm cộng thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi xóa điểm cộng:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSelectedDcKeys() {
        int viewRow = table.getSelectedRow();

        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn 1 dòng trong bảng.",
                    "Chưa chọn dòng",
                    JOptionPane.WARNING_MESSAGE
            );
            return null;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);

        // Cột cuối cùng trong table là dc_keys
        String dcKeys = Objects.toString(
                tableModel.getValueAt(modelRow, 8),
                ""
        ).trim();

        if (dcKeys.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không tìm thấy dc_keys của dòng đã chọn.",
                    "Dữ liệu không hợp lệ",
                    JOptionPane.WARNING_MESSAGE
            );
            return null;
        }

        return dcKeys;
    }

    private DiemCong showForm(DiemCong existing) {
        boolean isEdit = existing != null;

        JTextField txtCccd       = new JTextField(20);
        JTextField txtManganh    = new JTextField(20);
        JTextField txtMatohop    = new JTextField(20);
        JTextField txtPhuongthuc = new JTextField(20);
        JTextField txtDiemCC     = new JTextField(10);
        JTextField txtDiemUtxt   = new JTextField(10);
        JTextField txtDiemTong   = new JTextField(10);
        JTextField txtGhichu     = new JTextField(30);

        if (isEdit) {
            txtCccd.setText(Objects.toString(existing.getTs_cccd(), ""));
            txtCccd.setEditable(false);
            txtManganh.setText(Objects.toString(existing.getManganh(), ""));
            txtManganh.setEditable(false);
            txtMatohop.setText(Objects.toString(existing.getMatohop(), ""));
            txtMatohop.setEditable(false);
            txtPhuongthuc.setText(Objects.toString(existing.getPhuongthuc(), ""));
            txtPhuongthuc.setEditable(false);
            txtDiemCC.setText(toText(existing.getDiemCC()));
            txtDiemUtxt.setText(toText(existing.getDiemUtxt()));
            txtDiemTong.setText(toText(existing.getDiemTong()));
            txtGhichu.setText(Objects.toString(existing.getGhichu(), ""));
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("CCCD:"));
        form.add(txtCccd);
        form.add(new JLabel("Mã ngành:"));
        form.add(txtManganh);
        form.add(new JLabel("Mã tổ hợp:"));
        form.add(txtMatohop);
        form.add(new JLabel("Phương thức:"));
        form.add(txtPhuongthuc);
        form.add(new JLabel("Điểm CC:"));
        form.add(txtDiemCC);
        form.add(new JLabel("Điểm ưu tiên XT:"));
        form.add(txtDiemUtxt);
        form.add(new JLabel("Điểm tổng:"));
        form.add(txtDiemTong);
        form.add(new JLabel("Ghi chú:"));
        form.add(txtGhichu);

        int option = JOptionPane.showConfirmDialog(this,
                form,
                isEdit ? "Sửa điểm cộng" : "Thêm điểm cộng",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return null;

        String cccd       = txtCccd.getText().trim();
        String manganh    = txtManganh.getText().trim();
        String matohop    = txtMatohop.getText().trim();
        String phuongthuc = txtPhuongthuc.getText().trim();

        if (cccd.isBlank()) {
            JOptionPane.showMessageDialog(this, "CCCD không được để trống.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        DiemCong dc = isEdit ? existing : new DiemCong();
        dc.setTs_cccd(cccd);
        dc.setManganh(blankToNull(manganh));
        dc.setMatohop(blankToNull(matohop));
        dc.setPhuongthuc(blankToNull(phuongthuc));
        dc.setDiemCC(parseDecimalOrNull(txtDiemCC.getText(), "Điểm CC"));
        dc.setDiemUtxt(parseDecimalOrNull(txtDiemUtxt.getText(), "Điểm ưu tiên XT"));
        dc.setDiemTong(parseDecimalOrNull(txtDiemTong.getText(), "Điểm tổng"));
        dc.setGhichu(blankToNull(txtGhichu.getText()));
        if (!isEdit) {
            // Xây dc_keys theo convention: cccd_manganh_matohop_phuongthuc
            dc.setDc_keys(cccd + "_" + manganh + "_" + matohop + "_" + phuongthuc);
        }
        return dc;
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
                () -> new DiemCongImportService().importFromExcel(file, progressDialog)
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