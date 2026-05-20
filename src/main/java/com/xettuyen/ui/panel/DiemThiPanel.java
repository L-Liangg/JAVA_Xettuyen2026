package com.xettuyen.ui.panel;

import com.xettuyen.entity.DiemThi;
import com.xettuyen.service.impl.DiemThiService;
import com.xettuyen.service.imports.DiemThiImportService;
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
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class DiemThiPanel extends JPanel {

    private final DiemThiService service = new DiemThiService();
    private JTable table;
    private DefaultTableModel tableModel;
    private PaginationPanel paginationPanel;
    private int currentPage = 1;
    private JTextField txtCccdSearch;
    private JTextField txtSbdSearch;

    public DiemThiPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("QUẢN LÝ ĐIỂM THI");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        topPanel.add(title);

        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;

        PlaceholderTextField cccdField = new PlaceholderTextField("CCCD", 14);
        cccdField.setPlaceholderColor(Color.GRAY);
        txtCccdSearch = cccdField;

        PlaceholderTextField sbdField = new PlaceholderTextField("Số báo danh", 14);
        sbdField.setPlaceholderColor(Color.GRAY);
        txtSbdSearch = sbdField;

        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnReset  = new JButton("Làm mới");

        btnSearch.addActionListener(e -> search());
        btnReset.addActionListener(e -> reset());
        txtCccdSearch.addActionListener(e -> search());
        txtSbdSearch.addActionListener(e -> search());

        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; searchPanel.add(new JLabel("CCCD:"), gbc);
        gbc.gridx = 1; searchPanel.add(new JLabel("SBD:"), gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; searchPanel.add(txtCccdSearch, gbc);
        gbc.gridx = 1; searchPanel.add(txtSbdSearch, gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 2; searchPanel.add(btnSearch, gbc);
        gbc.gridx = 3; searchPanel.add(btnReset, gbc);

        JPanel btnPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.insets = new Insets(2, 3, 2, 3);
        gbcBtn.anchor = GridBagConstraints.EAST;

        gbcBtn.gridy = 0;
        gbcBtn.gridx = 0;
        gbcBtn.gridwidth = 5;
        gbcBtn.fill = GridBagConstraints.BOTH;
        JLabel placeholder = new JLabel(" ");
        placeholder.setPreferredSize(new Dimension(0, new JLabel("CCCD:").getPreferredSize().height));
        btnPanel.add(placeholder, gbcBtn);

        gbcBtn.gridy = 1;
        gbcBtn.gridwidth = 1;
        gbcBtn.fill = GridBagConstraints.NONE;

        JButton btnAdd    = new JButton("Thêm");
        JButton btnEdit   = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnStats  = new JButton("Thống kê");
        JButton btnImport = new JButton("Import");

        btnAdd.addActionListener(e -> addDiemThi());
        btnEdit.addActionListener(e -> updateDiemThi());
        btnDelete.addActionListener(e -> deleteDiemThi());
        btnStats.addActionListener(e -> showStatistics());
        btnImport.addActionListener(e -> importExcel());

        gbcBtn.gridx = 0; btnPanel.add(btnAdd, gbcBtn);
        gbcBtn.gridx = 1; btnPanel.add(btnEdit, gbcBtn);
        gbcBtn.gridx = 2; btnPanel.add(btnDelete, gbcBtn);
        gbcBtn.gridx = 3; btnPanel.add(btnStats, gbcBtn);
        gbcBtn.gridx = 4; btnPanel.add(btnImport, gbcBtn);

        actionPanel.add(searchPanel, BorderLayout.WEST);
        actionPanel.add(btnPanel, BorderLayout.EAST);

        topPanel.add(actionPanel);
        add(topPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        tableModel = new DefaultTableModel(TableHeaders.DIEM_THI, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== PAGINATION =====
        paginationPanel = new PaginationPanel();
        paginationPanel.setOnPageChange(() -> {
            currentPage = paginationPanel.getCurrentPage();
            loadData();
        });
        add(paginationPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        String cccd = txtCccdSearch != null ? txtCccdSearch.getText().trim() : "";
        String sbd = txtSbdSearch != null ? txtSbdSearch.getText().trim() : "";

        int totalPages = service.getTotalPagesAnd(cccd, sbd);
        if (currentPage > totalPages) currentPage = totalPages;

        List<DiemThi> list = service.searchAnd(cccd, sbd, currentPage);

        paginationPanel.update(currentPage, totalPages);

        tableModel.setRowCount(0);
        for (DiemThi d : list) {
            tableModel.addRow(new Object[]{
                    d.getCccd(), d.getSobaodanh(), d.getD_phuongthuc(),
                    d.getTO(), d.getLI(), d.getHO(), d.getSI(),
                    d.getSU(), d.getDI(), d.getVA(),
                    d.getN1_THI(), d.getN1_CC(), d.getCNCN(),
                    d.getCNNN(), d.getTI(), d.getKTPL(),
                    d.getNL1(), d.getNK1(), d.getNK2()
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
                () -> new DiemThiImportService().importFromExcel(file, progressDialog)
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
        txtSbdSearch.setText("");
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private void addDiemThi() {
        try {
            DiemThi created = showDiemThiForm(null);
            if (created == null) return;

            if (created.getCccd() == null || created.getCccd().isBlank()) {
                JOptionPane.showMessageDialog(this, "CCCD không được để trống.",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (service.findByCccd(created.getCccd()) != null) {
                JOptionPane.showMessageDialog(this, "CCCD đã tồn tại.",
                        "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            service.save(created);
            JOptionPane.showMessageDialog(this, "Đã thêm điểm thi thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reset();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi thêm điểm thi:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDiemThi() {
        try {
            String selectedCccd = getSelectedCccd();
            if (selectedCccd == null) return;

            DiemThi existing = service.findByCccd(selectedCccd);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy CCCD: " + selectedCccd,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            DiemThi updated = showDiemThiForm(existing);
            if (updated == null) return;

            service.update(updated);
            JOptionPane.showMessageDialog(this, "Đã cập nhật điểm thi thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi cập nhật điểm thi:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDiemThi() {
        try {
            String selectedCccd = getSelectedCccd();
            if (selectedCccd == null) return;

            DiemThi existing = service.findByCccd(selectedCccd);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy CCCD: " + selectedCccd,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xóa điểm thi của CCCD " + selectedCccd + "?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            service.delete(existing);
            JOptionPane.showMessageDialog(this, "Đã xóa điểm thi thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi xóa điểm thi:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStatistics() {
        List<DiemThi> list = service.getAllByKeyword("");
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để thống kê.",
                    "Trống dữ liệu", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Map<String, Function<DiemThi, BigDecimal>> fields = new LinkedHashMap<>();
        fields.put("Toán", DiemThi::getTO);
        fields.put("Lí", DiemThi::getLI);
        fields.put("Hóa", DiemThi::getHO);
        fields.put("Sinh", DiemThi::getSI);
        fields.put("Sử", DiemThi::getSU);
        fields.put("Địa", DiemThi::getDI);
        fields.put("Văn", DiemThi::getVA);
        fields.put("Tin", DiemThi::getTI);
        fields.put("KTPL", DiemThi::getKTPL);
        fields.put("N1 thi", DiemThi::getN1_THI);
        fields.put("N1 CC", DiemThi::getN1_CC);
        fields.put("CNCN", DiemThi::getCNCN);
        fields.put("CNNN", DiemThi::getCNNN);
        fields.put("NL1", DiemThi::getNL1);
        fields.put("NK1", DiemThi::getNK1);
        fields.put("NK2", DiemThi::getNK2);

        JComboBox<String> cbField = new JComboBox<>(fields.keySet().toArray(new String[0]));
        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Chọn loại điểm / môn:"));
        panel.add(cbField);

        int option = JOptionPane.showConfirmDialog(this,
                panel,
                "Thống kê điểm",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return;

        String key = Objects.toString(cbField.getSelectedItem(), "");
        Function<DiemThi, BigDecimal> getter = fields.get(key);
        if (getter == null) return;

        int count = 0;
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal min = null;
        BigDecimal max = null;

        for (DiemThi d : list) {
            BigDecimal val = getter.apply(d);
            if (val == null) continue;
            count++;
            sum = sum.add(val);
            if (min == null || val.compareTo(min) < 0) min = val;
            if (max == null || val.compareTo(max) > 0) max = val;
        }

        if (count == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu hợp lệ cho thống kê.",
                    "Trống dữ liệu", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        BigDecimal avg = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        String message = "Loại điểm: " + key + "\n" +
                "Số bản ghi: " + count + "\n" +
                "Điểm thấp nhất: " + min + "\n" +
                "Điểm cao nhất: " + max + "\n" +
                "Điểm trung bình: " + avg;

        JOptionPane.showMessageDialog(this, message,
                "Kết quả thống kê", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getSelectedCccd() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng trong bảng.",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        Object value = tableModel.getValueAt(modelRow, 0);
        String cccd = Objects.toString(value, "").trim();
        if (cccd.isBlank()) {
            JOptionPane.showMessageDialog(this, "Dòng đã chọn không có CCCD hợp lệ.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return cccd;
    }

    private DiemThi showDiemThiForm(DiemThi existing) {
        boolean isEdit = existing != null;

        JTextField txtCccd = new JTextField(20);
        JTextField txtSbd = new JTextField(20);
        JTextField txtPhuongThuc = new JTextField(20);
        JTextField txtTO = new JTextField(10);
        JTextField txtLI = new JTextField(10);
        JTextField txtHO = new JTextField(10);
        JTextField txtSI = new JTextField(10);
        JTextField txtSU = new JTextField(10);
        JTextField txtDI = new JTextField(10);
        JTextField txtVA = new JTextField(10);
        JTextField txtN1Thi = new JTextField(10);
        JTextField txtN1CC = new JTextField(10);
        JTextField txtCNCN = new JTextField(10);
        JTextField txtCNNN = new JTextField(10);
        JTextField txtTI = new JTextField(10);
        JTextField txtKTPL = new JTextField(10);
        JTextField txtNL1 = new JTextField(10);
        JTextField txtNK1 = new JTextField(10);
        JTextField txtNK2 = new JTextField(10);

        if (isEdit) {
            txtCccd.setText(existing.getCccd());
            txtCccd.setEditable(false);
            txtSbd.setText(Objects.toString(existing.getSobaodanh(), ""));
            txtPhuongThuc.setText(Objects.toString(existing.getD_phuongthuc(), ""));
            txtTO.setText(toText(existing.getTO()));
            txtLI.setText(toText(existing.getLI()));
            txtHO.setText(toText(existing.getHO()));
            txtSI.setText(toText(existing.getSI()));
            txtSU.setText(toText(existing.getSU()));
            txtDI.setText(toText(existing.getDI()));
            txtVA.setText(toText(existing.getVA()));
            txtN1Thi.setText(toText(existing.getN1_THI()));
            txtN1CC.setText(toText(existing.getN1_CC()));
            txtCNCN.setText(toText(existing.getCNCN()));
            txtCNNN.setText(toText(existing.getCNNN()));
            txtTI.setText(toText(existing.getTI()));
            txtKTPL.setText(toText(existing.getKTPL()));
            txtNL1.setText(toText(existing.getNL1()));
            txtNK1.setText(toText(existing.getNK1()));
            txtNK2.setText(toText(existing.getNK2()));
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("CCCD:"));
        form.add(txtCccd);
        form.add(new JLabel("Số báo danh:"));
        form.add(txtSbd);
        form.add(new JLabel("Phương thức:"));
        form.add(txtPhuongThuc);
        form.add(new JLabel("Toán:"));
        form.add(txtTO);
        form.add(new JLabel("Lí:"));
        form.add(txtLI);
        form.add(new JLabel("Hóa:"));
        form.add(txtHO);
        form.add(new JLabel("Sinh:"));
        form.add(txtSI);
        form.add(new JLabel("Sử:"));
        form.add(txtSU);
        form.add(new JLabel("Địa:"));
        form.add(txtDI);
        form.add(new JLabel("Văn:"));
        form.add(txtVA);
        form.add(new JLabel("N1 thi:"));
        form.add(txtN1Thi);
        form.add(new JLabel("N1 CC:"));
        form.add(txtN1CC);
        form.add(new JLabel("CNCN:"));
        form.add(txtCNCN);
        form.add(new JLabel("CNNN:"));
        form.add(txtCNNN);
        form.add(new JLabel("Tin:"));
        form.add(txtTI);
        form.add(new JLabel("KTPL:"));
        form.add(txtKTPL);
        form.add(new JLabel("NL1:"));
        form.add(txtNL1);
        form.add(new JLabel("NK1:"));
        form.add(txtNK1);
        form.add(new JLabel("NK2:"));
        form.add(txtNK2);

        int option = JOptionPane.showConfirmDialog(this,
                form,
                isEdit ? "Sửa điểm thi" : "Thêm điểm thi",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return null;

        String cccd = txtCccd.getText().trim();
        if (cccd.isBlank()) {
            JOptionPane.showMessageDialog(this, "CCCD không được để trống.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        DiemThi d = isEdit ? existing : new DiemThi();
        d.setCccd(cccd);
        d.setSobaodanh(blankToNull(txtSbd.getText()));
        d.setD_phuongthuc(blankToNull(txtPhuongThuc.getText()));
        BigDecimal to = parseDecimalOrNull(txtTO.getText(), "Toán");
        if (isInvalidDecimal(txtTO.getText(), to)) return null;
        BigDecimal li = parseDecimalOrNull(txtLI.getText(), "Lí");
        if (isInvalidDecimal(txtLI.getText(), li)) return null;
        BigDecimal ho = parseDecimalOrNull(txtHO.getText(), "Hóa");
        if (isInvalidDecimal(txtHO.getText(), ho)) return null;
        BigDecimal si = parseDecimalOrNull(txtSI.getText(), "Sinh");
        if (isInvalidDecimal(txtSI.getText(), si)) return null;
        BigDecimal su = parseDecimalOrNull(txtSU.getText(), "Sử");
        if (isInvalidDecimal(txtSU.getText(), su)) return null;
        BigDecimal di = parseDecimalOrNull(txtDI.getText(), "Địa");
        if (isInvalidDecimal(txtDI.getText(), di)) return null;
        BigDecimal va = parseDecimalOrNull(txtVA.getText(), "Văn");
        if (isInvalidDecimal(txtVA.getText(), va)) return null;
        BigDecimal n1Thi = parseDecimalOrNull(txtN1Thi.getText(), "N1 thi");
        if (isInvalidDecimal(txtN1Thi.getText(), n1Thi)) return null;
        BigDecimal n1Cc = parseDecimalOrNull(txtN1CC.getText(), "N1 CC");
        if (isInvalidDecimal(txtN1CC.getText(), n1Cc)) return null;
        BigDecimal cncn = parseDecimalOrNull(txtCNCN.getText(), "CNCN");
        if (isInvalidDecimal(txtCNCN.getText(), cncn)) return null;
        BigDecimal cnnn = parseDecimalOrNull(txtCNNN.getText(), "CNNN");
        if (isInvalidDecimal(txtCNNN.getText(), cnnn)) return null;
        BigDecimal ti = parseDecimalOrNull(txtTI.getText(), "Tin");
        if (isInvalidDecimal(txtTI.getText(), ti)) return null;
        BigDecimal ktpl = parseDecimalOrNull(txtKTPL.getText(), "KTPL");
        if (isInvalidDecimal(txtKTPL.getText(), ktpl)) return null;
        BigDecimal nl1 = parseDecimalOrNull(txtNL1.getText(), "NL1");
        if (isInvalidDecimal(txtNL1.getText(), nl1)) return null;
        BigDecimal nk1 = parseDecimalOrNull(txtNK1.getText(), "NK1");
        if (isInvalidDecimal(txtNK1.getText(), nk1)) return null;
        BigDecimal nk2 = parseDecimalOrNull(txtNK2.getText(), "NK2");
        if (isInvalidDecimal(txtNK2.getText(), nk2)) return null;

        d.setTO(to);
        d.setLI(li);
        d.setHO(ho);
        d.setSI(si);
        d.setSU(su);
        d.setDI(di);
        d.setVA(va);
        d.setN1_THI(n1Thi);
        d.setN1_CC(n1Cc);
        d.setCNCN(cncn);
        d.setCNNN(cnnn);
        d.setTI(ti);
        d.setKTPL(ktpl);
        d.setNL1(nl1);
        d.setNK1(nk1);
        d.setNK2(nk2);

        return d;
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
            JOptionPane.showMessageDialog(null, "Điểm không hợp lệ ở '" + label + "': " + trimmed,
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private static boolean isInvalidDecimal(String rawValue, BigDecimal parsedValue) {
        return rawValue != null && !rawValue.trim().isEmpty() && parsedValue == null;
    }

    private static String toText(BigDecimal value) {
        return value == null ? "" : value.toPlainString();
    }
}