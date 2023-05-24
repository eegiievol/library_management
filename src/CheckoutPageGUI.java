import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CheckoutPageGUI extends JFrame {
    private JTextArea cartItemsArea;
    private Connection connection;

    public CheckoutPageGUI(int cartID) {
        setTitle("Checkout");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        cartItemsArea = new JTextArea();
        cartItemsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(cartItemsArea);

        this.connect();
        retrieveCartItems(cartID);

        JButton payButton = new JButton("Pay");
        payButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Implement the payment logic here
                // For example, display a payment success message
                JOptionPane.showMessageDialog(null, "Payment successful!");
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane);
        panel.add(Box.createVerticalStrut(10));
        panel.add(payButton);

        add(panel);

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
            System.out.println("CheckoutPageGUI Connected to the database");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database");
            e.printStackTrace();
        }
    }
    private void retrieveCartItems(int shopCartID) {
        try {
            String sql = "SELECT s.item_id, b.title, s.quantity FROM ShoppingCartItem s INNER JOIN Books b ON s.book_id = b.book_id WHERE s.cart_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, shopCartID);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int itemId = resultSet.getInt("item_id");
                String title = resultSet.getString("title");
                int quantity = resultSet.getInt("quantity");

                String itemDetails = "Item ID: " + itemId + ", Title: " + title + ", Quantity: " + quantity;
                cartItemsArea.append(itemDetails + "\n");
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CheckoutPageGUI checkoutPageGUI = new CheckoutPageGUI(-1);
                checkoutPageGUI.setVisible(true);
            }
        });
    }

}
