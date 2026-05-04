package com.xettuyen.ui.panel;

import com.xettuyen.entity.ThiSinh;
import com.xettuyen.service.imports.ImportResult;
import com.xettuyen.service.imports.ThiSinhImportService;
import com.xettuyen.service.impl.ThiSinhService;
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

public class ThiSinhPanel extends JPanel {

    private final ThiSinhService service = new ThiSinhService();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private PaginationPanel paginationPanel;
    private int currentPage = 1;
    private String currentKeyword = "";

    public ThiSinhPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        // Top  
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        JLabel title = new JLabel("QUẢN LÝ THÍ SINH");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(title, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        PlaceholderTextField searchField = new PlaceholderTextField("Tìm kiếm theo CCCD / Họ tên", 20);
        searchField.setPlaceholderColor(Color.GRAY);
        txtSearch = searchField;
        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnReset = new JButton("Làm mới");
        JButton btnAdd = new JButton("Thêm mới");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");
        btnSearch.addActionListener(e -> search());
        btnReset.addActionListener(e -> reset());
        txtSearch.addActionListener(e -> search());
        btnAdd.addActionListener(e -> addThiSinh());
        btnEdit.addActionListener(e -> updateThiSinh());
        btnDelete.addActionListener(e -> deleteThiSinh());
        JButton btnImport = new JButton("Import Excel");
        btnImport.addActionListener(e -> importExcel());
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReset);
        searchPanel.add(btnAdd);
        searchPanel.add(btnEdit);
        searchPanel.add(btnDelete);
        searchPanel.add(btnImport);
        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(TableHeaders.THI_SINH, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Pagination
        paginationPanel = new PaginationPanel();
        paginationPanel.setOnPageChange(() -> {
            currentPage = paginationPanel.getCurrentPage();
            loadData();
        });
        add(paginationPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        int totalPages = Math.max(1, service.getTotalPages(currentKeyword));
        if (currentPage > totalPages) currentPage = totalPages;

        List<ThiSinh> list = service.search(currentKeyword, currentPage);
        paginationPanel.update(currentPage, totalPages);

        tableModel.setRowCount(0);
        for (ThiSinh ts : list) {
            tableModel.addRow(new Object[]{
                    ts.getCccd(), ts.getSobaodanh(), ts.getHo(), ts.getTen(),
                    ts.getNgay_sinh(), ts.getDien_thoai(), ts.getPassword(), ts.getGioi_tinh(),
                    ts.getEmail(), ts.getNoi_sinh(), ts.getUpdated_at(),
                    ts.getDoi_tuong(), ts.getKhu_vuc()
            });
        }
    }

    private void search() {
        currentKeyword = txtSearch.getText().trim();
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private void reset() {
        txtSearch.setText("");
        currentKeyword = "";
        currentPage = 1;
        paginationPanel.reset();
        loadData();
    }

    private void addThiSinh() {
        try {
            ThiSinh newThiSinh = showThiSinhForm(null);
            if (newThiSinh == null) return;

            if (newThiSinh.getCccd() == null || newThiSinh.getCccd().isBlank()) {
                JOptionPane.showMessageDialog(this, "CCCD không được để trống.",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (service.findByCccd(newThiSinh.getCccd().trim()) != null) {
                JOptionPane.showMessageDialog(this, "CCCD đã tồn tại.",
                        "Trùng dữ liệu", JOptionPane.WARNING_MESSAGE);
                return;
            }

            service.save(newThiSinh);
            JOptionPane.showMessageDialog(this, "Đã thêm thí sinh thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

            // Reset để tránh trường hợp đang lọc/tại trang khác nên không thấy dữ liệu vừa thêm
            txtSearch.setText("");
            currentKeyword = "";
            currentPage = 1;
            paginationPanel.reset();
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi thêm thí sinh:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateThiSinh() {
        try {
            String selectedCccd = getSelectedCccd();
            if (selectedCccd == null) return;

            ThiSinh existing = service.findByCccd(selectedCccd);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thí sinh theo CCCD: " + selectedCccd,
                        "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ThiSinh updated = showThiSinhForm(existing);
            if (updated == null) return;

            service.update(updated);
            JOptionPane.showMessageDialog(this, "Đã cập nhật thí sinh thành công.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi khi cập nhật thí sinh:\n" + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteThiSinh() {
        try {
            String selectedCccd = getSelectedCccd();
            if (selectedCccd == null) return;

            ThiSinh existing = service.findByCccd(selectedCccd);
            if (existing == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thí sinh theo CCCD: " + selectedCccd,
                "Không tìm thấy", JOptionPane.WARNING_MESSAGE);
            return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa thí sinh CCCD " + selectedCccd + "?\nDữ liệu liên quan (điểm thi, điểm cộng, nguyện vọng) có thể bị xóa theo (CASCADE).",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            service.delete(existing);
            JOptionPane.showMessageDialog(this, "Đã xóa thí sinh thành công.",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Có lỗi khi xóa thí sinh:\n" + ex.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getSelectedCccd() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 thí sinh trong bảng.",
                    "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        Object cccdValue = tableModel.getValueAt(modelRow, 0);
        String cccd = Objects.toString(cccdValue, "").trim();
        if (cccd.isBlank()) {
            JOptionPane.showMessageDialog(this, "Dòng đã chọn không có CCCD hợp lệ.",
                    "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return cccd;
    }

    private ThiSinh showThiSinhForm(ThiSinh existing) {
        boolean isEdit = existing != null;

        JTextField txtCccd = new JTextField(20);
        JTextField txtSoBaoDanh = new JTextField(20);
        JTextField txtHo = new JTextField(20);
        JTextField txtTen = new JTextField(20);
        JTextField txtNgaySinh = new JTextField(20);
        JTextField txtDienThoai = new JTextField(20);
        JTextField txtPassword = new JTextField(20);
        JTextField txtGioiTinh = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        JTextField txtNoiSinh = new JTextField(20);
        JTextField txtDoiTuong = new JTextField(20);
        JTextField txtKhuVuc = new JTextField(20);

        if (isEdit) {
            txtCccd.setText(existing.getCccd());
            txtSoBaoDanh.setText(existing.getSobaodanh());
            txtHo.setText(existing.getHo());
            txtTen.setText(existing.getTen());
            txtNgaySinh.setText(existing.getNgay_sinh());
            txtDienThoai.setText(existing.getDien_thoai());
            txtPassword.setText(existing.getPassword());
            txtGioiTinh.setText(existing.getGioi_tinh());
            txtEmail.setText(existing.getEmail());
            txtNoiSinh.setText(existing.getNoi_sinh());
            txtDoiTuong.setText(existing.getDoi_tuong());
            txtKhuVuc.setText(existing.getKhu_vuc());
            txtCccd.setEditable(false);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("CCCD:"));
        form.add(txtCccd);
        form.add(new JLabel("Số báo danh:"));
        form.add(txtSoBaoDanh);
        form.add(new JLabel("Họ:"));
        form.add(txtHo);
        form.add(new JLabel("Tên:"));
        form.add(txtTen);
        form.add(new JLabel("Ngày sinh:"));
        form.add(txtNgaySinh);
        form.add(new JLabel("Điện thoại:"));
        form.add(txtDienThoai);
        form.add(new JLabel("Mật khẩu:"));
        form.add(txtPassword);
        form.add(new JLabel("Giới tính:"));
        form.add(txtGioiTinh);
        form.add(new JLabel("Email:"));
        form.add(txtEmail);
        form.add(new JLabel("Nơi sinh:"));
        form.add(txtNoiSinh);
        form.add(new JLabel("Đối tượng:"));
        form.add(txtDoiTuong);
        form.add(new JLabel("Khu vực:"));
        form.add(txtKhuVuc);

        int option = JOptionPane.showConfirmDialog(this,
                form,
                isEdit ? "Sửa thí sinh" : "Thêm thí sinh",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) return null;

        String cccd = txtCccd.getText().trim();
        if (cccd.isBlank()) {
            JOptionPane.showMessageDialog(this, "CCCD không được để trống.",
                "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        ThiSinh ts = isEdit ? existing : new ThiSinh();
        ts.setCccd(cccd);
        ts.setSobaodanh(txtSoBaoDanh.getText().trim());
        ts.setHo(txtHo.getText().trim());
        ts.setTen(txtTen.getText().trim());
        ts.setNgay_sinh(txtNgaySinh.getText().trim());
        ts.setDien_thoai(txtDienThoai.getText().trim());
        String password = txtPassword.getText().trim();
        ts.setPassword(password.isBlank() ? cccd : password);
        ts.setGioi_tinh(txtGioiTinh.getText().trim());
        ts.setEmail(txtEmail.getText().trim());
        ts.setNoi_sinh(txtNoiSinh.getText().trim());
        ts.setDoi_tuong(txtDoiTuong.getText().trim());
        ts.setKhu_vuc(txtKhuVuc.getText().trim());
        return ts;
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
                () -> new ThiSinhImportService().importFromExcel(file, progressDialog)
        );

        // Nếu có lỗi thì hiện chi tiết
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
}