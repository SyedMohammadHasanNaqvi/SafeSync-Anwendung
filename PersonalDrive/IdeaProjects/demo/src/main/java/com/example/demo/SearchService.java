package com.example.demo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

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

    // public List<List<File>> searchFilesBySize(String uploadDir, String downloadDir, int maxSize) {
    //     List<File> uploadFiles = readFiles(uploadDir);
    //     List<File> downloadFiles = readFiles(downloadDir);

    //     List<File> filteredUploadFiles = filterFilesBySize(uploadFiles, maxSize);
    //     List<File> filteredDownloadFiles = filterFilesBySize(downloadFiles, maxSize);

    //     List<List<File>> result = new ArrayList<>();
    //     result.add(filteredUploadFiles);
    //     result.add(filteredDownloadFiles);

    //     return result;
    // }

    // private List<File> filterFilesBySize(List<File> files, int maxSizeBytes) {
    //     return files.stream()
    //             .filter(file -> file.length() <= maxSizeBytes)
    //             .collect(Collectors.toList());
    // }

    // Method to sort files by size
    public List<List<File>> sortFilesBySize(String uploadDir, String downloadDir, boolean largestFirst) {
        // Read files from the directories
        List<File> uploadFiles = readFiles(uploadDir);
        List<File> downloadFiles = readFiles(downloadDir);

        // Sort files by size
        Comparator<File> sizeComparator = Comparator.comparingLong(File::length);
        if (largestFirst) {
            sizeComparator = sizeComparator.reversed();  // Reverse the order for largest first
        }

        // Sort the lists
        List<File> sortedUploadFiles = uploadFiles.stream()
                .sorted(sizeComparator)
                .collect(Collectors.toList());

        List<File> sortedDownloadFiles = downloadFiles.stream()
                .sorted(sizeComparator)
                .collect(Collectors.toList());

        // Return the sorted lists
        List<List<File>> result = new ArrayList<>();
        result.add(sortedUploadFiles);
        result.add(sortedDownloadFiles);

        return result;
    }
    

    public List<List<File>> searchFilesByDate(String dateString, String uploadDir, String downloadDir) {
        List<File> uploadFiles = readFiles(uploadDir);
        List<File> downloadFiles = readFiles(downloadDir);

        List<File> filteredUploadFiles = filterFilesByDate(dateString, uploadFiles);
        List<File> filteredDownloadFiles = filterFilesByDate(dateString, downloadFiles);

        List<List<File>> result = new ArrayList<>();
        result.add(filteredUploadFiles);
        result.add(filteredDownloadFiles);

        return result;
    }

    private List<File> filterFilesByDate(String dateString, List<File> files) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        return files.stream()
                .filter(file -> {
                    long lastModified = file.lastModified();
                    String fileDate = dateFormat.format(new java.util.Date(lastModified));

                    return fileDate.equals(dateString);
                })
                .collect(Collectors.toList());
    }
}