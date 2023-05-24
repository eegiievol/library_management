import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserManagementPageGUI extends JFrame {
    private Connection connection;
    private JTextArea userTextArea;

    public UserManagementPageGUI() {
        setTitle("User Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadUsers();
            }
        });

        JButton modifyButton = new JButton("Modify Users");
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openModifyPage();
            }
        });

        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current window
                HomePageGUI homePage = new HomePageGUI();
                homePage.setVisible(true); // Open the HomePageGUI
            }
        });

        JPanel buttonPanel = new JPanel();
//        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(homeButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(modifyButton);

        userTextArea = new JTextArea();
        userTextArea.setEditable(false);
//        userTextArea.setPreferredSize(new Dimension(userTextArea.getPreferredSize().width, 150));
        JScrollPane scrollPane = new JScrollPane(userTextArea);

        // Add components to the frame
        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Connect to the database
        connect();

        // Load and display users
        loadUsers();
    }

    private void connect() {
        String url = "jdbc:mysql://localhost:3306/book_catalog";
        String username = "root";
        String password = "password";

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC driver not found");
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database");
            e.printStackTrace();
        }
    }

    private void openModifyPage() {
        ModifyPageGUI modifyPage = new ModifyPageGUI();
        modifyPage.setVisible(true);
    }
    private void loadUsers() {
        String query = "SELECT * FROM User";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            StringBuilder sb = new StringBuilder();
            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                String role = resultSet.getString("role");

                sb.append("User ID: ").append(userId)
                        .append(", Username: ").append(username)
                        .append(", Password: ").append(password)
                        .append(", Email: ").append(email)
                        .append(", Role: ").append(role)
                        .append("\n");
            }

            userTextArea.setText(sb.toString());
        } catch (SQLException e) {
            System.out.println("Failed to load users");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UserManagementPageGUI userManagementPageGUI = new UserManagementPageGUI();
                userManagementPageGUI.setVisible(true);
            }
        });
    }
}
