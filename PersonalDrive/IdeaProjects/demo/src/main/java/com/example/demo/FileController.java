package com.example.demo;

import org.apache.hc.core5.http.HttpStatus;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;


@RestController
public class FileController {
    private final String uploadDir = "C:\\Users\\syedm\\Desktop\\SMHN\\";   // Directory for file upload

    // Checks if file exists in the upload directory
    public boolean fileExists(String fileName) {
        File file = new File(uploadDir, fileName);
        return file.exists();
    }

    // Endpoints to upload a file
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        // Print the uploaded file's name
        System.out.println("HELLOOOOOOOOOOOO" + file.getOriginalFilename());

        // Check if the file is empty or already exists
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("file is empty").toString();

        }
        else if (fileExists(file.getOriginalFilename())) {
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("File already exists").toString();
        } try {
            // Save the uploaded file
            File uploadedFile = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(uploadedFile);

            return ResponseEntity.status(HttpStatus.SC_OK).body("File uploaded successfully: " + file.getOriginalFilename()).toString();
        } catch (IOException e) {
            // Handle errors during file upload
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Failed to upload file").toString();
        }
    }

    // Endpoint to download a file
    @GetMapping("/download")
    public byte[] downloadFile(@RequestParam String file) throws IOException {
        // Print requested file name
        System.out.println("Group 05" + file);
        
        if ( file!= "" ) {
            // Check if the file exists and download it
            File dir = new File(uploadDir+file);
        if(dir.exists()){
            Resource resource = new UrlResource(dir.toURI());
            InputStream inputStream = resource.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Read the file content and write to output steam
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } 
        
        } else {
            throw new IOException("File not found");    // File not found error
        }
                return null;    // Return null if file doesn't exist
        
    }
    
    // Endpoints to list all files
    @GetMapping("/files")
    public java.util.List<String> listFiles() throws IOException {
        return null;
    }
}