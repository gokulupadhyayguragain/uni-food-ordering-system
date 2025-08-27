// GOKUL UPADHYAY GURAGAIN (NP069822)
package JavaFiles;

import JavaFiles.Admin;
import JavaFiles.DeliveryRunners;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
  
public class LoginSignup extends JFrame {
    private JTabbedPane tabbedPane;
    
    // Login Components
    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;
    
    // Signup Components
    private JComboBox<String> userTypeComboBox;
    private JTextField firstNameField, lastNameField, signupUsernameField, emailField, contactField, roomField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> blockComboBox;
    
    // Define a larger font for contents
    private final Font contentFont = new Font("Arial", Font.PLAIN, 20);

    public LoginSignup() {
        setTitle("Login / Signup - University Food Ordering System");
        setSize(1920, 1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        
        // HEADER PANEL: Contains LBEF logo, title, and APU logo.
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        headerPanel.setBackground(new Color(220,220,220));
        // Load and scale the logos to 100x100 pixels.
        ImageIcon lbefIcon = loadScaledImage("/Images/LBEF.png", 100, 100);
        JLabel lbefLabel = new JLabel(lbefIcon);
        headerPanel.add(lbefLabel, BorderLayout.WEST);
        JLabel headerTitle = new JLabel("University Food Ordering System", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Arial", Font.BOLD, 32));
        headerPanel.add(headerTitle, BorderLayout.CENTER);
        ImageIcon apuIcon = loadScaledImage("/Images/APU.jpg", 100, 100);
        JLabel apuLabel = new JLabel(apuIcon);
        headerPanel.add(apuLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Create Tabbed Pane for Login & Signup panels.
        tabbedPane = new JTabbedPane();
        tabbedPane.add("Login", createLoginPanel());
        tabbedPane.add("Signup", createSignupPanel());
        
        // CENTER PANEL: Wrap the tabbedPane with 20% margins on left/right and 10% on top/bottom.
        JPanel centerPanel = new JPanel(new BorderLayout());
        // For a 1920px width, 20% is ~384 px on each side.
        // For a 1200px height, 10% is ~120 px on top and bottom.
        centerPanel.setBorder(new EmptyBorder(120, 384, 120, 384));
        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    // Helper method to load and scale images from the classpath.
    private ImageIcon loadScaledImage(String path, int width, int height) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImg);
        } else {
            System.err.println("Couldn't find file: " + path);
            return new ImageIcon();
        }
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(contentFont);
        userLabel.setBounds(50, 50, 200, 40);
        panel.add(userLabel);
        
        loginUsernameField = new JTextField();
        loginUsernameField.setFont(contentFont);
        loginUsernameField.setBounds(260, 50, 300, 40);
        panel.add(loginUsernameField);
        
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(contentFont);
        passLabel.setBounds(50, 120, 200, 40);
        panel.add(passLabel);
        
        loginPasswordField = new JPasswordField();
        loginPasswordField.setFont(contentFont);
        loginPasswordField.setBounds(260, 120, 300, 40);
        panel.add(loginPasswordField);
        
        JButton loginButton = new JButton("Login");
        loginButton.setFont(contentFont);
        loginButton.setBounds(260, 190, 150, 50);
        panel.add(loginButton);
        
        loginButton.addActionListener(e -> handleLogin());
        
        return panel;
    }
    
    private void handleLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        File credentialsFile = new File("src/TextFiles/Credentials.txt");
        if (!credentialsFile.exists()) {
            JOptionPane.showMessageDialog(this, "User database not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(credentialsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] details = line.split(" ");
                if (details.length >= 9 && details[3].equals(username) && details[4].equals(password)) {
                    String userType = details[0];
                    JOptionPane.showMessageDialog(this, "Login Successful! Redirecting...");
                    
                    dispose();
                    switch (userType) {
                        case "admin": new Admin(); break;
                        case "runner": new DeliveryRunners(); break;
                        case "customer": new Customers(); break;
                        case "vendor": new Vendors(); break;
                    }
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading file!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private JPanel createSignupPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        
        JLabel typeLabel = new JLabel("User Type:");
        typeLabel.setFont(contentFont);
        typeLabel.setBounds(50, 50, 200, 40);
        panel.add(typeLabel);
        
        userTypeComboBox = new JComboBox<>(new String[]{"admin", "runner", "customer", "vendor"});
        userTypeComboBox.setFont(contentFont);
        userTypeComboBox.setBounds(260, 50, 300, 40);
        panel.add(userTypeComboBox);
        
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(contentFont);
        firstNameLabel.setBounds(50, 110, 200, 40);
        panel.add(firstNameLabel);
        
        firstNameField = new JTextField();
        firstNameField.setFont(contentFont);
        firstNameField.setBounds(260, 110, 300, 40);
        panel.add(firstNameField);
        
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(contentFont);
        lastNameLabel.setBounds(50, 170, 200, 40);
        panel.add(lastNameLabel);
        
        lastNameField = new JTextField();
        lastNameField.setFont(contentFont);
        lastNameField.setBounds(260, 170, 300, 40);
        panel.add(lastNameField);
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(contentFont);
        usernameLabel.setBounds(50, 230, 200, 40);
        panel.add(usernameLabel);
        
        signupUsernameField = new JTextField();
        signupUsernameField.setFont(contentFont);
        signupUsernameField.setBounds(260, 230, 300, 40);
        panel.add(signupUsernameField);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(contentFont);
        passwordLabel.setBounds(50, 290, 200, 40);
        panel.add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setFont(contentFont);
        passwordField.setBounds(260, 290, 300, 40);
        panel.add(passwordField);
        
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(contentFont);
        confirmPasswordLabel.setBounds(50, 350, 200, 40);
        panel.add(confirmPasswordLabel);
        
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(contentFont);
        confirmPasswordField.setBounds(260, 350, 300, 40);
        panel.add(confirmPasswordField);
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(contentFont);
        emailLabel.setBounds(50, 410, 200, 40);
        panel.add(emailLabel);
        
        emailField = new JTextField();
        emailField.setFont(contentFont);
        emailField.setBounds(260, 410, 300, 40);
        panel.add(emailField);
        
        JLabel contactLabel = new JLabel("Contact Number:");
        contactLabel.setFont(contentFont);
        contactLabel.setBounds(50, 470, 200, 40);
        panel.add(contactLabel);
        
        contactField = new JTextField();
        contactField.setFont(contentFont);
        contactField.setBounds(260, 470, 300, 40);
        panel.add(contactField);
        
        JLabel blockLabel = new JLabel("Block:");
        blockLabel.setFont(contentFont);
        blockLabel.setBounds(50, 530, 200, 40);
        panel.add(blockLabel);
        
        blockComboBox = new JComboBox<>(new String[]{"A", "B", "C", "D", "E", "F"});
        blockComboBox.setFont(contentFont);
        blockComboBox.setBounds(260, 530, 300, 40);
        panel.add(blockComboBox);
        
        JLabel roomLabel = new JLabel("Room No:");
        roomLabel.setFont(contentFont);
        roomLabel.setBounds(50, 590, 200, 40);
        panel.add(roomLabel);
        
        roomField = new JTextField();
        roomField.setFont(contentFont);
        roomField.setBounds(260, 590, 300, 40);
        panel.add(roomField);
        
        JButton signupButton = new JButton("Signup");
        signupButton.setFont(contentFont);
        signupButton.setBounds(600, 230, 150, 50);
        panel.add(signupButton);
        
        signupButton.addActionListener(e -> handleSignup());
        
        return panel;
    }
    
    private void handleSignup() {
        String userType = userTypeComboBox.getSelectedItem().toString();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = signupUsernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String email = emailField.getText().trim();
        String contactNumber = contactField.getText().trim();
        String block = blockComboBox.getSelectedItem().toString();
        String roomNumber = roomField.getText().trim();
        
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() ||
            password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty() ||
            contactNumber.isEmpty() || roomNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (isUsernameTaken(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists! Choose a different one.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String userData = String.format("%s %s %s %s %s %s %s %s %s%n",
            userType, firstName, lastName, username, password, email, contactNumber, block, roomNumber);
        
        File credentialsFile = new File("src/TextFiles/Credentials.txt");
        try {
            if (!credentialsFile.exists()) {
                credentialsFile.getParentFile().mkdirs();
                credentialsFile.createNewFile();
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(credentialsFile, true))) {
                writer.write(userData);
                writer.flush();
            }
            
            JOptionPane.showMessageDialog(this, "Signup Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            redirectToLogin();
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing to file!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Checks if the username already exists in Credentials.txt.
    private boolean isUsernameTaken(String username) {
        File credentialsFile = new File("src/TextFiles/Credentials.txt");
        if (!credentialsFile.exists()) return false;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(credentialsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length > 3 && parts[3].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Redirect to login page by reopening the LoginSignup frame.
    private void redirectToLogin() {
        this.dispose();
        new LoginSignup().setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginSignup::new);
    }
}
