package com.xettuyen.main;

import javax.swing.*;

/**
 * MainApp - Entry point for Desktop Application
 * 
 * Uses ApplicationController to manage login and main application flow
 * Connects to Laravel Backend API (http://127.0.0.1:8000)
 */
public class MainApp {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ApplicationController controller = new ApplicationController();
            controller.start();
        });
    }
}