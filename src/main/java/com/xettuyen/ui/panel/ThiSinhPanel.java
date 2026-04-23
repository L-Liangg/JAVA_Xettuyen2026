package com.xettuyen.ui.panel;

import com.xettuyen.entity.ThiSinh;
import com.xettuyen.service.imports.ImportResult;
import com.xettuyen.service.imports.ThiSinhImportService;
import com.xettuyen.service.impl.ThiSinhService;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import com.xettuyen.ui.util.PaginationPanel;
import com.xettuyen.ui.util.TableHeaders;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
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
        txtSearch = new JTextField(20);
        JButton btnSearch = new JButton("Tìm kiếm");
        JButton btnReset = new JButton("Làm mới");
        btnSearch.addActionListener(e -> search());
        btnReset.addActionListener(e -> reset());
        txtSearch.addActionListener(e -> search());
        searchPanel.add(new JLabel("Tìm theo CCCD / Họ tên:"));
        JButton btnImport = new JButton("Import Excel");
        btnImport.addActionListener(e -> importExcel());
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReset);
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
        List<ThiSinh> list = service.search(currentKeyword, currentPage);
        int totalPages = Math.max(1, service.getTotalPages(currentKeyword));
        paginationPanel.update(currentPage, totalPages);

        tableModel.setRowCount(0);
        for (ThiSinh ts : list) {
            tableModel.addRow(new Object[]{
                    ts.getCccd(), ts.getSobaodanh(), ts.getHo(), ts.getTen(),
                    ts.getNgay_sinh(), ts.getDien_thoai(), ts.getGioi_tinh(),
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