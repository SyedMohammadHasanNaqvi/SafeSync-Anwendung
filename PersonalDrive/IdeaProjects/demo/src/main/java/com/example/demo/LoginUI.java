package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LoginUI extends JFrame {
    private JTextField usernameField;       // Text field for entering the username
    private JPasswordField passwordField;       // Password field for entering the password
    private LoginService loginService;      // Service that handles login logic

    // Constructor initializes the login window, sets up layout and components
    public LoginUI() {
        loginService = new LoginService();      // Instantiate the login service

        setTitle("Login Page");     // Set the title of the window
        setSize(800, 500);      // Set the size of the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     // Close application on window close
        setLocationRelativeTo(null);        // Center the window on screen

        // Header panel for the title
        JPanel headerPanel = new JPanel();
        JLabel headerLabel = new JLabel("Welcome to SafeSync", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(new Color(60, 80, 180));
        headerPanel.add(headerLabel);

        // Main panel for the login form
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Layout setup for the login form using GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        mainPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Username: "), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 25));
        mainPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Password: "), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 25));
        mainPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new JButton("Log In");
        loginButton.addActionListener(new LoginActionListener());       // Action listener for login button
        mainPanel.add(loginButton, gbc);

        gbc.gridx = 1;
        JButton registrationButton = new JButton("Register");
        registrationButton.addActionListener(new RegisterActionLister());       // Action listener for register button
        mainPanel.add(registrationButton, gbc);

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    // ActionListener for the login button
    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Verify the credentials using the loginService
            if (loginService.gegenprufen(username, password)) {
                JOptionPane.showMessageDialog(LoginUI.this, "Login Successful!");
                dispose();

                FrontendUI neuesFenster = new FrontendUI();
                neuesFenster.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(LoginUI.this, "Invalid username or password");
            }
        }
    }
    
     // ActionListener for the register button
    private class RegisterActionLister implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(() -> {
                RegistrationUI registrationUI = new RegistrationUI();
                registrationUI.setVisible(true);
            });
        }
    }    
    
    // Main method to launch the login UI window
    public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                LoginUI loginUI = new LoginUI();
                loginUI.setLocationRelativeTo(null);
                loginUI.setVisible(true);
            });
    }
}