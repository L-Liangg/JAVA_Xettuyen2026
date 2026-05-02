package com.xettuyen.ui.panel;

import com.xettuyen.entity.NganhToHop;
import com.xettuyen.service.impl.NganhToHopService;
import com.xettuyen.service.imports.ImportResult;
import com.xettuyen.service.imports.NganhToHopImportService;
import com.xettuyen.ui.dialog.ImportProgressDialog;
import com.xettuyen.ui.util.PaginationPanel;
import com.xettuyen.ui.util.TableHeaders;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class NganhToHopPanel extends JPanel {

    private final NganhToHopService service = new NganhToHopService();
    private JTable table;
    private DefaultTableModel tableModel;
    private PaginationPanel paginationPanel;
    private int currentPage = 1;

    public NganhToHopPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));

        JLabel title = new JLabel("QUẢN LÝ NGÀNH - TỔ HỢP");
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

        tableModel = new DefaultTableModel(TableHeaders.NGANH_TOHOP, 0) {
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
        List<NganhToHop> list = service.getPage(currentPage);
        int totalPages = service.getTotalPages();
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
}