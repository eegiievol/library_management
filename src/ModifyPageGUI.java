import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ModifyPageGUI extends JFrame {
    private Connection connection;
    private JTextField usernameField, passwordField, emailField;
    private JComboBox<String> roleComboBox;

    public ModifyPageGUI() {
        setTitle("Modify Users");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        // Initialize components
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        inputPanel.add(usernameField);
        inputPanel.add(new JLabel("Password:"));
        passwordField = new JTextField();
        inputPanel.add(passwordField);
        inputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Role:"));
        roleComboBox = new JComboBox<>(new String[]{"customer", "administrator", "employee"});
        inputPanel.add(roleComboBox);

        JButton addButton = new JButton("Add User");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        JButton deleteButton = new JButton("Delete User");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    deleteUser();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Set layout with horizontal and vertical gaps
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);


        // Add components to the frame
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);




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

    private void addUser() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String role = (String) roleComboBox.getSelectedItem();

        String query = "INSERT INTO User (username, password, email, role) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);
            statement.setString(4, role);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("User added successfully");
                JOptionPane.showMessageDialog(ModifyPageGUI.this, "User added successfully!");
                clearFields();
            }
        } catch (SQLException e) {
            System.out.println("Failed to add user");
            e.printStackTrace();
        }
    }

    private void deleteUser() throws SQLException {
        String username = usernameField.getText();

        //get userID first
        int userid = -1;
        String query = "SELECT user_id FROM User WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            userid = resultSet.getInt("user_id");
            System.out.println("getUserIdForCart -> userid: " + userid);
        }

        //get customer id
        int customerid = -1;
        String csquery = "SELECT customer_id FROM Customer WHERE user_id = ?";
        PreparedStatement csstatement = connection.prepareStatement(csquery);
        csstatement.setInt(1, userid);
        ResultSet csresultSet = csstatement.executeQuery();
        if (csresultSet.next()) {
            customerid = csresultSet.getInt("customer_id");
            System.out.println("getCustomerId -> customerid: " + customerid);
        }

        //delete customer
        String dQuery = "DELETE FROM Customer WHERE customer_id = ?";
        try {
            PreparedStatement dqStatement = connection.prepareStatement(dQuery);
            dqStatement.setInt(1, customerid);

            int rowsDeleted = dqStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("User deleted successfully");
                JOptionPane.showMessageDialog(ModifyPageGUI.this, "customer deleted successfully!");
                clearFields();
            }
        } catch (SQLException e) {
            System.out.println("Failed to delete customer");
            e.printStackTrace();
        }

        //delete user
        String duQuery = "DELETE FROM User WHERE user_id = ?";
        try {
            PreparedStatement duStatement = connection.prepareStatement(duQuery);
            duStatement.setInt(1, userid);

            int rowsDeleted = duStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("User deleted successfully");
                JOptionPane.showMessageDialog(ModifyPageGUI.this, "User deleted successfully!");
                clearFields();
            }
        } catch (SQLException e) {
            System.out.println("Failed to delete user");
            e.printStackTrace();
        }
    }

    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        roleComboBox.setSelectedItem(null);
    }

}