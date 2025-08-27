// SUGAM PAUDEL (NP069840)
package JavaFiles;

import JavaFiles.LoginSignup;
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

public class Vendors extends JFrame {

    // CardLayout and panel for switching views.
    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private final String MAIN_VIEW = "mainView";
    private final String ITEMS_VIEW = "itemsView";
    private final String HISTORY_VIEW = "historyView";
    private final String REVIEW_VIEW = "reviewView";
    private final String REVENUE_VIEW = "revenueView";

    // Components for Orders view.
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    private List<Object[]> orderData;
    private final String[] orderColumns = {"Customer", "Order Details", "Total", "Review", "Action"};
    // Map to store each order's state. 0 = pending; 1 = accepted; 2 = delivered.
    private Map<String, Integer> orderStateMap = new HashMap<>();

    // Components for Items view.
    private JTable itemsTable;
    private DefaultTableModel itemsTableModel;
    private final String[] itemColumns = {"Item Name", "Unit Price", "Image Path", "Picture", "Action"};

    // Components for History view.
    // (Assuming 4 columns: Order Details, Total, Status, Action)
    private final String[] historyColumns = {"Order Details", "Total", "Status", "Action"};
    private JTable historyTable;
    private DefaultTableModel historyModel;

    // Global bottom panel buttons.
    private JButton backBtn, logoutBtn;

    private JComboBox<String> filterTypeCombo;
    private JTextField filterValueField;
    private JButton filterButton;

    
    public Vendors() {
        setTitle("Vendor Dashboard - University Food Ordering System");
        setSize(1920, 1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // HEADER PANEL: Title and Logos.
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        headerPanel.setBackground(new Color(220, 220, 220));
        // Scale logos to 100x100.
        ImageIcon lbefIcon = loadScaledImage("src/Images/LBEF.png", 100, 100);
        JLabel lbefLabel = new JLabel(lbefIcon);
        headerPanel.add(lbefLabel, BorderLayout.WEST);
        JLabel headerTitle = new JLabel("University Food Ordering System", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Arial", Font.BOLD, 32));
        headerPanel.add(headerTitle, BorderLayout.CENTER);
        ImageIcon apuIcon = loadScaledImage("src/Images/APU.jpg", 100, 100);
        JLabel apuLabel = new JLabel(apuIcon);
        headerPanel.add(apuLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // CARDS PANEL: Create and add all views.
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.add(createOrdersView(), MAIN_VIEW);
        cardsPanel.add(createItemsView(), ITEMS_VIEW);
        cardsPanel.add(createHistoryView(), HISTORY_VIEW);
        cardsPanel.add(createReviewView(), REVIEW_VIEW);
        cardsPanel.add(createRevenueView(), REVENUE_VIEW);
        add(cardsPanel, BorderLayout.CENTER);

        // Global Bottom Panel with Back and Logout buttons.
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        backBtn = new JButton("Back");
        logoutBtn = new JButton("Logout");
        bottomPanel.add(backBtn);
        bottomPanel.add(logoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Back button always returns to the main (Orders) view.
        backBtn.addActionListener(e -> cardLayout.show(cardsPanel, MAIN_VIEW));
        logoutBtn.addActionListener(e -> logout());

        setVisible(true);
    }

    // Single loadScaledImage method.
    private ImageIcon loadScaledImage(String path, int width, int height) {
        // For Vendors, we load directly from the file path.
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    // -------------------- Orders View --------------------
    private JPanel createOrdersView() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(120, 192, 120, 192)); // ~10% margins

        JLabel subTitle = new JLabel("Vendor Dashboard - Orders", SwingConstants.CENTER);
        subTitle.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(subTitle, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton itemsBtn = new JButton("View Items");
        JButton historyBtn = new JButton("History");
        JButton reviewBtn = new JButton("Reviews");
        JButton revenueBtn = new JButton("Revenue Dashboard");
        controlPanel.add(itemsBtn);
        controlPanel.add(historyBtn);
        controlPanel.add(reviewBtn);
        controlPanel.add(revenueBtn);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        ordersTableModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setRowHeight(30);
        loadOrders();
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        ordersTable.getColumnModel().getColumn(4).setCellRenderer(new OrderButtonRenderer());
        ordersTable.getColumnModel().getColumn(4).setCellEditor(new OrderButtonEditor(new JCheckBox()));

        itemsBtn.addActionListener(e -> cardLayout.show(cardsPanel, ITEMS_VIEW));
        historyBtn.addActionListener(e -> cardLayout.show(cardsPanel, HISTORY_VIEW));
        reviewBtn.addActionListener(e -> cardLayout.show(cardsPanel, REVIEW_VIEW));
        revenueBtn.addActionListener(e -> cardLayout.show(cardsPanel, REVENUE_VIEW));

        return mainPanel;
    }

private void loadOrders() {
    orderData = new ArrayList<>();
    ordersTableModel.setRowCount(0);
    File file = new File("src/TextFiles/Orders.txt");
    if (!file.exists()) {
        JOptionPane.showMessageDialog(this, "Orders.txt file not found at: " + file.getAbsolutePath());
        return;
    }
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                // Split by pipe character, which is used as a delimiter in the customer order record.
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    // parts[0] should contain something like "Order: Samosa x1 ($20) , Paneer x2 ($50)"
                    // parts[1] should be " Total: $70" and parts[2] " Status: Pending"
                    String orderDetails = parts[0].replace("Order:", "").trim();
                    String total = parts[1].replace("Total:", "").trim();
                    String status = parts[2].replace("Status:", "").trim();
                    // If no customer information is provided, you can use a default placeholder.
                    String customer = "Customer";
                    // The review field may not be present; leave it as empty.
                    String review = "";
                    String orderId = customer + "|" + orderDetails;
                    orderStateMap.putIfAbsent(orderId, 0);
                    Object[] row = {customer, orderDetails, total, review, "Actions"};
                    orderData.add(row);
                    ordersTableModel.addRow(row);
                }
            }
        }
    } catch (IOException e) {
        JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
    }
}



    private class OrderButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton acceptButton;
        private JButton rejectButton;
        private JButton infoButton;

        public OrderButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            acceptButton = new JButton("Accept");
            rejectButton = new JButton("Reject");
            infoButton = new JButton("Info");
            add(acceptButton);
            add(rejectButton);
            add(infoButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            acceptButton.setVisible(true);
            rejectButton.setVisible(true);
            acceptButton.setBackground(null);
            return this;
        }
    }

    private class OrderButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JPanel panel;
        private JButton acceptButton;
        private JButton rejectButton;
        private JButton infoButton;
        private String currentOrderId;
        private int currentRow;

        public OrderButtonEditor(JCheckBox checkBox) {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            acceptButton = new JButton("Accept");
            rejectButton = new JButton("Reject");
            infoButton = new JButton("Info");
            panel.add(acceptButton);
            panel.add(rejectButton);
            panel.add(infoButton);

            acceptButton.addActionListener(this);
            rejectButton.addActionListener(this);
            infoButton.addActionListener(this);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            String customer = table.getValueAt(row, 0).toString();
            String orderDetails = table.getValueAt(row, 1).toString();
            currentOrderId = customer + "|" + orderDetails;
            int state = orderStateMap.getOrDefault(currentOrderId, 0);
            if (state > 0) {
                rejectButton.setVisible(false);
                if (state == 1) {
                    acceptButton.setBackground(Color.YELLOW);
                } else if (state == 2) {
                    acceptButton.setBackground(Color.GREEN);
                }
            } else {
                acceptButton.setBackground(null);
                rejectButton.setVisible(true);
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == acceptButton) {
                int state = orderStateMap.getOrDefault(currentOrderId, 0);
                if (state == 0) {
                    state = 1;
                    orderStateMap.put(currentOrderId, state);
                    acceptButton.setBackground(Color.YELLOW);
                    rejectButton.setVisible(false);
                    // Append to AcceptedOrders.txt and RunnerTasks.txt.
                    appendToFile("src/TextFiles/AcceptedOrders.txt", currentOrderId + " | Status: Accepted");
                    appendToFile("src/TextFiles/RunnerTasks.txt", currentOrderId + " | Status: Pending");
                    // Remove from Orders.txt and CustomerOrders.txt.
                    removeOrderFromFile("src/TextFiles/Orders.txt", currentOrderId);
                    removeOrderFromFile("src/TextFiles/CustomerOrders.txt", currentOrderId);
                } else if (state == 1) {
                    state = 2;
                    orderStateMap.put(currentOrderId, state);
                    acceptButton.setBackground(Color.GREEN);
                    // Update AcceptedOrders.txt status to Delivered.
                    updateOrderStatusInFile("src/TextFiles/AcceptedOrders.txt", currentOrderId, "Delivered");
                }
            } else if (e.getSource() == rejectButton) {
                appendToFile("src/TextFiles/RejectedOrders.txt", currentOrderId + " | Status: Rejected");
                removeOrderFromFile("src/TextFiles/Orders.txt", currentOrderId);
                removeOrderFromFile("src/TextFiles/CustomerOrders.txt", currentOrderId);
                orderData.remove(currentRow);
                ordersTableModel.removeRow(currentRow);
            } else if (e.getSource() == infoButton) {
                JOptionPane.showMessageDialog(null, "Review Info: " + ordersTableModel.getValueAt(currentRow, 3));
            }
            fireEditingStopped();
        }
    }

    // Helper method: Append a line to a file.
    private void appendToFile(String filePath, String record) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(record + "\n");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error writing to " + filePath + ": " + ex.getMessage());
        }
    }

    // Helper method: Remove any line from a file that contains the given order identifier.
    private void removeOrderFromFile(String filePath, String orderIdentifier) {
        File file = new File(filePath);
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                if(!line.contains(orderIdentifier)) {
                    lines.add(line);
                }
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    // Helper method: Update the order status in a file by replacing the status part.
    private void updateOrderStatusInFile(String filePath, String orderIdentifier, String newStatus) {
        File file = new File(filePath);
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = br.readLine()) != null) {
                if(line.contains(orderIdentifier)) {
                    // Here we assume the status is indicated after a "|" character.
                    // We'll replace the last part with the new status.
                    String[] parts = line.split("\\|");
                    if(parts.length >= 2) {
                        parts[parts.length - 1] = " Status: " + newStatus;
                        line = String.join(" |", parts);
                    }
                }
                lines.add(line);
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for(String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    // -------------------- Items View --------------------
    private JPanel createItemsView() {
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(new EmptyBorder(50, 100, 50, 100));

        JLabel title = new JLabel("Items List", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        itemsPanel.add(title, BorderLayout.NORTH);

        // Override getColumnClass so the Picture column shows ImageIcons.
        itemsTable = new JTable() {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 3) {
                    return ImageIcon.class;
                }
                return String.class;
            }
        };
        itemsTableModel = new DefaultTableModel(itemColumns, 0);
        itemsTable.setModel(itemsTableModel);
        itemsTable.setRowHeight(100);  // Set row height to 100 px.
        loadItems();

        JScrollPane sp = new JScrollPane(itemsTable);
        itemsPanel.add(sp, BorderLayout.CENTER);

        JPanel bottomItemsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton createItemBtn = new JButton("Create Item");
        bottomItemsPanel.add(createItemBtn);
        itemsPanel.add(bottomItemsPanel, BorderLayout.SOUTH);

        createItemBtn.addActionListener(e -> createNewItem());

        itemsTable.getColumnModel().getColumn(4).setCellRenderer(new ItemButtonRenderer());
        itemsTable.getColumnModel().getColumn(4).setCellEditor(new ItemButtonEditor(new JCheckBox()));

        return itemsPanel;
    }

    private void loadItems() {
        itemsTableModel.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader("src/TextFiles/Items.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.trim().isEmpty()){
                    String[] parts = line.split(" ");
                    if (parts.length >= 3) {
                        // Use the image path exactly as stored.
                        ImageIcon picIcon = loadScaledImage(parts[2], 100, 100);
                        Object[] row = {parts[0], parts[1], parts[2], picIcon, "Edit/Delete"};
                        itemsTableModel.addRow(row);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage());
        }
    }

    // Method to create a new item.
    private void createNewItem() {
        JTextField itemNameField = new JTextField();
        JTextField unitPriceField = new JTextField();
        // Prompt user for full image path.
        JTextField imagePathField = new JTextField("src/Images/samosa.png");
        Object[] fields = {
            "Item Name:", itemNameField,
            "Unit Price:", unitPriceField,
            "Image Path (e.g., src/Images/samosa.png):", imagePathField
        };
        int option = JOptionPane.showConfirmDialog(this, fields, "Create New Item", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String itemName = itemNameField.getText().trim();
            String unitPrice = unitPriceField.getText().trim();
            String imagePath = imagePathField.getText().trim();
            if (itemName.isEmpty() || unitPrice.isEmpty() || imagePath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/TextFiles/Items.txt", true))) {
                bw.write(itemName + " " + unitPrice + " " + imagePath + "\n");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving new item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            loadItems();
            JOptionPane.showMessageDialog(this, "Item created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Renderer for Items Action column.
    private class ItemButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton updateBtn;
        private JButton deleteBtn;

        public ItemButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            updateBtn = new JButton("Update");
            deleteBtn = new JButton("Delete");
            add(updateBtn);
            add(deleteBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Editor for Items Action column.
    private class ItemButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JPanel panel;
        private JButton updateBtn;
        private JButton deleteBtn;
        private int currentRow;

        public ItemButtonEditor(JCheckBox checkBox) {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            updateBtn = new JButton("Update");
            deleteBtn = new JButton("Delete");
            panel.add(updateBtn);
            panel.add(deleteBtn);

            updateBtn.addActionListener(this);
            deleteBtn.addActionListener(this);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == updateBtn) {
                // Retrieve current values.
                String currentName = (String) itemsTableModel.getValueAt(currentRow, 0);
                String currentPrice = (String) itemsTableModel.getValueAt(currentRow, 1);
                String currentImagePath = (String) itemsTableModel.getValueAt(currentRow, 2);
                
                JTextField itemNameField = new JTextField(currentName);
                JTextField unitPriceField = new JTextField(currentPrice);
                JTextField imagePathField = new JTextField(currentImagePath);
                Object[] message = {
                    "Item Name:", itemNameField,
                    "Unit Price:", unitPriceField,
                    "Image Path:", imagePathField
                };
                int option = JOptionPane.showConfirmDialog(null, message, "Update Item", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String newName = itemNameField.getText().trim();
                    String newPrice = unitPriceField.getText().trim();
                    String newImagePath = imagePathField.getText().trim();
                    if (!newName.isEmpty() && !newPrice.isEmpty() && !newImagePath.isEmpty()) {
                        itemsTableModel.setValueAt(newName, currentRow, 0);
                        itemsTableModel.setValueAt(newPrice, currentRow, 1);
                        itemsTableModel.setValueAt(newImagePath, currentRow, 2);
                        // Update picture column with new scaled image.
                        ImageIcon newPicIcon = loadScaledImage(newImagePath, 100, 100);
                        itemsTableModel.setValueAt(newPicIcon, currentRow, 3);
                        updateItemsFile();
                    }
                }
            } else if (e.getSource() == deleteBtn) {
                itemsTableModel.removeRow(currentRow);
                updateItemsFile();
            }
            fireEditingStopped();
        }
    }

    private void updateItemsFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/TextFiles/Items.txt"))) {
            for (int i = 0; i < itemsTableModel.getRowCount(); i++) {
                String itemName = (String) itemsTableModel.getValueAt(i, 0);
                String price = (String) itemsTableModel.getValueAt(i, 1);
                String imagePath = (String) itemsTableModel.getValueAt(i, 2);
                bw.write(itemName + " " + price + " " + imagePath + "\n");
            }
            JOptionPane.showMessageDialog(this, "Items updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error updating items file: " + e.getMessage());
        }
    }

    // -------------------- History View --------------------
    // The history view now displays: DateTime, Name, Block, RoomNo, ContactNumber, Ordered Things, Total Price, Status, Review.
    // (For simplicity, we assume exactly two items in each order.)
// -------------------- History View --------------------
// The history view now displays: DateTime, Name, Block, RoomNo, ContactNumber, Ordered Things, Total Price, Status, Review.
private JPanel createHistoryView() {
    JPanel historyPanel = new JPanel(new BorderLayout());
    historyPanel.setBorder(new EmptyBorder(50, 100, 50, 100));

    // Filter panel.
    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    filterPanel.add(new JLabel("Filter:"));
    JComboBox<String> filterTypeCombo = new JComboBox<>(new String[]{"Daily", "Monthly", "Quarterly"});
    filterPanel.add(filterTypeCombo);
    filterPanel.add(new JLabel("Value:"));
    JTextField filterValueField = new JTextField(15);
    filterPanel.add(filterValueField);
    JButton filterButton = new JButton("Search");
    filterPanel.add(filterButton);
    filterButton.addActionListener(e -> applyHistoryFilter(filterTypeCombo, filterValueField));
    historyPanel.add(filterPanel, BorderLayout.NORTH);

    historyModel = new DefaultTableModel(historyColumns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    historyTable = new JTable(historyModel);
    historyTable.setRowHeight(30);
    loadOrderHistory();
    JScrollPane sp = new JScrollPane(historyTable);
    historyPanel.add(sp, BorderLayout.CENTER);

    return historyPanel;
}

// Updated loadOrderHistory() method to parse the accepted orders format.
private void loadOrderHistory() {
    historyModel.setRowCount(0);
    File file = new File("src/TextFiles/AcceptedOrders.txt");
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                // Split the line by the pipe symbol.
                String[] tokens = line.split("\\|");
                if (tokens.length >= 3) {
                    // tokens[0]: Customer name, tokens[1]: Ordered items, tokens[2]: Status.
                    String name = tokens[0].trim();
                    String orderedItems = tokens[1].trim();
                    
                    // Compute total price by summing numbers inside parentheses in each item.
                    double totalPriceVal = 0.0;
                    String[] items = orderedItems.split(",");
                    for (String item : items) {
                        item = item.trim();
                        int start = item.indexOf('(');
                        int end = item.indexOf(')');
                        if (start != -1 && end != -1 && end > start) {
                            String priceStr = item.substring(start + 1, end).replace("$", "").trim();
                            try {
                                totalPriceVal += Double.parseDouble(priceStr);
                            } catch (NumberFormatException nfe) {
                                // Skip if invalid.
                            }
                        }
                    }
                    String totalPrice = "$" + String.format("%.2f", totalPriceVal);
                    
                    String status = tokens[2].trim();
                    if (status.startsWith("Status:")) {
                        status = status.substring("Status:".length()).trim();
                    }
                    
                    // Since the file doesn't include DateTime, Block, RoomNo, ContactNumber, or Review,
                    // we set those as empty strings.
                    Object[] row = {
                        "",          // DateTime
                        name,        // Name
                        "",          // Block
                        "",          // RoomNo
                        "",          // ContactNumber
                        orderedItems, // Ordered Things
                        totalPrice,  // Total Price
                        status,      // Status
                        ""           // Review
                    };
                    historyModel.addRow(row);
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// Apply filter based on selected type and input value.
private void applyHistoryFilter(JComboBox<String> filterTypeCombo, JTextField filterValueField) {
    String filterType = (String) filterTypeCombo.getSelectedItem();
    String filterVal = filterValueField.getText().trim().toLowerCase();
    if (filterVal.isEmpty()) {
        loadOrderHistory();
        return;
    }
    DefaultTableModel filteredModel = new DefaultTableModel(historyColumns, 0);
    int rowCount = historyModel.getRowCount();
    for (int i = 0; i < rowCount; i++) {
        String dateTime = historyModel.getValueAt(i, 0).toString().toLowerCase();
        boolean match = false;
        if (filterType.equals("Daily")) {
            if (dateTime.startsWith(filterVal))
                match = true;
        } else if (filterType.equals("Monthly")) {
            if (dateTime.contains(filterVal))
                match = true;
        } else if (filterType.equals("Quarterly")) {
            if (dateTime.length() >= 7) {
                String monthStr = dateTime.substring(5, 7);
                int month = Integer.parseInt(monthStr);
                String quarter = "";
                if (month >= 1 && month <= 3)
                    quarter = "q1";
                else if (month >= 4 && month <= 6)
                    quarter = "q2";
                else if (month >= 7 && month <= 9)
                    quarter = "q3";
                else if (month >= 10 && month <= 12)
                    quarter = "q4";
                if (quarter.equals(filterVal.toLowerCase()))
                    match = true;
            }
        }
        if (match) {
            Object[] row = new Object[historyColumns.length];
            for (int j = 0; j < historyColumns.length; j++) {
                row[j] = historyModel.getValueAt(i, j);
            }
            filteredModel.addRow(row);
        }
    }
    historyTable.setModel(filteredModel);
}


    // -------------------- History Action Renderer & Editor --------------------
    // Renderer that displays three buttons: Cancel Order, Reorder, and Add Review.
    private class HistoryActionRenderer extends JPanel implements TableCellRenderer {
        private JButton cancelBtn;
        private JButton reorderBtn;
        private JButton reviewBtn;
        public HistoryActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
            cancelBtn = new JButton("Cancel Order");
            reorderBtn = new JButton("Reorder");
            reviewBtn = new JButton("Add Review");
            add(cancelBtn);
            add(reorderBtn);
            add(reviewBtn);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Editor for the History Action column.
    private class HistoryActionEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JPanel panel;
        private JButton cancelBtn;
        private JButton reorderBtn;
        private JButton reviewBtn;
        private int currentRow;
        private DefaultTableModel historyModel;
        public HistoryActionEditor(JCheckBox checkBox, DefaultTableModel model) {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            cancelBtn = new JButton("Cancel Order");
            reorderBtn = new JButton("Reorder");
            reviewBtn = new JButton("Add Review");
            panel.add(cancelBtn);
            panel.add(reorderBtn);
            panel.add(reviewBtn);
            cancelBtn.addActionListener(this);
            reorderBtn.addActionListener(this);
            reviewBtn.addActionListener(this);
            historyModel = model;
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }
        @Override
        public Object getCellEditorValue() {
            return "";
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            String details = (String) historyModel.getValueAt(currentRow, 0);
            String total = (String) historyModel.getValueAt(currentRow, 1);
            if (e.getSource() == cancelBtn) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/TextFiles/CustomerOrders.txt", true))) {
                    bw.write("Order: " + details + " | Total: " + total + " | Status: Cancelled\n");
                    JOptionPane.showMessageDialog(null, "Order cancelled successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error cancelling order: " + ex.getMessage());
                }
            } else if (e.getSource() == reorderBtn) {
                String orderRecord = "Order: " + details + " | Total: " + total + " | Status: Pending";
                showVendorPaymentPanelForReorder(orderRecord);
            } else if (e.getSource() == reviewBtn) {
                String ratingStr = JOptionPane.showInputDialog("Enter rating (e.g., 4):");
                String reviewText = JOptionPane.showInputDialog("Enter review:");
                if (ratingStr != null && reviewText != null && !ratingStr.trim().isEmpty() && !reviewText.trim().isEmpty()) {
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter("src/TextFiles/Reviews.txt", true))) {
                        bw.write(details + " " + ratingStr.trim() + " " + reviewText.trim() + "\n");
                        JOptionPane.showMessageDialog(null, "Review added successfully!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error adding review: " + ex.getMessage());
                    }
                }
            }
            fireEditingStopped();
        }
    }

    // In the vendor workflow, when a vendor reorders, we show a similar payment panel.
    private void showVendorPaymentPanelForReorder(String orderRecord) {
        // For simplicity, we use the same payment panel.
        String pendingOrderRecord = orderRecord;
        String PAYMENT_VIEW = null;
        // You might want to update other files or statuses as needed.
        cardLayout.show(cardsPanel, PAYMENT_VIEW);
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
                g.setColor(Color.YELLOW);
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





    // -------------------- Logout --------------------
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
        if(confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginSignup().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Vendors::new);
    }
}
