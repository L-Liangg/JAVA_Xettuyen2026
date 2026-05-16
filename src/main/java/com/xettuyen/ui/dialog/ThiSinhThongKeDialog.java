package com.xettuyen.ui.dialog;

import com.xettuyen.service.impl.ThiSinhService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class ThiSinhThongKeDialog extends JDialog {

    private final ThiSinhService service;

    public ThiSinhThongKeDialog(JFrame parent, ThiSinhService service) {
        super(parent, "Thống kê thí sinh", true);
        this.service = service;
        setSize(500, 550);
        setLocationRelativeTo(parent);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ===== TỔNG THÍ SINH =====
        long total = service.countSearch("");
        JLabel lblTotal = new JLabel("Tổng số thí sinh: " + total);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblTotal);
        mainPanel.add(Box.createVerticalStrut(15));

        // ===== THEO ĐỐI TƯỢNG =====
        JLabel lblDoiTuong = new JLabel("Theo đối tượng:");
        lblDoiTuong.setFont(new Font("Arial", Font.BOLD, 13));
        lblDoiTuong.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblDoiTuong);
        mainPanel.add(Box.createVerticalStrut(5));

        Map<String, Long> doiTuongMap = service.countByDoiTuong();
        JScrollPane doiTuongScroll = new JScrollPane(createTable(doiTuongMap, "Đối tượng", "Số lượng"));
        doiTuongScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        doiTuongScroll.setPreferredSize(new Dimension(460, 150));
        doiTuongScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        mainPanel.add(doiTuongScroll);
        mainPanel.add(Box.createVerticalStrut(15));

        // ===== THEO KHU VỰC =====
        JLabel lblKhuVuc = new JLabel("Theo khu vực:");
        lblKhuVuc.setFont(new Font("Arial", Font.BOLD, 13));
        lblKhuVuc.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(lblKhuVuc);
        mainPanel.add(Box.createVerticalStrut(5));

        Map<String, Long> khuVucMap = service.countByKhuVuc();
        JScrollPane khuVucScroll = new JScrollPane(createTable(khuVucMap, "Khu vực", "Số lượng"));
        khuVucScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        khuVucScroll.setPreferredSize(new Dimension(460, 150));
        khuVucScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        mainPanel.add(khuVucScroll);

        // ===== NÚT ĐÓNG =====
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(btnClose);

        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JTable createTable(Map<String, Long> data, String col1, String col2) {
        DefaultTableModel model = new DefaultTableModel(new String[]{col1, col2}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        data.forEach((k, v) -> model.addRow(new Object[]{k, v}));
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }
}