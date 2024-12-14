package com.example.demo;

import java.io.File;
import java.util.List;

public class SearchServiceTest {
    public static void main(String[] args) {
        SearchService ss = new SearchService();

        String dir = "C:\\Users\\syedm\\Desktop\\SMHN\\";

        List<File> files = ss.readFiles(dir);

        if (files.isEmpty()) {
            System.out.println("No files found in the directory: " + dir);
        } else {
            System.out.println("Files found in the directory: " + dir);
            for (File file : files) {
                System.out.println("- " + file.getName());
            }
        }

        String query = "leis";
        List<File> ff = ss.filterFiles(query, files);

        if (ff.isEmpty()) {
            System.out.println("No files found with the query: " + query);
        } else {
            System.out.println("Files matching the query \"" + query + "\": ");
            for (File file : ff) {
                System.out.println("- " + file.getName());
            }
        }
    }
}