package com.example.demo;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@RestController
public class FileController {

    // Use this directory as a server dummy storage
    private final String uploadDir = "/home/sestudent/IdeaProjects/demo/src/main/resources/serverStorage";  // Change this to the desired path

    // !!! Ensure that the upload is functioning and exeptions are captured
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        // Insert your code here



        return "File uploaded successfully to ";
    }


    // !!! Ensure that the download is functioning and exeptions are captured
    @GetMapping("/download")
    public byte[] downloadFile(@RequestParam String file) throws IOException {
        if ( true ) {
            return null;
        } else {
            throw new IOException("File not found");
        }
    }

    // !!! Ensure that this method lists all files from a specified directory for the UI
    @GetMapping("/files")
    public java.util.List<String> listFiles() throws IOException {
        // List all files in the specific directory
        return null;
    }
}
