package com.xettuyen.ui.panel;

import com.xettuyen.entity.BangQuyDoi;
import com.xettuyen.service.impl.BangQuyDoiService;
import com.xettuyen.ui.util.PaginationPanel;
import com.xettuyen.ui.util.TableHeaders;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BangQuyDoiPanel extends JPanel {

    private final BangQuyDoiService service = new BangQuyDoiService();
    private JTable table;
    private DefaultTableModel tableModel;
    private PaginationPanel paginationPanel;
    private int currentPage = 1;

    public BangQuyDoiPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initUI();
        loadData();
    }

    private void initUI() {
        JLabel title = new JLabel("QUẢN LÝ BẢNG QUY ĐỔI");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(TableHeaders.BANG_QUY_DOI, 0) {
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
        List<BangQuyDoi> list = service.getPage(currentPage);
        int totalPages = service.getTotalPages();
        paginationPanel.update(currentPage, totalPages);

        tableModel.setRowCount(0);
        for (BangQuyDoi b : list) {
            tableModel.addRow(new Object[]{
                    b.getD_phuongthuc(), b.getD_tohop(), b.getD_mon(),
                    b.getD_diema(), b.getD_diemb(), b.getD_diemc(),
                    b.getD_diemd(), b.getD_maquydoi(), b.getD_phanvi()
            });
        }
    }
}