package doan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.sql.*;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Vector;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.table.DefaultTableModel;

public class XuLyDongThoiUI extends JFrame {
    private JPanel sideBar, mainPanel, headerPanel;
    private CardLayout cardLayout;
    private Color primaryColor = new Color(25, 42, 86);    // Xanh đậm
    private Color secondaryColor = new Color(46, 64, 83);  // Xanh nhạt
    private Color accentColor = new Color(46, 204, 113);   // Xanh lá
    private Color textColor = Color.WHITE;
    private Font titleFont = new Font("Arial", Font.BOLD, 24);
    private Font normalFont = new Font("Arial", Font.PLAIN, 14);
    private String maNvdp;
    private String tenNvdp;
    private Connection transactionConnection;
    private boolean isInTransaction = false;
    private JTextArea transactionLogArea;

    public XuLyDongThoiUI(String maNvdp, String tenNvdp) {
        this.maNvdp = maNvdp;
        this.tenNvdp = tenNvdp;
        setTitle("Xử lý đồng thời - Hệ thống Quản lý Thu gom Rác thải");
        setSize(1200, 760);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        createHeaderPanel();
        createSideBar();
        createMainPanel();
        add(headerPanel, BorderLayout.NORTH);
        add(sideBar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void createHeaderPanel() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(0, 60));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel titleLabel = new JLabel("XỬ LÝ ĐỒNG THỜI");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(textColor);
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        String userInfo = (tenNvdp != null && maNvdp != null) ? tenNvdp + " (" + "Mã: " + maNvdp + ") |" : "Admin |";
        JLabel userLabel = new JLabel(userInfo);
        userLabel.setForeground(textColor);
        JButton changePassBtn = new JButton("Đổi mật khẩu");
        JButton logoutBtn = new JButton("Đăng xuất");
        styleButton(changePassBtn);
        styleButton(logoutBtn);
        changePassBtn.addActionListener(e -> {
            showChangePasswordDialog();
        });
        logoutBtn.addActionListener(e -> {
            dispose();
            new GiaoDienDangNhap().setVisible(true);
        });
        userPanel.add(userLabel);
        userPanel.add(changePassBtn);
        userPanel.add(logoutBtn);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
    }

    private void createSideBar() {
        sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBackground(secondaryColor);
        sideBar.setPreferredSize(new Dimension(200, 0));
        sideBar.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create buttons
        JButton hoaDonBtn = createSideButton("Hóa đơn");
        JButton phanCongBtn = createSideButton("Phân công");
        JButton thongKeBtn = createSideButton("Thống kê phân công");

        // Add action listeners
        hoaDonBtn.addActionListener(e -> cardLayout.show(mainPanel, "Hóa đơn"));
        phanCongBtn.addActionListener(e -> cardLayout.show(mainPanel, "Phân công"));
        thongKeBtn.addActionListener(e -> cardLayout.show(mainPanel, "Thống kê phân công"));

        // Add buttons to sidebar
        sideBar.add(hoaDonBtn);
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(phanCongBtn);
        sideBar.add(Box.createVerticalStrut(10));
        sideBar.add(thongKeBtn);
        sideBar.add(Box.createVerticalStrut(10));
    }

    private JButton createSideButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(46, 64, 83));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(40, 42, 78));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(46, 64, 83));
            }
        });
        return button;
    }

    private void createMainPanel() {
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createHoaDonPanel(), "Hóa đơn");
        mainPanel.add(createPhanCongPanel(), "Phân công");
        mainPanel.add(createThongKePhanCongPanel(), "Thống kê phân công");

        cardLayout.show(mainPanel, "Hóa đơn");
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(46, 64, 83));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(40, 42, 78));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(46, 64, 83));
            }
        });
        button.addActionListener(e -> cardLayout.show(mainPanel, text));
        return button;
    }

    private void styleButton(JButton button) {
        button.setBackground(secondaryColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(normalFont);
    }

    private JPanel createHoaDonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Top panel for Transaction Control and Data Input
        JPanel topPanel = new JPanel(new BorderLayout());

        // Transaction control panel
        JPanel transactionControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        transactionControlPanel.setBackground(Color.WHITE);

        // Isolation level combo box
        String[] isolationLevels = {
            "READ COMMITTED",
            "SERIALIZABLE"
        };
        JComboBox<String> isolationLevelCombo = new JComboBox<>(isolationLevels);
        transactionControlPanel.add(new JLabel("Isolation Level:"));
        transactionControlPanel.add(isolationLevelCombo);

        // Transaction buttons
        JButton startTransactionBtn = new JButton("Start Transaction");
        JButton commitBtn = new JButton("COMMIT");
        JButton rollbackBtn = new JButton("ROLLBACK");
        styleButton(startTransactionBtn);
        styleButton(commitBtn);
        styleButton(rollbackBtn);
        commitBtn.setEnabled(false);
        rollbackBtn.setEnabled(false);

        transactionControlPanel.add(startTransactionBtn);
        transactionControlPanel.add(commitBtn);
        transactionControlPanel.add(rollbackBtn);

        // Status Label for Transaction
        JLabel transactionStatusLabel = new JLabel("Status: No Transaction");
        transactionControlPanel.add(transactionStatusLabel);

        // Data Input Panel
        JPanel dataInputPanel = new JPanel(new GridBagLayout());
        dataInputPanel.setBorder(BorderFactory.createTitledBorder("Data Input"));
        dataInputPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Fields for HoaDon table
        JTextField maHoaDonField = new JTextField(15);
        JTextField maHopDongField = new JTextField(15);
        JTextField maNvdpField = new JTextField(15);
        JSpinner ngLapSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(ngLapSpinner, "yyyy-MM-dd");
        ngLapSpinner.setEditor(dateEditor);
        JTextField soTienField = new JTextField(15);
        String[] tinhTrangOptions = {"Chưa thanh toán", "Thanh toán"};
        JComboBox<String> tinhTrangCombo = new JComboBox<>(tinhTrangOptions);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dataInputPanel.add(new JLabel("Mã hóa đơn:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        dataInputPanel.add(maHoaDonField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        dataInputPanel.add(new JLabel("Mã hợp đồng:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        dataInputPanel.add(maHopDongField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dataInputPanel.add(new JLabel("Mã NVĐP:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        dataInputPanel.add(maNvdpField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        dataInputPanel.add(new JLabel("Ngày lập:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        dataInputPanel.add(ngLapSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dataInputPanel.add(new JLabel("Số tiền:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        dataInputPanel.add(soTienField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        dataInputPanel.add(new JLabel("Tình trạng:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 2;
        dataInputPanel.add(tinhTrangCombo, gbc);

        // Center panel for Table and Database Operations
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Current Data"));
        centerPanel.setBackground(Color.WHITE);

        // Table
        String[] columns = {
            "Mã hóa đơn",
            "Mã hợp đồng",
            "Mã NVĐP",
            "Ngày lập",
            "Số tiền",
            "Tình trạng"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);

        // Add search panel
        String[] hoaDonSearchFields = {
            "MaHoaDon",
            "MaHopDong",
            "MaNvdp",
            "NgLap",
            "SoTien",
            "TinhTrang"
        };

        // Create transaction log area first
        JTextArea logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);

        // Create search panel with the log area
        JPanel searchPanel = createSearchPanel(hoaDonSearchFields, model, "HoaDon", logArea);

        // Add listener to table to load data to input fields
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int selectedRow = table.getSelectedRow();
                maHoaDonField.setText(table.getValueAt(selectedRow, 0).toString());
                maHopDongField.setText(table.getValueAt(selectedRow, 1).toString());
                maNvdpField.setText(table.getValueAt(selectedRow, 2).toString());

                // Handle date from table to spinner
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = sdf.parse(table.getValueAt(selectedRow, 3).toString());
                    ngLapSpinner.setValue(date);
                } catch (Exception ex) {
                    log(logArea, "Error parsing date from table: " + ex.getMessage());
                }

                soTienField.setText(table.getValueAt(selectedRow, 4).toString());
                tinhTrangCombo.setSelectedItem(table.getValueAt(selectedRow, 5).toString());

                // Disable editing MaHoaDon
                maHoaDonField.setEnabled(false);
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Database Operations buttons
        JPanel dbOperationsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dbOperationsPanel.setBackground(Color.WHITE);

        JButton readDataBtn = new JButton("READ Data");
        JButton updateDataBtn = new JButton("UPDATE Data");
        JButton insertDataBtn = new JButton("INSERT Data");
        JButton deleteDataBtn = new JButton("DELETE Data");

        styleButton(readDataBtn);
        styleButton(updateDataBtn);
        styleButton(insertDataBtn);
        styleButton(deleteDataBtn);

        dbOperationsPanel.add(readDataBtn);
        dbOperationsPanel.add(updateDataBtn);
        dbOperationsPanel.add(insertDataBtn);
        dbOperationsPanel.add(deleteDataBtn);

        centerPanel.add(dbOperationsPanel, BorderLayout.SOUTH);

        // Transaction Log
        JPanel transactionLogPanel = new JPanel(new BorderLayout());
        transactionLogPanel.setBorder(BorderFactory.createTitledBorder("Transaction Log"));
        transactionLogPanel.setBackground(Color.WHITE);
        transactionLogPanel.add(logScrollPane, BorderLayout.CENTER);

        // Add components to panel
        topPanel.add(transactionControlPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(dataInputPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(transactionLogPanel, BorderLayout.SOUTH);

        // Add action listeners
        startTransactionBtn.addActionListener(e -> {
            try {
                if (!isInTransaction) {
                    transactionConnection = ConnectionJDBC.getConnection();
                    transactionConnection.setAutoCommit(false);
                    String selectedLevel = (String) isolationLevelCombo.getSelectedItem();
                    int isolationLevel;
                    switch (selectedLevel) {
                        case "READ COMMITTED":
                            isolationLevel = Connection.TRANSACTION_READ_COMMITTED;
                            break;
                        case "SERIALIZABLE":
                            isolationLevel = Connection.TRANSACTION_SERIALIZABLE;
                            break;
                        default:
                            isolationLevel = Connection.TRANSACTION_READ_COMMITTED;
                    }
                    transactionConnection.setTransactionIsolation(isolationLevel);
                    isInTransaction = true;
                    startTransactionBtn.setEnabled(false);
                    commitBtn.setEnabled(true);
                    rollbackBtn.setEnabled(true);
                    transactionStatusLabel.setText("Status: Transaction Started (" + selectedLevel + ")");
                    log(logArea, "=== TRANSACTION STARTED ===");
                    log(logArea, "Isolation Level: " + selectedLevel);
                    log(logArea, "Auto-commit: OFF");
                }
            } catch (SQLException ex) {
                log(logArea, "Lỗi khi bắt đầu giao dịch: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Lỗi khi bắt đầu giao dịch: " + ex.getMessage());
            }
        });

        commitBtn.addActionListener(e -> {
            try {
                if (isInTransaction) {
                    transactionConnection.commit();
                    transactionConnection.close();
                    isInTransaction = false;
                    startTransactionBtn.setEnabled(true);
                    commitBtn.setEnabled(false);
                    rollbackBtn.setEnabled(false);
                    transactionStatusLabel.setText("Status: Transaction Committed");
                    log(logArea, "=== TRANSACTION COMMITTED ===");
                    log(logArea, "Auto-commit: ON");
                    JOptionPane.showMessageDialog(this, "Đã commit giao dịch thành công");
                }
            } catch (SQLException ex) {
                log(logArea, "Lỗi khi commit giao dịch: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Lỗi khi commit giao dịch: " + ex.getMessage());
            }
        });

        rollbackBtn.addActionListener(e -> {
            try {
                if (isInTransaction) {
                    transactionConnection.rollback();
                    transactionConnection.close();
                    isInTransaction = false;
                    startTransactionBtn.setEnabled(true);
                    commitBtn.setEnabled(false);
                    rollbackBtn.setEnabled(false);
                    transactionStatusLabel.setText("Status: Transaction Rolled Back");
                    log(logArea, "=== TRANSACTION ROLLED BACK ===");
                    log(logArea, "Auto-commit: ON");
                    JOptionPane.showMessageDialog(this, "Đã rollback giao dịch");
                }
            } catch (SQLException ex) {
                log(logArea, "Lỗi khi rollback giao dịch: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Lỗi khi rollback giao dịch: " + ex.getMessage());
            }
        });

        // Add action listeners for database operations
        readDataBtn.addActionListener(e -> readHoaDonData(model, logArea));
        updateDataBtn.addActionListener(e -> updateHoaDonData(maHoaDonField, maHopDongField, maNvdpField, ngLapSpinner, soTienField, tinhTrangCombo, logArea));
        insertDataBtn.addActionListener(e -> insertHoaDonData(maHoaDonField, maHopDongField, maNvdpField, ngLapSpinner, soTienField, tinhTrangCombo, logArea));
        deleteDataBtn.addActionListener(e -> deleteHoaDonData(maHoaDonField, logArea));

        return panel;
    }

    private void log(JTextArea logArea, String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());
        logArea.append("[" + timestamp + "] " + message + "\n");
        // Auto-scroll to the bottom
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private JPanel createSearchPanel(String[] searchFields, DefaultTableModel model, String tableName, JTextArea logArea) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm"));

        // Create combo box for search fields
        JComboBox<String> searchFieldCombo = new JComboBox<>(searchFields);
        searchFieldCombo.setPreferredSize(new Dimension(150, 30));

        // Create text field for search value
        JTextField searchValueField = new JTextField(20);
        searchValueField.setPreferredSize(new Dimension(200, 30));

        // Create search button
        JButton searchButton = new JButton("Tìm kiếm");
        styleButton(searchButton);

        // Add search functionality
        searchButton.addActionListener(e -> {
            if (!isInTransaction) {
                log(logArea, "SEARCH: No transaction started. Please start a transaction.");
                JOptionPane.showMessageDialog(this, "Please start a transaction first.");
                return;
            }

            String selectedField = (String) searchFieldCombo.getSelectedItem();
            String searchValue = searchValueField.getText().trim();

            if (searchValue.isEmpty()) {
                // If search value is empty, show all records
                readData(model, tableName, logArea);
                return;
            }

            log(logArea, "SEARCH: Searching for " + selectedField + " = '" + searchValue + "'");
            model.setRowCount(0); // Clear existing data

            try {
                String sql = "SELECT * FROM " + tableName + " WHERE " + selectedField + " = ?";
                log(logArea, "SEARCH: Executing SQL: " + sql);
                
                try (PreparedStatement pstmt = transactionConnection.prepareStatement(sql)) {
                    // Handle different data types
                    if (selectedField.equals("MaHoaDon") || selectedField.equals("MaHopDong") || 
                        selectedField.equals("MaNvdp") || selectedField.equals("MaPC") || 
                        selectedField.equals("MaLich") || selectedField.equals("MaNvtg") || 
                        selectedField.equals("SoTuyenDaNhan")) {
                        // Handle NUMBER fields
                        try {
                            int value = Integer.parseInt(searchValue);
                            pstmt.setInt(1, value);
                            log(logArea, "SEARCH: Setting parameter as INTEGER: " + value);
                        } catch (NumberFormatException ex) {
                            log(logArea, "SEARCH: Invalid number format for " + selectedField);
                            JOptionPane.showMessageDialog(this, 
                                "Vui lòng nhập số cho trường " + selectedField,
                                "Lỗi định dạng",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else if (selectedField.equals("NgLap")) {
                        // Handle DATE field
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = sdf.parse(searchValue);
                            pstmt.setDate(1, new java.sql.Date(date.getTime()));
                            log(logArea, "SEARCH: Setting parameter as DATE: " + searchValue);
                        } catch (ParseException ex) {
                            log(logArea, "SEARCH: Invalid date format for NgLap");
                            JOptionPane.showMessageDialog(this,
                                "Vui lòng nhập ngày theo định dạng yyyy-MM-dd",
                                "Lỗi định dạng",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else if (selectedField.equals("SoTien")) {
                        // Handle NUMBER(15,2) field
                        try {
                            double value = Double.parseDouble(searchValue);
                            pstmt.setDouble(1, value);
                            log(logArea, "SEARCH: Setting parameter as DOUBLE: " + value);
                        } catch (NumberFormatException ex) {
                            log(logArea, "SEARCH: Invalid number format for SoTien");
                            JOptionPane.showMessageDialog(this,
                                "Vui lòng nhập số cho trường Số tiền",
                                "Lỗi định dạng",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else {
                        // Handle VARCHAR2 fields (like TinhTrang)
                        pstmt.setString(1, searchValue);
                        log(logArea, "SEARCH: Setting parameter as STRING: " + searchValue);
                    }

                    log(logArea, "SEARCH: Executing query...");
                    ResultSet rs = pstmt.executeQuery();
                    int rowCount = 0;
                    while (rs.next()) {
                        Vector<Object> row = new Vector<>();
                        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                            row.add(rs.getObject(i));
                        }
                        model.addRow(row);
                        rowCount++;
                    }
                    log(logArea, "SEARCH: Found " + rowCount + " records.");
                }
            } catch (SQLException ex) {
                handleSQLException(ex, "SEARCH", logArea);
            }
        });

        // Add key listener to search field for Enter key
        searchValueField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchButton.doClick();
                }
            }
        });

        searchPanel.add(new JLabel("Tìm theo:"));
        searchPanel.add(searchFieldCombo);
        searchPanel.add(new JLabel("Giá trị:"));
        searchPanel.add(searchValueField);
        searchPanel.add(searchButton);

        return searchPanel;
    }

    private void readData(DefaultTableModel model, String tableName, JTextArea logArea) {
        String sql = "SELECT * FROM " + tableName;
        try (Statement stmt = transactionConnection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int rowCount = 0;
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getObject(i));
                }
                model.addRow(row);
                rowCount++;
            }
            log(logArea, "READ: Retrieved " + rowCount + " records.");
        } catch (SQLException ex) {
            handleSQLException(ex, "READ", logArea);
        }
    }

    private void handleSQLException(SQLException ex, String operation, JTextArea logArea) {
        String errorMessage = ex.getMessage();
        if (errorMessage != null && (errorMessage.contains("ORA-00060") || errorMessage.toLowerCase().contains("deadlock"))) {
            try {
                if (isInTransaction && transactionConnection != null) {
                    transactionConnection.rollback();
                    log(logArea, operation + ": Deadlock detected - Transaction automatically rolled back");
                    JOptionPane.showMessageDialog(this,
                        "Phát hiện deadlock - Giao dịch đã được tự động rollback.\nVui lòng thử lại sau.",
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                    
                    // Reset transaction state
                    transactionConnection.close();
                    transactionConnection = null;
                    isInTransaction = false;
                    
                    // Update UI state
                    for (Window window : Window.getWindows()) {
                        if (window instanceof XuLyDongThoiUI) {
                            XuLyDongThoiUI ui = (XuLyDongThoiUI) window;
                            // Find and update transaction control buttons
                            for (Component comp : ui.getContentPane().getComponents()) {
                                if (comp instanceof JPanel) {
                                    updateTransactionControls((JPanel) comp);
                                }
                            }
                        }
                    }
                }
            } catch (SQLException rollbackEx) {
                log(logArea, operation + ": Error during rollback - " + rollbackEx.getMessage());
            }
        } else {
            log(logArea, operation + ": Error - " + errorMessage);
            JOptionPane.showMessageDialog(this, 
                operation + " error: " + errorMessage,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTransactionControls(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel subPanel = (JPanel) comp;
                if (subPanel.getLayout() instanceof FlowLayout) {
                    for (Component btn : subPanel.getComponents()) {
                        if (btn instanceof JButton) {
                            JButton button = (JButton) btn;
                            if (button.getText().equals("Start Transaction")) {
                                button.setEnabled(true);
                            } else if (button.getText().equals("COMMIT") || button.getText().equals("ROLLBACK")) {
                                button.setEnabled(false);
                            }
                        } else if (btn instanceof JLabel && btn.toString().contains("Status:")) {
                            ((JLabel) btn).setText("Status: No Transaction");
                        }
                    }
                }
                updateTransactionControls(subPanel);
            }
        }
    }

    private void readHoaDonData(DefaultTableModel model, JTextArea logArea) {
        if (!isInTransaction) {
            log(logArea, "READ: No transaction started. Please start a transaction.");
            JOptionPane.showMessageDialog(this, "Please start a transaction first.");
            return;
        }
        log(logArea, "READ: Attempting to read data...");
        model.setRowCount(0); // Clear existing data
        String sql = "SELECT * FROM HoaDon";
        try (Statement stmt = transactionConnection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int rowCount = 0;
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getObject("MaHoaDon"));
                row.add(rs.getObject("MaHopDong"));
                row.add(rs.getObject("MaNvdp"));
                row.add(rs.getObject("NgLap"));
                row.add(rs.getObject("SoTien"));
                row.add(rs.getObject("TinhTrang"));
                model.addRow(row);
                rowCount++;
            }
            log(logArea, "READ: Retrieved " + rowCount + " records.");
        } catch (SQLException ex) {
            handleSQLException(ex, "READ", logArea);
        }
    }

    private void insertHoaDonData(JTextField maHoaDonField, JTextField maHopDongField, JTextField maNvdpField, JSpinner ngLapSpinner, JTextField soTienField, JComboBox<String> tinhTrangCombo, JTextArea logArea) {
        if (!isInTransaction) {
            log(logArea, "INSERT: No transaction started. Please start a transaction.");
            JOptionPane.showMessageDialog(this, "Please start a transaction first.");
            return;
        }
        log(logArea, "INSERT: Attempting to insert data...");
        String sql = "INSERT INTO HoaDon (MaHoaDon, MaHopDong, MaNvdp, NgLap, SoTien, TinhTrang) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = transactionConnection.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(maHoaDonField.getText()));
            pstmt.setInt(2, Integer.parseInt(maHopDongField.getText()));
            pstmt.setInt(3, Integer.parseInt(maNvdpField.getText()));
            Date selectedDate = (Date) ngLapSpinner.getValue();
            pstmt.setDate(4, new java.sql.Date(selectedDate.getTime()));
            pstmt.setDouble(5, Double.parseDouble(soTienField.getText()));
            pstmt.setString(6, tinhTrangCombo.getSelectedItem().toString());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                log(logArea, "INSERT: Successfully inserted 1 record.");
            } else {
                log(logArea, "INSERT: No records inserted.");
            }
        } catch (SQLException ex) {
            handleSQLException(ex, "INSERT", logArea);
        } catch (NumberFormatException ex) {
            log(logArea, "INSERT: Invalid number format - " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Invalid number format: " + ex.getMessage());
        }
    }

    private void updateHoaDonData(JTextField maHoaDonField, JTextField maHopDongField, JTextField maNvdpField, JSpinner ngLapSpinner, JTextField soTienField, JComboBox<String> tinhTrangCombo, JTextArea logArea) {
        if (!isInTransaction) {
            log(logArea, "UPDATE: No transaction started. Please start a transaction.");
            JOptionPane.showMessageDialog(this, "Please start a transaction first.");
            return;
        }

        // Validate input fields
        if (maHoaDonField.getText().trim().isEmpty()) {
            log(logArea, "UPDATE: Mã hóa đơn không được để trống");
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hóa đơn");
            return;
        }

        log(logArea, "UPDATE: Validating input data...");
        try {
            // Validate numeric fields
            int maHoaDon = Integer.parseInt(maHoaDonField.getText().trim());
            int maHopDong = Integer.parseInt(maHopDongField.getText().trim());
            int maNvdp = Integer.parseInt(maNvdpField.getText().trim());
            double soTien = Double.parseDouble(soTienField.getText().trim());

            // Validate date
            Date selectedDate = (Date) ngLapSpinner.getValue();
            if (selectedDate == null) {
                log(logArea, "UPDATE: Ngày lập không hợp lệ");
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày lập hợp lệ");
                return;
            }

            log(logArea, "UPDATE: Input validation successful");
            log(logArea, "UPDATE: Executing update query...");

            String sql = "UPDATE HoaDon SET MaHopDong = ?, MaNvdp = ?, NgLap = ?, SoTien = ?, TinhTrang = ? WHERE MaHoaDon = ?";
            try (PreparedStatement pstmt = transactionConnection.prepareStatement(sql)) {
                pstmt.setInt(1, maHopDong);
                pstmt.setInt(2, maNvdp);
                pstmt.setDate(3, new java.sql.Date(selectedDate.getTime()));
                pstmt.setDouble(4, soTien);
                pstmt.setString(5, tinhTrangCombo.getSelectedItem().toString());
                pstmt.setInt(6, maHoaDon);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    log(logArea, "UPDATE: Successfully updated " + affectedRows + " record(s).");
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                } else {
                    log(logArea, "UPDATE: No records updated with the provided ID.");
                    JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn với mã: " + maHoaDon);
                }
            }
        } catch (SQLException ex) {
            handleSQLException(ex, "UPDATE", logArea);
        } catch (NumberFormatException ex) {
            log(logArea, "UPDATE: Invalid number format - " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số cho các trường số");
        }
    }

    private void deleteHoaDonData(JTextField maHoaDonField, JTextArea logArea) {
        if (!isInTransaction) {
            log(logArea, "DELETE: No transaction started. Please start a transaction.");
            JOptionPane.showMessageDialog(this, "Please start a transaction first.");
            return;
        }
        log(logArea, "DELETE: Attempting to delete data...");
        String sql = "DELETE FROM HoaDon WHERE MaHoaDon = ?";
        try (PreparedStatement pstmt = transactionConnection.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(maHoaDonField.getText()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                log(logArea, "DELETE: Successfully deleted " + affectedRows + " record(s).");
            } else {
                log(logArea, "DELETE: No records deleted with the provided ID.");
            }
        } catch (SQLException ex) {
            handleSQLException(ex, "DELETE", logArea);
        } catch (NumberFormatException ex) {
            log(logArea, "DELETE: Invalid number format - " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Invalid number format: " + ex.getMessage());
        }
    }

    private JPanel createPhanCongPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Top panel for Transaction Control and Data Input
        JPanel topPanel = new JPanel(new BorderLayout());

        // Transaction control panel
        JPanel transactionControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        transactionControlPanel.setBackground(Color.WHITE);

        // Isolation level combo box
        String[] isolationLevels = {
            "READ COMMITTED",
            "SERIALIZABLE"
        };
        JComboBox<String> isolationLevelCombo = new JComboBox<>(isolationLevels);
        transactionControlPanel.add(new JLabel("Isolation Level:"));
        transactionControlPanel.add(isolationLevelCombo);

        // Transaction buttons
        JButton startTransactionBtn = new JButton("Start Transaction");
        JButton commitBtn = new JButton("COMMIT");
        JButton rollbackBtn = new JButton("ROLLBACK");
        styleButton(startTransactionBtn);
        styleButton(commitBtn);
        styleButton(rollbackBtn);
        commitBtn.setEnabled(false);
        rollbackBtn.setEnabled(false);

        transactionControlPanel.add(startTransactionBtn);
        transactionControlPanel.add(commitBtn);
        transactionControlPanel.add(rollbackBtn);

        // Status Label for Transaction
        JLabel transactionStatusLabel = new JLabel("Status: No Transaction");
        transactionControlPanel.add(transactionStatusLabel);

        // Data Input Panel
        JPanel dataInputPanel = new JPanel(new GridBagLayout());
        dataInputPanel.setBorder(BorderFactory.createTitledBorder("Data Input"));
        dataInputPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Fields for PhanCong table
        JTextField maPCField = new JTextField(15);
        JTextField maNvdpField = new JTextField(15);
        JTextField maLichField = new JTextField(15);
        JTextField maNvtgField = new JTextField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dataInputPanel.add(new JLabel("Mã phân công:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        dataInputPanel.add(maPCField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        dataInputPanel.add(new JLabel("Mã NVĐP:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        dataInputPanel.add(maNvdpField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dataInputPanel.add(new JLabel("Mã lịch:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        dataInputPanel.add(maLichField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        dataInputPanel.add(new JLabel("Mã NV thu gom:"), gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        dataInputPanel.add(maNvtgField, gbc);

        // Center panel for Table and Database Operations
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Current Data"));
        centerPanel.setBackground(Color.WHITE);

        // Table
        String[] columns = {
            "Mã phân công",
            "Mã NVĐP",
            "Mã lịch",
            "Mã NV thu gom"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);

        // Add search panel
        String[] phanCongSearchFields = {
            "MaPC",
            "MaNvdp",
            "MaLich",
            "MaNvtg"
        };
        JPanel searchPanel = createSearchPanel(phanCongSearchFields, model, "PhanCong", transactionLogArea);

        // Add listener to table to load data to input fields
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int selectedRow = table.getSelectedRow();
                maPCField.setText(table.getValueAt(selectedRow, 0).toString());
                maNvdpField.setText(table.getValueAt(selectedRow, 1).toString());
                maLichField.setText(table.getValueAt(selectedRow, 2).toString());
                maNvtgField.setText(table.getValueAt(selectedRow, 3).toString());

                // Disable editing MaPC
                maPCField.setEnabled(false);
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Database Operations buttons
        JPanel dbOperationsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dbOperationsPanel.setBackground(Color.WHITE);

        JButton readDataBtn = new JButton("READ Data");
        JButton updateDataBtn = new JButton("UPDATE Data");
        JButton insertDataBtn = new JButton("INSERT Data");
        JButton deleteDataBtn = new JButton("DELETE Data");

        styleButton(readDataBtn);
        styleButton(updateDataBtn);
        styleButton(insertDataBtn);
        styleButton(deleteDataBtn);

        dbOperationsPanel.add(readDataBtn);
        dbOperationsPanel.add(updateDataBtn);
        dbOperationsPanel.add(insertDataBtn);
        dbOperationsPanel.add(deleteDataBtn);

        centerPanel.add(dbOperationsPanel, BorderLayout.SOUTH);

        // Transaction Log
        JPanel transactionLogPanel = new JPanel(new BorderLayout());
        transactionLogPanel.setBorder(BorderFactory.createTitledBorder("Transaction Log"));
        transactionLogPanel.setBackground(Color.WHITE);

        JTextArea logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);

        transactionLogPanel.add(logScrollPane, BorderLayout.CENTER);

        // Add components to panel
        topPanel.add(transactionControlPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(dataInputPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(transactionLogPanel, BorderLayout.SOUTH);

        // Add action listeners
        startTransactionBtn.addActionListener(e -> {
            try {
                if (!isInTransaction) {
                    transactionConnection = ConnectionJDBC.getConnection();
                    transactionConnection.setAutoCommit(false);
                    String selectedLevel = (String) isolationLevelCombo.getSelectedItem();
                    int isolationLevel;
                    switch (selectedLevel) {
                        case "READ COMMITTED":
                            isolationLevel = Connection.TRANSACTION_READ_COMMITTED;
                            break;
                        case "SERIALIZABLE":
                            isolationLevel = Connection.TRANSACTION_SERIALIZABLE;
                            break;
                        default:
                            isolationLevel = Connection.TRANSACTION_READ_COMMITTED;
                    }
                    transactionConnection.setTransactionIsolation(isolationLevel);
                    isInTransaction = true;
                    startTransactionBtn.setEnabled(false);
                    commitBtn.setEnabled(true);
                    rollbackBtn.setEnabled(true);
                    transactionStatusLabel.setText("Status: Transaction Started (" + selectedLevel + ")");
                    log(logArea, "=== TRANSACTION STARTED ===");
                    log(logArea, "Isolation Level: " + selectedLevel);
                    log(logArea, "Auto-commit: OFF");
                }
            } catch (SQLException ex) {
                log(logArea, "Lỗi khi bắt đầu giao dịch: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Lỗi khi bắt đầu giao dịch: " + ex.getMessage());
            }
        });

        commitBtn.addActionListener(e -> {
            try {
                if (isInTransaction) {
                    transactionConnection.commit();
                    transactionConnection.close();
                    isInTransaction = false;
                    startTransactionBtn.setEnabled(true);
                    commitBtn.setEnabled(false);
                    rollbackBtn.setEnabled(false);
                    transactionStatusLabel.setText("Status: Transaction Committed");
                    log(logArea, "=== TRANSACTION COMMITTED ===");
                    log(logArea, "Auto-commit: ON");
                    JOptionPane.showMessageDialog(this, "Đã commit giao dịch thành công");
                }
            } catch (SQLException ex) {
                log(logArea, "Lỗi khi commit giao dịch: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Lỗi khi commit giao dịch: " + ex.getMessage());
            }
        });

        rollbackBtn.addActionListener(e -> {
            try {
                if (isInTransaction) {
                    transactionConnection.rollback();
                    transactionConnection.close();
                    isInTransaction = false;
                    startTransactionBtn.setEnabled(true);
                    commitBtn.setEnabled(false);
                    rollbackBtn.setEnabled(false);
                    transactionStatusLabel.setText("Status: Transaction Rolled Back");
                    log(logArea, "=== TRANSACTION ROLLED BACK ===");
                    log(logArea, "Auto-commit: ON");
                    JOptionPane.showMessageDialog(this, "Đã rollback giao dịch");
                }
            } catch (SQLException ex) {
                log(logArea, "Lỗi khi rollback giao dịch: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Lỗi khi rollback giao dịch: " + ex.getMessage());
            }
        });

        // Add action listeners for database operations
        readDataBtn.addActionListener(e -> readPhanCongData(model, logArea));
        updateDataBtn.addActionListener(e -> updatePhanCongData(maPCField, maNvdpField, maLichField, maNvtgField, logArea));
        insertDataBtn.addActionListener(e -> insertPhanCongData(maPCField, maNvdpField, maLichField, maNvtgField, logArea));
        deleteDataBtn.addActionListener(e -> deletePhanCongData(maPCField, logArea));

        return panel;
    }

    private void readPhanCongData(DefaultTableModel model, JTextArea logArea) {
        if (!isInTransaction) {
            log(logArea, "READ: No transaction started. Please start a transaction.");
            JOptionPane.showMessageDialog(this, "Please start a transaction first.");
            return;
        }
        log(logArea, "READ: Attempting to read data...");
        model.setRowCount(0); // Clear existing data
        String sql = "SELECT * FROM PhanCong";
        try (Statement stmt = transactionConnection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int rowCount = 0;
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getObject("MaPC"));
                row.add(rs.getObject("MaNvdp"));
                row.add(rs.getObject("MaLich"));
                row.add(rs.getObject("MaNvtg"));
                model.addRow(row);
                rowCount++;
            }
            log(logArea, "READ: Retrieved " + rowCount + " records.");
        } catch (SQLException ex) {
            handleSQLException(ex, "READ", logArea);
        }
    }

    private void insertPhanCongData(JTextField maPCField, JTextField maNvdpField, JTextField maLichField, JTextField maNvtgField, JTextArea logArea) {
        if (!isInTransaction) {
            log(logArea, "INSERT: No transaction started. Please start a transaction.");
            JOptionPane.showMessageDialog(this, "Please start a transaction first.");
            return;
        }
        log(logArea, "INSERT: Attempting to insert data...");
        String sql = "INSERT INTO PhanCong (MaPC, MaNvdp, MaLich, MaNvtg) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = transactionConnection.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(maPCField.getText()));
            pstmt.setInt(2, Integer.parseInt(maNvdpField.getText()));
            pstmt.setInt(3, Integer.parseInt(maLichField.getText()));
            pstmt.setInt(4, Integer.parseInt(maNvtgField.getText()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                log(logArea, "INSERT: Successfully inserted 1 record.");
            } else {
                log(logArea, "INSERT: No records inserted.");
            }
        } catch (SQLException ex) {
            handleSQLException(ex, "INSERT", logArea);
        } catch (NumberFormatException ex) {
            log(logArea, "INSERT: Invalid number format - " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Invalid number format: " + ex.getMessage());
        }
    }

    private void updatePhanCongData(JTextField maPCField, JTextField maNvdpField, JTextField maLichField, JTextField maNvtgField, JTextArea logArea) {
        if (!isInTransaction) {
            log(logArea, "UPDATE: No transaction started. Please start a transaction.");
            JOptionPane.showMessageDialog(this, "Please start a transaction first.");
            return;
        }

        // Validate input fields
        if (maPCField.getText().trim().isEmpty()) {
            log(logArea, "UPDATE: Mã phân công không được để trống");
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phân công");
            return;
        }

        log(logArea, "UPDATE: Validating input data...");
        try {
            // Validate numeric fields
            int maPC = Integer.parseInt(maPCField.getText().trim());
            int maNvdp = Integer.parseInt(maNvdpField.getText().trim());
            int maLich = Integer.parseInt(maLichField.getText().trim());
            int maNvtg = Integer.parseInt(maNvtgField.getText().trim());

            log(logArea, "UPDATE: Input validation successful");
            log(logArea, "UPDATE: Executing update query...");

            String sql = "UPDATE PhanCong SET MaNvdp = ?, MaLich = ?, MaNvtg = ? WHERE MaPC = ?";
            try (PreparedStatement pstmt = transactionConnection.prepareStatement(sql)) {
                pstmt.setInt(1, maNvdp);
                pstmt.setInt(2, maLich);
                pstmt.setInt(3, maNvtg);
                pstmt.setInt(4, maPC);

                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    log(logArea, "UPDATE: Successfully updated " + affectedRows + " record(s).");
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                } else {
                    log(logArea, "UPDATE: No records updated with the provided ID.");
                    JOptionPane.showMessageDialog(this, "Không tìm thấy phân công với mã: " + maPC);
                }
            }
        } catch (SQLException ex) {
            handleSQLException(ex, "UPDATE", logArea);
        } catch (NumberFormatException ex) {
            log(logArea, "UPDATE: Invalid number format - " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số cho các trường số");
        }
    }

    private void deletePhanCongData(JTextField maPCField, JTextArea logArea) {
        if (!isInTransaction) {
            log(logArea, "DELETE: No transaction started. Please start a transaction.");
            JOptionPane.showMessageDialog(this, "Please start a transaction first.");
            return;
        }
        log(logArea, "DELETE: Attempting to delete data...");
        String sql = "DELETE FROM PhanCong WHERE MaPC = ?";
        try (PreparedStatement pstmt = transactionConnection.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(maPCField.getText()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                log(logArea, "DELETE: Successfully deleted " + affectedRows + " record(s).");
            } else {
                log(logArea, "DELETE: No records deleted with the provided ID.");
            }
        } catch (SQLException ex) {
            handleSQLException(ex, "DELETE", logArea);
        } catch (NumberFormatException ex) {
            log(logArea, "DELETE: Invalid number format - " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Invalid number format: " + ex.getMessage());
        }
    }

    private JPanel createHopDongPanel() {
        // Similar structure to createHoaDonPanel but for HopDong
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        // Add components and functionality
        return panel;
    }

    private JPanel createLichThuGomPanel() {
        // Similar structure to createHoaDonPanel but for LichThuGom
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        // Add components and functionality
        return panel;
    }

    private JPanel createChamCongPanel() {
        // Similar structure to createHoaDonPanel but for ChamCong
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        // Add components and functionality
        return panel;
    }

    private JPanel createPhanAnhPanel() {
        // Similar structure to createHoaDonPanel but for PhanAnh
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        // Add components and functionality
        return panel;
    }

    private JPanel createYeuCauDatLichPanel() {
        // Similar structure to createHoaDonPanel but for YeuCauDatLich
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        // Add components and functionality
        return panel;
    }

    private JPanel createSettingsPanel() {
        // Similar structure to createHoaDonPanel but for Settings
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        // Add components and functionality
        return panel;
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Đổi mật khẩu", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel oldPassLabel = new JLabel("Mật khẩu cũ:");
        JPasswordField oldPassField = new JPasswordField();
        JLabel newPassLabel = new JLabel("Mật khẩu mới:");
        JPasswordField newPassField = new JPasswordField();
        JLabel confirmPassLabel = new JLabel("Xác nhận mật khẩu:");
        JPasswordField confirmPassField = new JPasswordField();

        formPanel.add(oldPassLabel);
        formPanel.add(oldPassField);
        formPanel.add(newPassLabel);
        formPanel.add(newPassField);
        formPanel.add(confirmPassLabel);
        formPanel.add(confirmPassField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        styleButton(saveButton);
        styleButton(cancelButton);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            String oldPass = new String(oldPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng điền đầy đủ thông tin!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(dialog,
                    "Mật khẩu mới không khớp!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "UPDATE NhanVienDieuPhoi SET Password = ? WHERE MaNvdp = ? AND Password = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newPass);
                pstmt.setString(2, maNvdp);
                pstmt.setString(3, oldPass);
                int result = pstmt.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(dialog,
                        "Đổi mật khẩu thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "Mật khẩu cũ không đúng!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Lỗi khi đổi mật khẩu: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private JPanel createThongKePhanCongPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Top panel for Transaction Control
        JPanel topPanel = new JPanel(new BorderLayout());

        // Transaction control panel
        JPanel transactionControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        transactionControlPanel.setBackground(Color.WHITE);

        // Isolation level combo box
        String[] isolationLevels = {
            "READ COMMITTED",
            "SERIALIZABLE"
        };
        JComboBox<String> isolationLevelCombo = new JComboBox<>(isolationLevels);
        transactionControlPanel.add(new JLabel("Isolation Level:"));
        transactionControlPanel.add(isolationLevelCombo);

        // Transaction buttons
        JButton startTransactionBtn = new JButton("Start Transaction");
        JButton commitBtn = new JButton("COMMIT");
        JButton rollbackBtn = new JButton("ROLLBACK");
        styleButton(startTransactionBtn);
        styleButton(commitBtn);
        styleButton(rollbackBtn);
        commitBtn.setEnabled(false);
        rollbackBtn.setEnabled(false);

        transactionControlPanel.add(startTransactionBtn);
        transactionControlPanel.add(commitBtn);
        transactionControlPanel.add(rollbackBtn);

        // Status Label for Transaction
        JLabel transactionStatusLabel = new JLabel("Status: No Transaction");
        transactionControlPanel.add(transactionStatusLabel);

        // Center panel for Table
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Thống kê phân công"));
        centerPanel.setBackground(Color.WHITE);

        // Table
        String[] columns = {
            "Mã NV thu gom",
            "Số tuyến đã nhận"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);

        // Add search panel
        String[] thongKeSearchFields = {
            "MaNvtg",
            "SoTuyenDaNhan"
        };
        JPanel searchPanel = createSearchPanel(thongKeSearchFields, model, "ThongKePhanCong", transactionLogArea);

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Database Operations buttons
        JPanel dbOperationsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dbOperationsPanel.setBackground(Color.WHITE);

        JButton readDataBtn = new JButton("READ Data");
        styleButton(readDataBtn);
        dbOperationsPanel.add(readDataBtn);
        centerPanel.add(dbOperationsPanel, BorderLayout.SOUTH);

        // Transaction Log
        JPanel transactionLogPanel = new JPanel(new BorderLayout());
        transactionLogPanel.setBorder(BorderFactory.createTitledBorder("Transaction Log"));
        transactionLogPanel.setBackground(Color.WHITE);

        JTextArea logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);

        transactionLogPanel.add(logScrollPane, BorderLayout.CENTER);

        // Add components to panel
        topPanel.add(transactionControlPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(transactionLogPanel, BorderLayout.SOUTH);

        // Add action listeners
        startTransactionBtn.addActionListener(e -> {
            try {
                if (!isInTransaction) {
                    transactionConnection = ConnectionJDBC.getConnection();
                    transactionConnection.setAutoCommit(false);
                    String selectedLevel = (String) isolationLevelCombo.getSelectedItem();
                    int isolationLevel;
                    switch (selectedLevel) {
                        case "READ COMMITTED":
                            isolationLevel = Connection.TRANSACTION_READ_COMMITTED;
                            break;
                        case "SERIALIZABLE":
                            isolationLevel = Connection.TRANSACTION_SERIALIZABLE;
                            break;
                        default:
                            isolationLevel = Connection.TRANSACTION_READ_COMMITTED;
                    }
                    transactionConnection.setTransactionIsolation(isolationLevel);
                    isInTransaction = true;
                    startTransactionBtn.setEnabled(false);
                    commitBtn.setEnabled(true);
                    rollbackBtn.setEnabled(true);
                    transactionStatusLabel.setText("Status: Transaction Started (" + selectedLevel + ")");
                    log(logArea, "=== TRANSACTION STARTED ===");
                    log(logArea, "Isolation Level: " + selectedLevel);
                    log(logArea, "Auto-commit: OFF");
                }
            } catch (SQLException ex) {
                log(logArea, "Lỗi khi bắt đầu giao dịch: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Lỗi khi bắt đầu giao dịch: " + ex.getMessage());
            }
        });

        commitBtn.addActionListener(e -> {
            try {
                if (isInTransaction) {
                    transactionConnection.commit();
                    transactionConnection.close();
                    isInTransaction = false;
                    startTransactionBtn.setEnabled(true);
                    commitBtn.setEnabled(false);
                    rollbackBtn.setEnabled(false);
                    transactionStatusLabel.setText("Status: Transaction Committed");
                    log(logArea, "=== TRANSACTION COMMITTED ===");
                    log(logArea, "Auto-commit: ON");
                    JOptionPane.showMessageDialog(this, "Đã commit giao dịch thành công");
                }
            } catch (SQLException ex) {
                log(logArea, "Lỗi khi commit giao dịch: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Lỗi khi commit giao dịch: " + ex.getMessage());
            }
        });

        rollbackBtn.addActionListener(e -> {
            try {
                if (isInTransaction) {
                    transactionConnection.rollback();
                    transactionConnection.close();
                    isInTransaction = false;
                    startTransactionBtn.setEnabled(true);
                    commitBtn.setEnabled(false);
                    rollbackBtn.setEnabled(false);
                    transactionStatusLabel.setText("Status: Transaction Rolled Back");
                    log(logArea, "=== TRANSACTION ROLLED BACK ===");
                    log(logArea, "Auto-commit: ON");
                    JOptionPane.showMessageDialog(this, "Đã rollback giao dịch");
                }
            } catch (SQLException ex) {
                log(logArea, "Lỗi khi rollback giao dịch: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Lỗi khi rollback giao dịch: " + ex.getMessage());
            }
        });

        // Add action listener for read operation
        readDataBtn.addActionListener(e -> readThongKePhanCongData(model, logArea));

        return panel;
    }

    private void readThongKePhanCongData(DefaultTableModel model, JTextArea logArea) {
        if (!isInTransaction) {
            log(logArea, "READ: No transaction started. Please start a transaction.");
            JOptionPane.showMessageDialog(this, "Please start a transaction first.");
            return;
        }
        log(logArea, "READ: Attempting to read data...");
        model.setRowCount(0); // Clear existing data
        String sql = "SELECT * FROM ThongKePhanCong";
        try (Statement stmt = transactionConnection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int rowCount = 0;
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getObject("MaNvtg"));
                row.add(rs.getObject("SoTuyenDaNhan"));
                model.addRow(row);
                rowCount++;
            }
            log(logArea, "READ: Retrieved " + rowCount + " records.");
        } catch (SQLException ex) {
            handleSQLException(ex, "READ", logArea);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create and run the first transaction UI in a new thread
            Thread transaction1Thread = new Thread(() -> {
                XuLyDongThoiUI transaction1 = new XuLyDongThoiUI("", "Transaction 1");
                transaction1.setTitle("Transaction-1");
                transaction1.setVisible(true);
            });
            transaction1Thread.start();

            // Create and run the second transaction UI in a new thread
            Thread transaction2Thread = new Thread(() -> {
                 XuLyDongThoiUI transaction2 = new XuLyDongThoiUI("", "Transaction 2");
                 transaction2.setTitle("Transaction-2");
                 transaction2.setVisible(true);
            });
            transaction2Thread.start();
        });
    }
} 