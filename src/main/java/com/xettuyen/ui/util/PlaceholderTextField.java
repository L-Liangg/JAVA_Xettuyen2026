package com.xettuyen.ui.util;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class PlaceholderTextField extends JTextField {

    private String placeholder;
    private Color placeholderColor;

    public PlaceholderTextField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;

        // repaint when content changes so placeholder shows/hides immediately
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                repaint();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                repaint();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                repaint();
            }
        });

        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                repaint();
            }
        });
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public Color getPlaceholderColor() {
        return placeholderColor;
    }

    /**
     * Set to override the placeholder text color.
     * If null, the component will use Look & Feel inactive text color.
     */
    public void setPlaceholderColor(Color placeholderColor) {
        this.placeholderColor = placeholderColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (placeholder == null || placeholder.isBlank()) return;
        if (!getText().isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setFont(getFont());

            Color hintColor = placeholderColor;
            if (hintColor == null) hintColor = UIManager.getColor("textInactiveText");
            if (hintColor == null) hintColor = UIManager.getColor("TextField.inactiveForeground");
            if (hintColor == null) hintColor = getDisabledTextColor();
            if (hintColor == null) hintColor = getForeground();
            g2.setColor(hintColor);

            Insets ins = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int x = ins.left + 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(placeholder, x, y);
        } finally {
            g2.dispose();
        }
    }
}
