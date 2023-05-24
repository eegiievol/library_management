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

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(salesAmountButton);
        buttonPanel.add(popularGenresButton);
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
            PreparedStatement statement = connection.prepareStatement(sql);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                double totalSalesAmount = resultSet.getDouble("total_sales_amount");
                resultArea.setText("Total Sales Amount: $" + totalSalesAmount);
            }

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

            ResultSet resultSet = statement.executeQuery();

            StringBuilder report = new StringBuilder();
            report.append("Popular Genres Report:\n");
            while (resultSet.next()) {
                String genre = resultSet.getString("genre");
                int totalSold = resultSet.getInt("total_sold");
                report.append("Genre: ").append(genre).append(", Total Sold: ").append(totalSold).append("\n");
            }

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
