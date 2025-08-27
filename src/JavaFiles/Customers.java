// MOHIT BHANDARI (NP069596)
package JavaFiles;

import JavaFiles.LoginSignup;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
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

public class Customers extends JFrame {

    // CardLayout for switching between Main Menu, Order History, and Payment views.
    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private final String MAIN_VIEW = "mainView";
    private final String HISTORY_VIEW = "historyView";
    private final String PAYMENT_VIEW = "paymentView";

    // Components for Main Menu view.
    private JTable menuTable;
    private DefaultTableModel menuModel;
    private JLabel totalPriceLabel;

    // Components for Order History view (original customer history).
    private JTable historyTable;
    private DefaultTableModel historyModel;
    private final String[] historyColumns = {"Order Details", "Total", "Status", "Action"};

    // List to hold selected items.
    private List<String> selectedItems;

    // Fields for Payment view.
    private String pendingOrderRecord; // The order record to be paid.
    private double pendingTotal;       // The total amount of the pending order.
    private JPanel paymentPanel;
    private JTextField phoneField;
    private JTextField mpinField;
    private JLabel paymentConfirmLabel; // Shows "Do you want to pay the $XYZ ?"

    public Customers() {
        setTitle("Customer Dashboard - University Food Ordering System");
        setSize(1920, 1200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // HEADER PANEL: Logos and Title.
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 50, 20, 50));
        headerPanel.setBackground(new Color(220,220,220));
        // Try to load logos via getResource; if not found, load from file system.
        ImageIcon lbefIcon = loadScaledImage("/Images/lbef_logo.png", 100, 100);
        JLabel lbefLabel = new JLabel(lbefIcon);
        headerPanel.add(lbefLabel, BorderLayout.WEST);
        JLabel headerTitle = new JLabel("University Food Ordering System", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Arial", Font.BOLD, 32));
        headerPanel.add(headerTitle, BorderLayout.CENTER);
        ImageIcon apuIcon = loadScaledImage("/Images/apu_logo.png", 100, 100);
        JLabel apuLabel = new JLabel(apuIcon);
        headerPanel.add(apuLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // CARDS PANEL: Create and add the views.
        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.add(createMainMenuView(), MAIN_VIEW);
        cardsPanel.add(createHistoryView(), HISTORY_VIEW);
        paymentPanel = createPaymentPanel();
        cardsPanel.add(paymentPanel, PAYMENT_VIEW);
        add(cardsPanel, BorderLayout.CENTER);

        // BOTTOM PANEL: Global Logout button.
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton logoutBtn = new JButton("Logout");
        bottomPanel.add(logoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);
        logoutBtn.addActionListener(e -> logout());

        selectedItems = new ArrayList<>();
        setVisible(true);
    }

    // Loads an image from the classpath; if not found, loads from the file system.
    private ImageIcon loadScaledImage(String path, int width, int height) {
        java.net.URL imgURL = getClass().getResource(path);
        ImageIcon icon;
        if (imgURL != null) {
            icon = new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            icon = new ImageIcon(path);
        }
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    // -------------------- Main Menu View --------------------
    private JPanel createMainMenuView() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(120, 192, 120, 192));

        JLabel subTitle = new JLabel("Menu", SwingConstants.CENTER);
        subTitle.setFont(new Font("Arial", Font.BOLD, 28));
        mainPanel.add(subTitle, BorderLayout.NORTH);

        // Table columns: Image, Item Name, Unit Price, Quantity, Select, Review.
        String[] columns = {"Image", "Item Name", "Unit Price", "Quantity", "Select", "Review"};
        menuModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };
        menuTable = new JTable(menuModel) {
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0: return ImageIcon.class;
                    case 3: return Integer.class;
                    case 4: return Boolean.class;
                    default: return Object.class;
                }
            }
        };
        menuTable.setRowHeight(100);
        loadMenu();

        // Set custom cell editor for Quantity column.
        Integer[] quantities = new Integer[10];
        for (int i = 0; i < 10; i++) {
            quantities[i] = i + 1;
        }
        JComboBox<Integer> quantityEditor = new JComboBox<>(quantities);
        menuTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(quantityEditor));

        JScrollPane scrollPane = new JScrollPane(menuTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with Order Now button, Total Price label, and History button.
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        JPanel orderPanel = new JPanel(new FlowLayout());
        JButton orderNowBtn = new JButton("Order Now");
        totalPriceLabel = new JLabel("Total Price: $0.00");
        orderPanel.add(orderNowBtn);
        orderPanel.add(totalPriceLabel);
        JPanel historyPanel = new JPanel(new FlowLayout());
        JButton historyBtn = new JButton("History");
        historyPanel.add(historyBtn);
        bottomPanel.add(orderPanel);
        bottomPanel.add(historyPanel);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        orderNowBtn.addActionListener(e -> {
            String orderRecord = computeOrderRecord();
            if (orderRecord != null) {
                showPaymentPanel(orderRecord);
            }
        });
        historyBtn.addActionListener(e -> {
            loadOrderHistory();
            cardLayout.show(cardsPanel, HISTORY_VIEW);
        });

        menuModel.addTableModelListener((TableModelEvent e) -> recalcTotal());
        menuTable.addPropertyChangeListener(evt -> {
            if ("tableCellEditor".equals(evt.getPropertyName()) && !menuTable.isEditing()) {
                recalcTotal();
            }
        });
        return mainPanel;
    }

    // Loads items from Items.txt.
    // Expected format per line: ItemName UnitPrice ImagePath
    private void loadMenu() {
        menuModel.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader("src/TextFiles/Items.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] data = line.split(" ");
                    if (data.length >= 3) {
                        String itemName = data[0];
                        double unitPrice = Double.parseDouble(data[1]);
                        String imageFile = data[2]; // e.g., "src/Images/samosa.jpg"
                        ImageIcon imageIcon = loadScaledImage(imageFile, 100, 100);
                        int defaultQuantity = 1;
                        Boolean selectDefault = Boolean.FALSE;
                        String reviewInfo = getItemRating(itemName);
                        Object[] row = {imageIcon, itemName, unitPrice, defaultQuantity, selectDefault, reviewInfo};
                        menuModel.addRow(row);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Computes the order record and saves the total amount.
    private String computeOrderRecord() {
        List<String> orderList = new ArrayList<>();
        double total = 0.0;
        for (int i = 0; i < menuModel.getRowCount(); i++) {
            Boolean selected = (Boolean) menuModel.getValueAt(i, 4);
            if (selected != null && selected) {
                String itemName = (String) menuModel.getValueAt(i, 1);
                double price = (double) menuModel.getValueAt(i, 2);
                int quantity = (Integer) menuModel.getValueAt(i, 3);
                orderList.add(itemName + " x" + quantity + " ($" + (price * quantity) + ")");
                total += price * quantity;
            }
        }
        if (orderList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select items to order!");
            return null;
        }
        pendingTotal = total;
        return "Order: " + String.join(", ", orderList) + " | Total: $" + total + " | Status: Pending";
    }

    // Recalculates the total price.
    private void recalcTotal() {
        double total = 0.0;
        for (int i = 0; i < menuModel.getRowCount(); i++) {
            Boolean selected = (Boolean) menuModel.getValueAt(i, 4);
            if (selected != null && selected) {
                double price = (double) menuModel.getValueAt(i, 2);
                int quantity = (Integer) menuModel.getValueAt(i, 3);
                total += price * quantity;
            }
        }
        totalPriceLabel.setText(String.format("Total Price: $%.2f", total));
    }

    // Instead of immediately placing the order, show the Payment panel.
    private void showPaymentPanel(String orderRecord) {
        pendingOrderRecord = orderRecord;
        phoneField.setText("");
        mpinField.setText("");
        paymentConfirmLabel.setText("Do you want to pay the $" + pendingTotal + " ?");
        cardLayout.show(cardsPanel, PAYMENT_VIEW);
    }

    // Helper method to append a line to a file.
    private void appendToFile(String filePath, String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(content + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // -------------------- Payment Panel --------------------
    private JPanel createPaymentPanel() {
        // Outer panel with GridBagLayout to center the payment panel.
        JPanel outerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        // Inner payment panel with fixed size 500x500.
        JPanel payPanel = new JPanel(new BorderLayout());
        // Compound border: skyblue line border plus padding.
        Border lineBorder = BorderFactory.createLineBorder(new Color(135,206,250), 3);
        Border padding = new EmptyBorder(20, 20, 20, 20);
        payPanel.setBorder(BorderFactory.createCompoundBorder(lineBorder, padding));
        payPanel.setPreferredSize(new Dimension(500, 500));
        payPanel.setBackground(new Color(230, 250, 255));

        // Header: Center the eSewa logo and label "eSewa Pay".
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        header.setOpaque(false);
        ImageIcon esewaIcon = loadScaledImage("src/Images/esewa.png", 100, 100);
        JLabel esewaLogo = new JLabel(esewaIcon);
        header.add(esewaLogo);
        JLabel esewaLabel = new JLabel("eSewa Pay");
        esewaLabel.setFont(new Font("Arial", Font.BOLD, 36));
        header.add(esewaLabel);
        payPanel.add(header, BorderLayout.NORTH);

        // Center: Payment form inside the 500x500 panel.
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.insets = new Insets(10, 10, 10, 10);
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        fgbc.gridx = 0;
        fgbc.gridy = 0;
        JLabel phoneLabel = new JLabel("Phone Number / Email:");
        formPanel.add(phoneLabel, fgbc);
        fgbc.gridx = 1;
        phoneField = new JTextField(15);
        formPanel.add(phoneField, fgbc);

        fgbc.gridx = 0;
        fgbc.gridy = 1;
        JLabel mpinLabel = new JLabel("MPIN:");
        formPanel.add(mpinLabel, fgbc);
        fgbc.gridx = 1;
        mpinField = new JTextField(15);
        formPanel.add(mpinField, fgbc);

        payPanel.add(formPanel, BorderLayout.CENTER);

        // South: Confirmation prompt.
        JPanel confirmPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        confirmPanel.setOpaque(false);
        paymentConfirmLabel = new JLabel("Do you want to pay $0 ?");
        paymentConfirmLabel.setFont(new Font("Arial", Font.BOLD, 24));
        confirmPanel.add(paymentConfirmLabel);
        JButton yesBtn = new JButton("Yes");
        JButton noBtn = new JButton("No");
        confirmPanel.add(yesBtn);
        confirmPanel.add(noBtn);
        payPanel.add(confirmPanel, BorderLayout.SOUTH);

        yesBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Append the order record to both Orders.txt and CustomerOrders.txt.
                appendToFile("src/TextFiles/Orders.txt", pendingOrderRecord);
                appendToFile("src/TextFiles/CustomerOrders.txt", pendingOrderRecord);
                JOptionPane.showMessageDialog(null, "Payment successful!");
                cardLayout.show(cardsPanel, MAIN_VIEW);
            }
        });
        noBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardsPanel, MAIN_VIEW);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        outerPanel.add(payPanel, gbc);
        return outerPanel;
    }

    // -------------------- Order History View (Original Customer History) --------------------
    private JPanel createHistoryView() {
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(new EmptyBorder(50, 100, 50, 100));

        JLabel title = new JLabel("Order History", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        historyPanel.add(title, BorderLayout.NORTH);

        historyModel = new DefaultTableModel(historyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        historyTable = new JTable(historyModel);
        historyTable.setRowHeight(30);
        loadOrderHistory();
        JScrollPane sp = new JScrollPane(historyTable);
        historyPanel.add(sp, BorderLayout.CENTER);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton backHistoryBtn = new JButton("Back");
        backHistoryBtn.addActionListener(e -> cardLayout.show(cardsPanel, MAIN_VIEW));
        backPanel.add(backHistoryBtn);
        historyPanel.add(backPanel, BorderLayout.SOUTH);

        // Use custom renderer/editor with three buttons: Cancel Order, Reorder, and Add Review.
        historyTable.getColumnModel().getColumn(3).setCellRenderer(new HistoryActionRenderer());
        historyTable.getColumnModel().getColumn(3).setCellEditor(new HistoryActionEditor(new JCheckBox(), historyModel));
        return historyPanel;
    }

    // Loads order history from CustomerOrders.txt.
    private void loadOrderHistory() {
        historyModel.setRowCount(0);
        try (BufferedReader br = new BufferedReader(new FileReader("src/TextFiles/CustomerOrders.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 3) {
                        String details = parts[0].replace("Order:", "").trim();
                        String total = parts[1].replace("Total:", "").trim();
                        String status = parts[2].replace("Status:", "").trim();
                        historyModel.addRow(new Object[]{details, total, status, "Actions"});
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------- getItemRating Implementation --------------------
    // This implementation returns "Reviews" if there is at least one review for the item,
    // otherwise it returns "No Reviews".
    private String getItemRating(String itemName) {
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader("src/TextFiles/Reviews.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(itemName + " ")) {
                    found = true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return found ? "Reviews" : "No Reviews";
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
                showPaymentPanel(orderRecord);
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
    private JPanel createRevenueView() {
        JPanel revenuePanel = new JPanel(new BorderLayout());
        revenuePanel.setBorder(new EmptyBorder(50, 100, 50, 100));

        JLabel title = new JLabel("Revenue Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        revenuePanel.add(title, BorderLayout.NORTH);

        Map<String, Integer> monthlyRevenue = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try (BufferedReader br = new BufferedReader(new FileReader("src/TextFiles/AcceptedOrders.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(" ");
                if (tokens.length < 16) continue;
                String dateStr = tokens[14];
                Date d;
                try {
                    d = sdf.parse(dateStr);
                } catch (ParseException pe) {
                    continue;
                }
                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");
                String monthAbbr = monthFormat.format(d);
                int total = 0;
                try {
                    total = Integer.parseInt(tokens[11]);
                } catch (NumberFormatException nfe) {
                    continue;
                }
                monthlyRevenue.put(monthAbbr, monthlyRevenue.getOrDefault(monthAbbr, 0) + total);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (monthlyRevenue.isEmpty()) return;
                int width = getWidth();
                int height = getHeight();
                int numMonths = monthlyRevenue.size();
                int barWidth = width / numMonths;
                int maxRevenue = Collections.max(monthlyRevenue.values());
                int i = 0;
                for (String month : monthlyRevenue.keySet()) {
                    int rev = monthlyRevenue.get(month);
                    int barHeight = (int) (((double) rev / maxRevenue) * (height - 50));
                    g.setColor(Color.BLUE);
                    g.fillRect(i * barWidth + 10, height - barHeight - 30, barWidth - 20, barHeight);
                    g.setColor(Color.BLACK);
                    g.drawRect(i * barWidth + 10, height - barHeight - 30, barWidth - 20, barHeight);
                    g.drawString(month, i * barWidth + 15, height - 10);
                    g.drawString(String.valueOf(rev), i * barWidth + 15, height - barHeight - 35);
                    i++;
                }
            }
        };
        revenuePanel.add(new JScrollPane(chartPanel), BorderLayout.CENTER);

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton backRevenueBtn = new JButton("Back");
        backRevenueBtn.addActionListener(e -> cardLayout.show(cardsPanel, MAIN_VIEW));
        backPanel.add(backRevenueBtn);
        revenuePanel.add(backPanel, BorderLayout.SOUTH);
        return revenuePanel;
    }

    // -------------------- Logout --------------------
    private void logout() {
        dispose();
        new LoginSignup().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Customers::new);
    }
}
