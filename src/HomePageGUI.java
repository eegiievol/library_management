import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HomePageGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Connection connection;
    public HomePageGUI() {
        setTitle("Home Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 300);
        setLocationRelativeTo(null);

        // Initialize components
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));
        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        inputPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = String.valueOf(passwordField.getPassword());
                if (login(username, password)) {
                    openMainGUI(username);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(HomePageGUI.this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegistrationPage();
                dispose();
            }
        });

        // Initialize components
//        JButton userManagementButton = new JButton("User Management");
//        userManagementButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                openUserManagementPage();
//            }
//        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Set layout with horizontal and vertical gaps
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
//        buttonPanel.add(userManagementButton);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

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
    private boolean login(String username, String password) {

        String query = "SELECT * FROM User WHERE username = ? AND password = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Login successful");
                JOptionPane.showMessageDialog(HomePageGUI.this, "Login successful!");
                return true;
            } else {
                System.out.println("Invalid username or password");
                JOptionPane.showMessageDialog(HomePageGUI.this, "Invalid username or password");
            }
        } catch (SQLException e) {
            System.out.println("Failed to execute login query");
            e.printStackTrace();
        }
        return false;
    }

    private void openMainGUI(String username) {
        BookCatalogGUI bookCatalogGUI = new BookCatalogGUI(username);
        bookCatalogGUI.setVisible(true);
    }

    private void openRegistrationPage() {
        RegistrationPageGUI registrationPageGUI = new RegistrationPageGUI();
        registrationPageGUI.setVisible(true);
    }

//    private void openUserManagementPage() {
//        UserManagementPageGUI userManagementPage = new UserManagementPageGUI();
//        userManagementPage.setVisible(true);
//        dispose(); // Close the home page
//    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                HomePageGUI homePageGUI = new HomePageGUI();
                homePageGUI.setVisible(true);
            }
        });
    }


}
