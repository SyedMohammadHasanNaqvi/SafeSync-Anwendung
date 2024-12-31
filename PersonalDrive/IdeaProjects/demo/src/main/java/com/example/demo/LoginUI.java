package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI extends JFrame{
    private JTextField usernameField;
    private JPasswordField passwordField;
    private LoginService loginService;

    public LoginUI() {
        loginService = new LoginService();

        setTitle("Login Page");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel headerLabel = new JLabel("Welcome to SafeSync", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(new Color(60, 80, 180));
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Username: ", SwingConstants.RIGHT));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password: ", SwingConstants.RIGHT));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);

        JButton loginButton = new JButton("Log In");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 14));
        loginButton.addActionListener(new LoginActionListener());
        // loginButton.addActionListener(e -> handleLogin());
        buttonPanel.add(loginButton);

        JButton registrationButton = new JButton("Register");
        registrationButton.setFont(new Font("Arial", Font.PLAIN, 14));
        registrationButton.addActionListener(new RegisterActionLister());
        // registrationButton.addActionListener(e -> RegistrationActionListener());
        buttonPanel.add(registrationButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // JPanel panel = new JPanel();
        // panel.setLayout(new GridLayout(3,2));

        // panel.add(new JLabel("Username: "));
        // usernameField = new JTextField();
        // panel.add(usernameField);

        // panel.add(new JLabel("Password: "));
        // passwordField = new JPasswordField();
        // panel.add(passwordField);

        // JButton loginButton = new JButton("Log In");
        // loginButton.addActionListener(new LoginActionListener());
        // panel.add(loginButton);

        // JButton registrationButton = new JButton("Register");
        // registrationButton.addActionListener(new RegisterActionLister());
        // panel.add(registrationButton);

        add(mainPanel);
    }



    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (usernameField.getText().isEmpty() || passwordField.getPassword().toString().isEmpty()) {
                JOptionPane.showMessageDialog(LoginUI.this, "Please fill all the fields");
                
            }
            else if (loginService.gegenprufen(username, password)) {
                JOptionPane.showMessageDialog(LoginUI.this, "Login Successful!");
                dispose();

                FrontendUI neuesFenster = new FrontendUI();
                neuesFenster.setLocationRelativeTo(null);
                neuesFenster.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(LoginUI.this, "Invalid username or password");
            }
        }
    }



    private class RegisterActionLister implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String benutzername = usernameField.getText();
            String passwort = new String(passwordField.getPassword());
            if (usernameField.getText().isEmpty() || passwordField.getPassword().toString().isEmpty()) {
                JOptionPane.showMessageDialog(LoginUI.this, "Please fill all the fields");
                
            }
            else if(Registration.WriteUser(benutzername, passwort)) {
                JOptionPane.showMessageDialog(LoginUI.this, "Successfully Registered");
            } else {
                JOptionPane.showMessageDialog(LoginUI.this, "User already exists");
            }
        }
    }



        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                LoginUI loginUI = new LoginUI();
                loginUI.setLocationRelativeTo(null);
                loginUI.setVisible(true);
            });
    }
}