package com.example.demo;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
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
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.classic.methods.HttpGet;


public class FrontendUI extends JFrame {

    private JTree downloadFileTree;
    private DefaultMutableTreeNode downloadRoot;
    private JButton uploadButton;
    private JButton downloadButton;
    JPanel downloadPanel = new JPanel(new BorderLayout());
    JPanel listPanel;
    JPanel gridPanel;
    String path = "C:\\Users\\syedm\\Desktop\\SMHN\\";
    JScrollPane downloadTreeScroll;
    String[] viewTypes = {"Tree View","List View","Grid View"};
    JComboBox<String> fileViewsComboBox = new JComboBox<>(viewTypes);
    File selectedFileListnGrid = null;
    private JPanel previousSelectedPanel = null; 
    private String serverUrl = "http://localhost:8080/upload";
    private String downloadUrl = "http://localhost:8080/download?file=";

    public FrontendUI() {
        setTitle("SafeSync");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JPanel searchSubPanel = new JPanel(new BorderLayout());
        JLabel iconLabel = new JLabel("\uD83D\uDD0D");
        iconLabel.setFont(new Font("", Font.PLAIN, 18));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(-5, 0, 0,0));
        searchPanel.add(iconLabel, BorderLayout.WEST);
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 25));
        searchField.setText("Search");
        searchSubPanel.add(searchField, BorderLayout.CENTER);
        
        JPanel dateSubPanel = new JPanel(new BorderLayout());
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
        searchPanel.add(fileViewsComboBox);
        searchPanel.add(settingsComboBox);

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
        
        downloadRoot = new DefaultMutableTreeNode("Server Files");
        downloadFileTree = new JTree(downloadRoot);
        uploadButton = new JButton("Upload");
        downloadButton = new JButton("Download");

        uploadButton.addActionListener(this::uploadFile);
        downloadButton.addActionListener(this::downloadFile);

        fileViewsComboBox.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedView = fileViewsComboBox.getSelectedItem().toString();
                updateviews(selectedView);
            }
            
        });
        
        downloadTreeScroll = new JScrollPane(downloadFileTree);
        downloadPanel.add(downloadTreeScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(uploadButton);
        buttonPanel.add(downloadButton);
        JLabel downloadLabel = new JLabel("File Directory", JLabel.CENTER);

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
        
        downloadPanel.setTransferHandler(new FileTransferHandler());
        downloadPanel.add(downloadLabel, BorderLayout.NORTH);
        
        getContentPane().add(searchPanel, BorderLayout.NORTH);
        getContentPane().add(downloadPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();

        loadServerFileSystem(new File(path));
    }


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
    
        if (day < 1 || day > 31 || day > 29 && month == 02 || month < 1 || month > 12 || year < 2024 || year > 2025) {
            return false;
        }
        
        return true;
    }


    private void applyDefaultFilter() {
        String downloadDir = path;
    
        SearchService searchService = new SearchService();
    
        List<File> searchResults = searchService.searchFiles("", downloadDir);
        List<File> allDownloadFiles = searchResults;
    
        if (fileViewsComboBox.getSelectedItem().toString().equals("List View")) {
            updateListView(allDownloadFiles,listPanel,false);
        } else if (fileViewsComboBox.getSelectedItem().toString().equals("Grid View")) {
            updateListView(allDownloadFiles,gridPanel,true);
        } else {
        updateFileTree(allDownloadFiles, downloadRoot, downloadFileTree);
        }
    }


    private void sortFilesBySize(boolean largestFirst) {
        String uploadDir = path;
        String downloadDir = path;

        SearchService searchService = new SearchService();

        List<File> sortedFiles = searchService.sortFilesBySize(downloadDir, largestFirst);
        List<File> sortedDownloadFiles = sortedFiles;

        if (fileViewsComboBox.getSelectedItem().toString().equals("List View")) {
            updateListView(sortedDownloadFiles,listPanel,false);
        } else if (fileViewsComboBox.getSelectedItem().toString().equals("Grid View")) {
            updateListView(sortedDownloadFiles,gridPanel,true);
        } else {
            updateFileTree(sortedDownloadFiles, downloadRoot, downloadFileTree); 
        }
    }


    private void applyDate(String dateString) {
        String uploadDir = path;
        String downloadDir = path;

        SearchService searchService = new SearchService();

        List<File> searchResults = searchService.searchFilesByDate(dateString, downloadDir);
        List<File> filteredDownloadFiles = searchResults;

        if (fileViewsComboBox.getSelectedItem().toString().equals("List View")) {
            updateListView(filteredDownloadFiles,listPanel,false);
        } else if (fileViewsComboBox.getSelectedItem().toString().equals("Grid View")) {
            updateListView(filteredDownloadFiles,gridPanel,true);
        } else {
        updateFileTree(filteredDownloadFiles, downloadRoot, downloadFileTree);}
    }


    private void performSearch(String query) {
        String uploadDir = path;
        String downloadDir = path;

        SearchService searchService = new SearchService();

        List<File> searchResults = searchService.searchFiles(query, downloadDir);
        List<File> filteredDownloadFiles = searchResults;

        
        if (fileViewsComboBox.getSelectedItem().toString().equals("List View")) {
            updateListView(filteredDownloadFiles,listPanel,false);
        } else if (fileViewsComboBox.getSelectedItem().toString().equals("Grid View")) {
            updateListView(filteredDownloadFiles,gridPanel,true);
        } else {
        updateFileTree(filteredDownloadFiles, downloadRoot, downloadFileTree);
        }
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


    private void loadServerFileSystem(File dir) {
        if(fileViewsComboBox.getSelectedItem().toString().equals("Grid View") || fileViewsComboBox.getSelectedItem().toString().equals("List View")){
            updateviews(fileViewsComboBox.getSelectedItem().toString());
        }
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
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);

        CloseableHttpResponse response = client.execute(uploadFile);
            HttpEntity responseEntity = response.getEntity();
            String responseString = EntityUtils.toString(responseEntity);
            if (response.getCode() == 200) {
                JOptionPane.showMessageDialog(this, "File uploaded successfully!", "Upload Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "File upload failed: " + responseString, "Upload Error", JOptionPane.ERROR_MESSAGE);
            }
            loadServerFileSystem(new File(path));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Server not running", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void downloadFile(ActionEvent event) {
        String selectedFile;
        TreePath selectedPath = downloadFileTree.getSelectionPath();
        
        String encodedFileName="";
        if (fileViewsComboBox.getSelectedItem().toString().equals("Tree View")) {
            if (selectedPath == null) {
                JOptionPane.showMessageDialog(this, "Please select a file to download.");
                return;
            }
            selectedFile = selectedPath.getLastPathComponent().toString();
        }else {
            selectedFile = selectedFileListnGrid.getName();
        }
        try {
            encodedFileName = URLEncoder.encode(selectedFile, "UTF-8");
        } catch (UnsupportedEncodingException e) {
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


    public JPanel createGridView(String path) {
        gridPanel = new JPanel(new GridLayout(0, 4, 5, 5));
        
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        JPanel filePanel = createFilePanel(file,true);
                        gridPanel.add(filePanel);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid directory path!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return gridPanel;
    }


    public JPanel createListView(String path) {
        listPanel=new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    JPanel filePanel = createFilePanel(file, false);
                    if (file.isFile()) {
                        listPanel.add(filePanel);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid directory path!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return listPanel;
    }


    private JPanel createFilePanel(File file, boolean isGridView) {
        JPanel filePanel = new JPanel();
        filePanel.setLayout(isGridView ? new BorderLayout() : new FlowLayout(FlowLayout.LEFT));
        filePanel.setPreferredSize(isGridView ? new Dimension(150, 150) : new Dimension(600, 80));

        JLabel thumbnailLabel = new JLabel();
        thumbnailLabel.setHorizontalAlignment(SwingConstants.CENTER);
        thumbnailLabel.setPreferredSize(isGridView ? new Dimension(100, 100) : new Dimension(25, 25));

        try {
            if (isImageFile(file)) {
                BufferedImage img = ImageIO.read(file);
                ImageIcon icon = new ImageIcon(img.getScaledInstance(
                    isGridView ? 100 : 60,
                    isGridView ? 100 : 60,
                    Image.SCALE_SMOOTH
                ));
                thumbnailLabel.setIcon(icon);
            } else {
                Icon fileIcon = FileSystemView.getFileSystemView().getSystemIcon(file);
                thumbnailLabel.setIcon(fileIcon);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        JLabel nameLabel = new JLabel(file.getName());
        nameLabel.setHorizontalAlignment(isGridView ? SwingConstants.CENTER : SwingConstants.LEFT);

        if (isGridView) {
            filePanel.add(thumbnailLabel, BorderLayout.CENTER);
            filePanel.add(nameLabel, BorderLayout.SOUTH);
        } else {
            filePanel.add(thumbnailLabel);
            filePanel.add(nameLabel);
        }


        filePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (previousSelectedPanel != null) {
                    previousSelectedPanel.setBackground(null);
                }

                filePanel.setBackground(Color.LIGHT_GRAY);
    
                selectedFileListnGrid = file;
                previousSelectedPanel = filePanel;
            }
        });

        return filePanel;
    }


    private boolean isImageFile(File file) {
        String[] imageExtensions = { "jpg", "jpeg", "png", "gif", "bmp" };
        String fileName = file.getName().toLowerCase();
        for (String ext : imageExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }


    public void updateviews(String ViewType){
        if (ViewType.equals("Tree View")) {
            downloadTreeScroll = new JScrollPane(downloadFileTree);
            downloadPanel.removeAll();
            downloadPanel.add(downloadTreeScroll, BorderLayout.CENTER);
            downloadPanel.revalidate();
            downloadPanel.repaint();
        } else if (ViewType.equals("List View")) {
            downloadTreeScroll = new JScrollPane(createListView(path));
            downloadPanel.removeAll();
            downloadPanel.add(downloadTreeScroll, BorderLayout.CENTER);
            downloadPanel.revalidate();
            downloadPanel.repaint();
        } else {
            downloadTreeScroll = new JScrollPane(createGridView(path));
            downloadPanel.removeAll();
            downloadPanel.add(downloadTreeScroll, BorderLayout.CENTER);
            downloadPanel.revalidate();
            downloadPanel.repaint();
        }
    }


    private void updateListView(List<File> files, JPanel listPanel, boolean isGridView) {
        listPanel.removeAll();

        for (File file : files) {
        JPanel filePanel = createFilePanel(file, isGridView);
        listPanel.add(filePanel);
        }
    
        listPanel.revalidate();
        listPanel.repaint();
    }
}