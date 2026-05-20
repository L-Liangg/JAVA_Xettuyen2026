package com.xettuyen.ui.util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {

    private int radius = 12;

    public RoundedButton(String text) {
        super(text);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Nền nút
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));

        // Hover border
        if (getModel().isRollover()) {
            g2.setColor(new Color(0, 0, 0, 30));
            g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, radius, radius));
        }

        super.paintComponent(g2);
        g2.dispose();
    }
}