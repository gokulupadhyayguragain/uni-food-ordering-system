// GOKUL UPADHYAY GURAGAIN (NP069822)
package JavaFiles;

import JavaFiles.LoginSignup;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Admin class for managing the University Food Ordering System.
 * This panel allows the admin to create, update, delete users,
 * search, sort, generate and send receipts.
 *
 * The frame always displays a header with:
 *  - "University Food Ordering System" in the center,
 *  - LBEF logo at the left, and
 *  - APU logo at the right.
 * Underneath, a sub-title "Admin Page" is displayed.
 * The main area displays the user table with a 10% margin from all sides.
 * Buttons at the bottom include Create, Update, Delete, Generate Receipt, Send Receipt, Back, and Logout.
 */
public class Admin extends JFrame {
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchBtn, resetBtn, updateUserBtn, deleteUserBtn, generateReceiptBtn, sendReceiptBtn, sortByNameBtn, sortByRoomBtn;
    private JButton logoutBtn, backBtn, createUserBtn;
    private List<String[]> userData;

    /**
     * Constructor: sets up the Admin panel.
     */
    public Admin() {
        // Frame configuration
        setTitle("Admin Panel - University Food Ordering System");
        setSize(1920, 1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ----------------------------
        // HEADER PANEL: Logos and Title
        // ----------------------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        headerPanel.setBackground(new Color(220, 220, 220));

        // Left: LBEF logo
        ImageIcon lbefLogo = new ImageIcon("src/Images/lbef_logo.png"); // adjust path as needed
        JLabel lbefLabel = new JLabel(lbefLogo);
        headerPanel.add(lbefLabel, BorderLayout.WEST);

        // Center: Main Title
        JLabel titleLabel = new JLabel("University Food Ordering System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Right: APU logo
        ImageIcon apuLogo = new ImageIcon("src/Images/apu_logo.png"); // adjust path as needed
        JLabel apuLabel = new JLabel(apuLogo);
        headerPanel.add(apuLabel, BorderLayout.EAST);

        // ----------------------------
        // MAIN PANEL: Contains sub-title, table, search, and action buttons
        // ----------------------------
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(50, 192, 50, 192)); // 10% margin approximated for 1920x1200

        // Sub-title: "Admin Page"
        JLabel adminPageLabel = new JLabel("Admin Page", SwingConstants.CENTER);
        adminPageLabel.setFont(new Font("Arial", Font.BOLD, 26));
        mainPanel.add(adminPageLabel, BorderLayout.NORTH);

        // Table for user management
        String[] columns = {"User Type", "First Name", "Last Name", "Username", "Password", "Email", "Contact", "Block", "Room"};
        tableModel = new DefaultTableModel(columns, 0);
        userTable = new JTable(tableModel);
        userTable.setRowHeight(30);

        loadUsers(); // load data from file

        JScrollPane tableScrollPane = new JScrollPane(userTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Search Panel: Allows search and sorting
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search User:"));
        searchField = new JTextField(20);
        searchBtn = new JButton("Search");
        resetBtn = new JButton("Reset");
        sortByNameBtn = new JButton("Sort by Name");
        sortByRoomBtn = new JButton("Sort by Room");
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(resetBtn);
        searchPanel.add(sortByNameBtn);
        searchPanel.add(sortByRoomBtn);
        // Place search panel at the top of mainPanel (under sub-title)
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Buttons Panel: Create, Update, Delete, Generate Receipt, Send Receipt, Back, Logout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        createUserBtn = new JButton("Create User");
        updateUserBtn = new JButton("Update User");
        deleteUserBtn = new JButton("Delete User");
        generateReceiptBtn = new JButton("Generate Receipt");
        sendReceiptBtn = new JButton("Send Receipt");
        backBtn = new JButton("Back");
        logoutBtn = new JButton("Logout");

        buttonPanel.add(createUserBtn);
        buttonPanel.add(updateUserBtn);
        buttonPanel.add(deleteUserBtn);
        buttonPanel.add(generateReceiptBtn);
        buttonPanel.add(sendReceiptBtn);
        buttonPanel.add(backBtn);
        buttonPanel.add(logoutBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add header and main panel to frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // ----------------------------
        // ACTION LISTENERS
        // ----------------------------
        searchBtn.addActionListener(e -> searchUser());
        resetBtn.addActionListener(e -> loadUsers());
        sortByNameBtn.addActionListener(e -> sortUsers(1));  // sort by First Name
        sortByRoomBtn.addActionListener(e -> sortUsers(8));  // sort by Room

        createUserBtn.addActionListener(e -> createUser());
        updateUserBtn.addActionListener(e -> updateUser());
        deleteUserBtn.addActionListener(e -> deleteUser());
        generateReceiptBtn.addActionListener(e -> generateReceipt());
        sendReceiptBtn.addActionListener(e -> sendReceipt());
        logoutBtn.addActionListener(e -> logout());
        backBtn.addActionListener(e -> goBack());

        setVisible(true);
    }

    /**
     * Loads users from Credentials.txt into userData and tableModel.
     */
    private void loadUsers() {
        userData = new ArrayList<>();
        tableModel.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader("src/TextFiles/Credentials.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Assuming fields are separated by spaces
                String[] data = line.split(" ");
                userData.add(data);
                tableModel.addRow(data);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading users!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Searches users by first name or username.
     */
    private void searchUser() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            loadUsers();
            return;
        }
        tableModel.setRowCount(0);
        for (String[] user : userData) {
            if (user[1].toLowerCase().contains(query) || user[3].toLowerCase().contains(query)) {
                tableModel.addRow(user);
            }
        }
    }

    /**
     * Sorts users based on a given column index.
     */
    private void sortUsers(int columnIndex) {
        userData.sort(Comparator.comparing(u -> u[columnIndex]));
        tableModel.setRowCount(0);
        for (String[] user : userData) {
            tableModel.addRow(user);
        }
    }

    /**
     * Updates the selected user.
     */
    private void updateUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to update.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Retrieve user information
        String[] userInfo = userData.get(selectedRow);
        // Prevent updating admin users
        if (userInfo[0].equalsIgnoreCase("admin")) {
            JOptionPane.showMessageDialog(this, "Admin users cannot be updated!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Prompt for new values (for simplicity, updating first name and room)
        String newFirstName = JOptionPane.showInputDialog("Enter new First Name:", userInfo[1]);
        String newLastName = JOptionPane.showInputDialog("Enter new Last Name:", userInfo[2]);
        String newPassword = JOptionPane.showInputDialog("Enter new Password:", userInfo[4]);
        String newEmail = JOptionPane.showInputDialog("Enter new Email:", userInfo[5]);
        String newContact = JOptionPane.showInputDialog("Enter new Contact:", userInfo[6]);
        String newBlock = JOptionPane.showInputDialog("Enter new Block (A-F):", userInfo[7]);
        String newRoom = JOptionPane.showInputDialog("Enter new Room Number:", userInfo[8]);

        // Update values
        userInfo[1] = newFirstName;
        userInfo[2] = newLastName;
        userInfo[4] = newPassword;
        userInfo[5] = newEmail;
        userInfo[6] = newContact;
        userInfo[7] = newBlock;
        userInfo[8] = newRoom;

        // Reflect in table model
        tableModel.setValueAt(newFirstName, selectedRow, 1);
        tableModel.setValueAt(newLastName, selectedRow, 2);
        tableModel.setValueAt(newPassword, selectedRow, 4);
        tableModel.setValueAt(newEmail, selectedRow, 5);
        tableModel.setValueAt(newContact, selectedRow, 6);
        tableModel.setValueAt(newBlock, selectedRow, 7);
        tableModel.setValueAt(newRoom, selectedRow, 8);

        updateFile();
    }

    /**
     * Deletes the selected user.
     */
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to delete.");
            return;
        }
        userData.remove(selectedRow);
        tableModel.removeRow(selectedRow);
        updateFile();
    }

    /**
     * Creates a new user by prompting for all required fields.
     */
    private void createUser() {
        // Prompt for each field
        String userType = JOptionPane.showInputDialog("Enter User Type (admin/customer):");
        if (userType == null || (!userType.equalsIgnoreCase("admin") && !userType.equalsIgnoreCase("customer"))) {
            JOptionPane.showMessageDialog(this, "Invalid User Type!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String firstName = JOptionPane.showInputDialog("Enter First Name:");
        String lastName = JOptionPane.showInputDialog("Enter Last Name:");
        String username = JOptionPane.showInputDialog("Enter Username:");
        String password = JOptionPane.showInputDialog("Enter Password:");
        String email = JOptionPane.showInputDialog("Enter Email:");
        String contact = JOptionPane.showInputDialog("Enter Contact:");
        String block = JOptionPane.showInputDialog("Enter Block (A-F):");
        String room = JOptionPane.showInputDialog("Enter Room Number:");

        // Validate that no field is empty
        if (firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            contact == null || contact.trim().isEmpty() ||
            block == null || block.trim().isEmpty() ||
            room == null || room.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check for duplicate username
        for (String[] user : userData) {
            if (user[3].equalsIgnoreCase(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Create new user record (default values provided for demonstration)
        String[] newUser = {userType, firstName, lastName, username, password, email, contact, block, room};
        userData.add(newUser);
        tableModel.addRow(newUser);
        updateFile();

        JOptionPane.showMessageDialog(this, "User created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Generates a receipt for the selected customer and saves it to a file.
     */
    private void generateReceipt() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer to generate a receipt.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String username = (String) tableModel.getValueAt(selectedRow, 3);
        try (PrintWriter writer = new PrintWriter("src/TextFiles/Receipt_" + username + ".txt")) {
            writer.println("Receipt for " + username);
            writer.println("--------------------");
            writer.println("Order Details:");
            writer.println("Food Item: Pizza");
            writer.println("Amount: $10");
            writer.println("--------------------");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving receipt.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Receipt generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Sends the receipt to the customer by reading it from the saved file.
     */
    private void sendReceipt() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer to send the receipt.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String username = (String) tableModel.getValueAt(selectedRow, 3);
        StringBuilder receipt = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("src/TextFiles/Receipt_" + username + ".txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                receipt.append(line).append("\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading receipt!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Receipt sent to customer:\n" + receipt, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Logout function - closes this panel and returns to login.
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginSignup();
        }
    }

    /**
     * Back function - navigates to the previous dashboard.
     */
    private void goBack() {
        dispose();
        new LoginSignup();
    }

    /**
     * Updates the Credentials.txt file with the current user data.
     */
    private void updateFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/TextFiles/Credentials.txt"))) {
            for (String[] user : userData) {
                bw.write(String.join(" ", user) + "\n");
            }
            JOptionPane.showMessageDialog(this, "User data updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error updating file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Main method to launch the Admin panel.
     */
    public static void main(String[] args) {
        new Admin();
    }
}
