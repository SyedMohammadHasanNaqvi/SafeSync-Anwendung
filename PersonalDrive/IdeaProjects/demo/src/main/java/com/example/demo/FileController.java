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

    // Use this directory as a server dummy storage
    private final String uploadDir = "D:\\University\\Software Engineering\\PATH\\";  // Change this to the desired path

    // !!! Ensure that the upload is functioning and exeptions are captured
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        // Insert your code here
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body("file is empty").toString();

        }
        try {
            // Get the file and save it somewhere
            File uploadedFile = new File(uploadDir + file.getOriginalFilename());
            file.transferTo(uploadedFile);

            return ResponseEntity.status(HttpStatus.SC_OK).body("File uploaded successfully: " + file.getOriginalFilename()).toString();
        }
        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Failed to upload file").toString();
        }


        // return "File uploaded successfully to naveen kumar ";
    }


    // !!! Ensure that the download is functioning and exeptions are captured
    @GetMapping("/download")
    public byte[] downloadFile(@RequestParam String file) throws IOException {
        System.out.println("naveen kumaer" + file);
        // if ( true ) {
        //     return null;
        // } else {
        //     throw new IOException("File not found");
        // }
        if ( file!= "" ) {
            File dir = new File(uploadDir+file);
        // try{
        if(dir.exists()){
            Resource resource = new UrlResource(dir.toURI());
            InputStream inputStream = resource.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray(); // Return the byte array
        } 
        //  }/catch (Exception e){
        //     System.out.println(e.getMessage());
        //     return null;
        // }
        } else {
            throw new IOException("File not found");
        }
                return null;
        
    }

    // !!! Ensure that this method lists all files from a specified directory for the UI
    @GetMapping("/files")
    public java.util.List<String> listFiles() throws IOException {
        // List all files in the specific directory
        return null;
    }
}
