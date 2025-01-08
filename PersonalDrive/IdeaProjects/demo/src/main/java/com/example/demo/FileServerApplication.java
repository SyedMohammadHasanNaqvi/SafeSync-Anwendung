package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication  // Indicates this is a Spring Boot application
public class FileServerApplication {

    public static void main(String[] args) {
        // Launches the Spring Boot application
        SpringApplication.run(FileServerApplication.class, args);
    }
}