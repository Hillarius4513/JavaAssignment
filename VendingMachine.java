# JavaAssignmentimport javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class VendingMachine {
    // Create a map to hold product information (name, price, and image path)
    private static final HashMap<String, Product> products = new HashMap<>();
    private static final LinkedHashMap<String, Integer> selectedProducts = new LinkedHashMap<>();

    // Create a map to hold user credentials (username, password)
    private static final HashMap<String, String> users = new HashMap<>();

    // Variable to track if the user is logged in
    private static boolean isLoggedIn = false;
    private static String currentUsername = "";

    // Flag to track if the user has logged out
    private static boolean isLoggedOut = false;

    public static void main(String[] args) {
        // Define the path to the image folder
        String imageFolderPath = "C:\\Users\\hillarius\\Desktop\\images\\";

        // Initialize the product data
        products.put("CocaCola", new Product("CocaCola", 1000, imageFolderPath + "coca.jpeg"));
        products.put("Pepsi", new Product("Pepsi", 1000, imageFolderPath + "pepsi.jpeg"));
        products.put("Fanta Orange", new Product("Fanta Orange", 1000, imageFolderPath + "fanta.jpeg"));
        products.put("Sprite", new Product("Sprite", 1000, imageFolderPath + "sprite.jpeg"));
        products.put("7Up", new Product("7Up", 9000, imageFolderPath + "7up.jpeg"));
        products.put("Mountain Dew", new Product("Mountain Dew", 1100, imageFolderPath + "mountain.jpeg"));
        products.put("Mirinda", new Product("Mirinda", 1000, imageFolderPath + "mirinda.jpeg"));
        products.put("Root Beer", new Product("Root Beer", 2000, imageFolderPath + "root.jpeg"));
        products.put("Ginger", new Product("Ginger", 1200, imageFolderPath + "ginger.jpeg"));
        products.put("Tonic Water", new Product("Tonic Water", 1400, imageFolderPath + "tonic.jpeg"));
        products.put("Dr Pepper", new Product("Dr Pepper", 1500, imageFolderPath + "pepper.jpeg"));
        products.put("Water", new Product("Water", 1000, imageFolderPath + "Water.jpeg"));

        // Create the main frame
        JFrame frame = new JFrame("Vending Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Prompt for login if not logged in and not logged out
        if (!isLoggedOut) {
            loginPrompt(frame);
        }

        // Create a panel for the header (Welcome Message)
        JPanel headerPanel = new JPanel();
        JLabel welcomeLabel = new JLabel("Welcome to Hillarius's Products!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        headerPanel.add(welcomeLabel);

        // Create a panel for the products
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new GridLayout(6, 2, 10, 10));

        // Create a label to display the total cost
        JLabel costLabel = new JLabel("Total: Tsh 0.00", JLabel.CENTER);
        costLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));

        // Create a button for making payments
        JButton payButton = new JButton("Make Payment");
        payButton.setEnabled(false); // Initially disabled until a product is selected
        payButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        payButton.setBackground(Color.GRAY);
        payButton.setForeground(Color.PINK);

        // Add product buttons with images and labels
        for (String key : products.keySet()) {
            Product product = products.get(key);

            JPanel productContainer = new JPanel();
            productContainer.setLayout(new BorderLayout(5, 5));

            // Create a button with the product image
            JButton productButton = new JButton(new ImageIcon(product.getImagePath()));
            productButton.setToolTipText(product.getName());
            productButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Prompt the user for the quantity
                    String input = JOptionPane.showInputDialog(frame, "How many " + key + " do you want?", "Quantity", JOptionPane.PLAIN_MESSAGE);
                    if (input == null || input.isEmpty()) {
                        return;
                    }

                    try {
                        int quantity = Integer.parseInt(input);
                        if (quantity > 0) {
                            selectedProducts.put(key, quantity);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Please enter a valid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    // Update total cost
                    double totalCost = selectedProducts.keySet().stream()
                            .mapToDouble(k -> products.get(k).getPrice() * selectedProducts.get(k))
                            .sum();
                    costLabel.setText(String.format("Total: Tsh%.2f", totalCost));

                    // Enable or disable the pay button based on selection
                    payButton.setEnabled(!selectedProducts.isEmpty());
                }
            });

            // Add product name and price labels
            JLabel nameLabel = new JLabel(product.getName(), JLabel.CENTER);
            JLabel priceLabel = new JLabel(String.format("Tsh%.2f", product.getPrice()), JLabel.CENTER);

            productContainer.add(productButton, BorderLayout.CENTER);
            productContainer.add(nameLabel, BorderLayout.NORTH);
            productContainer.add(priceLabel, BorderLayout.SOUTH);

            productPanel.add(productContainer);
        }

        // Add action listener to the payment button
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ask the user to input the payment amount
                String input = JOptionPane.showInputDialog(frame, "Enter the amount of money you are depositing:", "Payment", JOptionPane.PLAIN_MESSAGE);
                if (input == null || input.isEmpty()) {
                    return;
                }

                try {
                    double paymentAmount = Double.parseDouble(input);

                    // Calculate total cost of selected products
                    double totalCost = selectedProducts.keySet().stream()
                            .mapToDouble(k -> products.get(k).getPrice() * selectedProducts.get(k))
                            .sum();

                    if (paymentAmount >= totalCost) {
                        double change = paymentAmount - totalCost;

                        StringBuilder receipt = new StringBuilder("Receipt:\n");
                        for (String key : selectedProducts.keySet()) {
                            Product product = products.get(key);
                            int quantity = selectedProducts.get(key);
                            double cost = product.getPrice() * quantity;
                            receipt.append(String.format("%s x%d - Tsh%.2f\n", product.getName(), quantity, cost));
                        }
                        receipt.append(String.format("\nTotal: Tsh%.2f\n", totalCost));
                        receipt.append(String.format("Amount Paid: Tsh%.2f\n", paymentAmount));
                        receipt.append(String.format("Change: Tsh%.2f", change));

                        JOptionPane.showMessageDialog(frame, receipt.toString(), "Payment Successful", JOptionPane.INFORMATION_MESSAGE);

                        // Thank the customer
                        JOptionPane.showMessageDialog(frame, "Thank you for using Hillarius's Products!", "Thank You", JOptionPane.INFORMATION_MESSAGE);

                        // If logged in, prompt for logout
                        if (isLoggedIn) {
                            int logoutChoice = JOptionPane.showConfirmDialog(frame, "Would you like to log out?", "Logout", JOptionPane.YES_NO_OPTION);
                            if (logoutChoice == JOptionPane.YES_OPTION) {
                                isLoggedIn = false;
                                currentUsername = "";
                                isLoggedOut = true;
                                JOptionPane.showMessageDialog(frame, "You have logged out successfully.");
                            }
                        }

                        // Reset the selections
                        selectedProducts.clear();
                        costLabel.setText("Total: Tsh 0.00");
                        payButton.setEnabled(false);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Insufficient funds. Please deposit at least $" + totalCost, "Payment Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid amount entered. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Create the layout for the frame
        frame.setLayout(new BorderLayout());
        frame.add(headerPanel, BorderLayout.NORTH);  // Add the welcome panel at the top
        frame.add(new JScrollPane(productPanel), BorderLayout.CENTER); // Add scroll for the product panel
        frame.add(costLabel, BorderLayout.SOUTH);
        frame.add(payButton, BorderLayout.EAST);

        // Make the frame visible
        frame.setVisible(true);
    }

    // Login prompt
    private static void loginPrompt(JFrame frame) {
        String[] options = {"Yes", "No"};
        int choice = JOptionPane.showOptionDialog(frame, "Would you like to have an account?", "Account Check", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 1) {  // If the customer does not have an account
            // Directly access products
            return;
        }

        // Register a new account
        String username = JOptionPane.showInputDialog(frame, "Enter your username:");
        String password = JOptionPane.showInputDialog(frame, "Enter your password:");

        // Store the credentials
        users.put(username, password);

        JOptionPane.showMessageDialog(frame, "Account created successfully. Please log in.");

        // Log in
        while (true) {
            username = JOptionPane.showInputDialog(frame, "Enter your username:");
            password = JOptionPane.showInputDialog(frame, "Enter your password:");

            // Validate login
            if (users.containsKey(username) && users.get(username).equals(password)) {
                isLoggedIn = true;
                currentUsername = username;
                JOptionPane.showMessageDialog(frame, "Welcome " + username + "to hillarius's Poducts!");
                break;
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Product class to hold product details
    static class Product {
        private final String name;
        private final double price;
        private final String imagePath;

        public Product(String name, double price, String imagePath) {
            this.name = name;
            this.price = price;
            this.imagePath = imagePath;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String getImagePath() {
            return imagePath;
        }
    }
}




