package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class RegistrationUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    public RegistrationUI() {
        setTitle("Registration Page");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        
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
        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 25));
        JButton togglePasswordButton = createToggleButton(passwordField);
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(togglePasswordButton, BorderLayout.EAST);
        mainPanel.add(passwordPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Confirm Password: "), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JPanel confirmPasswordPanel = new JPanel(new BorderLayout());
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(new Dimension(200, 25));
        JButton toggleConfirmPasswordButton = createToggleButton(confirmPasswordField);
        confirmPasswordPanel.add(confirmPasswordField, BorderLayout.CENTER);
        confirmPasswordPanel.add(toggleConfirmPasswordButton, BorderLayout.EAST);
        mainPanel.add(confirmPasswordPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new RegisterActionListener());
        mainPanel.add(registerButton, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }


    private JButton createToggleButton(JPasswordField passwordField) {
        JButton toggleButton = new JButton("👁");
        toggleButton.setPreferredSize(new Dimension(30, 30));
        toggleButton.setFocusable(false);
        toggleButton.addActionListener(e -> {
            if (passwordField.getEchoChar() != '\u0000') {
                passwordField.setEchoChar('\u0000');
                toggleButton.setText("🙈");
            } else {
                passwordField.setEchoChar('•');
                toggleButton.setText("👁");
            }
        });
        
        return toggleButton;
    }


    private class RegisterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(RegistrationUI.this, "Username cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                usernameField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                return;
            }

            if (password.length() < 8) {
                JOptionPane.showMessageDialog(RegistrationUI.this, "Password must be at least 8 characters long", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(RegistrationUI.this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (Registration.WriteUser(username, password)) {
                JOptionPane.showMessageDialog(RegistrationUI.this, "Successfully Registered");
                dispose();
            } else {
                JOptionPane.showMessageDialog(RegistrationUI.this, "User already exists", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}