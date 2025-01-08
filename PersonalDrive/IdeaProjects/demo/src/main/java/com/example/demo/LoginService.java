package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.FileReader;
import java.io.BufferedReader;


public class LoginService {
    private final File userFile;        // File to store the user credentials

    // Constructor initializes the path to the user credentials file.
    public LoginService() {
        this.userFile = new File("C:/GitHub/Softwareentwicklung/user&pass.txt");
    }

    // Checks if the provided username and password match an entry in the user file
    public boolean gegenprufen(String username, String password) {
        try (BufferedReader lesen = new BufferedReader(new FileReader(userFile))) {
            String line;

            // Hash the input password for comparison
            String hashedInputPassword = hashPassword(password);
            if (hashedInputPassword == null) {
                System.out.println("Error while hashing the input password.");
                return false;
            }
            
            // Read each line in the file and compare username and password
            while ((line = lesen.readLine()) != null) {
                String[] credentials = line.split(",");

                if (credentials.length == 2) {
                    String benutzername = credentials[0];
                    String passwort = credentials[1];

                    // Compare username and hashed password
                    if (benutzername.equals(username.toLowerCase().replaceAll("\\s", "")) && passwort.equals(hashedInputPassword)) {
                        System.out.println("Username & Password are matched");
                        return true;
                    }
                }
            }
            System.out.println("Username & Password aren't matched");

        } catch (IOException e) {
            System.out.println("An error occurred while reading the user&pass.txt: " + e.getMessage());
        }

        return false;
    }

    // Hashes the given password using SHA-256 and returns the hashed string
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}