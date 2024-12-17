package com.example.demo;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import javax.swing.tree.TreePath;
import java.util.List;
import java.text.SimpleDateFormat;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.FileEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.classic.methods.HttpGet;


public class FrontendUI extends JFrame {

    private JTree uploadFileTree;
    private JTree downloadFileTree;
    private DefaultMutableTreeNode uploadRoot;
    private DefaultMutableTreeNode downloadRoot;
    private JButton uploadButton;
    private JButton downloadButton;
    private JSlider sizeSlider;
    private JLabel sizeRangeLabel;

    // Replace with your server URLs
    private String serverUrl = "http://localhost:8080/upload";
    private String downloadUrl = "http://localhost:8080/download?file=";

    public FrontendUI() {
        setTitle("File Server UI");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Search Bar & Button
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));

        JPanel searchSubPanel = new JPanel(new BorderLayout());
        searchSubPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        JTextField searchField = new JTextField();
        searchSubPanel.add(searchField, BorderLayout.CENTER);
        JButton searchButton = new JButton("Search");
        searchSubPanel.add(searchButton, BorderLayout.EAST);
        
        JPanel dateSubPanel = new JPanel(new BorderLayout());
        dateSubPanel.add(new Label("Enter Date (DD-MM-YYYY)"), BorderLayout.WEST);
        JTextField dateField = new JTextField();
        dateSubPanel.add(dateField, BorderLayout.CENTER);
        JButton dateButton = new JButton("Apply");
        dateSubPanel.add(dateButton, BorderLayout.EAST);

        searchPanel.add(searchSubPanel);
        searchPanel.add(dateSubPanel);

        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            performSearch(query);
        });

        dateButton.addActionListener(e -> {
            String dateString  = dateField.getText().trim();

            if (dateString.isEmpty()) {
                applyDefaultFilter();
                return;
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            dateFormat.setLenient(false);

            try {
                dateFormat.parse(dateString);
                applyDate(dateString);
            } catch (java.text.ParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        
        // Size Filter Panel
        JPanel sizeFilterPanel = new JPanel(new BorderLayout());
        sizeSlider = new JSlider(0, 1024000, 1024000);
        sizeSlider.setMajorTickSpacing(102400);
        sizeSlider.setMinorTickSpacing(10240);

        sizeRangeLabel = new JLabel("File Size: 0 KB - 1000 KB");
        sizeSlider.addChangeListener(e -> updateSizeLabel());
        sizeSlider.addChangeListener(e -> performSizeFilter());

        sizeFilterPanel.add(sizeRangeLabel, BorderLayout.NORTH);
        sizeFilterPanel.add(sizeSlider, BorderLayout.CENTER);


        // File Trees
        uploadRoot = new DefaultMutableTreeNode("Local Files (Upload)");
        uploadFileTree = new JTree(uploadRoot);
        JScrollPane uploadTreeScroll = new JScrollPane(uploadFileTree);

        downloadRoot = new DefaultMutableTreeNode("Server Files (Download)");
        downloadFileTree = new JTree(downloadRoot);
        JScrollPane downloadTreeScroll = new JScrollPane(downloadFileTree);

        // Buttons
        uploadButton = new JButton("Upload");
        downloadButton = new JButton("Download");

        uploadButton.addActionListener(this::uploadFile);
        downloadButton.addActionListener(this::downloadFile);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(uploadButton);
        buttonPanel.add(downloadButton);

        // Split pane to hold both file trees (upload and download)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(300);  // Initial divider position

        // Labels for file trees
        JLabel uploadLabel = new JLabel("Upload Directory (Local Files)", JLabel.CENTER);
        JLabel downloadLabel = new JLabel("Download Directory (Server Files)", JLabel.CENTER);

        // Drag and drop Functionality//
        class FileTransferHandler extends TransferHandler {
            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }
        
            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }
        
                try {
                    // Get the files from the dropped data
                    java.util.List<File> files = (java.util.List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    for (File file : files) {
                        // textArea.append(file.getAbsolutePath() + "\n");
                        uploadFileToServer(file);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        }

        // Upload Panel with label and tree
        JPanel uploadPanel = new JPanel(new BorderLayout());
        uploadPanel.add(uploadLabel, BorderLayout.NORTH);
        uploadPanel.add(uploadTreeScroll, BorderLayout.CENTER);
        uploadPanel.setTransferHandler(new FileTransferHandler());

        // Download Panel with label and tree
        JPanel downloadPanel = new JPanel(new BorderLayout());
        downloadPanel.add(downloadLabel, BorderLayout.NORTH);
        downloadPanel.add(downloadTreeScroll, BorderLayout.CENTER);

        // Add both panels to the split pane
        splitPane.setLeftComponent(uploadPanel);
        splitPane.setRightComponent(downloadPanel);

        // Main layout: SplitPane for file trees and buttons at the bottom
        getContentPane().add(searchPanel, BorderLayout.NORTH);
        getContentPane().add(sizeFilterPanel, BorderLayout.WEST);
        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();

        loadLocalFileSystem(new File("C:\\Users\\syedm\\Desktop\\SMHN\\"));  // Load current local directory
        loadServerFileSystem(new File("C:\\Users\\syedm\\Desktop\\SMHN\\")); // Load server-side file structure (dummy data here)
    }

    private void updateSizeLabel() {
        int maxSizeKB = sizeSlider.getValue() / 1024;
        sizeRangeLabel.setText("File Size: 0KB - " + maxSizeKB + "KB");
    }

    private void applyDefaultFilter() {
        String uploadDir = "C:\\Users\\syedm\\Desktop\\SMHN\\";
        String downloadDir = "C:\\Users\\syedm\\Desktop\\SMHN\\";
    
        SearchService searchService = new SearchService();
    
        // Get all files without any filtering
        List<List<File>> searchResults = searchService.searchFiles("", uploadDir, downloadDir);
    
        List<File> allUploadFiles = searchResults.get(0);
        List<File> allDownloadFiles = searchResults.get(1);
    
        // Update the trees with all the files
        updateFileTree(allUploadFiles, uploadRoot, uploadFileTree);
        updateFileTree(allDownloadFiles, downloadRoot, downloadFileTree);
    }

    private void performSizeFilter() {
        int maxSize = sizeSlider.getValue();
        String uploadDir = "C:\\Users\\syedm\\Desktop\\SMHN\\";
        String downloadDir = "C:\\Users\\syedm\\Desktop\\SMHN\\";

        SearchService searchService = new SearchService();
        List<List<File>> searchResults = searchService.searchFilesBySize(uploadDir, downloadDir, maxSize);

        List<File> filteredUploadFiles = searchResults.get(0);
        List<File> filteredDownloadFiles = searchResults.get(1);

        updateFileTree(filteredUploadFiles, uploadRoot, uploadFileTree);
        updateFileTree(filteredDownloadFiles, downloadRoot, downloadFileTree);
    }

    private void applyDate(String dateString) {
        String uploadDir = "C:\\Users\\syedm\\Desktop\\SMHN\\";
        String downloadDir = "C:\\Users\\syedm\\Desktop\\SMHN\\";

        SearchService searchService = new SearchService();

        List<List<File>> searchResults = searchService.searchFilesByDate(dateString, uploadDir, downloadDir);

        List<File> filteredUploadFiles = searchResults.get(0);
        List<File> filteredDownloadFiles = searchResults.get(1);

        updateFileTree(filteredUploadFiles, uploadRoot, uploadFileTree);
        updateFileTree(filteredDownloadFiles, downloadRoot, downloadFileTree);
    }

    private void performSearch(String query) {
        String uploadDir = "C:\\Users\\syedm\\Desktop\\SMHN\\";
        String downloadDir = "C:\\Users\\syedm\\Desktop\\SMHN\\";

        SearchService searchService = new SearchService();

        List<List<File>> searchResults = searchService.searchFiles(query, uploadDir, downloadDir);

        List<File> filteredUploadFiles = searchResults.get(0);
        List<File> filteredDownloadFiles = searchResults.get(1);

        updateFileTree(filteredUploadFiles, uploadRoot, uploadFileTree);
        updateFileTree(filteredDownloadFiles, downloadRoot, downloadFileTree);
    }

    public void updateFileTree(List<File> files, DefaultMutableTreeNode root, JTree tree) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();

        root.removeAllChildren();

        for (File file : files) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName());
            root.add(childNode);
        }

        model.reload(root);
    }

    // Load local file system structure
    private void loadLocalFileSystem(File dir) {
        DefaultTreeModel model = (DefaultTreeModel) uploadFileTree.getModel();
        uploadRoot.removeAllChildren();
        addFilesToNode(dir, uploadRoot);
        model.reload();
    }

    // Load server-side file structure (this can be populated via an HTTP request in a real app)
    private void loadServerFileSystem(File dir) {
        // Dummy files for now; in a real application
        // !!! Change it so you get the structure from the server
        DefaultTreeModel model = (DefaultTreeModel) downloadFileTree.getModel();
        downloadRoot.removeAllChildren();
        addFilesToNode(dir, downloadRoot);
        
        model.reload();
    }

    private void addFilesToNode(File dir, DefaultMutableTreeNode node) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getName());
                node.add(childNode);
                if (file.isDirectory()) {
                    addFilesToNode(file, childNode);
                }
            }
        }
    }

    // Upload file
    private void uploadFile(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            uploadFileToServer(selectedFile);
        }
    }

    private void uploadFileToServer(File file){
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost(serverUrl);

            // here
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // Add the file to the request
        builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());

        // Set the entity to the HttpPost request
        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);

        // Execute the request
        CloseableHttpResponse response = client.execute(uploadFile);
            

            //!!! change the content type based on the file types your application should support (i.e. PDF, JPG, etc.)
            // FileEntity fileEntity = new FileEntity(selectedFile, ContentType.IMAGE_PNG);

            // till here

            // uploadFile.setEntity(fileEntity);
            // CloseableHttpResponse response = client.execute(uploadFile);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            if (response.getCode() == 200) {
                JOptionPane.showMessageDialog(this, "File uploaded successfully!", "Upload Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "File upload failed: " + responseString, "Upload Error", JOptionPane.ERROR_MESSAGE);
            }
            loadLocalFileSystem(new File("C:\\Users\\syedm\\Desktop\\SMHN\\"));
            loadServerFileSystem(new File("C:\\Users\\syedm\\Desktop\\SMHN\\"));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Server not running", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Download file
    private void downloadFile(ActionEvent event) {
        // !!! Make sure that you can download your desired files

        TreePath selectedPath = downloadFileTree.getSelectionPath();
        if (selectedPath == null) {
            JOptionPane.showMessageDialog(this, "Please select a file to download.");
            return;
        }
        String encodedFileName="";
        String selectedFile = selectedPath.getLastPathComponent().toString();
        try {
            encodedFileName = URLEncoder.encode(selectedFile, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String downloadFileUrl = downloadUrl + encodedFileName;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(selectedFile));
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpGet getFile = new HttpGet(downloadFileUrl);
                CloseableHttpResponse response = client.execute(getFile);
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    byte[] fileData = EntityUtils.toByteArray(responseEntity);
                    Files.write(saveFile.toPath(), fileData);
                    JOptionPane.showMessageDialog(this, "Download complete.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FrontendUI frontendUI = new FrontendUI();
            frontendUI.setVisible(true);
        });
    }
}