package com.xettuyen.ui.panel;

import com.xettuyen.entity.Nganh;
import com.xettuyen.entity.NganhToHop;
import com.xettuyen.entity.ToHopMon;
import com.xettuyen.service.impl.NganhService;
import com.xettuyen.service.impl.NganhToHopService;
import com.xettuyen.service.impl.ToHopMonService;
import com.xettuyen.service.imports.ImportResult;
import com.xettuyen.service.imports.NganhToHopImportService;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import com.xettuyen.ui.util.PaginationPanel;
import com.xettuyen.ui.util.PlaceholderTextField;
import com.xettuyen.ui.util.TableHeaders;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NganhToHopPanel extends JPanel {

    private final NganhToHopService service = new NganhToHopService();
    private final NganhService nganhService = new NganhService();
    private final ToHopMonService toHopMonService = new ToHopMonService();
    private JTable table;
    private DefaultTableModel tableModel;
    private PaginationPanel paginationPanel;
    private int currentPage = 1;
    private JTextField txtManganhSearch;
    private JTextField txtMatohopSearch;

    public NganhToHopPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        // ===== PANEL CHA (DỌC) =====
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        // ===== TITLE =====
        JLabel title = new JLabel("QUẢN LÝ NGÀNH - TỔ HỢP");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(title);

        // khoảng cách
        topPanel.add(Box.createVerticalStrut(8));

        // ===== PANEL INPUT + BUTTON =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        PlaceholderTextField manganhField = new PlaceholderTextField("Mã ngành", 12);
        manganhField.setPlaceholderColor(Color.GRAY);
        txtManganhSearch = manganhField;

        PlaceholderTextField matohopField = new PlaceholderTextField("Mã tổ hợp", 12);
        matohopField.setPlaceholderColor(Color.GRAY);
        txtMatohopSearch = matohopField;

        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnRefresh = new JButton("Làm mới");
        JButton btnAdd = new JButton("Thêm mới");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        JButton btnImport = new JButton("Import Excel");

        btnSearch.addActionListener(e -> search());
        btnRefresh.addActionListener(e -> reset());
        txtManganhSearch.addActionListener(e -> search());
        txtMatohopSearch.addActionListener(e -> search());
        btnImport.addActionListener(e -> importExcel());
        btnAdd.addActionListener(e -> addNganhToHop());
        btnEdit.addActionListener(e -> updateNganhToHop());
        btnDelete.addActionListener(e -> deleteNganhToHop());

        btnPanel.add(new JLabel("Mã ngành:"));
        btnPanel.add(txtManganhSearch);
        btnPanel.add(new JLabel("Mã tổ hợp:"));
        btnPanel.add(txtMatohopSearch);
        btnPanel.add(btnSearch);
        btnPanel.add(btnRefresh);
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnImport);

        topPanel.add(btnPanel);

        add(topPanel, BorderLayout.NORTH);

        // ===== TABLE =====
        tableModel = new DefaultTableModel(TableHeaders.NGANH_TOHOP, 0) {
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

    private void importExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter("Excel files", "xlsx"));

        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);

        ImportProgressDialog progressDialog = new ImportProgressDialog(parent);
        ImportResult result = progressDialog.startImport(
                () -> new NganhToHopImportService().importFromExcel(file, progressDialog)
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
        String manganh = txtManganhSearch != null ? txtManganhSearch.getText().trim() : "";
        String matohop = txtMatohopSearch != null ? txtMatohopSearch.getText().trim() : "";
        int totalPages = Math.max(1, service.getTotalPagesAnd(manganh, matohop));
        if (currentPage > totalPages) currentPage = totalPages;

        List<NganhToHop> list = service.searchAnd(manganh, matohop, currentPage);

        paginationPanel.update(currentPage, totalPages);

        tableModel.setRowCount(0);
        for (NganhToHop nt : list) {
            tableModel.addRow(new Object[]{
                    nt.getManganh(), nt.getMatohop(),
                    nt.getTh_mon1(), nt.getHsmon1(),
                    nt.getTh_mon2(), nt.getHsmon2(),
                    nt.getTh_mon3(), nt.getHsmon3(),
                    boolToDisplay(nt.getN1()),
                    boolToDisplay(nt.getTO()),
                    boolToDisplay(nt.getLI()),
                    boolToDisplay(nt.getHO()),
                    boolToDisplay(nt.getSI()),
                    boolToDisplay(nt.getVA()),
                    boolToDisplay(nt.getSU()),
                    boolToDisplay(nt.getDI()),
                    boolToDisplay(nt.getTI()),
                    boolToDisplay(nt.getKHAC()),
                    boolToDisplay(nt.getKTPL()),
                    nt.getDolech()
            });
        }
    }

    private String boolToDisplay(Boolean val) {
        return Boolean.TRUE.equals(val) ? "1" : "";
    }

    private void search() {
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private void reset() {
        txtManganhSearch.setText("");
        txtMatohopSearch.setText("");
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private void addNganhToHop() {
        try {
            NganhToHop created = showNganhToHopForm(null);
            if (created == null) return;

            if (service.findByTbKeys(created.getTb_keys()) != null) {
                JOptionPane.showMessageDialog(this, "Bản ghi đã tồn tại: " + created.getTb_keys(),
                        "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            service.save(created);
            JOptionPane.showMessageDialog(this, "Đã thêm ngành - tổ hợp thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi thêm ngành - tổ hợp:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateNganhToHop() {
        try {
            String tbKeys = getSelectedTbKeys();
            if (tbKeys == null) return;

            NganhToHop existing = service.findByTbKeys(tbKeys);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi: " + tbKeys,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            NganhToHop updated = showNganhToHopForm(existing);
            if (updated == null) return;

            service.update(updated);
            JOptionPane.showMessageDialog(this, "Đã cập nhật ngành - tổ hợp thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi cập nhật ngành - tổ hợp:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteNganhToHop() {
        try {
            String tbKeys = getSelectedTbKeys();
            if (tbKeys == null) return;

            NganhToHop existing = service.findByTbKeys(tbKeys);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy bản ghi: " + tbKeys,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xóa ngành - tổ hợp " + tbKeys + "?\nNếu đang được dùng ở bảng khác, hệ thống có thể không cho xóa.",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            service.delete(existing);
            JOptionPane.showMessageDialog(this, "Đã xóa ngành - tổ hợp thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi xóa ngành - tổ hợp:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSelectedTbKeys() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng trong bảng.",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        String manganh = Objects.toString(tableModel.getValueAt(modelRow, 0), "").trim();
        String matohop = Objects.toString(tableModel.getValueAt(modelRow, 1), "").trim();
        if (manganh.isBlank() || matohop.isBlank()) {
            JOptionPane.showMessageDialog(this, "Dòng đã chọn không có mã ngành hoặc mã tổ hợp hợp lệ.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return manganh + "_" + matohop;
    }

    private NganhToHop showNganhToHopForm(NganhToHop existing) {
        boolean isEdit = existing != null;

        List<Nganh> nganhList = nganhService.getAll();
        List<ToHopMon> toHopList = toHopMonService.getAll();
        if (nganhList.isEmpty() || toHopList.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Cần có dữ liệu ngành và tổ hợp môn trước khi thao tác.",
                    "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        JComboBox<String> cbNganh = new JComboBox<>();
        List<String> nganhCodes = new ArrayList<>();
        for (Nganh n : nganhList) {
            String code = Objects.toString(n.getManganh(), "").trim();
            String name = Objects.toString(n.getTennganh(), "").trim();
            cbNganh.addItem(code + " - " + name);
            nganhCodes.add(code);
        }

        JComboBox<String> cbToHop = new JComboBox<>();
        List<String> toHopCodes = new ArrayList<>();
        for (ToHopMon t : toHopList) {
            String code = Objects.toString(t.getMatohop(), "").trim();
            // String display = code + " (" + t.getMon1() + "-" + t.getMon2() + "-" + t.getMon3() + ")";
            cbToHop.addItem(code);
            toHopCodes.add(code);
        }

        JTextField txtMon1 = new JTextField(10);
        JTextField txtHs1 = new JTextField(10);
        JTextField txtMon2 = new JTextField(10);
        JTextField txtHs2 = new JTextField(10);
        JTextField txtMon3 = new JTextField(10);
        JTextField txtHs3 = new JTextField(10);
        JTextField txtDoLech = new JTextField(10);

        if (isEdit) {
            cbNganh.setSelectedIndex(indexOfCode(nganhCodes, existing.getManganh()));
            cbToHop.setSelectedIndex(indexOfCode(toHopCodes, existing.getMatohop()));
            cbNganh.setEnabled(false);
            cbToHop.setEnabled(false);

            txtMon1.setText(Objects.toString(existing.getTh_mon1(), ""));
            txtHs1.setText(existing.getHsmon1() == null ? "" : String.valueOf(existing.getHsmon1()));
            txtMon2.setText(Objects.toString(existing.getTh_mon2(), ""));
            txtHs2.setText(existing.getHsmon2() == null ? "" : String.valueOf(existing.getHsmon2()));
            txtMon3.setText(Objects.toString(existing.getTh_mon3(), ""));
            txtHs3.setText(existing.getHsmon3() == null ? "" : String.valueOf(existing.getHsmon3()));
            txtDoLech.setText(existing.getDolech() == null ? "" : existing.getDolech().toPlainString());
        } else {
            applyToHopDefaults(toHopList, cbToHop.getSelectedIndex(), txtMon1, txtMon2, txtMon3);
            cbToHop.addActionListener(e -> applyToHopDefaults(
                    toHopList, cbToHop.getSelectedIndex(), txtMon1, txtMon2, txtMon3));
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Ngành:"));
        form.add(cbNganh);
        form.add(new JLabel("Tổ hợp:"));
        form.add(cbToHop);
        form.add(new JLabel("Môn  1:"));
        form.add(txtMon1);
        form.add(new JLabel("HS môn 1:"));
        form.add(txtHs1);
        form.add(new JLabel("Môn 2:"));
        form.add(txtMon2);
        form.add(new JLabel("HS môn 2:"));
        form.add(txtHs2);
        form.add(new JLabel("Môn 3:"));
        form.add(txtMon3);
        form.add(new JLabel("HS môn 3:"));
        form.add(txtHs3);
        form.add(new JLabel("Độ lệch:"));
        form.add(txtDoLech);

        int option = JOptionPane.showConfirmDialog(this,
                form,
                isEdit ? "Sửa ngành - tổ hợp" : "Thêm ngành - tổ hợp",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return null;

        String manganh = nganhCodes.get(cbNganh.getSelectedIndex());
        String matohop = toHopCodes.get(cbToHop.getSelectedIndex());
        String tbKeys = manganh + "_" + matohop;

        Byte hs1 = parseByteOrNull(txtHs1.getText());
        if (hs1 == null && !txtHs1.getText().trim().isEmpty()) return null;
        Byte hs2 = parseByteOrNull(txtHs2.getText());
        if (hs2 == null && !txtHs2.getText().trim().isEmpty()) return null;
        Byte hs3 = parseByteOrNull(txtHs3.getText());
        if (hs3 == null && !txtHs3.getText().trim().isEmpty()) return null;

        BigDecimal dolech = parseDecimalOrNull(txtDoLech.getText());
        if (dolech == null && !txtDoLech.getText().trim().isEmpty()) return null;

        NganhToHop target = isEdit ? existing : new NganhToHop();
        if (!isEdit) {
            target.setManganh(manganh);
            target.setMatohop(matohop);
            target.setTb_keys(tbKeys);
        }

        target.setTh_mon1(blankToNull(txtMon1.getText()));
        target.setHsmon1(hs1);
        target.setTh_mon2(blankToNull(txtMon2.getText()));
        target.setHsmon2(hs2);
        target.setTh_mon3(blankToNull(txtMon3.getText()));
        target.setHsmon3(hs3);
        target.setDolech(dolech);

        return target;
    }

    private static int indexOfCode(List<String> codes, String code) {
        for (int i = 0; i < codes.size(); i++) {
            if (Objects.equals(codes.get(i), code)) return i;
        }
        return 0;
    }

    private static void applyToHopDefaults(List<ToHopMon> list, int index,
                                           JTextField txtMon1, JTextField txtMon2, JTextField txtMon3) {
        if (index < 0 || index >= list.size()) return;
        ToHopMon t = list.get(index);
        txtMon1.setText(Objects.toString(t.getMon1(), ""));
        txtMon2.setText(Objects.toString(t.getMon2(), ""));
        txtMon3.setText(Objects.toString(t.getMon3(), ""));
    }

    private static String blankToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    private static Byte parseByteOrNull(String value) {
        String trimmed = blankToNull(value);
        if (trimmed == null) return null;
        try {
            return Byte.parseByte(trimmed);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Hệ số môn không hợp lệ: " + trimmed,
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private static BigDecimal parseDecimalOrNull(String value) {
        String trimmed = blankToNull(value);
        if (trimmed == null) return null;
        try {
            return new BigDecimal(trimmed);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Độ lệch không hợp lệ: " + trimmed,
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
}