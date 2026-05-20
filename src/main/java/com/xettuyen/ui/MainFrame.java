package com.xettuyen.ui;

import com.xettuyen.repository.LoginRepository;
import com.xettuyen.ui.dialog.LoginDialog;
import com.xettuyen.ui.panel.*;
import com.xettuyen.ui.util.RoundedButton;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private LoginRepository.LoginResponse loginResponse;
    private JButton selectedButton = null;

    public MainFrame(LoginRepository.LoginResponse response) {
        this.loginResponse = response;
        
        setTitle("Hệ thống Xét tuyển 2026 - " + response.getFullName());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1420, 820);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1200, 700));

        initUI();
    }

    private void initUI() {
        // Menu bên trái (Sidebar)
        JPanel menuPanel = createMenuPanel();

        // Content panel
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

        // Hiển thị panel mặc định
        cardLayout.show(contentPanel, "thi_sinh");
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(240, 0));
        panel.setBackground(new Color(249, 250, 251));   // Nền sáng
        panel.setBorder(BorderFactory.createEmptyBorder(20, 15, 15, 15));

        // Title
        JLabel title = new JLabel("QUẢN LÝ TUYỂN SINH");
        title.setForeground(new Color(15, 23, 42));
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));

        // User Info
        JLabel userLabel = new JLabel("Xin chào, " + loginResponse.getFullName());
        userLabel.setForeground(new Color(55, 65, 81));
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(userLabel);

        JLabel roleLabel = new JLabel(loginResponse.getRoleName());
        roleLabel.setForeground(new Color(100, 116, 139));
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(roleLabel);

        panel.add(Box.createVerticalStrut(25));

        // Menu Items
        addMenuItem(panel, "Thí sinh", "thi_sinh", true);
        addMenuItem(panel, "Ngành", "nganh", false);
        addMenuItem(panel, "Tổ hợp môn", "tohop_mon", false);
        addMenuItem(panel, "Ngành - Tổ hợp", "nganh_tohop", false);
        addMenuItem(panel, "Điểm thi", "diem_thi", false);
        addMenuItem(panel, "Điểm DGNL/VSAT", "diem_thi_dgnl_vsat", false);
        addMenuItem(panel, "Điểm cộng", "diem_cong", false);
        addMenuItem(panel, "Nguyện vọng", "nguyen_vong", false);
        addMenuItem(panel, "Bảng quy đổi", "bang_quy_doi", false);

        panel.add(Box.createVerticalGlue());

        // Logout Button
        RoundedButton logoutButton = new RoundedButton("Đăng xuất");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn đăng xuất?",
                    "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
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

    private void addMenuItem(JPanel panel, String label, String cardName, boolean isSelected) {
        JButton btn = new JButton(label);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        if (isSelected) {
            btn.setBackground(new Color(224, 242, 254));
            btn.setForeground(new Color(0, 122, 255));
            selectedButton = btn;
        } else {
            btn.setBackground(new Color(249, 250, 251));
            btn.setForeground(new Color(51, 65, 85));
        }

        // Hover Effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn != selectedButton) {
                    btn.setBackground(new Color(241, 245, 255));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn != selectedButton) {
                    btn.setBackground(new Color(249, 250, 251));
                }
            }
        });

        // Click Action
        btn.addActionListener(e -> {
            if (selectedButton != null) {
                selectedButton.setBackground(new Color(249, 250, 251));
                selectedButton.setForeground(new Color(51, 65, 85));
            }
            btn.setBackground(new Color(224, 242, 254));
            btn.setForeground(new Color(0, 122, 255));
            selectedButton = btn;

            cardLayout.show(contentPanel, cardName);
        });

        panel.add(btn);
        panel.add(Box.createVerticalStrut(4));
    }
}