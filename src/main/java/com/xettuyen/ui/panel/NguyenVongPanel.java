package com.xettuyen.ui.panel;

import com.xettuyen.entity.NguyenVong;
import com.xettuyen.service.impl.NguyenVongService;
import com.xettuyen.service.impl.XetTuyenService;
import com.xettuyen.service.imports.ImportResult;
import com.xettuyen.service.imports.NguyenVongImportService;
import com.xettuyen.ui.dialog.CalculationProgressDialog;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import com.xettuyen.ui.util.PaginationPanel;
import com.xettuyen.ui.util.PlaceholderTextField;
import com.xettuyen.ui.util.TableHeaders;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class NguyenVongPanel extends JPanel {

    private final NguyenVongService service = new NguyenVongService();
    private JTable table;
    private DefaultTableModel tableModel;
    private PaginationPanel paginationPanel;
    private int currentPage = 1;

    private JTextField txtCccdSearch;
    private JTextField txtManganhSearch;

    public NguyenVongPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("QUẢN LÝ NGUYỆN VỌNG");
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
        JButton btnXetTuyen = new JButton("Xét tuyển");

        btnAdd.addActionListener(e -> addNguyenVong());
        btnEdit.addActionListener(e -> updateNguyenVong());
        btnDelete.addActionListener(e -> deleteNguyenVong());
        btnImport.addActionListener(e -> importExcel());
        btnXetTuyen.addActionListener(e -> runXetTuyen());

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnImport);
        btnPanel.add(btnXetTuyen);

        actionPanel.add(searchPanel, BorderLayout.WEST);
        actionPanel.add(btnPanel, BorderLayout.EAST);

        topPanel.add(actionPanel);

        add(topPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        tableModel = new DefaultTableModel(TableHeaders.NGUYEN_VONG, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        applyKetQuaRenderer();
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

        List<NguyenVong> list = service.searchAnd(cccd, manganh, currentPage);
        paginationPanel.update(currentPage, totalPages);

        tableModel.setRowCount(0);
        for (NguyenVong nv : list) {
            tableModel.addRow(new Object[]{
                    nv.getNn_cccd(), nv.getNv_manganh(), nv.getNv_tt(),
                    nv.getTt_phuongthuc(), nv.getDiem_thxt(),
                    nv.getDiem_utqd(), nv.getDiem_cong(),
                    nv.getDiem_xettuyen(), nv.getNv_ketqua(),
                    nv.getTt_thm()
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
        // JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        // CalculationProgressDialog progressDialog = new CalculationProgressDialog(parent);
        // List<String> warnings = progressDialog.startCalculation(
        //         () -> service.recalculateThxtAll(progressDialog::updateProgress)
        // );
        // showCalcWarnings(warnings);
        loadData();
    }

    private void addNguyenVong() {
        try {
            NguyenVong created = showForm(null);
            if (created == null) return;

            if (created.getNv_keys() == null || created.getNv_keys().isBlank()) {
                JOptionPane.showMessageDialog(this, "Khóa nguyện vọng (nv_keys) không hợp lệ.",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (service.findByNvKeys(created.getNv_keys()) != null) {
                JOptionPane.showMessageDialog(this, "Nguyện vọng đã tồn tại: " + created.getNv_keys(),
                        "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            service.save(created);
            JOptionPane.showMessageDialog(this, "Đã thêm nguyện vọng thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reset();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi thêm nguyện vọng:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateNguyenVong() {
        try {
            String nvKeys = getSelectedNvKeys();
            if (nvKeys == null) return;

            NguyenVong existing = service.findByNvKeys(nvKeys);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy nguyện vọng: " + nvKeys,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            NguyenVong updated = showForm(existing);
            if (updated == null) return;

            service.update(updated);
            JOptionPane.showMessageDialog(this, "Đã cập nhật nguyện vọng thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi cập nhật nguyện vọng:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteNguyenVong() {
        try {
            String nvKeys = getSelectedNvKeys();
            if (nvKeys == null) return;

            NguyenVong existing = service.findByNvKeys(nvKeys);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy nguyện vọng: " + nvKeys,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xóa nguyện vọng của CCCD " + existing.getNn_cccd() +
                    " ngành " + existing.getNv_manganh() + " (thứ tự " + existing.getNv_tt() + ")?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            service.delete(existing);
            JOptionPane.showMessageDialog(this, "Đã xóa nguyện vọng thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi xóa nguyện vọng:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** nv_keys = cccd_manganh_thutu theo convention trong schema */
    private String getSelectedNvKeys() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng trong bảng.",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        String cccd    = Objects.toString(tableModel.getValueAt(modelRow, 0), "").trim();
        String manganh = Objects.toString(tableModel.getValueAt(modelRow, 1), "").trim();
        String thutu   = Objects.toString(tableModel.getValueAt(modelRow, 2), "").trim();
        if (cccd.isBlank() || manganh.isBlank() || thutu.isBlank()) {
            JOptionPane.showMessageDialog(this, "Dòng đã chọn thiếu thông tin để xác định bản ghi.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return cccd + "_" + manganh + "_" + thutu;
    }

    private NguyenVong showForm(NguyenVong existing) {
        boolean isEdit = existing != null;

        JTextField txtCccd        = new JTextField(20);
        JTextField txtManganh     = new JTextField(20);
        JTextField txtThuTu       = new JTextField(10);
        JTextField txtPhuongThuc  = new JTextField(20);
        JTextField txtDiemThxt    = new JTextField(10);
        JTextField txtDiemUtqd    = new JTextField(10);
        JTextField txtDiemCong    = new JTextField(10);
        JTextField txtDiemXt      = new JTextField(10);
        JTextField txtKetQua      = new JTextField(20);
        JTextField txtThm         = new JTextField(20);

        if (isEdit) {
            txtCccd.setText(Objects.toString(existing.getNn_cccd(), ""));
            txtCccd.setEditable(false);
            txtManganh.setText(Objects.toString(existing.getNv_manganh(), ""));
            txtManganh.setEditable(false);
            txtThuTu.setText(existing.getNv_tt() == null ? "" : String.valueOf(existing.getNv_tt()));
            txtThuTu.setEditable(false);
            txtPhuongThuc.setText(Objects.toString(existing.getTt_phuongthuc(), ""));
            txtDiemThxt.setText(toText(existing.getDiem_thxt()));
            txtDiemUtqd.setText(toText(existing.getDiem_utqd()));
            txtDiemCong.setText(toText(existing.getDiem_cong()));
            txtDiemXt.setText(toText(existing.getDiem_xettuyen()));
            txtKetQua.setText(Objects.toString(existing.getNv_ketqua(), ""));
            txtThm.setText(Objects.toString(existing.getTt_thm(), ""));
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("CCCD:"));
        form.add(txtCccd);
        form.add(new JLabel("Mã ngành:"));
        form.add(txtManganh);
        form.add(new JLabel("Thứ tự nguyện vọng:"));
        form.add(txtThuTu);
        form.add(new JLabel("Phương thức:"));
        form.add(txtPhuongThuc);
        form.add(new JLabel("Điểm thi xét tuyển:"));
        form.add(txtDiemThxt);
        form.add(new JLabel("Điểm ưu tiên quy đổi:"));
        form.add(txtDiemUtqd);
        form.add(new JLabel("Điểm cộng:"));
        form.add(txtDiemCong);
        form.add(new JLabel("Điểm xét tuyển:"));
        form.add(txtDiemXt);
        form.add(new JLabel("Kết quả:"));
        form.add(txtKetQua);
        form.add(new JLabel("Thông tin thêm:"));
        form.add(txtThm);

        int option = JOptionPane.showConfirmDialog(this,
                form,
                isEdit ? "Sửa nguyện vọng" : "Thêm nguyện vọng",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return null;

        String cccd    = txtCccd.getText().trim();
        String manganh = txtManganh.getText().trim();
        String thuTuStr= txtThuTu.getText().trim();

        if (cccd.isBlank() || manganh.isBlank() || thuTuStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "CCCD, mã ngành và thứ tự nguyện vọng không được để trống.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Integer thuTu;
        try {
            thuTu = Integer.parseInt(thuTuStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Thứ tự nguyện vọng phải là số nguyên.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        NguyenVong nv = isEdit ? existing : new NguyenVong();
        nv.setNn_cccd(cccd);
        nv.setNv_manganh(manganh);
        nv.setNv_tt(thuTu);
        nv.setTt_phuongthuc(blankToNull(txtPhuongThuc.getText()));
        nv.setDiem_thxt(parseDecimalOrNull(txtDiemThxt.getText(), "Điểm thi xét tuyển"));
        nv.setDiem_utqd(parseDecimalOrNull(txtDiemUtqd.getText(), "Điểm ưu tiên quy đổi"));
        nv.setDiem_cong(parseDecimalOrNull(txtDiemCong.getText(), "Điểm cộng"));
        nv.setDiem_xettuyen(parseDecimalOrNull(txtDiemXt.getText(), "Điểm xét tuyển"));
        nv.setNv_ketqua(blankToNull(txtKetQua.getText()));
        nv.setTt_thm(blankToNull(txtThm.getText()));
        if (!isEdit) {
            nv.setNv_keys(cccd + "_" + manganh + "_" + thuTu);
        }
        return nv;
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
                () -> new NguyenVongImportService().importFromExcel(file, progressDialog)
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

        JFrame recalcParent = (JFrame) SwingUtilities.getWindowAncestor(this);
        CalculationProgressDialog recalcDialog = new CalculationProgressDialog(recalcParent);
        List<String> warnings = recalcDialog.startCalculation(
            () -> service.recalculateThxtAll(recalcDialog::updateProgress)
        );
        showCalcWarnings(warnings);

        loadData();
    }

    private void runXetTuyen() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Thực hiện xét tuyển và cập nhật kết quả?",
                "Xác nhận xét tuyển",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        CalculationProgressDialog progressDialog = new CalculationProgressDialog(parent);
        progressDialog.updateProgress(0, "Dang xet tuyen...");

        XetTuyenService.Result result = progressDialog.startTask(() ->
            service.runXetTuyenAll(progressDialog::updateProgress)
        );

        if (result != null) {
            JOptionPane.showMessageDialog(this,
                "Đã xét tuyển. Đậu: " + result.getAccepted() +
                ", Rớt: " + result.getRejected() +
                ", Tổng: " + result.getTotal(),
                "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Xét tuyển thất bại.",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        loadData();
    }

    private void applyKetQuaRenderer() {
        int ketQuaColumnIndex = 8;
        table.getColumnModel().getColumn(ketQuaColumnIndex)
                .setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        Component comp = super.getTableCellRendererComponent(
                                table, value, isSelected, hasFocus, row, column);

                        String raw = Objects.toString(value, "").trim();
                        if ("1".equals(raw)) {
                            setText("TRÚNG TUYỂN");
                            if (!isSelected) {
                                comp.setForeground(new Color(0, 110, 0));
                            }
                        } else if ("0".equals(raw)) {
                            setText("RỚT");
                            if (!isSelected) {
                                comp.setForeground(new Color(160, 0, 0));
                            }
                        } else {
                            setText(raw);
                            if (!isSelected) {
                                comp.setForeground(table.getForeground());
                            }
                        }
                        return comp;
                    }
                });
    }

    private void showCalcWarnings(List<String> warnings) {
        if (warnings == null || warnings.isEmpty()) return;
        JTextArea textArea = new JTextArea(String.join("\n", warnings));
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(480, 220));
        JOptionPane.showMessageDialog(this, scroll,
                "Cảnh báo tính THXT", JOptionPane.WARNING_MESSAGE);
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