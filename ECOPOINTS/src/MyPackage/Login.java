package MyPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame implements ActionListener {

    private JLabel titleLabel;
    private JLabel emailLabel;
    private JTextField emailField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JLabel roleLabel;
    private JComboBox<String> roleComboBox;
    private JButton loginButton;
    private JLabel noAccountLabel;
    private JButton registerButton;
    private JPanel leftPanel;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecopoints_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "code"; // Replace with your MySQL password

    public Login() {
        setTitle("EcoPoints - Login");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Left Panel
        leftPanel = new JPanel();
        leftPanel.setBounds(0, 0, 450, 550);
        leftPanel.setBackground(new Color(42, 62, 80));
        leftPanel.setLayout(null);
        add(leftPanel);

        JLabel ecoPointsLabel = new JLabel("Eco Points");
        ecoPointsLabel.setFont(new Font("Script MT Bold", Font.PLAIN, 70));
        ecoPointsLabel.setForeground(new Color(92, 184, 92));
        ecoPointsLabel.setBounds(100, 180, 350, 80);
        leftPanel.add(ecoPointsLabel);

        // Right Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setBounds(450, 0, 450, 550);
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(null);
        add(rightPanel);

        titleLabel = new JLabel("LOGIN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(92, 184, 92));
        titleLabel.setBounds(180, 50, 150, 40); // Thoda upar kiya
        rightPanel.add(titleLabel);

        emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        emailLabel.setBounds(150, 130, 100, 25); // Thoda neeche kiya
        rightPanel.add(emailLabel);
        emailField = new JTextField();
        emailField.setBounds(150, 155, 230, 30); // Thoda neeche kiya
        rightPanel.add(emailField);

        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordLabel.setBounds(150, 200, 100, 25); // Aur neeche kiya
        rightPanel.add(passwordLabel);
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 225, 230, 30); // Aur neeche kiya
        rightPanel.add(passwordField);

        roleLabel = new JLabel("Role");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        roleLabel.setBounds(150, 270, 100, 25); // Aur neeche kiya
        rightPanel.add(roleLabel);
        String[] roles = {"user", "admin"};
        roleComboBox = new JComboBox<>(roles);
        roleComboBox.setBounds(150, 295, 100, 30); // Aur neeche kiya
        rightPanel.add(roleComboBox);

        loginButton = new JButton("LOGIN");
        loginButton.setBounds(150, 350, 120, 40); // Aur neeche kiya
        loginButton.setBackground(new Color(92, 184, 92));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(this);
        rightPanel.add(loginButton);

        noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        noAccountLabel.setBounds(130, 410, 150, 20); // Aur neeche kiya
        rightPanel.add(noAccountLabel);

        registerButton = new JButton("SIGN UP");
        registerButton.setBounds(280, 405, 100, 30); // Aur neeche kiya
        registerButton.setBackground(new Color(92, 184, 92));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(this);
        rightPanel.add(registerButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String selectedRole = (String) roleComboBox.getSelectedItem();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both email and password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "SELECT user_id, role FROM users WHERE email = ? AND password = ?")) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int userId = resultSet.getInt("user_id");
                    String databaseRole = resultSet.getString("role");
                    if (selectedRole.equals(databaseRole)) {
                        JOptionPane.showMessageDialog(this, "Login successful as " + selectedRole + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        
                        if ("user".equals(selectedRole)) {
                            new UserDashboard(userId,email).setVisible(true);
                        } else if ("admin".equals(selectedRole)) {
                            new AdminDashboard(userId, email).setVisible(true); // Pass both user ID and email
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Incorrect role selected for the given credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid email or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == registerButton) {
            this.dispose();
            new Registration().setVisible(true);
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}