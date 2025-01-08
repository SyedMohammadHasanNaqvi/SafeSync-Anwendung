package com.example.demo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;


public class SearchService {
    
    public List<File> readFiles(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            return List.of(directory.listFiles());
        } else {
            System.out.println("Directory  not found: " + directoryPath);
            return new ArrayList<>();
        }
    }


    public List<File> filterFiles(String query, List<File> files) {
        return files.stream()
                .filter(file -> file.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }


    public List<File> searchFiles (String query, String downloadDir) {
        List<File> downloadFiles = readFiles(downloadDir);
        List<File> filteredDownloadFiles = filterFiles(query, downloadFiles);

        List<File> result = new ArrayList<>();
        result = filteredDownloadFiles;

        return result;
    }


    public List<File> sortFilesBySize(String downloadDir, boolean largestFirst) {
        List<File> downloadFiles = readFiles(downloadDir);
        Comparator<File> sizeComparator = Comparator.comparingLong(File::length);

        if (largestFirst) {
            sizeComparator = sizeComparator.reversed();
        }

        List<File> sortedDownloadFiles = downloadFiles.stream()
                .sorted(sizeComparator)
                .collect(Collectors.toList());

        List<File> result = new ArrayList<>();
        result = sortedDownloadFiles;

        return result;
    }
    

    public List<File> searchFilesByDate(String dateString, String downloadDir) {
        List<File> downloadFiles = readFiles(downloadDir);
        List<File> filteredDownloadFiles = filterFilesByDate(dateString, downloadFiles);

        List<File> result = new ArrayList<>();
        result = filteredDownloadFiles;

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