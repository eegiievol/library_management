import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistrationPageGUI extends JFrame {
    private JTextField usernameField, passwordField;

    private Connection connection;

    public RegistrationPageGUI() {
        setTitle("Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        // Initialize components
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Password:"));
        passwordField = new JTextField();
        inputPanel.add(passwordField);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();

                // Switch to the homepage (login page)
                HomePageGUI homePageGUI = new HomePageGUI();
                homePageGUI.setVisible(true);
            }
        });

        // Add components to the frame
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER);
        add(registerButton, BorderLayout.SOUTH);

        // Connect to the database
        connect();
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

    private void registerUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        String query = "INSERT INTO User (username, password) VALUES (?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("New user registered successfully");
                JOptionPane.showMessageDialog(RegistrationPageGUI.this, "Registration successful!");
                dispose(); // Close the registration page after successful registration
            }
        } catch (SQLException e) {
            System.out.println("Failed to register the user");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                RegistrationPageGUI registrationPageGUI = new RegistrationPageGUI();
                registrationPageGUI.setVisible(true);
            }
        });
    }
}
