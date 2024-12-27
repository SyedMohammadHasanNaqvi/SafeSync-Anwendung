package com.example.demo;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import javax.swing.tree.TreePath;
import java.util.List;
import java.text.SimpleDateFormat;
import java.awt.event.FocusEvent;

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
    private JTextField dateField;
    // private JSlider sizeSlider;
    // private JLabel sizeRangeLabel;

    // Replace with your server URLs
    private String serverUrl = "http://localhost:8080/upload";
    private String downloadUrl = "http://localhost:8080/download?file=";

    public FrontendUI() {
        setTitle("Office Drive");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Search Bar & Button
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JPanel searchSubPanel = new JPanel(new BorderLayout());
        JLabel iconLabel = new JLabel("\uD83D\uDD0D");
        iconLabel.setFont(new Font("", Font.PLAIN, 18));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(-5, 0, 0,0));
        searchPanel.add(iconLabel, BorderLayout.WEST);
        // searchSubPanel.add(new JLabel("\uD83D\uDD0D  "), BorderLayout.WEST);
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 25));
        searchField.setText("Search");
        searchSubPanel.add(searchField, BorderLayout.CENTER);
        // JButton searchButton = new JButton("Search");
        // searchSubPanel.add(searchButton, BorderLayout.EAST);

        // JPanel askDate = new JPanel(new BorderLayout());
        // JLabel dateLabel = new JLabel("Enter Date");
        // askDate.add(dateLabel, BorderLayout.NORTH);
        // Label formatLabel = new Label("(DD-MM-YYYY)");
        // askDate.add(formatLabel, BorderLayout.CENTER);
        
        JPanel dateSubPanel = new JPanel(new BorderLayout());
        // dateSubPanel.add(askDate, BorderLayout.WEST);
        JTextField dateField = new JTextField(10);
        dateField.setText("    DD-MM-YYYY");
        dateSubPanel.add(dateField, BorderLayout.CENTER);
        JButton dateButton = new JButton("Apply");
        dateSubPanel.add(dateButton, BorderLayout.EAST);

        searchField.addFocusListener(new FocusListener() {
           @Override
           public void focusGained(FocusEvent e) {
            if (searchField.getText().equals("Search")) {
                searchField.setText("");
            }
           } 

           @Override
           public void focusLost(FocusEvent e) {
            if (searchField.getText().isEmpty()) {
                searchField.setText(("Search"));
            }
           }
        });

        dateField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
             if (dateField.getText().equals("    DD-MM-YYYY")) {
                 dateField.setText("");
             }
            } 
 
            @Override
            public void focusLost(FocusEvent e) {
             if (dateField.getText().isEmpty()) {
                 dateField.setText(("    DD-MM-YYYY"));
             }
            }
         });

        SwingUtilities.invokeLater(() -> {
            searchPanel.requestFocusInWindow();
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String searchText = searchField.getText().trim();
                if (searchText.equals("Search")) {
                    applyDefaultFilter();
                } else {
                performSearch(searchText);}
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String searchText = searchField.getText().trim();
                if (searchText.equals("Search")) {
                    applyDefaultFilter();
                } else {
                performSearch(searchText);}
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });

        dateField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String dateText = dateField.getText();
                if (dateText.length() == 2 && !dateText.contains("-")) {
                    SwingUtilities.invokeLater(() -> {
                        dateField.setText(dateText + "-");
                    });
                } else if (dateText.length() == 5 && !dateText.substring(3).equals("-")) {
                    SwingUtilities.invokeLater(() -> {
                        dateField.setText(dateText.substring(0, 5) + "-2025");
                    });
                }

                if (dateText.isEmpty() || dateText.equals("    DD-MM-YYYY")) {
                    applyDefaultFilter();
                    dateField.setBackground(Color.WHITE);
                } else if (validDate(dateText)) {
                    dateField.setBackground(Color.GREEN);
                } else {
                    dateField.setBackground(Color.PINK);
                }
            }

             @Override
            public void removeUpdate(DocumentEvent e) {
                String dateText = dateField.getText();
                if (dateText.isEmpty() || dateText.equals("    DD-MM-YYYY")) {
                    applyDefaultFilter();
                    dateField.setBackground(Color.WHITE);
                } else if (validDate(dateText)) {
                    dateField.setBackground(Color.GREEN);
                } else {
                    dateField.setBackground(Color.PINK);
                }
            }
        
            @Override
            public void changedUpdate(DocumentEvent e) {
            }});

        String[] sortOptions = {"Sort", "Decending Order", "Ascending Order"};
        JComboBox<String> sortComboBox = new JComboBox<>(sortOptions);

        sortComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOption = sortComboBox.getSelectedItem().toString();
                if (selectedOption.equals("Ascending Order")) {
                    sortFilesBySize(true);
                } else if (selectedOption.equals("Decending Order")) {
                    sortFilesBySize(false);
                } else {
                    applyDefaultFilter();
                }
            }
        });

        String[] settings = {"      📂", "Log Out"};
        JComboBox<String> settingsComboBox = new JComboBox<>(settings);

        settingsComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (settingsComboBox.getSelectedItem().equals("Log Out")) {
                    dispose();
                    LoginUI reinitiate = new LoginUI();
                    reinitiate.setLocationRelativeTo(null);
                    reinitiate.setVisible(true);
                } else {}
            }
        });

        searchPanel.add(searchSubPanel);
        searchPanel.add(dateSubPanel);
        searchPanel.add(sortComboBox);
        searchPanel.add(settingsComboBox);

        // searchButton.addActionListener(e -> {
        //     String query = searchField.getText().trim();
        //     performSearch(query);
        // });

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
        // JPanel sizeFilterPanel = new JPanel(new BorderLayout());
        // JButton sortLargestButton = new JButton("Sort by Largest Size");
        // JButton sortSmallestButton = new JButton("Sort by Smallest Size");

        // sizeFilterPanel.add(sortLargestButton, BorderLayout.NORTH);
        // sizeFilterPanel.add(sortSmallestButton, BorderLayout.SOUTH);

        // sortLargestButton.addActionListener(e -> sortFilesBySize(true));
        // sortSmallestButton.addActionListener(e -> sortFilesBySize(false));


        // File Trees
        uploadRoot = new DefaultMutableTreeNode("Uploaded Files");
        uploadFileTree = new JTree(uploadRoot);
        JScrollPane uploadTreeScroll = new JScrollPane(uploadFileTree);

        downloadRoot = new DefaultMutableTreeNode("Downloadable Files");
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
        splitPane.setDividerLocation(380);  // Initial divider position

        // Labels for file trees
        JLabel uploadLabel = new JLabel("Upload Directory", JLabel.CENTER);
        JLabel downloadLabel = new JLabel("Download Directory", JLabel.CENTER);

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
        // getContentPane().add(sizeFilterPanel, BorderLayout.WEST);
        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();

        loadLocalFileSystem(new File("C:\\Users\\syedm\\Desktop\\SMHN\\"));  // Load current local directory
        loadServerFileSystem(new File("C:\\Users\\syedm\\Desktop\\SMHN\\")); // Load server-side file structure (dummy data here)
    }

    // private void updateSizeLabel() {
    //     int maxSizeKB = sizeSlider.getValue() / 1024;
    //     sizeRangeLabel.setText("File Size: 0KB - " + maxSizeKB + "KB");
    // }

    private boolean validDate(String date) {
        String regex = "^\\d{2}-\\d{2}-\\d{4}$";
    
        if (!date.matches(regex)) {
            return false;
        }
        
        String dayPart = date.substring(0, 2);
        String monthPart = date.substring(3, 5);
        String yearPart = date.substring(6, 10);

        int day = Integer.parseInt(dayPart);
        int month = Integer.parseInt(monthPart);
        int year = Integer.parseInt(yearPart);
    
        if (day < 1 || day > 31 || month < 1 || month > 12 || year < 2024 || year > 2025) {
            //2024 is when the project started and 2025 is the current year
            return false;
        }
        
        return true;
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


    // private void performSizeFilter() {
    //     int maxSize = sizeSlider.getValue();
    //     String uploadDir = "C:\\Users\\syedm\\Desktop\\SMHN\\";
    //     String downloadDir = "C:\\Users\\syedm\\Desktop\\SMHN\\";

    //     SearchService searchService = new SearchService();
    //     List<List<File>> searchResults = searchService.searchFilesBySize(uploadDir, downloadDir, maxSize);

    //     List<File> filteredUploadFiles = searchResults.get(0);
    //     List<File> filteredDownloadFiles = searchResults.get(1);

    //     updateFileTree(filteredUploadFiles, uploadRoot, uploadFileTree);
    //     updateFileTree(filteredDownloadFiles, downloadRoot, downloadFileTree);
    //}

    private void sortFilesBySize(boolean largestFirst) {
    String uploadDir = "C:\\Users\\syedm\\Desktop\\SMHN\\";
    String downloadDir = "C:\\Users\\syedm\\Desktop\\SMHN\\";

    SearchService searchService = new SearchService();
    List<List<File>> sortedFiles = searchService.sortFilesBySize(uploadDir, downloadDir, largestFirst);

    List<File> sortedUploadFiles = sortedFiles.get(0);
    List<File> sortedDownloadFiles = sortedFiles.get(1);

    updateFileTree(sortedUploadFiles, uploadRoot, uploadFileTree);
    updateFileTree(sortedDownloadFiles, downloadRoot, downloadFileTree);
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
            frontendUI.setLocationRelativeTo(null);
            frontendUI.setVisible(true);
        });
    }
}