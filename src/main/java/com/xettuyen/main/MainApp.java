package com.xettuyen.main;

import com.xettuyen.ui.dialog.LoginDialog;

import javax.swing.*;

public class MainApp {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog dialog = new LoginDialog(null);
            dialog.setVisible(true);
        });
    }
}