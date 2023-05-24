import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BookCatalogGUI extends JFrame {
    private JTextField titleField, authorField, genreField, descriptionField, priceField, availabilityField;

    private JTextField cartBookId, cartQuantity;
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
        setSize(500, 700);
//        pack();
        setLocationRelativeTo(null);

        /////////////////////////////////////
        ////inputPanel///////////////////////
        /////////////////////////////////////
        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
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

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        /////////////////////////////////////
        ////inputPanelCart///////////////////
        /////////////////////////////////////
        JPanel cartPanel = new JPanel(new GridLayout(3, 2));
        cartPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        cartPanel.add(new JLabel("Book ID:"));
        cartBookId = new JTextField();
        cartPanel.add(cartBookId);
        cartPanel.add(new JLabel("Quantity:"));
        cartQuantity = new JTextField();
        cartPanel.add(cartQuantity);

        mainPanel.add(cartPanel, BorderLayout.CENTER);

        /////////////////////////////////////
        ////BUTTONS//////////////////////////
        /////////////////////////////////////
        JButton addButton = new JButton("AddBook");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook();
            }
        });

        JButton retrieveButton = new JButton("Search");
        retrieveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchTerm = JOptionPane.showInputDialog(BookCatalogGUI.this, "Enter book title:");
                retrieveBooks(searchTerm);
            }
        });

        JButton userManagementButton = new JButton("ManageUser");
        userManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUserManagementPage();
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

        JButton addToCartButton = new JButton("AddToCart");
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToShoppingCart();
            }
        });

        /////////////////////////////////////
        ////SCROLL///////////////////////////
        /////////////////////////////////////
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setPreferredSize(new Dimension(resultArea.getPreferredSize().width, 200));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(400, 150)); // Set the preferred size of the scroll pane

        /////////////////////////////////////
        ////PANELS///////////////////////////
        /////////////////////////////////////

        //main buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Set layout with horizontal and vertical gaps
        buttonPanel.add(homeButton);
        buttonPanel.add(addButton);
        buttonPanel.add(retrieveButton);
        buttonPanel.add(addToCartButton);
        buttonPanel.add(userManagementButton);

        //layouts
        mainPanel.add(cartPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // initialization
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
                    System.out.println("userRole: " + userRole);
                }
            } catch (SQLException e) {
                System.out.println("Failed to retrieve user's role");
                e.printStackTrace();
            }
        }
    }
    private void openUserManagementPage() {
        if (userRole!=null && userRole.equals("administrator")) {
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
                int id = resultSet.getInt("book_id");
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


    private void addToShoppingCart() {
        int bookId = Integer.parseInt(cartBookId.getText());
        int quantity = Integer.parseInt(cartQuantity.getText());

        String query = "INSERT INTO ShoppingCartItem (cart_id, book_id, quantity) VALUES (?, ?, ?)";

        try {
            // Get the cart ID for the current customer
            int cartId = getCartIdForCustomer();

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, cartId);
            statement.setInt(2, bookId);
            statement.setInt(3, quantity);
            System.out.println("cartId: " + cartId + ", bookId: " +bookId + ", quantity: " +quantity);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("The book was added to the shopping cart successfully");
                cartBookId.setText("");
            }
        } catch (SQLException e) {
            System.out.println("Failed to add the book to the shopping cart");
            e.printStackTrace();
        }
    }

    private int getCartIdForCustomer() throws SQLException {
        String query = "SELECT cart_id FROM ShoppingCart WHERE customer_id = ?";

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, getCustomerId());

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("cart_id");
        }

        return createNewShoppingCart();
    }

    private int createNewShoppingCart() throws SQLException {
        String query = "INSERT INTO ShoppingCart (customer_id) VALUES (?)";
        PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, getCustomerId());

        int rowsInserted = statement.executeUpdate();
        if (rowsInserted > 0) {
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }

        throw new SQLException("Failed to create a new shopping cart");
    }

    private int getCustomerId() throws SQLException {
        //
        int userid=0;
        int customerid=0;

        //
        String query = "SELECT customer_id FROM Customer WHERE user_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, userid);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            customerid = resultSet.getInt("customer_id");
            System.out.println("getCustomerId -> customerid: " + customerid);
            return customerid;
        }

        throw new SQLException("Failed to retrieve customer ID");
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
