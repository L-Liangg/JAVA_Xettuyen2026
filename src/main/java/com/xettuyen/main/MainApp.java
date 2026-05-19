package com.xettuyen.main;


import javax.swing.*;

public class MainApp {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ApplicationController controller = new ApplicationController();
            controller.start();
        });
    }
}