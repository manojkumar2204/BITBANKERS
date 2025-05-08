package MyPackage;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ViewSubmission extends JFrame implements ActionListener {

    private JTable submissionTable;
    private DefaultTableModel tableModel;
    private JButton backButton, refreshButton;
    private int loggedInUserId;
    private String loggedInEmail;

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ecopoints_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "code";

    public ViewSubmission(int userId, String email) {
        this.loggedInUserId = userId;
        this.loggedInEmail = email;

        setTitle("EcoPoints - View Submissions");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 245, 240)); // Light green background

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(getContentPane().getBackground());
        add(mainPanel);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Your Submissions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(34, 139, 34)); // Forest green
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Add refresh button to header
        refreshButton = createStyledButton("Refresh", new Color(70, 130, 180), this);
        refreshButton.setPreferredSize(new Dimension(100, 35));
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"ID", "Waste Type", "Quantity (kg)", "Submission Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        submissionTable = new JTable(tableModel);
        styleTable(submissionTable);
        
        JScrollPane scrollPane = new JScrollPane(submissionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with back button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        backButton = createStyledButton("Back to Dashboard", new Color(169, 169, 169), this);
        backButton.setPreferredSize(new Dimension(180, 40));
        bottomPanel.add(backButton);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        fetchSubmissions();
        setVisible(true);
    }

    private void styleTable(JTable table) {
        // Table appearance
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(220, 237, 200));
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 1));
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.BLACK);
        header.setReorderingAllowed(false);
        
        // Remove the cursor change effect on header
        header.setCursor(Cursor.getDefaultCursor());
        
        // Center align ID column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        // Right align quantity column
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        
        // Status column styling
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
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

    private void fetchSubmissions() {
        tableModel.setRowCount(0); // Clear existing data

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT submission_id, waste_type, quantity, submission_date, status " +
                     "FROM submissions WHERE user_id = ? ORDER BY submission_date DESC")) {
            
            preparedStatement.setInt(1, loggedInUserId);
            ResultSet resultSet = preparedStatement.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
            
            while (resultSet.next()) {
                Object[] row = {
                    resultSet.getInt("submission_id"),
                    resultSet.getString("waste_type"),
                    String.format("%.2f", resultSet.getDouble("quantity")),
                    dateFormat.format(resultSet.getTimestamp("submission_date")),
                    resultSet.getString("status")
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
        	new UserDashboard(loggedInUserId, loggedInEmail).setVisible(true);
            this.dispose();
        } else if (e.getSource() == refreshButton) {
            fetchSubmissions();
        }
    }

    // Custom renderer for status column
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            String status = value.toString();
            setHorizontalAlignment(JLabel.CENTER);
            
            switch (status.toLowerCase()) {
                case "pending":
                    c.setBackground(new Color(255, 243, 205)); // Light yellow
                    c.setForeground(new Color(133, 100, 4));
                    break;
                case "approved":
                    c.setBackground(new Color(212, 237, 218)); // Light green
                    c.setForeground(new Color(15, 81, 50));
                    break;
                case "rejected":
                    c.setBackground(new Color(248, 215, 218)); // Light red
                    c.setForeground(new Color(132, 32, 41));
                    break;
                default:
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
            }
            
            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            }
            
            return c;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ViewSubmission(1, "test@example.com");
        });
    }
}