package com.xettuyen.ui.panel;

import com.xettuyen.entity.DiemThi;
import com.xettuyen.service.impl.DiemThiService;
import com.xettuyen.service.imports.DiemThiImportService;
import com.xettuyen.service.imports.ImportResult;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import com.xettuyen.ui.util.PaginationPanel;
import com.xettuyen.ui.util.TableHeaders;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class DiemThiPanel extends JPanel {

    private final DiemThiService service = new DiemThiService();
    private JTable table;
    private DefaultTableModel tableModel;
    private PaginationPanel paginationPanel;
    private int currentPage = 1;

    public DiemThiPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));

        JLabel title = new JLabel("QUẢN LÝ ĐIỂM THI");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(title, BorderLayout.WEST);

        // Nút Import
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        JButton btnImport = new JButton("Import Excel");
        btnImport.addActionListener(e -> importExcel());
        btnPanel.add(btnRefresh);
        btnPanel.add(btnImport);
        topPanel.add(btnPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);


        tableModel = new DefaultTableModel(TableHeaders.DIEM_THI, 0) {
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
        List<DiemThi> list = service.getPage(currentPage);
        int totalPages = service.getTotalPages();
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
}