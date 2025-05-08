package MyPackage;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CheckEcoPoints extends JFrame implements ActionListener {

    private JLabel totalPointsLabel;
    private JButton backButton, refreshButton;
    private int loggedInUserId;
    private String loggedInEmail;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecopoints_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "code";

    public CheckEcoPoints(String email) { // Changed constructor to accept email
        this.loggedInEmail = email;
        fetchUserId(); // Fetch user ID based on email

        setTitle("EcoPoints - Check Your Points");
        setSize(500, 350); // Slightly larger window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 245, 240)); // Light green background

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(25, 40, 25, 40)); // More padding
        mainPanel.setBackground(getContentPane().getBackground());
        add(mainPanel);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Your EcoPoints");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(new Color(34, 139, 34)); // Forest green
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Add refresh button
        refreshButton = createStyledButton("Refresh", new Color(70, 130, 180), this);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        headerPanel.add(refreshButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center panel for displaying points with card-like design
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        // Create a card panel for the points display
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 200), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        cardPanel.setPreferredSize(new Dimension(350, 150));

        JPanel pointsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pointsPanel.setOpaque(false);

        JLabel pointsTextLabel = new JLabel("Your Total EcoPoints:");
        pointsTextLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        pointsPanel.add(pointsTextLabel);

        totalPointsLabel = new JLabel("0"); // Initial value
        totalPointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        totalPointsLabel.setForeground(new Color(0, 128, 0)); // Darker green
        pointsPanel.add(totalPointsLabel);

        cardPanel.add(pointsPanel, BorderLayout.CENTER);

        // Add eco icon
        JLabel ecoIcon = new JLabel(new ImageIcon("ecopoint_icon.png")); // Replace with your icon path
        ecoIcon.setBorder(new EmptyBorder(0, 0, 0, 10));
        cardPanel.add(ecoIcon, BorderLayout.WEST);

        centerPanel.add(cardPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with back button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        backButton = createStyledButton("Back to Dashboard", new Color(70, 130, 180), this);
        backButton.setPreferredSize(new Dimension(200, 45));
        bottomPanel.add(backButton);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        fetchTotalPoints();
        setVisible(true);
    }

    private void fetchUserId() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT user_id FROM users WHERE email = ?")) {
            preparedStatement.setString(1, loggedInEmail);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                loggedInUserId = resultSet.getInt("user_id");
            } else {
                JOptionPane.showMessageDialog(this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
                // Handle the case where the user is not found
                loggedInUserId = -1; // Or some other default value
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10); // More rounded corners
                g2.dispose();

                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
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

    private void fetchTotalPoints() {
        if (loggedInUserId != -1) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "SELECT eco_points FROM users WHERE user_id = ?")) { // Corrected column name to eco_points
                preparedStatement.setInt(1, loggedInUserId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int totalPoints = resultSet.getInt("eco_points"); // Corrected column name
                    totalPointsLabel.setText(String.valueOf(totalPoints));

                    // Add animation when points update
                    animatePointsChange();
                } else {
                    totalPointsLabel.setText("Error");
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error fetching total points: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
                totalPointsLabel.setText("Error");
            }
        }
    }

    private void animatePointsChange() {
        Timer timer = new Timer(50, new ActionListener() {
            float scale = 1.0f;
            boolean growing = true;
            int count = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (count++ > 10) { // Run for 10 cycles
                    ((Timer) e.getSource()).stop();
                    totalPointsLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
                    return;
                }

                if (growing) {
                    scale += 0.05f;
                    if (scale >= 1.2f) growing = false;
                } else {
                    scale -= 0.05f;
                    if (scale <= 1.0f) growing = true;
                }

                Font originalFont = new Font("Segoe UI", Font.BOLD, 48);
                totalPointsLabel.setFont(originalFont.deriveFont(originalFont.getSize2D() * scale));
            }
        });
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
        	new UserDashboard(loggedInUserId, loggedInEmail).setVisible(true); // Use loggedInEmail
            this.dispose();
        } else if (e.getSource() == refreshButton) {
            fetchTotalPoints();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new CheckEcoPoints("test@example.com");
        });
    }
}