import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class StatisticsGUI extends JFrame {
    private JTextArea resultArea;
    private Connection connection;

    public StatisticsGUI() {
        setTitle("Statistics");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        JButton salesAmountButton = new JButton("Generate Book Sales Report");
        JButton popularGenresButton = new JButton("Generate Popular Genres Report");
        JButton maxCartItemButton = new JButton("Generate Max Amount Cart Items Report");
        JButton mostExpensiveSolBookButton = new JButton("Best Most Expensive Books being Sold Report");

        setLayout(new BorderLayout());

        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2));
        buttonPanel.add(salesAmountButton);
        buttonPanel.add(popularGenresButton);
        buttonPanel.add(maxCartItemButton);
        buttonPanel.add(mostExpensiveSolBookButton);

        add(buttonPanel, BorderLayout.SOUTH);

        connect();

        salesAmountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateBookSalesReport();
            }
        });

        popularGenresButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generatePopularGenresReport();
            }
        });

        maxCartItemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateMaxQuantityCartItemReport();
            }
        });

        mostExpensiveSolBookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateMostExpensiveSolBookReport();
            }
        });

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
            System.out.println("StatisticsGUI Connected to the database");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database");
            e.printStackTrace();
        }
    }

    private void generateBookSalesReport() {
        try {
            String sql = "SELECT SUM(s.quantity * b.price) AS total_sales_amount " +
                    "FROM ShoppingCartItem s " +
                    "JOIN Books b ON s.book_id = b.book_id";

            StringBuilder report = new StringBuilder();
            PreparedStatement statement = connection.prepareStatement(sql);
            long startTime = System.currentTimeMillis();
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                double totalSalesAmount = resultSet.getDouble("total_sales_amount");
                report.append("Total Sales Amount: $").append(totalSalesAmount).append("\n");
            }
            long endTime = System.currentTimeMillis();
            long runtime = endTime - startTime;
            report.append("\nRuntime in Millis: ").append(runtime);

            resultArea.setText(report.toString());

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void generatePopularGenresReport() {
        try {
            String sql = "SELECT b.genre, COUNT(s.item_id) AS total_sold " +
                    "FROM ShoppingCartItem s " +
                    "JOIN Books b ON s.book_id = b.book_id " +
                    "GROUP BY b.genre " +
                    "ORDER BY total_sold DESC";
            PreparedStatement statement = connection.prepareStatement(sql);

            long startTime = System.currentTimeMillis();
            ResultSet resultSet = statement.executeQuery();

            StringBuilder report = new StringBuilder();
            report.append("Popular Genres Report:\n");
            while (resultSet.next()) {
                String genre = resultSet.getString("genre");
                int totalSold = resultSet.getInt("total_sold");
                report.append("Genre: ").append(genre).append(", Total Sold: ").append(totalSold).append("\n");
            }
            long endTime = System.currentTimeMillis();
            long runtime = endTime - startTime;
            report.append("\nRuntime in Millis: ").append(runtime);

            resultArea.setText(report.toString());

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void generateMaxQuantityCartItemReport() {
        try {
            String sql = "SELECT cus.name, c.cart_id, i.quantity " +
                    "FROM ShoppingCartItem i " +
                    "JOIN ShoppingCart c ON i.cart_id = c.cart_id " +
                    "JOIN Customer cus ON cus.customer_id = c.customer_id " +
                    "ORDER BY i.quantity DESC";
            PreparedStatement statement = connection.prepareStatement(sql);

            long startTime = System.currentTimeMillis();
            ResultSet resultSet = statement.executeQuery();

            StringBuilder report = new StringBuilder();
            report.append("Max Quantity Cart Items Report:\n");
            while (resultSet.next()) {
                String customerName = resultSet.getString("cus.name");
                int cartId = resultSet.getInt("c.cart_id");
                int quantity = resultSet.getInt("i.quantity");

                report.append("Customer Name: ").append(customerName)
                        .append(", Cart ID: ").append(cartId)
                        .append(", Quantity: ").append(quantity)
                        .append("\n");
            }
            long endTime = System.currentTimeMillis();
            long runtime = endTime - startTime;
            report.append("\nRuntime in Millis: ").append(runtime);

            resultArea.setText(report.toString());

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void generateMostExpensiveSolBookReport() {
        try {
            String sql = "SELECT DISTINCT b.title, b.price " +
                    "FROM ShoppingCartItem s " +
                    "JOIN Books b ON s.book_id = b.book_id " +
                    "ORDER BY b.price DESC";

            PreparedStatement statement = connection.prepareStatement(sql);

            long startTime = System.currentTimeMillis();
            ResultSet resultSet = statement.executeQuery();

            StringBuilder report = new StringBuilder();
            report.append("Best Most Expensive Books being Sold Report:\n");
            while (resultSet.next()) {
                String bookName = resultSet.getString("title");
                double price = resultSet.getDouble("price");
                report.append("Book Name: ").append(bookName).append(", Price: ").append(price).append("\n");
            }
            long endTime = System.currentTimeMillis();
            long runtime = endTime - startTime;
            report.append("\nRuntime in Millis: ").append(runtime);

            resultArea.setText(report.toString());

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                StatisticsGUI statisticsGUI = new StatisticsGUI();
                statisticsGUI.setVisible(true);
            }
        });
    }

}
