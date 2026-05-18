package com.xettuyen.main;

import com.xettuyen.service.LoginService;
import com.xettuyen.ui.MainFrame;
import com.xettuyen.ui.dialog.LoginDialog;

import javax.swing.*;

/**
 * ApplicationController - Manages login and main app flow.
 */
public class ApplicationController implements LoginDialog.LoginCallback, MainFrame.LogoutCallback {

    private JFrame ownerFrame;
    private LoginService.LoginResponse currentUser;
    private MainFrame mainFrame;

    public void start() {
        showLoginDialog();
    }

    private void showLoginDialog() {
        if (ownerFrame == null) {
            ownerFrame = new JFrame();
            ownerFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        }
        LoginDialog dialog = new LoginDialog(ownerFrame, this);
        dialog.setVisible(true);
    }

    private void openMainFrame() {
        if (currentUser == null) {
            System.exit(0);
            return;
        }
        mainFrame = new MainFrame(currentUser, this);
        mainFrame.setVisible(true);
    }

    @Override
    public void onLoginSuccess(LoginService.LoginResponse response) {
        this.currentUser = response;
        openMainFrame();
    }

    @Override
    public void onLoginFailed(String message) {
        System.exit(0);
    }

    @Override
    public void onLogout() {
        currentUser = null;
        if (mainFrame != null) {
            mainFrame.dispose();
            mainFrame = null;
        }
        showLoginDialog();
    }
}
