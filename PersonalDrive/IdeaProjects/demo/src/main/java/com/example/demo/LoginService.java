package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;

public class LoginService {
    private final File userFile;

    public LoginService() {
        this.userFile = new File("C:/Users/syedm/Downloads/IdeaProjects/user&pass.txt");
    }

    public boolean gegenprufen(String username, String password) {
        try (BufferedReader lesen = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = lesen.readLine()) != null) {
                String[] credentials = line.split(",");

                if (credentials.length == 2) {
                    String benutzername = credentials[0];
                    String passwort = credentials[1];

                    if (benutzername.equals(username) && passwort.equals(password)) {
                        System.out.println("Username & Password are matched");
                        return true;
                    }
                }
            }
            System.out.println("Username & Password aren't matched");

        } catch (IOException e) {
                System.out.println("An error occured while reading the user&pass.txt: " + e.getMessage());
            }

            return false;
        }

        public static void main (String[] args) {
        LoginService ls = new LoginService();
        ls.gegenprufen("smhn", "123");
        }
    }