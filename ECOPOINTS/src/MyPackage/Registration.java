package MyPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends JFrame implements ActionListener {

    private JLabel titleLabel;
    private JLabel fullNameLabel;
    private JTextField fullNameField;
    private JLabel emailLabel;
    private JTextField emailField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JLabel roleLabel;
    private JComboBox<String> roleComboBox;
    private JButton signUpButton;
    private JLabel haveAccountLabel;
    private JButton loginButton;
    private JPanel leftPanel; // Panel for the dark blue background

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecopoints_db";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "code"; // Replace with your MySQL password

    public Registration() {
        setTitle("EcoPoints - Sign Up");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        setLocationRelativeTo(null);
        setLayout(null); // Use absolute layout

        // Left Panel with dark blue background
        leftPanel = new JPanel();
        leftPanel.setBounds(0, 0, 450, 550); // Covers the left side
        leftPanel.setBackground(new Color(42, 62, 80)); // Dark blue color
        leftPanel.setLayout(null); // Use null layout for components within this panel
        add(leftPanel);

        // "Eco Points" Label (on the left panel)
        JLabel ecoPointsLabel = new JLabel("Eco Points");
        ecoPointsLabel.setFont(new Font("Script MT Bold", Font.PLAIN, 70));
        ecoPointsLabel.setForeground(new Color(92, 184, 92)); // Green color
        ecoPointsLabel.setBounds(100, 180, 350, 80);
        leftPanel.add(ecoPointsLabel);

        // Right side (white background)
        JPanel rightPanel = new JPanel();
        rightPanel.setBounds(450, 0, 450, 550); // Covers the right side
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(null); // Use null layout for components within this panel
        add(rightPanel);

        // Title Label ("SIGN UP")
        titleLabel = new JLabel("SIGN UP");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(92, 184, 92)); // Green color
        titleLabel.setBounds(150, 50, 150, 40); // Relative to the rightPanel
        rightPanel.add(titleLabel);

        // Full Name Label and Field
        fullNameLabel = new JLabel("Full Name");
        fullNameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        fullNameLabel.setBounds(150, 120, 100, 25); // Relative to the rightPanel
        rightPanel.add(fullNameLabel);
        fullNameField = new JTextField();
        fullNameField.setBounds(150, 145, 230, 30); // Relative to the rightPanel
        rightPanel.add(fullNameField);

        // Email Label and Field
        emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setBounds(150, 190, 100, 25); // Relative to the rightPanel
        rightPanel.add(emailLabel);
        emailField = new JTextField();
        emailField.setBounds(150, 215, 230, 30); // Relative to the rightPanel
        rightPanel.add(emailField);

        // Password Label and Field
        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordLabel.setBounds(150, 260, 100, 25); // Relative to the rightPanel
        rightPanel.add(passwordLabel);
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 285, 230, 30); // Relative to the rightPanel
        rightPanel.add(passwordField);

        // Role Label and ComboBox
        roleLabel = new JLabel("Role");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        roleLabel.setBounds(150, 330, 100, 25); // Relative to the rightPanel
        rightPanel.add(roleLabel);
        String[] roles = {"user", "admin"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setBounds(150, 355, 100, 30); // Relative to the rightPanel
        rightPanel.add(roleComboBox);

        // Sign Up Button
        signUpButton = new JButton("SIGN UP");
        signUpButton.setBounds(150, 400, 120, 40); // Relative to the rightPanel
        signUpButton.setBackground(new Color(92, 184, 92));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFont(new Font("Arial", Font.BOLD, 16));
        signUpButton.setFocusPainted(false);
        signUpButton.addActionListener(this);
        rightPanel.add(signUpButton);

        // "I have an Account" Label
        haveAccountLabel = new JLabel("I have an Account");
        haveAccountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        haveAccountLabel.setBounds(130, 455, 150, 20); // Relative to the rightPanel
        rightPanel.add(haveAccountLabel);

        // Login Button
        loginButton = new JButton("LOGIN");
        loginButton.setBounds(280, 450, 100, 30); // Relative to the rightPanel
        loginButton.setBackground(new Color(92, 184, 92));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(this);
        rightPanel.add(loginButton);

        setVisible(true);
    }

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Password validation pattern (at least one uppercase, one number, one special character)
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!¡¿'´`~¨,:;.<>/\\|*\\[\\](){}-]).+$");

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == signUpButton) {
            // Your existing sign up logic remains the same
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            if (fullName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your full name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
            if (!emailMatcher.matches()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Matcher passwordMatcher = PASSWORD_PATTERN.matcher(password);
            if (!passwordMatcher.matches()) {
                JOptionPane.showMessageDialog(this, "Password must contain at least one uppercase letter, one number, and one special character.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "INSERT INTO users (full_name, email, password, role) VALUES (?, ?, ?, ?)")) {
                preparedStatement.setString(1, fullName);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, password);
                preparedStatement.setString(4, role);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Registration successful as " + role + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    fullNameField.setText("");
                    emailField.setText("");
                    passwordField.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == loginButton) {
            // Close the current registration window
            this.dispose();
            
            // Open the login window
            SwingUtilities.invokeLater(() -> {
                new Login().setVisible(true);
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Registration::new);
    }
}