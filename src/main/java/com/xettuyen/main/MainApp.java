package com.xettuyen.main;

import com.xettuyen.ui.dialog.LoginDialog;

import javax.swing.*;

/**
 * MainApp - Entry point for Desktop Application
 * 
 * Opens login dialog, then main app on success
 */
public class MainApp {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog dialog = new LoginDialog(null);
            dialog.setVisible(true);
        });
    }
}