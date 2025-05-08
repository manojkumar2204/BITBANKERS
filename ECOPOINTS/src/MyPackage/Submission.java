package MyPackage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Submission extends JFrame implements ActionListener {

    private JTextField wasteTypeField;
    private JTextField quantityField;
    private JLabel selectedImageLabel;
    private JButton uploadImageButton;
    private JButton submitButton;
    private JButton backButton;
    private JFileChooser fileChooser;
    private String imagePath;
    private int loggedInUserId;
    private String loggedInEmail; // Need to store logged-in user's email

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecopoints_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "code";

    public Submission(int userId, String email) { // Modified constructor to accept email
        this.loggedInUserId = userId;
        this.loggedInEmail = email; // Store the email

        // Frame setup
        setTitle("EcoPoints - Waste Submission");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 245, 240)); // Light green background

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        mainPanel.setBackground(getContentPane().getBackground());
        add(mainPanel);

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Waste Submission Form");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(34, 139, 34)); // Forest green
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 215, 200)),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 15, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Waste Type
        JLabel wasteTypeLabel = createFormLabel("Waste Type:");
        formPanel.add(wasteTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        wasteTypeField = createFormTextField();
        formPanel.add(wasteTypeField, gbc);

        // Quantity
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel quantityLabel = createFormLabel("Quantity (kg):");
        formPanel.add(quantityLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        quantityField = createFormTextField();
        formPanel.add(quantityField, gbc);

        // Image Upload
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel imageLabel = createFormLabel("Upload Image:");
        formPanel.add(imageLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPanel uploadPanel = new JPanel(new BorderLayout(10, 0));
        uploadPanel.setOpaque(false);
        uploadImageButton = createStyledButton("Choose File", new Color(70, 130, 180), this);
        selectedImageLabel = new JLabel("No file selected");
        selectedImageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        selectedImageLabel.setForeground(Color.GRAY);

        uploadPanel.add(uploadImageButton, BorderLayout.WEST);
        uploadPanel.add(selectedImageLabel, BorderLayout.CENTER);
        formPanel.add(uploadPanel, gbc);

        // Button panel
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);

        backButton = createStyledButton("Back", new Color(169, 169, 169), this);
        submitButton = createStyledButton("Submit Waste", new Color(46, 139, 87), this);

        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // File chooser setup
        fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        setVisible(true);
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(new Color(70, 70, 70));
        return label;
    }

    private JTextField createFormTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        textField.setBackground(Color.WHITE);
        textField.setPreferredSize(new Dimension(200, 35));
        return textField;
    }

    private JButton createStyledButton(String text, Color bgColor, ActionListener listener) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.addActionListener(listener);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return button;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadImageButton) {
            fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image Files", "jpg", "jpeg", "png", "gif");
            fileChooser.setFileFilter(filter);

            // Set default directory to user's home or pictures folder
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Pictures"));

            int returnVal = fileChooser.showOpenDialog(Submission.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                imagePath = selectedFile.getAbsolutePath();
                selectedImageLabel.setText(selectedFile.getName());
                selectedImageLabel.setForeground(new Color(70, 130, 180));

                // Optional: Display thumbnail preview (Keep this for user feedback)
                try {
                    ImageIcon icon = new ImageIcon(selectedFile.getPath());
                    Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    uploadImageButton.setIcon(new ImageIcon(scaledImage));
                    uploadImageButton.setText(""); // Remove text if showing image
                } catch (Exception ex) {
                    // If image loading fails, just show the filename
                    System.out.println("Couldn't load image preview: " + ex.getMessage());
                }
            }
        } else if (e.getSource() == submitButton) {
            submitWaste();
        } else if (e.getSource() == backButton) {
            // Go back to UserDashboard, passing the user ID and email
        	new UserDashboard(loggedInUserId, loggedInEmail).setVisible(true);
            this.dispose();
        }
    }

    private void submitWaste() {
        String wasteType = wasteTypeField.getText().trim();
        String quantityStr = quantityField.getText().trim();

        if (wasteType.isEmpty()) {
            showError(wasteTypeField, "Please enter waste type");
            return;
        }

        if (quantityStr.isEmpty()) {
            showError(quantityField, "Please enter quantity");
            return;
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) {
                showError(quantityField, "Quantity must be positive");
                return;
            }

            insertSubmission(wasteType, quantity, imagePath);
        } catch (NumberFormatException ex) {
            showError(quantityField, "Invalid quantity format");
        }
    }

    private void showError(JComponent component, String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        component.requestFocusInWindow();
        if (component instanceof JTextField) {
            ((JTextField) component).selectAll();
        }
    }

    private void insertSubmission(String wasteType, double quantity, String imagePath) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO submissions (user_id, waste_type, quantity, image_path, submission_date, status) VALUES (?, ?, ?, ?, NOW(), ?)")) {

            preparedStatement.setInt(1, loggedInUserId);
            preparedStatement.setString(2, wasteType);
            preparedStatement.setDouble(3, quantity);
            preparedStatement.setString(4, imagePath);
            preparedStatement.setString(5, "pending"); // Initial status is 'pending'

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Submission successful! Waiting for admin approval.", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Submission failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void resetForm() {
        wasteTypeField.setText("");
        quantityField.setText("");
        selectedImageLabel.setText("No file selected");
        selectedImageLabel.setForeground(Color.GRAY);
        imagePath = null;
        uploadImageButton.setIcon(null); // Clear the image icon
        uploadImageButton.setText("Choose File"); // Reset button text
    }

    // Method to get the logged-in user's email (if needed elsewhere)
    public String getLoggedInEmail() {
        return loggedInEmail;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // In a real application, you would get the user ID and email from the login or previous screen
            new Submission(1, "test@example.com");
        });
    }
}