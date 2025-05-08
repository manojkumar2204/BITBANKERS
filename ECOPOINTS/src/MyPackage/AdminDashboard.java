package MyPackage;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminDashboard extends JFrame implements ActionListener {

    private JTable pendingSubmissionsTable;
    private DefaultTableModel tableModel;
    private JButton approveButton, rejectButton, logoutButton, refreshButton, viewImageButton;
    private int loggedInAdminId;
    private String loggedInAdminEmail;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecopoints_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "code";

    public AdminDashboard(int adminId, String adminEmail) {
        this.loggedInAdminId = adminId;
        this.loggedInAdminEmail = adminEmail;

        setTitle("EcoPoints - Admin Dashboard");
        setSize(900, 600); // Increased height to accommodate the new button
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 245, 255));

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(getContentPane().getBackground());
        add(mainPanel);

        // Header panel with title and refresh button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Pending Submissions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 70, 150));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        refreshButton = createStyledButton("Refresh", new Color(70, 130, 180), this);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        headerPanel.add(refreshButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"User", "Waste Type", "Quantity (kg)", "Image", "Submission ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        pendingSubmissionsTable = new JTable(tableModel);
        styleTable(pendingSubmissionsTable);

        // Hide submission ID column
        pendingSubmissionsTable.getColumnModel().getColumn(4).setMinWidth(0);
        pendingSubmissionsTable.getColumnModel().getColumn(4).setMaxWidth(0);
        pendingSubmissionsTable.getColumnModel().getColumn(4).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(pendingSubmissionsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        viewImageButton = createStyledButton("View Image", new Color(139, 69, 19), this); // Brown color
        approveButton = createStyledButton("Approve", new Color(50, 150, 50), this);
        rejectButton = createStyledButton("Reject", new Color(200, 50, 50), this);
        logoutButton = createStyledButton("Logout", new Color(100, 100, 100), this);

        Dimension buttonSize = new Dimension(120, 40);
        viewImageButton.setPreferredSize(buttonSize);
        approveButton.setPreferredSize(buttonSize);
        rejectButton.setPreferredSize(buttonSize);
        logoutButton.setPreferredSize(new Dimension(100, 40));

        buttonPanel.add(viewImageButton);
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        fetchPendingSubmissions();
        setVisible(true);
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(50);
        table.setSelectionBackground(new Color(220, 230, 240));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.BLACK);
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(3).setCellRenderer(new ImageRenderer());
    }

    private class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            JLabel label = new JLabel();
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setOpaque(true);

            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }

            if (value instanceof String) {
                String imagePath = (String) value;
                if (imagePath != null && !imagePath.isEmpty()) {
                    ImageIcon icon = new ImageIcon(imagePath);
                    Image scaledImage = icon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
                    label.setIcon(new ImageIcon(scaledImage));
                    label.setText(""); // Remove text if showing image
                } else {
                    label.setText("No Image");
                    label.setIcon(null);
                }
            } else {
                label.setText("No Image");
                label.setIcon(null);
            }
            return label;
        }
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
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.addActionListener(listener);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(MouseEvent evt) {
                button.setCursor(Cursor.getDefaultCursor());
            }
        });

        return button;
    }

    private void fetchPendingSubmissions() {
        tableModel.setRowCount(0);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT s.submission_id, u.full_name, s.waste_type, s.quantity, s.image_path " +
                             "FROM submissions s JOIN users u ON s.user_id = u.user_id " +
                             "WHERE s.status = 'pending' ORDER BY s.submission_date DESC")) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Object[] row = {
                        resultSet.getString("full_name"),
                        resultSet.getString("waste_type"),
                        String.format("%.2f", resultSet.getDouble("quantity")),
                        resultSet.getString("image_path"),
                        resultSet.getInt("submission_id")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error fetching submissions: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void approveSubmission() {
        int selectedRow = pendingSubmissionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a submission to approve",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int submissionId = (int) tableModel.getValueAt(selectedRow, 4);
        String fullName = (String) tableModel.getValueAt(selectedRow, 0);
        int userId = -1; // Initialize userId

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            connection.setAutoCommit(false);

            // Get the user_id for the selected submission
            String getUserIdQuery = "SELECT user_id FROM submissions WHERE submission_id = ?";
            ps = connection.prepareStatement(getUserIdQuery);
            ps.setInt(1, submissionId);
            rs = ps.executeQuery();

            if (rs.next()) {
                userId = rs.getInt("user_id");
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not retrieve User ID for this submission.", "Database Error", JOptionPane.ERROR_MESSAGE);
                return; // Exit if user_id cannot be found
            }

            // 1. Update submission status
            try (PreparedStatement updateSubmissionStmt = connection.prepareStatement(
                    "UPDATE submissions SET status = 'approved' WHERE submission_id = ?")) {
                updateSubmissionStmt.setInt(1, submissionId);
                updateSubmissionStmt.executeUpdate();
            }

            // 2. Award eco-points to the user
            try (PreparedStatement updateUserPointsStmt = connection.prepareStatement(
                    "UPDATE users SET eco_points = eco_points + 4 WHERE user_id = ?")) {
                updateUserPointsStmt.setInt(1, userId);
                updateUserPointsStmt.executeUpdate();
            }

            connection.commit();
            JOptionPane.showMessageDialog(this,
                    "Submission approved and 4 points awarded to " + fullName,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            fetchPendingSubmissions();

        } catch (SQLException ex) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(this,
                    "Error approving submission: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            // Close resources
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    private void rejectSubmission() {
        int selectedRow = pendingSubmissionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a submission to reject",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int submissionId = (int) tableModel.getValueAt(selectedRow, 4);
        String fullName = (String) tableModel.getValueAt(selectedRow, 0); // To inform the admin

        Connection connection = null; // Declare connection outside try block
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            try (PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE submissions SET status = 'rejected' WHERE submission_id = ?")) {

                stmt.setInt(1, submissionId);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Submission from " + fullName + " rejected.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    fetchPendingSubmissions();
                }

            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error rejecting submission: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    private void viewImage() {
        int selectedRow = pendingSubmissionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a submission to view the image.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String imagePath = (String) tableModel.getValueAt(selectedRow, 3);

        if (imagePath != null && !imagePath.isEmpty()) {
            ImageIcon icon = new ImageIcon(imagePath);
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                Image image = icon.getImage().getScaledInstance(600, 600, Image.SCALE_SMOOTH);
                JOptionPane.showMessageDialog(this, new ImageIcon(image), "Submission Image", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Could not load the image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No image available for this submission.", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == approveButton) {
            approveSubmission();
        } else if (e.getSource() == rejectButton) {
            rejectSubmission();
        } else if (e.getSource() == logoutButton) {
            this.dispose();
            new Welcome().setVisible(true);
        } else if (e.getSource() == refreshButton) {
            fetchPendingSubmissions();
        } else if (e.getSource() == viewImageButton) {
            viewImage();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AdminDashboard(1, "admin@example.com");
        });
    }
}