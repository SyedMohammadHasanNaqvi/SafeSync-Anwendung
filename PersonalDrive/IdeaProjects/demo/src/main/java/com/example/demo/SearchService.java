package com.example.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchService {
    
    // Method to read files from the directory
    public List<File> readFiles(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            return List.of(directory.listFiles());
        } else {
            System.out.println("Directory  not found: " + directoryPath);
            return new ArrayList<>();
        }
    }

    // Method to filter files based on query
    public List<File> filterFiles(String query, List<File> files) {
        return files.stream()
                .filter(file -> file.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Method to perform full search process
    public List<List<File>> searchFiles (String query, String uploadDir, String downloadDir) {
        List<File> uploadFiles = readFiles(uploadDir);
        List<File> downloadFiles = readFiles(downloadDir);

        List<File> filteredUploadFiles = filterFiles(query, uploadFiles);
        List<File> filteredDownloadFiles = filterFiles(query, downloadFiles);

        List<List<File>> result = new ArrayList<>();
        result.add(filteredUploadFiles);
        result.add(filteredDownloadFiles);

        return result;
    }
}