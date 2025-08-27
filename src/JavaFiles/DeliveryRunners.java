// AAYUSH PANERU (NP069584)
package JavaFiles;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class DeliveryRunners extends JFrame {
    // CardLayout and panel to switch views.
    private CardLayout cardLayout;
    private JPanel cardsPanel;
    
    // Card names.
    private final String MAIN_VIEW = "mainView";
    private final String HISTORY_VIEW = "historyView";
    private final String REVIEW_VIEW = "reviewView";
    private final String REVENUE_VIEW = "revenueView";
    
    // Components for the main dashboard view.
    // Using the same column names in both main and history views.
    private final String[] columns = {"Username", "Email", "Contact", "Block", "Room", "Items", "Total Price", "Review", "Date", "Time", "Status", "Actions"};
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private List<String[]> taskData;  // Each row represents a task.
    
    // Top control buttons (present in the main view).
    private JButton historyBtn, reviewBtn, revenueBtn;
    // Bottom navigation button.
    private JButton logoutBtn;
    
    public DeliveryRunners() {
        setTitle("Delivery Runner Dashboard - University Food Ordering System");
        setSize(1920, 1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // HEADER PANEL: Title and Logos.
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        headerPanel.setBackground(new Color(220, 220, 220));
        // LBEF logo on left.
        JLabel lbefLabel = new JLabel(loadScaledImage("src/Images/lbef_logo.png", 100, 100));
        headerPanel.add(lbefLabel, BorderLayout.WEST);
        // Title (center).
        JLabel headerTitle = new JLabel("University Food Ordering System", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Arial", Font.BOLD, 32));
        headerPanel.add(headerTitle, BorderLayout.CENTER);
        // APU logo on right.
        JLabel apuLabel = new JLabel(loadScaledImage("src/Images/APU.jpg", 100, 100));
        headerPanel.add(apuLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // CARDS PANEL: Contains Main View, History, Review, Revenue.
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        
        // Create and add main view panel.
        JPanel mainView = createMainView();
        cardsPanel.add(mainView, MAIN_VIEW);
        
        // Create and add history view panel.
        JPanel historyView = createHistoryView();
        cardsPanel.add(historyView, HISTORY_VIEW);
        
        // Create and add review view panel.
        JPanel reviewView = createReviewView();
        cardsPanel.add(reviewView, REVIEW_VIEW);
        
        // Create and add revenue view panel.
        JPanel revenueView = createRevenueView();
        cardsPanel.add(revenueView, REVENUE_VIEW);
        
        add(cardsPanel, BorderLayout.CENTER);
        
        // BOTTOM PANEL: Logout Button.
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        logoutBtn = new JButton("Logout");
        bottomPanel.add(logoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);
        
        logoutBtn.addActionListener(e -> logout());
        
        setVisible(true);
    }
    
    // Utility: Load and scale image from a file path.
    private ImageIcon loadScaledImage(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }
    
    // -------------------- Main View --------------------
    private JPanel createMainView() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(120, 192, 120, 192));
        
        JLabel subTitle = new JLabel("Delivery Runner Dashboard", SwingConstants.CENTER);
        subTitle.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(subTitle, BorderLayout.NORTH);
        
        // Top control panel.
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        historyBtn = new JButton("History");
        reviewBtn = new JButton("Review");
        revenueBtn = new JButton("Revenue Dashboard");
        controlPanel.add(historyBtn);
        controlPanel.add(reviewBtn);
        controlPanel.add(revenueBtn);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.SOUTH);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Table setup.
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 11; // Only Actions column editable.
            }
        };
        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(35);
        loadTasks();
        JScrollPane scrollPane = new JScrollPane(taskTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        historyBtn.addActionListener(e -> cardLayout.show(cardsPanel, HISTORY_VIEW));
        reviewBtn.addActionListener(e -> cardLayout.show(cardsPanel, REVIEW_VIEW));
        revenueBtn.addActionListener(e -> cardLayout.show(cardsPanel, REVENUE_VIEW));
        
        return mainPanel;
    }
    
    // Loads tasks from RunnerTasks.txt.
    private void loadTasks() {
        taskData = new ArrayList<>();
        tableModel.setRowCount(0);
        File file = new File("src/TextFiles/RunnerTasks.txt");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    // Assume the line contains at least 10 tokens corresponding to:
                    // Username, Email, Contact, Block, Room, Items, Total Price, Review, Date, Time
                    String[] tokens = line.split("\\s+");
                    String[] row = new String[12];
                    for (int i = 0; i < Math.min(10, tokens.length); i++) {
                        row[i] = tokens[i];
                    }
                    row[10] = "Pending"; // Default status.
                    row[11] = "";        // Actions placeholder.
                    taskData.add(row);
                    tableModel.addRow(row);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading tasks: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        taskTable.getColumnModel().getColumn(11).setCellRenderer(new ActionRenderer());
        taskTable.getColumnModel().getColumn(11).setCellEditor(new ActionEditor());
    }
    
    // -------------------- History View --------------------
    private JPanel createHistoryView() {
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(new EmptyBorder(50, 100, 50, 100));
        
        JLabel title = new JLabel("Task History", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        historyPanel.add(title, BorderLayout.NORTH);
        
        DefaultTableModel historyModel = new DefaultTableModel(columns, 0);
        JTable historyTable = new JTable(historyModel);
        historyTable.setRowHeight(35);
        
        // Load history rows from AcceptedTasks.txt and RejectedTasks.txt.
        addHistoryRows(historyModel, "src/TextFiles/AcceptedTasks.txt", "Accepted");
        addHistoryRows(historyModel, "src/TextFiles/RejectedTasks.txt", "Rejected");
        
        JScrollPane sp = new JScrollPane(historyTable);
        historyPanel.add(sp, BorderLayout.CENTER);
        
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton backHistoryBtn = new JButton("Back");
        backHistoryBtn.addActionListener(e -> cardLayout.show(cardsPanel, MAIN_VIEW));
        backPanel.add(backHistoryBtn);
        historyPanel.add(backPanel, BorderLayout.SOUTH);
        
        return historyPanel;
    }
    
    /**
     * Helper method to add rows from a given file to the history table model.
     * Assumes each line in the file has at least 10 tokens.
     */
    private void addHistoryRows(DefaultTableModel model, String filename, String statusLabel) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] tokens = line.split("\\s+");
                    if (tokens.length >= 10) {
                        String[] row = new String[12];
                        for (int i = 0; i < 10; i++) {
                            row[i] = tokens[i];
                        }
                        row[10] = statusLabel;
                        row[11] = "";
                        model.addRow(row);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // -------------------- Review View --------------------
    private JPanel createReviewView() {
        JPanel reviewPanel = new JPanel(new BorderLayout());
        reviewPanel.setBorder(new EmptyBorder(50, 100, 50, 100));
        
        JLabel title = new JLabel("Customer Reviews", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        reviewPanel.add(title, BorderLayout.NORTH);
        
        DefaultTableModel reviewModel = new DefaultTableModel(new String[]{"Review"}, 0);
        JTable reviewTable = new JTable(reviewModel);
        try (BufferedReader br = new BufferedReader(new FileReader("src/TextFiles/Reviews.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    reviewModel.addRow(new Object[]{line});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        reviewPanel.add(new JScrollPane(reviewTable), BorderLayout.CENTER);
        
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton backReviewBtn = new JButton("Back");
        backReviewBtn.addActionListener(e -> cardLayout.show(cardsPanel, MAIN_VIEW));
        backPanel.add(backReviewBtn);
        reviewPanel.add(backPanel, BorderLayout.SOUTH);
        
        return reviewPanel;
    }
    
    // -------------------- Revenue View --------------------
    // This method computes the profit (revenue) per food item with a 20% margin.
    // For each accepted order in AcceptedOrders.txt, it parses the "Order:" portion,
    // then for each food item (e.g., "Samosa x2"), it looks up the unit price from Items.txt,
    // computes profit = quantity * unitPrice * 0.2, and sums the profit per food item.
private JPanel createRevenueView() {
    JPanel revenuePanel = new JPanel(new BorderLayout());
    revenuePanel.setBorder(new EmptyBorder(50, 100, 50, 100));

    // Title at top.
    JLabel title = new JLabel("Food Profit Dashboard", SwingConstants.CENTER);
    title.setFont(new Font("Arial", Font.BOLD, 28));
    
    // Create a northPanel that will hold both the title (center) and the info panel (left).
    JPanel northPanel = new JPanel(new BorderLayout());
    northPanel.add(title, BorderLayout.CENTER);
    
    // 1. Load unit prices from Items.txt.
    Map<String, Double> itemPrices = new HashMap<>();
    File itemsFile = new File("src/TextFiles/Items.txt");
    try (BufferedReader br = new BufferedReader(new FileReader(itemsFile))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 2) {
                    String foodName = parts[0].trim();
                    try {
                        double price = Double.parseDouble(parts[1].trim());
                        itemPrices.put(foodName, price);
                    } catch (NumberFormatException ex) {
                        // Skip if price is invalid.
                    }
                }
            }
        }
    } catch (IOException ex) {
        ex.printStackTrace();
    }
    System.out.println("Item Prices: " + itemPrices);
    
    // 2. Parse AcceptedOrders.txt to build foodCounts and foodProfit maps.
    Map<String, Integer> foodCounts = new HashMap<>();
    Map<String, Double> foodProfit = new HashMap<>();
    
    File ordersFile = new File("src/TextFiles/AcceptedOrders.txt");
    if (!ordersFile.exists()) {
        JOptionPane.showMessageDialog(this, "AcceptedOrders.txt not found at: " + ordersFile.getAbsolutePath());
    } else {
        try (BufferedReader br = new BufferedReader(new FileReader(ordersFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    // Expected format (using pipe as delimiter):
                    // "Customer |Samosa x3 ($60.0), ChickenBriyani x1 ($375.0)  | Status: Delivered"
                    String[] tokens = line.split("\\|");
                    if (tokens.length >= 2) {
                        // Use the second token as the order items string.
                        String orderPart = tokens[1].trim();
                        // Remove any parenthesis content (e.g., "($60.0)")
                        orderPart = orderPart.replaceAll("\\(.*?\\)", "").trim();
                        // Split by comma to get individual items.
                        String[] items = orderPart.split(",");
                        for (String item : items) {
                            item = item.trim();
                            if (item.isEmpty()) continue;
                            // Expect format "FoodName xQuantity"
                            String[] itemParts = item.split("x");
                            if (itemParts.length == 2) {
                                String foodName = itemParts[0].trim();
                                int quantity = 0;
                                try {
                                    quantity = Integer.parseInt(itemParts[1].trim());
                                } catch (NumberFormatException nfe) {
                                    continue;
                                }
                                // Update total quantity sold.
                                foodCounts.put(foodName, foodCounts.getOrDefault(foodName, 0) + quantity);
                                // Compute profit using 20% margin if unit price is available.
                                if (itemPrices.containsKey(foodName)) {
                                    double unitPrice = itemPrices.get(foodName);
                                    double profit = quantity * unitPrice * 0.20;
                                    foodProfit.put(foodName, foodProfit.getOrDefault(foodName, 0.0) + profit);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    System.out.println("Food Counts: " + foodCounts);
    System.out.println("Food Profit: " + foodProfit);
    
    // 3. Compute overall totals.
    int totalItemsSold = 0;
    double totalRevenueMade = 0.0;
    for (int qty : foodCounts.values()) {
        totalItemsSold += qty;
    }
    for (double profit : foodProfit.values()) {
        totalRevenueMade += profit;
    }
    
    // 4. Create an info panel for overall totals.
    JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel totalItemsLabel = new JLabel("Total Items Sold: " + totalItemsSold);
    totalItemsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    JLabel totalRevenueLabel = new JLabel("Total Revenue Made: $" + String.format("%.2f", totalRevenueMade));
    totalRevenueLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    infoPanel.add(totalItemsLabel);
    infoPanel.add(Box.createHorizontalStrut(20)); // spacing
    infoPanel.add(totalRevenueLabel);
    
    // Add the info panel to the northPanel at the WEST.
    northPanel.add(infoPanel, BorderLayout.WEST);
    revenuePanel.add(northPanel, BorderLayout.NORTH);
    
    // 5. Create a chart panel to draw the bar graph based on foodProfit.
    JPanel chartPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (foodProfit.isEmpty()) {
                g.drawString("No sales data available.", 50, 50);
                return;
            }
            int width = getWidth();
            int height = getHeight();
            int numFoods = foodProfit.size();
            int barWidth = width / numFoods;
            double maxProfit = Collections.max(foodProfit.values());
            int i = 0;
            for (Map.Entry<String, Double> entry : foodProfit.entrySet()) {
                String food = entry.getKey();
                double profit = entry.getValue();
                int barHeight = (int) ((profit / maxProfit) * (height - 50));
                g.setColor(Color.PINK);
                g.fillRect(i * barWidth + 10, height - barHeight - 30, barWidth - 20, barHeight);
                g.setColor(Color.BLACK);
                g.drawRect(i * barWidth + 10, height - barHeight - 30, barWidth - 20, barHeight);
                g.drawString(food, i * barWidth + 15, height - 10);
                g.drawString(String.format("$%.2f", profit), i * barWidth + 15, height - barHeight - 35);
                i++;
            }
        }
    };
    // Set a preferred size for the chart panel.
    chartPanel.setPreferredSize(new Dimension(600, 400));
    
    // Wrap the chart panel in a container with horizontal margins (10% on each side).
    JPanel chartContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
    chartContainer.add(chartPanel);
    
    revenuePanel.add(chartContainer, BorderLayout.CENTER);

    // Bottom panel with a Back button.
    JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    JButton backRevenueBtn = new JButton("Back");
    backRevenueBtn.addActionListener(e -> cardLayout.show(cardsPanel, MAIN_VIEW));
    backPanel.add(backRevenueBtn);
    revenuePanel.add(backPanel, BorderLayout.SOUTH);
    
    return revenuePanel;
}


    private void logout() {
                dispose();
                new LoginSignup().setVisible(true);
    }
    
    // -------------------- Action Renderer & Editor for Tasks --------------------
    private class ActionRenderer extends JPanel implements TableCellRenderer {
        private JButton acceptBtn;
        private JButton rejectBtn;
        private JButton deliveredBtn;
        
        public ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            acceptBtn = new JButton("Accept");
            rejectBtn = new JButton("Decline");
            deliveredBtn = new JButton("Delivered");
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String status = (String) table.getValueAt(row, 10);
            removeAll();
            if (status.equals("Pending")) {
                add(acceptBtn);
                add(rejectBtn);
            } else if (status.equals("Accepted") || status.startsWith("Delivered")) {
                add(deliveredBtn);
                deliveredBtn.setBackground(status.equals("Delivered-Green") ? Color.GREEN : Color.RED);
            }
            return this;
        }
    }
    
    private class ActionEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JPanel panel;
        private JButton acceptBtn;
        private JButton rejectBtn;
        private JButton deliveredBtn;
        private int currentRow;
        
        public ActionEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            acceptBtn = new JButton("Accept");
            rejectBtn = new JButton("Decline");
            deliveredBtn = new JButton("Delivered");
            
            acceptBtn.addActionListener(this);
            rejectBtn.addActionListener(this);
            deliveredBtn.addActionListener(this);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            String status = (String) table.getValueAt(row, 10);
            panel.removeAll();
            if (status.equals("Pending")) {
                panel.add(acceptBtn);
                panel.add(rejectBtn);
            } else if (status.equals("Accepted") || status.startsWith("Delivered")) {
                panel.add(deliveredBtn);
                deliveredBtn.setBackground(status.equals("Delivered-Green") ? Color.GREEN : Color.RED);
            }
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == acceptBtn) {
                handleTaskAction(currentRow, "accept");
            } else if (e.getSource() == rejectBtn) {
                handleTaskAction(currentRow, "reject");
            } else if (e.getSource() == deliveredBtn) {
                handleTaskAction(currentRow, "delivered");
            }
            fireEditingStopped();
        }
    }
    
    /**
     * Handles a task action based on the provided row index and action.
     */
    private void handleTaskAction(int rowIndex, String action) {
        String[] task = taskData.get(rowIndex);
        String taskLine = String.join(" ", Arrays.copyOfRange(task, 0, 10));
        if (action.equals("accept")) {
            appendToFile("src/TextFiles/AcceptedTasks.txt", taskLine + " | Status: Accepted");
            appendToFile("src/TextFiles/RunnerTasks.txt", taskLine + " | Status: Pending");
            task[10] = "Accepted";
        } else if (action.equals("reject")) {
            appendToFile("src/TextFiles/RejectedTasks.txt", taskLine + " | Status: Rejected");
            taskData.remove(rowIndex);
            tableModel.removeRow(rowIndex);
            updateRunnerTasksFile();
            return;
        } else if (action.equals("delivered")) {
            if (task[10].equals("Delivered-Green")) {
                task[10] = "Delivered-Red";
            } else {
                task[10] = "Delivered-Green";
            }
        }
        tableModel.setValueAt(task[10], rowIndex, 10);
        updateRunnerTasksFile();
    }
    
    // Helper method: Append a line to the specified file.
    private void appendToFile(String filename, String line) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(line + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Writes back only the pending tasks to RunnerTasks.txt.
    private void updateRunnerTasksFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/TextFiles/RunnerTasks.txt"))) {
            for (String[] row : taskData) {
                if (row[10].equals("Pending")) {
                    String line = String.join(" ", Arrays.copyOfRange(row, 0, 10));
                    bw.write(line + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Main method to launch the Delivery Runner Dashboard.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeliveryRunners::new);
    }
}
