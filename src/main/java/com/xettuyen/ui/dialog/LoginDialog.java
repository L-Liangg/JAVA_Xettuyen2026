package com.xettuyen.ui.dialog;

import com.xettuyen.repository.LoginRepository;
import com.xettuyen.service.LoginService;
import com.xettuyen.ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * LoginDialog - Login Form for Desktop Application
 * 
 * Authenticates user and opens main window on success
 */
public class LoginDialog extends JDialog {
    private static final Color OUTER_BG = Color.WHITE;

    private static final Color LEFT_BG = new Color(30, 42, 68);
    private static final Color RIGHT_BG = Color.WHITE;
    private static final Color BUTTON_BG = new Color(30, 42, 68);
    private static final Color BUTTON_HOVER_BG = new Color(41, 58, 92);
    private static final Color UNDERLINE = new Color(189, 189, 189);
    private static final Color PLACEHOLDER_COLOR = new Color(120, 120, 120);
    private static final Color INPUT_COLOR = Color.BLACK;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton closeButton;
    private JLabel errorLabel;
    private JLabel loadingLabel;
    private char defaultEchoChar;

    private String usernamePlaceholder = "Username";
    private String passwordPlaceholder = "Password";

    private LoginService loginService;
    private LoginRepository.LoginResponse lastLoginResponse;

    public LoginDialog(Frame owner) {
        super(owner, "Đăng nhập - Hệ thống Xét tuyển 2026", true);
        this.loginService = new LoginService();
        
        initUI();
        setupListeners();
    }
    
    private void initUI() {
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setSize(1080, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);
        getContentPane().setBackground(OUTER_BG);

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(OUTER_BG);
        outerPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(OUTER_BG);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        topBar.setBackground(OUTER_BG);
        closeButton = new JButton("×");
        closeButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        closeButton.setForeground(Color.BLACK);
        closeButton.setBackground(RIGHT_BG);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.setContentAreaFilled(true);
        closeButton.setOpaque(true);
        topBar.add(closeButton);

        JPanel splitPanel = new JPanel(new BorderLayout());
        splitPanel.setBackground(OUTER_BG);

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(LEFT_BG);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(70, 60, 60, 60));
        leftPanel.setPreferredSize(new Dimension(540, 0));

        JLabel welcomeLabel = new JLabel("WELCOME");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 60));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(welcomeLabel);

        leftPanel.add(Box.createVerticalStrut(10));

        JLabel subtitleLabel = new JLabel("Xét tuyển 2026");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(subtitleLabel);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(RIGHT_BG);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("LOGIN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField(usernamePlaceholder);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setForeground(PLACEHOLDER_COLOR);
        usernameField.setOpaque(false);
        usernameField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UNDERLINE));
        usernameField.setMaximumSize(new Dimension(310, 32));
        usernameField.setPreferredSize(new Dimension(310, 32));

        passwordField = new JPasswordField(passwordPlaceholder);
        defaultEchoChar = passwordField.getEchoChar();
        passwordField.setEchoChar((char) 0);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(PLACEHOLDER_COLOR);
        passwordField.setOpaque(false);
        passwordField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UNDERLINE));
        passwordField.setMaximumSize(new Dimension(310, 32));
        passwordField.setPreferredSize(new Dimension(310, 32));

        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(BUTTON_BG);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setBorder(BorderFactory.createEmptyBorder());
        loginButton.setOpaque(true);
        loginButton.setPreferredSize(new Dimension(310, 42));
        loginButton.setMaximumSize(new Dimension(310, 42));

        errorLabel = new JLabel();
        errorLabel.setForeground(new Color(192, 57, 43));
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loadingLabel = new JLabel();
        loadingLabel.setForeground(new Color(41, 128, 185));
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formPanel = new JPanel();
        formPanel.setBackground(RIGHT_BG);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonRow.setBackground(RIGHT_BG);
        buttonRow.setMaximumSize(new Dimension(310, 42));
        buttonRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonRow.add(loginButton);

        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(18));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(22));
        formPanel.add(buttonRow);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(loadingLabel);

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(formPanel);
        rightPanel.add(Box.createVerticalGlue());

        splitPanel.add(leftPanel, BorderLayout.WEST);
        splitPanel.add(rightPanel, BorderLayout.CENTER);

        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(splitPanel, BorderLayout.CENTER);

        outerPanel.add(mainPanel, BorderLayout.CENTER);
        add(outerPanel);
    }
    
    private void setupListeners() {
        // Login button
        loginButton.addActionListener(e -> performLogin());

        closeButton.addActionListener(e -> {
            dispose();
            System.exit(0);
        });

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(BUTTON_HOVER_BG);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(BUTTON_BG);
            }
        });

        // Enter key to login
        usernameField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) passwordField.requestFocus();
            }
        });
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) performLogin();
            }
        });
        
        // Close button
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals(usernamePlaceholder)) {
                    usernameField.setText("");
                    usernameField.setForeground(INPUT_COLOR);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (usernameField.getText().trim().isEmpty()) {
                    usernameField.setText(usernamePlaceholder);
                    usernameField.setForeground(PLACEHOLDER_COLOR);
                }
            }
        });

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String current = new String(passwordField.getPassword());

                if (current.equals(passwordPlaceholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(INPUT_COLOR);
                    passwordField.setEchoChar(defaultEchoChar);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                String current = new String(passwordField.getPassword());

                if (current.trim().isEmpty()) {
                    passwordField.setText(passwordPlaceholder);
                    passwordField.setForeground(PLACEHOLDER_COLOR);
                    passwordField.setEchoChar((char) 0);
                }
            }
        });
    }
    
    private void performLogin() {
        String usernameInput = usernameField.getText().trim();
        String passwordInput = new String(passwordField.getPassword()).trim();

        if (usernameInput.equals(usernamePlaceholder)) {
            usernameInput = "";
        }

        if (passwordInput.equals(passwordPlaceholder)) {
            passwordInput = "";
        }

        final String username = usernameInput;
        final String password = passwordInput;
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
            return;
        }
        
        // Disable buttons during login
        loginButton.setEnabled(false);
        closeButton.setEnabled(false);
        errorLabel.setText("");
        loadingLabel.setText("Đang đăng nhập...");
        
        // Call login in separate thread to avoid UI blocking
        new Thread(() -> {
            try {
                LoginRepository.LoginResponse response = loginService.login(username, password);
                
                // Update UI in EDT
                SwingUtilities.invokeLater(() -> {
                    loginButton.setEnabled(true);
                    closeButton.setEnabled(true);
                    loadingLabel.setText("");

                    if (response.isSuccess()) {
                        lastLoginResponse = response;
                        MainFrame mainFrame = new MainFrame(response);
                        mainFrame.setVisible(true);
                        dispose();
                    } else {
                        showError(response.getMessage());
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    loginButton.setEnabled(true);
                    closeButton.setEnabled(true);
                    loadingLabel.setText("");
                    showError("Lỗi: " + e.getMessage());
                });
            }
        }).start();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
    }
    
    public LoginRepository.LoginResponse getLoginResponse() {
        return lastLoginResponse;
    }
}
