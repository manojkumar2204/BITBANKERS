package MyPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;

public class Certificate extends JFrame implements ActionListener {
    
    private JButton backButton, viewButton, downloadButton;
    private JLabel messageLabel;
    private int userPoints;
    private int userId;
    private String userEmail;
    
    // Database config
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecopoints_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "code";
    
    public Certificate(int userId, String email, int points) {
        this.userId = userId;
        this.userEmail = email;
        this.userPoints = points;
        
        setupUI();
    }
    
    private void setupUI() {
        setTitle("EcoPoints - Certificate");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 245, 240));
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(getContentPane().getBackground());
        add(mainPanel);
        
        // Header with icon
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        
        ImageIcon icon = createScaledIcon("src/MyPackage/certificate_icon.png", 40, 40);
        JLabel iconLabel = new JLabel(icon);
        JLabel titleLabel = new JLabel("EcoPoints Certificate");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(30, 130, 76));
        
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel pointsLabel = new JLabel("Your Current EcoPoints: " + userPoints);
        pointsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(pointsLabel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(messageLabel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        
        backButton = createStyledButton("Back", new Color(41, 128, 185));
        viewButton = createStyledButton("View", new Color(46, 125, 50));
        downloadButton = createStyledButton("Download", new Color(142, 68, 173));
        
        buttonPanel.add(backButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(downloadButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private ImageIcon createScaledIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btn.addActionListener(this);
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.darker());
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            this.dispose();
            new UserDashboard(userId, userEmail).setVisible(true);
        } 
        else if (e.getSource() == viewButton) {
            viewCertificate();
        } 
        else if (e.getSource() == downloadButton) {
            downloadCertificate();
        }
    }
    
    private void viewCertificate() {
        try {
            File imageFile = new File("src/MyPackage/certificate_template.png");
            if (imageFile.exists()) {
                Desktop.getDesktop().open(imageFile);
                messageLabel.setText("Certificate opened in viewer!");
                messageLabel.setForeground(new Color(30, 130, 76));
            } else {
                messageLabel.setText("Certificate template not found!");
                messageLabel.setForeground(Color.RED);
            }
        } catch (Exception ex) {
            messageLabel.setText("Error: " + ex.getMessage());
            messageLabel.setForeground(Color.RED);
        }
    }
    
    private void downloadCertificate() {
        if (userPoints >= 100) {
            try {
                File sourceFile = new File("src/MyPackage/certificate_template.png");
                File downloadDir = new File(System.getProperty("user.home") + "/Downloads");
                String userName = getUserName();
                File destFile = new File(downloadDir, "EcoPoints_Certificate_" + userName + ".png");
                
                Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                messageLabel.setText("Downloaded to: " + destFile.getName());
                messageLabel.setForeground(new Color(30, 130, 76));
                
                Desktop.getDesktop().open(downloadDir);
            } catch (IOException ex) {
                messageLabel.setText("Download failed: " + ex.getMessage());
                messageLabel.setForeground(Color.RED);
            }
        } else {
            messageLabel.setText("You need " + (100 - userPoints) + " more points to download!");
            messageLabel.setForeground(Color.RED);
        }
    }
    
    private String getUserName() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT full_name FROM users WHERE user_id = ?")) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("full_name").replaceAll(" ", "_");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "user_" + userId;
    }
    
    public static void main(String[] args) {
        // For testing the certificate screen independently
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Test with sample user (ID 1, email "test@example.com", 100 points)
                Certificate certificate = new Certificate(1, "test@example.com", 100);
                certificate.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}