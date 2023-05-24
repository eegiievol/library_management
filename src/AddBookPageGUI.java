import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
class AddBookPageGUI extends JFrame {
    private JTextField titleField, authorField, genreField, descriptionField, priceField, availabilityField;

    private Connection connection;
    private JLabel messageLabel;

    public AddBookPageGUI() {
        setTitle("Add Book");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Create the inputPanel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
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

        // Create the messageLabel
        messageLabel = new JLabel();
        messageLabel.setForeground(Color.GREEN);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);


        // Create the buttonPanel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBook();
            }
        });
        buttonPanel.add(saveButton);

        // Create the mainPanel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(messageLabel, BorderLayout.NORTH);

        // Add the mainPanel to the contentPane
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mainPanel, BorderLayout.CENTER);

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

    private void saveBook() {
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
                messageLabel.setText("Book added successfully!");
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
}