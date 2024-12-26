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

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3,2));

        panel.add(new JLabel("Username: "));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password: "));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Log In");
        loginButton.addActionListener(new LoginActionListener());
        panel.add(loginButton);

        JButton registrationButton = new JButton("Register");
        registrationButton.addActionListener(new RegisterActionLister());
        panel.add(registrationButton);

        add(panel);
    }



    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (loginService.gegenprufen(username, password)) {
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
            
            if(Registration.WriteUser(benutzername, passwort)) {
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