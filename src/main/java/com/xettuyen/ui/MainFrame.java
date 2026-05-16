package com.xettuyen.ui;

import com.xettuyen.ui.panel.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame() {
        setTitle("Hệ thống Xét tuyển 2026");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        // Menu bên trái
        JPanel menuPanel = createMenuPanel();

        // Content panel dùng CardLayout để chuyển giữa các panel
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(new ThiSinhPanel(), "thi_sinh");
        contentPanel.add(new NganhPanel(), "nganh");
        contentPanel.add(new ToHopMonPanel(), "tohop_mon");
        contentPanel.add(new NganhToHopPanel(), "nganh_tohop");
        contentPanel.add(new DiemThiPanel(), "diem_thi");
        contentPanel.add(new DiemCongPanel(), "diem_cong");
        contentPanel.add(new NguyenVongPanel(), "nguyen_vong");
        contentPanel.add(new BangQuyDoiPanel(), "bang_quy_doi");

        // Layout chính
        setLayout(new BorderLayout());
        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Hiển thị panel đầu tiên
        cardLayout.show(contentPanel, "thi_sinh");
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBackground(new Color(44, 62, 80));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel title = new JLabel("QUẢN LÝ TUYỂN SINH");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 13));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        // Menu items
        addMenuItem(panel, "Thí sinh",       "thi_sinh");
        addMenuItem(panel, "Ngành",           "nganh");
        addMenuItem(panel, "Tổ hợp môn",     "tohop_mon");
        addMenuItem(panel, "Ngành - Tổ hợp", "nganh_tohop");
        addMenuItem(panel, "Điểm thi",        "diem_thi");
        addMenuItem(panel, "Điểm cộng",       "diem_cong");
        addMenuItem(panel, "Nguyện vọng - Xét tuyển",     "nguyen_vong");
        addMenuItem(panel, "Bảng quy đổi",    "bang_quy_doi");

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void addMenuItem(JPanel panel, String label, String cardName) {
        JButton btn = new JButton(label);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        panel.add(btn);
        panel.add(Box.createVerticalStrut(5));
    }
}
