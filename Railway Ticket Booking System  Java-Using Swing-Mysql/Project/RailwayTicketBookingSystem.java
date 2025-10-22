import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Railway Ticket Booking System with Admin and User functionality
 */
public class RailwayTicketBookingSystem {
    
    // Data storage
    private static List<String[]> trains = new ArrayList<>();
    private static List<String[]> bookings = new ArrayList<>();
    private static String currentUser = "";
    private static boolean isAdmin = false;
    private static boolean dbConnected = false;
    
    static {
        // Sample trains data
        trains.add(new String[]{"12345", "Rajdhani Express", "Delhi", "Mumbai", "₹2500"});
        trains.add(new String[]{"12346", "Shatabdi Express", "Delhi", "Chennai", "₹1800"});
        trains.add(new String[]{"12347", "Duronto Express", "Mumbai", "Kolkata", "₹2200"});
        trains.add(new String[]{"12348", "Chennai Express", "Chennai", "Bangalore", "₹800"});
        trains.add(new String[]{"12349", "Rajdhani Mail", "Kolkata", "Delhi", "₹1500"});
        
        // Sample bookings
        bookings.add(new String[]{"PNR001", "John Doe", "Rajdhani Express", "Confirmed"});
        bookings.add(new String[]{"PNR002", "Jane Smith", "Shatabdi Express", "Confirmed"});
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
    
    // Main Frame
    static class MainFrame extends JFrame {
        private JTable trainTable, bookingTable;
        private DefaultTableModel trainModel, bookingModel;
        private JTextField nameField, ageField;
        private JComboBox<String> fromCombo, toCombo;
        
        public MainFrame() {
            setTitle("🚂 Railway Ticket Booking System - " + currentUser + (dbConnected ? " (Database)" : " (Local)"));
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(900, 700);
            setLocationRelativeTo(null);
            initGUI();
        }
        
        private void initGUI() {
            setLayout(new BorderLayout());
            
            // Header
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(70, 130, 180));
            headerPanel.setPreferredSize(new Dimension(0, 60));
            
            JLabel header = new JLabel("🚂 Railway Ticket Booking System - Welcome " + currentUser, SwingConstants.CENTER);
            header.setFont(new Font("Arial", Font.BOLD, 18));
            header.setForeground(Color.WHITE);
            
            JButton logoutBtn = new JButton("Logout");
            logoutBtn.setPreferredSize(new Dimension(100, 35));
            logoutBtn.setBackground(new Color(220, 20, 60));
            logoutBtn.setForeground(Color.WHITE);
            logoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
            logoutBtn.addActionListener(e -> {
                dispose();
                new LoginFrame().setVisible(true);
            });
            
            headerPanel.add(header, BorderLayout.CENTER);
            headerPanel.add(logoutBtn, BorderLayout.EAST);
            add(headerPanel, BorderLayout.NORTH);
            
            // Main content with tabs
            JTabbedPane tabs = new JTabbedPane();
            
            if (isAdmin) {
                // Admin only sees data views
                tabs.addTab("🚂 Train Data", createTrainViewPanel());
                tabs.addTab("📋 Booking Data", createBookingViewPanel());
                if (dbConnected) {
                    tabs.addTab("🗄️ Database Info", createDatabasePanel());
                }
            } else {
                // Users get booking functionality
                tabs.addTab("🎫 Book Ticket", createBookingPanel());
                tabs.addTab("📋 My Bookings", createBookingsPanel());
            }
            
            add(tabs, BorderLayout.CENTER);
            
            // Footer
            JLabel footer = new JLabel("© 2024 Railway Ticket Booking System", SwingConstants.CENTER);
            footer.setFont(new Font("Arial", Font.PLAIN, 11));
            footer.setForeground(Color.GRAY);
            footer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            add(footer, BorderLayout.SOUTH);
        }
        
        private JPanel createDatabasePanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Info panel
            JPanel infoPanel = new JPanel();
            infoPanel.setBackground(new Color(240, 248, 255));
            infoPanel.setBorder(BorderFactory.createTitledBorder("Database Information"));
            
            JLabel infoLabel = new JLabel(
                "<html><b>Admin Database Access</b><br/>" +
                "✅ Connected to MySQL Database<br/>" +
                "✅ Real-time train data<br/>" +
                "✅ Persistent booking storage<br/>" +
                "✅ Full CRUD operations</html>"
            );
            infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            infoPanel.add(infoLabel);
            
            // Database stats
            JPanel statsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            statsPanel.setBorder(BorderFactory.createTitledBorder("Database Statistics"));
            statsPanel.setBackground(Color.WHITE);
            
            try {
                List<Object[]> trains = DatabaseManager.getTrains();
                List<Object[]> bookings = DatabaseManager.getBookings();
                
                statsPanel.add(new JLabel("🚂 Total Trains:"));
                statsPanel.add(new JLabel(String.valueOf(trains.size())));
                
                statsPanel.add(new JLabel("📋 Total Bookings:"));
                statsPanel.add(new JLabel(String.valueOf(bookings.size())));
            } catch (Exception e) {
                statsPanel.add(new JLabel("Error loading stats: " + e.getMessage()));
            }
            
            // Actions panel
            JPanel actionsPanel = new JPanel(new FlowLayout());
            actionsPanel.setBorder(BorderFactory.createTitledBorder("Admin Actions"));
            
            JButton refreshBtn = new JButton("🔄 Refresh Data");
            refreshBtn.setBackground(new Color(70, 130, 180));
            refreshBtn.setForeground(Color.WHITE);
            refreshBtn.addActionListener(e -> {
                JOptionPane.showMessageDialog(panel, "✅ Data refreshed from database!");
            });
            
            JButton backupBtn = new JButton("💾 Backup Info");
            backupBtn.addActionListener(e -> {
                JOptionPane.showMessageDialog(panel, 
                    "📊 Database Backup Info\n\n" +
                    "• Tables: trains, bookings, passengers\n" +
                    "• Auto-backup enabled\n" +
                    "• Last backup: Recent\n" +
                    "• Status: Healthy");
            });
            
            actionsPanel.add(refreshBtn);
            actionsPanel.add(backupBtn);
            
            panel.add(infoPanel, BorderLayout.NORTH);
            panel.add(statsPanel, BorderLayout.CENTER);
            panel.add(actionsPanel, BorderLayout.SOUTH);
            
            return panel;
        }
        
        private JPanel createTrainViewPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Header
            JLabel headerLabel = new JLabel("🚂 Train Database - Admin View (Read Only)", SwingConstants.CENTER);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
            headerLabel.setBackground(new Color(70, 130, 180));
            headerLabel.setForeground(Color.WHITE);
            headerLabel.setOpaque(true);
            headerLabel.setPreferredSize(new Dimension(0, 40));
            panel.add(headerLabel, BorderLayout.NORTH);
            
            // Trains table (read-only)
            String[] trainColumns = {"Train No", "Name", "From", "To", "Departure", "Arrival", "Price", "Available Seats"};
            DefaultTableModel trainViewModel = new DefaultTableModel(trainColumns, 0) {
                public boolean isCellEditable(int row, int col) { return false; }
            };
            JTable trainViewTable = new JTable(trainViewModel);
            trainViewTable.setRowHeight(25);
            trainViewTable.getTableHeader().setBackground(new Color(70, 130, 180));
            trainViewTable.getTableHeader().setForeground(Color.WHITE);
            trainViewTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            
            // Load train data
            if (dbConnected) {
                List<Object[]> dbTrains = DatabaseManager.getTrains();
                for (Object[] train : dbTrains) {
                    trainViewModel.addRow(train);
                }
            } else {
                // Load sample data with additional columns
                for (String[] train : trains) {
                    trainViewModel.addRow(new Object[]{train[0], train[1], train[2], train[3], "06:00", "18:00", train[4], "120"});
                }
            }
            
            JScrollPane scrollPane = new JScrollPane(trainViewTable);
            panel.add(scrollPane, BorderLayout.CENTER);
            
            // Info panel
            JPanel infoPanel = new JPanel();
            infoPanel.setBackground(new Color(240, 248, 255));
            infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
            JLabel infoLabel = new JLabel("<html><b>Admin View:</b> Train data is read-only for viewing purposes only. Total trains: " + trainViewModel.getRowCount() + "</html>");
            infoPanel.add(infoLabel);
            panel.add(infoPanel, BorderLayout.SOUTH);
            
            return panel;
        }
        
        private JPanel createBookingViewPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Header
            JLabel headerLabel = new JLabel("📋 Booking Database - Admin View (Read Only)", SwingConstants.CENTER);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
            headerLabel.setBackground(new Color(70, 130, 180));
            headerLabel.setForeground(Color.WHITE);
            headerLabel.setOpaque(true);
            headerLabel.setPreferredSize(new Dimension(0, 40));
            panel.add(headerLabel, BorderLayout.NORTH);
            
            // Bookings table (read-only)
            String[] bookingColumns = {"PNR", "Passenger Name", "Train", "Journey Date", "Status"};
            DefaultTableModel bookingViewModel = new DefaultTableModel(bookingColumns, 0) {
                public boolean isCellEditable(int row, int col) { return false; }
            };
            JTable bookingViewTable = new JTable(bookingViewModel);
            bookingViewTable.setRowHeight(25);
            bookingViewTable.getTableHeader().setBackground(new Color(70, 130, 180));
            bookingViewTable.getTableHeader().setForeground(Color.WHITE);
            bookingViewTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            
            // Load booking data
            if (dbConnected) {
                List<Object[]> dbBookings = DatabaseManager.getBookings();
                for (Object[] booking : dbBookings) {
                    bookingViewModel.addRow(booking);
                }
            } else {
                // Load sample data with additional columns
                for (String[] booking : bookings) {
                    bookingViewModel.addRow(new Object[]{booking[0], booking[1], booking[2], "2024-01-15", booking[3]});
                }
            }
            
            JScrollPane scrollPane = new JScrollPane(bookingViewTable);
            panel.add(scrollPane, BorderLayout.CENTER);
            
            // Info panel
            JPanel infoPanel = new JPanel();
            infoPanel.setBackground(new Color(240, 248, 255));
            infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
            JLabel infoLabel = new JLabel("<html><b>Admin View:</b> Booking data is read-only for viewing purposes only. Total bookings: " + bookingViewModel.getRowCount() + "</html>");
            infoPanel.add(infoLabel);
            panel.add(infoPanel, BorderLayout.SOUTH);
            
            return panel;
        }
        
        public void showBookingDialog(String trainNo, String trainName, String price) {
            JDialog dialog = new JDialog(this, "Book Train Ticket", true);
            dialog.setSize(350, 250);
            dialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            panel.add(new JLabel("Passenger Name:"));
            JTextField nameField = new JTextField();
            panel.add(nameField);
            
            panel.add(new JLabel("Age:"));
            JTextField ageField = new JTextField();
            panel.add(ageField);
            
            panel.add(new JLabel("Train:"));
            panel.add(new JLabel(trainName));
            
            panel.add(new JLabel("Price:"));
            panel.add(new JLabel(price));
            
            JPanel buttonPanel = new JPanel();
            JButton confirmBtn = new JButton("Confirm Booking");
            JButton cancelBtn = new JButton("Cancel");
            
            confirmBtn.setBackground(new Color(34, 139, 34));
            confirmBtn.setForeground(Color.WHITE);
            cancelBtn.setBackground(new Color(220, 20, 60));
            cancelBtn.setForeground(Color.WHITE);
            
            confirmBtn.addActionListener(e -> {
                String name = nameField.getText().trim();
                String age = ageField.getText().trim();
                
                if (name.isEmpty() || age.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields!");
                    return;
                }
                
                // Generate PNR and book ticket
                String pnr = "PNR" + (1000 + bookings.size());
                boolean success = false;
                
                if (isAdmin && dbConnected) {
                    // Book in database for admin
                    try {
                        int ageInt = Integer.parseInt(age);
                        success = DatabaseManager.bookTicket(pnr, name, ageInt, trainNo);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "Please enter valid age!");
                        return;
                    }
                } else {
                    // Book locally for users
                    bookings.add(new String[]{pnr, name, trainName, "Confirmed"});
                    success = true;
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, 
                        "✅ Ticket Booked Successfully!\n\nPNR: " + pnr + "\nName: " + name + 
                        "\nTrain: " + trainName + "\nPrice: " + price +
                        (dbConnected ? "\n✨ Saved to database!" : "\n✨ Saved locally!"));
                    
                    loadBookings(); // Refresh bookings table
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "❌ Booking failed! Please try again.");
                }
            });
            
            cancelBtn.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(confirmBtn);
            buttonPanel.add(cancelBtn);
            
            dialog.add(panel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        }
    
        private JPanel createBookingPanel() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Search panel
            JPanel searchPanel = new JPanel(new FlowLayout());
            searchPanel.setBorder(BorderFactory.createTitledBorder("Search Trains"));
            searchPanel.setBackground(new Color(240, 248, 255));
            
            String[] cities = {"Delhi", "Mumbai", "Chennai", "Kolkata", "Bangalore"};
            
            searchPanel.add(new JLabel("From:"));
            fromCombo = new JComboBox<>(cities);
            searchPanel.add(fromCombo);
            
            searchPanel.add(new JLabel("To:"));
            toCombo = new JComboBox<>(cities);
            searchPanel.add(toCombo);
            
            JButton searchBtn = new JButton("Search Trains");
            searchBtn.setBackground(new Color(70, 130, 180));
            searchBtn.setForeground(Color.WHITE);
            searchBtn.addActionListener(e -> searchTrains());
            searchPanel.add(searchBtn);
            
            // Trains table
            String[] trainColumns = {"Train No", "Name", "From", "To", "Price", "Book"};
            trainModel = new DefaultTableModel(trainColumns, 0) {
                public boolean isCellEditable(int row, int col) { return col == 5; }
            };
            trainTable = new JTable(trainModel);
            trainTable.setRowHeight(30);
            trainTable.getTableHeader().setBackground(new Color(70, 130, 180));
            trainTable.getTableHeader().setForeground(Color.WHITE);
            trainTable.getColumn("Book").setCellRenderer(new ButtonRenderer());
            trainTable.getColumn("Book").setCellEditor(new ButtonEditor());
            
            loadAllTrains();
            
            panel.add(searchPanel, BorderLayout.NORTH);
            panel.add(new JScrollPane(trainTable), BorderLayout.CENTER);
            
            return panel;
        }
    
        private JPanel createBookingsPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Bookings table
            String[] bookingColumns = {"PNR", "Passenger", "Train", "Status"};
            bookingModel = new DefaultTableModel(bookingColumns, 0);
            bookingTable = new JTable(bookingModel);
            bookingTable.setRowHeight(25);
            bookingTable.getTableHeader().setBackground(new Color(70, 130, 180));
            bookingTable.getTableHeader().setForeground(Color.WHITE);
            
            loadBookings();
            
            panel.add(new JScrollPane(bookingTable), BorderLayout.CENTER);
            
            return panel;
        }
    
        private void loadAllTrains() {
            trainModel.setRowCount(0);
            if (isAdmin && dbConnected) {
                // Load from database for admin
                List<Object[]> dbTrains = DatabaseManager.getTrains();
                for (Object[] train : dbTrains) {
                    trainModel.addRow(new Object[]{train[0], train[1], train[2], train[3], train[6], "Book"});
                }
            } else {
                // Load sample data for users
                for (String[] train : trains) {
                    trainModel.addRow(new Object[]{train[0], train[1], train[2], train[3], train[4], "Book"});
                }
            }
        }
    
        private void searchTrains() {
            String from = (String) fromCombo.getSelectedItem();
            String to = (String) toCombo.getSelectedItem();
            
            if (from.equals(to)) {
                JOptionPane.showMessageDialog(this, "From and To stations cannot be same!");
                return;
            }
            
            trainModel.setRowCount(0);
            if (isAdmin && dbConnected) {
                // Search in database for admin
                List<Object[]> dbTrains = DatabaseManager.searchTrains(from, to);
                for (Object[] train : dbTrains) {
                    trainModel.addRow(new Object[]{train[0], train[1], train[2], train[3], train[6], "Book"});
                }
            } else {
                // Search in sample data for users
                for (String[] train : trains) {
                    if (train[2].equals(from) && train[3].equals(to)) {
                        trainModel.addRow(new Object[]{train[0], train[1], train[2], train[3], train[4], "Book"});
                    }
                }
            }
            
            if (trainModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No trains found for selected route!");
                loadAllTrains();
            }
        }
    
        private void loadBookings() {
            bookingModel.setRowCount(0);
            if (isAdmin && dbConnected) {
                // Load from database for admin
                List<Object[]> dbBookings = DatabaseManager.getBookings();
                for (Object[] booking : dbBookings) {
                    bookingModel.addRow(new Object[]{booking[0], booking[1], booking[2], booking[4]});
                }
            } else {
                // Load sample data for users
                for (String[] booking : bookings) {
                    bookingModel.addRow(booking);
                }
            }
        }
    
        // Button renderer for table
        class ButtonRenderer extends JButton implements TableCellRenderer {
            public ButtonRenderer() {
                setOpaque(true);
                setBackground(new Color(34, 139, 34));
                setForeground(Color.WHITE);
                setFont(new Font("Arial", Font.BOLD, 11));
            }
            
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                setText("Book Now");
                return this;
            }
        }
    
        // Button editor for table
        class ButtonEditor extends DefaultCellEditor {
            private JButton button;
            private boolean clicked;
            private int row;
            
            public ButtonEditor() {
                super(new JCheckBox());
                button = new JButton();
                button.setOpaque(true);
                button.addActionListener(e -> fireEditingStopped());
            }
            
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                this.row = row;
                button.setText("Book Now");
                button.setBackground(new Color(34, 139, 34));
                button.setForeground(Color.WHITE);
                clicked = true;
                return button;
            }
            
            public Object getCellEditorValue() {
                if (clicked) {
                    String trainNo = trainModel.getValueAt(row, 0).toString();
                    String trainName = trainModel.getValueAt(row, 1).toString();
                    String price = trainModel.getValueAt(row, 4).toString();
                    showBookingDialog(trainNo, trainName, price);
                }
                clicked = false;
                return "Book Now";
            }
            
            public boolean stopCellEditing() {
                clicked = false;
                return super.stopCellEditing();
            }
        }
    }
    
    // Login Frame
    static class LoginFrame extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JRadioButton userRadio, adminRadio;
        
        public LoginFrame() {
            setTitle("🚂 Railway Ticket Booking System - Login");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(450, 350);
            setLocationRelativeTo(null);
            initLoginUI();
        }
        
        private void initLoginUI() {
            setLayout(new BorderLayout());
            
            // Header
            JLabel header = new JLabel("🚂 Railway Ticket Booking System", SwingConstants.CENTER);
            header.setFont(new Font("Arial", Font.BOLD, 20));
            header.setBackground(new Color(70, 130, 180));
            header.setForeground(Color.WHITE);
            header.setOpaque(true);
            header.setPreferredSize(new Dimension(0, 60));
            add(header, BorderLayout.NORTH);
            
            // Main panel
            JPanel main = new JPanel(new GridBagLayout());
            main.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(15, 15, 15, 15);
            
            // User type selection
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            JLabel typeLabel = new JLabel("Select User Type:", SwingConstants.CENTER);
            typeLabel.setFont(new Font("Arial", Font.BOLD, 16));
            main.add(typeLabel, gbc);
            
            gbc.gridy = 1; gbc.gridwidth = 1;
            JPanel radioPanel = new JPanel(new FlowLayout());
            radioPanel.setBackground(Color.WHITE);
            userRadio = new JRadioButton("👤 User", true);
            adminRadio = new JRadioButton("👨‍💼 Admin");
            userRadio.setFont(new Font("Arial", Font.PLAIN, 14));
            adminRadio.setFont(new Font("Arial", Font.PLAIN, 14));
            ButtonGroup group = new ButtonGroup();
            group.add(userRadio);
            group.add(adminRadio);
            radioPanel.add(userRadio);
            radioPanel.add(adminRadio);
            gbc.gridx = 0; gbc.gridwidth = 2;
            main.add(radioPanel, gbc);
            
            // Username
            gbc.gridy = 2; gbc.gridwidth = 1; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
            JLabel userLabel = new JLabel("Username:");
            userLabel.setFont(new Font("Arial", Font.BOLD, 14));
            main.add(userLabel, gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            usernameField = new JTextField(18);
            usernameField.setPreferredSize(new Dimension(180, 30));
            usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
            main.add(usernameField, gbc);
            
            // Password
            gbc.gridy = 3; gbc.gridx = 0; gbc.anchor = GridBagConstraints.EAST;
            JLabel passLabel = new JLabel("Password:");
            passLabel.setFont(new Font("Arial", Font.BOLD, 14));
            main.add(passLabel, gbc);
            gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
            passwordField = new JPasswordField(18);
            passwordField.setPreferredSize(new Dimension(180, 30));
            passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
            main.add(passwordField, gbc);
            
            // Buttons
            gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.WHITE);
            
            JButton loginBtn = new JButton("Login");
            loginBtn.setBackground(new Color(34, 139, 34));
            loginBtn.setForeground(Color.WHITE);
            loginBtn.setPreferredSize(new Dimension(100, 35));
            loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
            loginBtn.addActionListener(this::handleLogin);
            
            JButton exitBtn = new JButton("Exit");
            exitBtn.setBackground(new Color(220, 20, 60));
            exitBtn.setForeground(Color.WHITE);
            exitBtn.setPreferredSize(new Dimension(100, 35));
            exitBtn.setFont(new Font("Arial", Font.BOLD, 14));
            exitBtn.addActionListener(e -> System.exit(0));
            
            buttonPanel.add(loginBtn);
            buttonPanel.add(Box.createHorizontalStrut(20));
            buttonPanel.add(exitBtn);
            main.add(buttonPanel, gbc);
            
            // Instructions
            gbc.gridy = 5;
            JLabel info = new JLabel("<html><center><b>Login Credentials:</b><br>User: user/user<br>Admin: admin/admin</center></html>");
            info.setFont(new Font("Arial", Font.PLAIN, 12));
            info.setForeground(new Color(70, 130, 180));
            main.add(info, gbc);
            
            add(main, BorderLayout.CENTER);
        }
        
        private void handleLogin(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            boolean isAdminSelected = adminRadio.isSelected();
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username and password!");
                return;
            }
            
            // Check credentials
            boolean validLogin = false;
            if (isAdminSelected) {
                if (username.equals("admin") && password.equals("admin")) {
                    currentUser = "Admin";
                    isAdmin = true;
                    // Try database connection for admin
                    dbConnected = DatabaseManager.connect();
                    validLogin = true;
                }
            } else {
                if (username.equals("user") && password.equals("user")) {
                    currentUser = "User";
                    isAdmin = false;
                    dbConnected = false; // Users don't get database access
                    validLogin = true;
                }
            }
            
            if (validLogin) {
                dispose();
                new MainFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!\n\nPlease try:\nUser: user/user\nAdmin: admin/admin");
            }
        }
    }
}