package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.io.FileWriter;
import java.security.MessageDigest;

public class Registration {
    public void CreateFile() {
        try {
            File geheimnis = new File("user&pass.txt");  //Path may be changed to desired location
            if (geheimnis.createNewFile()) {
                System.out.println("File created: " + geheimnis.getName());
            } else {
                System.out.println("File already exists");
            }
        } catch (IOException e) {
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }

    private static boolean IsUserExist(String username) {
        try (Scanner lesen = new Scanner (new File("user&pass.txt"))) {
            while (lesen.hasNextLine()) {
                String userpass = lesen.nextLine();
                String[] user = userpass.split(",");
                if (user[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("An error occured while reading the file");
            e.printStackTrace();
        }
        return false;
    }

    // public static boolean WriteUser (String username, String password) {
    //     if (IsUserExist(username.toLowerCase().replaceAll("\\s", ""))) {
    //         System.out.println("This username: " + username + " is taken");
    //         return false;
    //     } else {
    //     try (FileWriter schreiben = new FileWriter("user&pass.txt", true)) {
    //         schreiben.write(username.toLowerCase().replaceAll("\\s", "") + "," + password + "\n");
    //         return true;
    //     } catch (IOException e) {
    //         System.out.println("An error occured while writing to the file");
    //         e.printStackTrace();
    //         return false;
    //     }
    // }
    // }

    public static boolean WriteUser(String username, String password) {
        // Check if the user already exists
        if (IsUserExist(username.toLowerCase().replaceAll("\\s", ""))) {
            System.out.println("This username: " + username + " is taken");
            return false;
        } else {
            try (FileWriter schreiben = new FileWriter("user&pass.txt", true)) {
                // Hash the password
                String hashedPassword = hashPassword(password);
                if (hashedPassword == null) {
                    System.out.println("An error occurred while hashing the password.");
                    return false;
                }

                // Write the username and hashed password to the file
                schreiben.write(username.toLowerCase().replaceAll("\\s", "") + "," + hashedPassword + "\n");
                return true;
            } catch (IOException e) {
                System.out.println("An error occurred while writing to the file");
                e.printStackTrace();
                return false;
            }
        }
    }

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

    // public static void main(String[] args) {
    //     Registration reg = new Registration();
    //     reg.CreateFile();
    //     reg.WriteUser("smhn", "pass123");
    //     reg.WriteUser("ali", "yoyo");
    //     //reg.WriteUser("ubaid", "123");
    // }
}