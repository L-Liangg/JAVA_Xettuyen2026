package com.xettuyen.ui.util;

import javax.swing.*;
import java.awt.*;

public class PaginationPanel extends JPanel {

    private JButton btnPrev;
    private JButton btnNext;
    private JLabel lblPage;
    private JTextField txtJump;
    private JButton btnJump;
    private int currentPage = 1;
    private int totalPages = 1;
    private Runnable onPageChange;

    public PaginationPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER));

        btnPrev = new JButton("◀");
        btnNext = new JButton("▶");
        lblPage = new JLabel("Trang 1 / 1");
        txtJump = new JTextField(5);
        btnJump = new JButton("Đi");

        btnPrev.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateLabel();
                if (onPageChange != null) onPageChange.run();
            }
        });

        btnNext.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                updateLabel();
                if (onPageChange != null) onPageChange.run();
            }
        });

        // Nhảy đến trang
        Runnable jump = () -> {
            try {
                int page = Integer.parseInt(txtJump.getText().trim());
                if (page < 1) page = 1;
                if (page > totalPages) page = totalPages;
                currentPage = page;
                txtJump.setText("");
                updateLabel();
                if (onPageChange != null) onPageChange.run();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Số trang không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        };

        btnJump.addActionListener(e -> jump.run());
        txtJump.addActionListener(e -> jump.run()); // Enter cũng nhảy được

        add(btnPrev);
        add(lblPage);
        add(btnNext);
        add(Box.createHorizontalStrut(20));
        add(new JLabel("Đến trang:"));
        add(txtJump);
        add(btnJump);
    }

    public void setOnPageChange(Runnable callback) { this.onPageChange = callback; }

    public void update(int currentPage, int totalPages) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        updateLabel();
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    public int getCurrentPage() { return currentPage; }

    public void reset() {
        currentPage = 1;
        updateLabel();
    }

    private void updateLabel() {
        lblPage.setText("Trang " + currentPage + " / " + totalPages);
    }
}