package com.xettuyen.ui;

import com.xettuyen.repository.LoginRepository;
import com.xettuyen.ui.dialog.LoginDialog;
import com.xettuyen.ui.panel.*;
import com.xettuyen.service.LoginService;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private LoginRepository.LoginResponse loginResponse;

    public MainFrame(LoginRepository.LoginResponse response) {
        this.loginResponse = response;
        
        setTitle("Hệ thống Xét tuyển 2026 - " + response.getFullName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        contentPanel.add(new DiemThiDgnlVsatPanel(), "diem_thi_dgnl_vsat");
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

        // User info
        JLabel userLabel = new JLabel("Đăng nhập: " + loginResponse.getUsername());
        userLabel.setForeground(new Color(189, 195, 199));
        userLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(userLabel);
        
        JLabel roleLabel = new JLabel("Vai trò: " + loginResponse.getRoleName());
        roleLabel.setForeground(new Color(189, 195, 199));
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(roleLabel);
        
        panel.add(Box.createVerticalStrut(20));

        // Menu items
        addMenuItem(panel, "Thí sinh",       "thi_sinh");
        addMenuItem(panel, "Ngành",           "nganh");
        addMenuItem(panel, "Tổ hợp môn",     "tohop_mon");
        addMenuItem(panel, "Ngành - Tổ hợp", "nganh_tohop");
        addMenuItem(panel, "Điểm thi",        "diem_thi");
        addMenuItem(panel, "Điểm DGNL/VSAT",  "diem_thi_dgnl_vsat");
        addMenuItem(panel, "Điểm cộng",       "diem_cong");
        addMenuItem(panel, "Nguyện vọng",     "nguyen_vong");
        addMenuItem(panel, "Bảng quy đổi",    "bang_quy_doi");

        panel.add(Box.createVerticalGlue());
        
        // Logout button
        JButton logoutButton = new JButton("Đăng Xuất");
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setBackground(new Color(192, 57, 43));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginDialog dialog = new LoginDialog(null);
                    dialog.setVisible(true);
                });
            }
        });
        panel.add(logoutButton);

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