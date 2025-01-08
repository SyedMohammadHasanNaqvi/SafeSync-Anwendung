package com.example.demo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class RegistrationUI extends JFrame {
    private JTextField usernameField;       // Text field to enter the username
    private JPasswordField passwordField;       // Password field to enter the password
    private JPasswordField confirmPasswordField;        // Password field to confirm the password

    // Constructor to initialize and set up the registration UI
    public RegistrationUI() {
        setTitle("Registration Page");      // Set the title of the window
        setSize(500, 400);      // Set the window size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);      // Dispose the window when closed
        setLocationRelativeTo(null);        // Center the window on the screen

        // Center the window on the screen
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username field setup
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(new JLabel("Username: "), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(200, 25));
        mainPanel.add(usernameField, gbc);

        // Password field setup with a toggle button to show/hide password
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

        // Confirm password field setup with a toggle button to show/hide password
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

        // Add the main panel to the center of the window
        add(mainPanel, BorderLayout.CENTER);
    }

    // Helper method to create a toggle button for password visibility
    private JButton createToggleButton(JPasswordField passwordField) {
        JButton toggleButton = new JButton("👁");       // Button to toggle password visibility
        toggleButton.setPreferredSize(new Dimension(30, 30));
        toggleButton.setFocusable(false);
        toggleButton.addActionListener(e -> {
            if (passwordField.getEchoChar() != '\u0000') {
                passwordField.setEchoChar('\u0000');        // Show password
                toggleButton.setText("🙈");     // Change icon to indicate password is visible
            } else {
                passwordField.setEchoChar('•');     // Hide password
                toggleButton.setText("👁");     // Change icon to indicate password is hidden
            }
        });
        
        return toggleButton;
    }


    // Action listener for the register button
    private class RegisterActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Check if username is empty
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(RegistrationUI.this, "Username cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                usernameField.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                return;
            }

            // Check if password meets the minimum length requirement
            if (password.length() < 8) {
                JOptionPane.showMessageDialog(RegistrationUI.this, "Password must be at least 8 characters long", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if password and confirm password match
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(RegistrationUI.this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

             // Call a method to save the new user
            if (Registration.WriteUser(username, password)) {
                JOptionPane.showMessageDialog(RegistrationUI.this, "Successfully Registered");
                dispose();
            } else {
                JOptionPane.showMessageDialog(RegistrationUI.this, "User already exists", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}