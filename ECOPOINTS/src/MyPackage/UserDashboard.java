package MyPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserDashboard extends JFrame implements ActionListener {

    // Components
    private JLabel welcomeLabel, pointsLabel;
    private JButton submitBtn, statusBtn, pointsBtn, logoutBtn, refreshDashboardButton;
    private String userEmail, userName;
    private int userPoints;
    private int userId;

    // Database config
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecopoints_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "code";

    public UserDashboard(int userId, String email) {
        this.userId = userId;
        this.userEmail = email;

        setTitle("EcoPoints - User Dashboard");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 245, 240));

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(getContentPane().getBackground());
        add(mainPanel);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        headerPanel.setOpaque(false);

        welcomeLabel = new JLabel("Welcome, Loading...");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(30, 130, 76));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        pointsLabel = new JLabel("Your EcoPoints: 0");
        pointsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        pointsLabel.setForeground(new Color(60, 60, 60));
        pointsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(welcomeLabel);
        headerPanel.add(pointsLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 25, 25));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        submitBtn = createButton("Submit Waste", new Color(46, 125, 50));
        statusBtn = createButton("View Status", new Color(41, 128, 185));
        pointsBtn = createButton("Check Points", new Color(241, 196, 15));
        logoutBtn = createButton("Logout", new Color(192, 57, 43));
        refreshDashboardButton = createButton("Refresh Dashboard", new Color(241, 196, 15));

        centerPanel.add(submitBtn);
        centerPanel.add(statusBtn);
        centerPanel.add(pointsBtn);
        centerPanel.add(refreshDashboardButton);
        centerPanel.add(logoutBtn);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JLabel footer = new JLabel("EcoPoints - Making a Greener World");
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        footer.setForeground(new Color(100, 100, 100));
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);

        fetchUserData();
        fetchUserPoints();

        setVisible(true);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setForeground(Color.BLACK);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        btn.addActionListener(this);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(color.darker());
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
                btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return btn;
    }

    private boolean fetchUserData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT full_name FROM users WHERE user_id = ?")) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                userName = rs.getString("full_name");
                welcomeLabel.setText("Welcome, " + userName + "!");
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Error: User not found", "Database Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading user data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        }
    }

    private void fetchUserPoints() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT eco_points FROM users WHERE user_id = ?")) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                userPoints = rs.getInt("eco_points");
                pointsLabel.setText("Your EcoPoints: " + userPoints);
            } else {
                 userPoints = 0;
                pointsLabel.setText("Your EcoPoints: " + userPoints);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching points: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            pointsLabel.setText("Your EcoPoints: Error");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitBtn) {
            openNewWindow(new Submission(userId, userEmail));
        } else if (e.getSource() == statusBtn) {
            openNewWindow(new ViewSubmission(userId, userEmail));
        } else if (e.getSource() == pointsBtn) {
            openNewWindow(new CheckEcoPoints(userEmail));
        } else if (e.getSource() == logoutBtn) {
            this.dispose();
            new Welcome().setVisible(true);
        } else if (e.getSource() == refreshDashboardButton) {
            // Refetch data and revalidate/repaint the UI to ensure changes are reflected
            boolean userDataFetched = fetchUserData(); // Store the return value
            fetchUserPoints();
            if (userDataFetched)
            {
                this.revalidate();  // Important:  Forces layout manager to re-layout
                this.repaint();     // Important:  Forces component to repaint
            }

        }
    }

    private void openNewWindow(JFrame newWindow) {
        this.dispose();
        newWindow.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new UserDashboard(1, "test@example.com");
        });
    }
}
