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
    private int shopCartID;
    private JPanel mainPanel;
    private JPanel cartPanel;


    JTextField searchField;

    public BookCatalogGUI(String username) {
        this.username = username;
        this.userRole = "";
        retrieveUserRole();

        setTitle("Book Catalog");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 530);
//        pack();
        setLocationRelativeTo(null);

        /////////////////////////////////////
        ////BUTTONS//////////////////////////
        /////////////////////////////////////
        JButton addButton = new JButton("AddBook");
//        addButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                addBook();
//            }
//        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                dispose(); // Close the current window
                AddBookPageGUI addBookPage = new AddBookPageGUI();
                addBookPage.setVisible(true);
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

        JButton statisticsButton = new JButton("Statistics");
        statisticsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openStatistics();
            }

        });

        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current window
                HomePageGUI homePage = new HomePageGUI();
                homePage.setVisible(true);
            }
        });

        JButton addToCartButton = new JButton("AddToCart");
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToShoppingCart();
            }
        });

        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openCheckoutPage();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

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

        /////////////////////////////////////
        ////Shop Cart Panel//////////////////
        /////////////////////////////////////
        JPanel cartInputPanel = new JPanel(new GridLayout(5, 2));
        cartInputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        cartInputPanel.add(new JLabel("To add Book to Shopping Cart, enter book_id and quantity below:"));
        cartInputPanel.add(new JLabel("Book ID:"));
        cartBookId = new JTextField();
        cartInputPanel.add(cartBookId);
        cartInputPanel.add(new JLabel("Quantity:"));
        cartQuantity = new JTextField();
        cartInputPanel.add(cartQuantity);

        /////////////////////////////////////
        ////SCROLL///////////////////////////
        /////////////////////////////////////
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setPreferredSize(new Dimension(resultArea.getPreferredSize().width, 500));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        /////////////////////////////////////
        ////PANELS///////////////////////////
        /////////////////////////////////////

        //main buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
//        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(homeButton);
        buttonPanel.add(addButton);
        buttonPanel.add(retrieveButton);

        //extra button
        buttonPanel.add(userManagementButton);
        buttonPanel.add(statisticsButton);

        //cart buttons
        JPanel buttonPanelCart = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
//        buttonPanelCart.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanelCart.add(addToCartButton);
        buttonPanelCart.add(checkoutButton);

        // Create parent panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
//        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Cart panel
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.add(cartInputPanel, BorderLayout.CENTER);
        cartPanel.add(buttonPanelCart, BorderLayout.SOUTH);

        // Add main panel and cart panel to parent panel
        contentPanel.add(mainPanel);
        contentPanel.add(cartPanel);

        // Add the parent panel and scroll pane to the container
        add(contentPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);


        // initialization
        connect();
        retrieveUserRole();
        retrieveBooks(null);
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

    private void openStatistics(){
        if (userRole.equals("administrator")) {
            // Open the StatisticsGUI
            StatisticsGUI statisticsGUI = new StatisticsGUI();
            statisticsGUI.setVisible(true);
        }
        else {
            JOptionPane.showMessageDialog(BookCatalogGUI.this, "You do not have permission to access statistics.");

        }
    }

    private void retrieveUserRole() {
        if (connection != null) {
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
            dispose();
        } else {
            JOptionPane.showMessageDialog(BookCatalogGUI.this, "You do not have permission to access user management.");
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

    private int getUserIdForCart() throws SQLException {
        int userid = -1;
        String query = "SELECT user_id FROM User WHERE username = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, this.username);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            userid = resultSet.getInt("user_id");
            System.out.println("getUserIdForCart -> userid: " + userid);
            return userid;
        }

        throw new SQLException("Failed to retrieve USER ID");
    }
    private int getCartIdForCustomer() throws SQLException {
        String query = "SELECT cart_id FROM ShoppingCart WHERE customer_id = ?";
        int cartid = -1;

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, getCustomerId());

        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            cartid = resultSet.getInt("cart_id");
            System.out.println("getCartIdForCustomer -> cartid: " + cartid);
            return cartid;
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
        int userid = getUserIdForCart();
        int customerid = -1;

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

        throw new SQLException("Failed to retrieve CUSTOMER ID");
    }

    private void openCheckoutPage() throws SQLException {
        int cartID = getCartIdForCustomer();
        System.out.println("openCheckoutPage -> cartID: " + cartID);
        CheckoutPageGUI checkoutPage = new CheckoutPageGUI(cartID);
        checkoutPage.setVisible(true);
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
