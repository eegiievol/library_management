import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BookCatalogGUI extends JFrame {
    private JTextField titleField, authorField, genreField, descriptionField, priceField, availabilityField;
    private JTextArea resultArea;

    private Connection connection;
    private String username;
    private String userRole;
    private JPanel mainPanel;
    JTextField searchField;

    public BookCatalogGUI(String username) {
        this.username = username;
        this.userRole = "";
        retrieveUserRole();

        setTitle("Book Catalog");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);

        // Initialize components
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(7, 2));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:"));
        authorField = new JTextField();
        inputPanel.add(authorField);
        inputPanel.add(new JLabel("Genre:"));
        genreField = new JTextField();
        inputPanel.add(genreField);
        inputPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Availability:"));
        availabilityField = new JTextField();
        inputPanel.add(availabilityField);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });

        JButton retrieveButton = new JButton("Search books");
        retrieveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = JOptionPane.showInputDialog(BookCatalogGUI.this, "Enter book title:");
                retrieveBooks(searchTerm);
            }
        });

        JButton userManagementButton = new JButton("User Management");
        userManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUserManagementPage();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Set layout with horizontal and vertical gaps
        buttonPanel.add(addButton);
        buttonPanel.add(retrieveButton);
        buttonPanel.add(userManagementButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setPreferredSize(new Dimension(resultArea.getPreferredSize().width, 150));
        JScrollPane scrollPane = new JScrollPane(resultArea);


        // Add components to the frame
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Connect to the database
        connect();
        retrieveUserRole();
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

    private void retrieveUserRole() {
        if (connection != null) { // Check if connection is established
            String query = "SELECT role FROM User WHERE username = ?";

            try {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    userRole = resultSet.getString("role");
                }
            } catch (SQLException e) {
                System.out.println("Failed to retrieve user's role");
                e.printStackTrace();
            }
        }
    }
    private void openUserManagementPage() {
        if (userRole.equals("administrator")) {
            UserManagementPageGUI userManagementPage = new UserManagementPageGUI();
            userManagementPage.setVisible(true);
            dispose(); // Close the home page
        } else {
            JOptionPane.showMessageDialog(BookCatalogGUI.this, "You do not have permission to access user management.");
        }
    }

    private void addBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String genre = genreField.getText();
        String description = descriptionField.getText();
        double price = Double.parseDouble(priceField.getText());
        boolean availability = Boolean.parseBoolean(availabilityField.getText());

        String query = "INSERT INTO books (title, author, genre, description, price, availability) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, title);
            statement.setString(2, author);
            statement.setString(3, genre);
            statement.setString(4, description);
            statement.setDouble(5, price);
            statement.setBoolean(6, availability);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new book was inserted successfully");
                titleField.setText("");
                authorField.setText("");
                genreField.setText("");
                descriptionField.setText("");
                priceField.setText("");
                availabilityField.setText("");
            }
        } catch (SQLException e) {
            System.out.println("Failed to insert the book");
            e.printStackTrace();
        }
    }

    private void retrieveBooks(String searchTerm) {
        String query;

        System.out.println("searchTerm: " + searchTerm);
        if (searchTerm != null && !searchTerm.isEmpty()) {
            query = "SELECT * FROM books WHERE title LIKE ?";
        } else {
            query = "SELECT * FROM books";
        }

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            if (searchTerm != null && !searchTerm.isEmpty()) {
                statement.setString(1, "%" + searchTerm + "%");
            }

            ResultSet resultSet = statement.executeQuery();

            StringBuilder sb = new StringBuilder();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String genre = resultSet.getString("genre");
                String description = resultSet.getString("description");
                double price = resultSet.getDouble("price");
                boolean availability = resultSet.getBoolean("availability");

                sb.append("ID: ")
                        .append(id).append(", Title: ")
                        .append(title)
                        .append(", Author: ")
                        .append(author)
                        .append(", Genre: ")
                        .append(genre)
                        .append(", Description: ")
                        .append(description)
                        .append(", Price: ")
                        .append(price)
                        .append(", Availability: ")
                        .append(availability)
                        .append("\n");
            }

            resultArea.setText(sb.toString());
        } catch (SQLException e) {
            System.out.println("Failed to retrieve books");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BookCatalogGUI bookCatalogGUI = new BookCatalogGUI("unknown");
                bookCatalogGUI.setVisible(true);
            }
        });
    }

}
