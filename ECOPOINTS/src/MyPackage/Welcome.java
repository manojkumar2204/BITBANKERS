package MyPackage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Welcome extends JFrame implements ActionListener {

    private JLabel titleLabel;
    private JLabel taglineLabel;
    private JButton registerButton;
    private JButton loginButton;
    private JButton aboutButton;

    public Welcome() {
        // Frame Setup
        setTitle("ECOPOINTS – Waste Management Reward System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame
        getContentPane().setBackground(new Color(42, 62, 80));
        setLayout(null); // Use absolute layout

        // Title Label
        titleLabel = new JLabel("EcoPoints");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 60));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(250, 100, 300, 60);
        add(titleLabel);

        // Tagline Label
        taglineLabel = new JLabel("Turn Waste into Rewards");
        taglineLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        taglineLabel.setForeground(Color.WHITE);
        taglineLabel.setBounds(250, 160, 300, 30);
        add(taglineLabel);

        // Register Button
        registerButton = createButton("REGISTER", 225, 250, 150, 50);
        registerButton.addActionListener(this);
        add(registerButton);

        // Login Button
        loginButton = createButton("LOGIN", 425, 250, 150, 50);
        loginButton.addActionListener(this);
        add(loginButton);

        // About Button
        aboutButton = createButton("ABOUT", 670, 420, 100, 30);
        aboutButton.addActionListener(this);
        add(aboutButton);

        // Make the frame visible
        setVisible(true);
    }

    private JButton createButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setBackground(new Color(92, 184, 92)); // Green color
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            System.out.println("Register button clicked");
            Registration registrationForm = new Registration();
        } else if (e.getSource() == loginButton) {
            System.out.println("Login button clicked");
            Login log = new Login();
        } else if (e.getSource() == aboutButton) {
            JOptionPane.showMessageDialog(this,
                    "This application encourages users to submit their household waste with proof, which is then verified by administrators. Upon approval, users earn EcoPoints that are added to their accounts.\n\n" +
                    "Our goals are to promote waste collection, foster a cleaner environment, and reward individuals for their responsible waste management efforts.",
                    "About EcoPoints",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Welcome::new);
    }

    
}