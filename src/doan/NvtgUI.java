/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package doan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.*;
import java.text.SimpleDateFormat;
import javax.swing.border.TitledBorder;

public class NvtgUI extends JFrame {
    private String maNvtg;
    private String tenNvtg;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public NvtgUI(String maNvtg, String tenNvtg) {
        this.maNvtg = maNvtg;
        this.tenNvtg = tenNvtg;
        setTitle("Nhân viên thu gom");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(50, 202, 50));
        headerPanel.setPreferredSize(new Dimension(1000, 40));

        JLabel titleLabel = new JLabel("Nhân Viên thu gom", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new javax.swing.border.EmptyBorder(0, 20, 0, 0));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        JLabel staffInfoLabel = new JLabel(tenNvtg + " - Mã: " + maNvtg);
        staffInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        staffInfoLabel.setForeground(Color.WHITE);
        staffInfoLabel.setBorder(new javax.swing.border.EmptyBorder(5, 0, 0, 0));
        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new GiaoDienDangNhap().setVisible(true);
            }
        });
        rightPanel.add(staffInfoLabel);
        rightPanel.add(logoutButton);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        // Sidebar menu
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(new Color(25, 42, 86));
        menuPanel.setPreferredSize(new Dimension(200, 0));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(new javax.swing.border.EmptyBorder(20, 5, 20, 10));

        JButton lichButton = createMenuButton("Xem lịch thu gom");
        JButton settingsButton = createMenuButton("Cài đặt");

        menuPanel.add(lichButton);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(settingsButton);
        menuPanel.add(Box.createVerticalGlue());

        // Content panels
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(createLichThuGomPanel(), "Xem lịch thu gom");
        contentPanel.add(createSettingsPanel(), "Cài đặt");

        // Add action for menu buttons
        lichButton.addActionListener(e -> cardLayout.show(contentPanel, "Xem lịch thu gom"));
        settingsButton.addActionListener(e -> cardLayout.show(contentPanel, "Cài đặt"));

        // Layout
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        cardLayout.show(contentPanel, "Xem lịch thu gom");
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(46, 64, 83));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
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

    // Panel xem lịch thu gom (có thể thay bằng bảng dữ liệu thực tế)
    private JPanel createLichThuGomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel("Lịch Thu Gom", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(new javax.swing.border.EmptyBorder(0, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);

        // Table model
        String[] columnNames = {"Mã Lịch", "Tuyến Thu Gom", "Khu Vực", "Thứ", "Giờ Thu", "Trạng Thái"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load data from database
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT l.MaLich, t.TenTuyen, q.TenQuan, l.NgThu, l.GioThu, l.TrangThai " +
                        "FROM LichThuGom l " +
                        "JOIN PhanCong pc ON l.MaLich = pc.MaLich " +
                        "JOIN TuyenDuongThuGom t ON l.MaTuyen = t.MaTuyen " +
                        "JOIN Quan q ON t.KhuVuc = q.MaQuan " +
                        "WHERE pc.MaNvtg = ? " +
                        "ORDER BY l.NgThu ASC, l.GioThu ASC";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, maNvtg);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    String thu = "Thứ " + rs.getString("NgThu");
                    if (rs.getString("NgThu").equals("1")) {
                        thu = "Chủ nhật";
                    }
                    
                    Object[] row = {
                        rs.getString("MaLich"),
                        rs.getString("TenTuyen"),
                        rs.getString("TenQuan"),
                        thu,
                        rs.getString("GioThu"),
                        rs.getString("TrangThai")
                    };
                    model.addRow(row);
                }
            }
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu lịch thu gom: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        // Add filter panel at the top
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        
        JLabel statusLabel = new JLabel("Trạng thái:");
        String[] statuses = {"Tất cả", "Hoạt động", "Tạm dừng"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        
        statusCombo.addActionListener(e -> {
            String selectedStatus = (String)statusCombo.getSelectedItem();
            filterTable(table, selectedStatus);
        });
        
        filterPanel.add(statusLabel);
        filterPanel.add(statusCombo);
        
        // Add refresh button
        JButton refreshBtn = new JButton("Làm mới");
        refreshBtn.addActionListener(e -> refreshTable(model));
        filterPanel.add(refreshBtn);
        
        panel.add(filterPanel, BorderLayout.NORTH);

        return panel;
    }

    private void filterTable(JTable table, String status) {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        
        if (!status.equals("Tất cả")) {
            sorter.setRowFilter(RowFilter.regexFilter(status, 5)); // 5 is the status column
        } else {
            sorter.setRowFilter(null);
        }
    }

    private void refreshTable(DefaultTableModel model) {
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT l.MaLich, t.TenTuyen, q.TenQuan, l.NgThu, l.GioThu, l.TrangThai " +
                        "FROM LichThuGom l " +
                        "JOIN PhanCong pc ON l.MaLich = pc.MaLich " +
                        "JOIN TuyenDuongThuGom t ON l.MaTuyen = t.MaTuyen " +
                        "JOIN Quan q ON t.KhuVuc = q.MaQuan " +
                        "WHERE pc.MaNvtg = ? " +
                        "ORDER BY l.NgThu ASC, l.GioThu ASC";
            
            model.setRowCount(0); // Clear existing data
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, maNvtg);
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    String thu = "Thứ " + rs.getString("NgThu");
                    if (rs.getString("NgThu").equals("1")) {
                        thu = "Chủ nhật";
                    }
                    
                    Object[] row = {
                        rs.getString("MaLich"),
                        rs.getString("TenTuyen"),
                        rs.getString("TenQuan"),
                        thu,
                        rs.getString("GioThu"),
                        rs.getString("TrangThai")
                    };
                    model.addRow(row);
                }
            }
            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Lỗi khi làm mới dữ liệu: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Panel cài đặt (có thể thay bằng form đổi mật khẩu/thông tin cá nhân)
    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Cài đặt", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBorder(new javax.swing.border.EmptyBorder(0, 0, 20, 0));

        // Main content panel with GridBagLayout for better control
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Personal Information Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Thông tin cá nhân",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        // Create text fields for personal information
        JTextField tenNvtgField = new JTextField(20);
        JTextField sdtField = new JTextField(20);
        JComboBox<String> gioiTinhCombo = new JComboBox<>(new String[]{"Nam", "Nữ"});
        JTextField maDvField = new JTextField(20);
        JTextField maTruongNhomField = new JTextField(20);

        // Make some fields uneditable
        maDvField.setEditable(false);
        maTruongNhomField.setEditable(false);

        // Load current information
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT nvtg.*, dv.TenDv, tn.TenNvtg as TenTruongNhom " +
                        "FROM NhanVienThuGom nvtg " +
                        "LEFT JOIN DonViThuGom dv ON nvtg.MaDv = dv.MaDv " +
                        "LEFT JOIN NhanVienThuGom tn ON nvtg.MaTruongNhom = tn.MaNvtg " +
                        "WHERE nvtg.MaNvtg = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, maNvtg);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    tenNvtgField.setText(rs.getString("TenNvtg"));
                    sdtField.setText(rs.getString("Sdt"));
                    gioiTinhCombo.setSelectedItem(rs.getString("GioiTinh"));
                    maDvField.setText(rs.getString("MaDv") + " - " + rs.getString("TenDv"));
                    String truongNhom = rs.getString("TenTruongNhom");
                    maTruongNhomField.setText(truongNhom != null ? 
                        rs.getString("MaTruongNhom") + " - " + truongNhom : "Không có");
                }
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải thông tin nhân viên: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }

        // Add components to info panel
        GridBagConstraints gbcInfo = new GridBagConstraints();
        gbcInfo.insets = new Insets(5, 5, 5, 5);
        gbcInfo.fill = GridBagConstraints.HORIZONTAL;

        // Row 1
        gbcInfo.gridx = 0; gbcInfo.gridy = 0;
        infoPanel.add(new JLabel("Họ tên:"), gbcInfo);
        gbcInfo.gridx = 1;
        infoPanel.add(tenNvtgField, gbcInfo);

        // Row 2
        gbcInfo.gridx = 0; gbcInfo.gridy = 1;
        infoPanel.add(new JLabel("Số điện thoại:"), gbcInfo);
        gbcInfo.gridx = 1;
        infoPanel.add(sdtField, gbcInfo);

        // Row 3
        gbcInfo.gridx = 0; gbcInfo.gridy = 2;
        infoPanel.add(new JLabel("Giới tính:"), gbcInfo);
        gbcInfo.gridx = 1;
        infoPanel.add(gioiTinhCombo, gbcInfo);

        // Row 4
        gbcInfo.gridx = 0; gbcInfo.gridy = 3;
        infoPanel.add(new JLabel("Đơn vị:"), gbcInfo);
        gbcInfo.gridx = 1;
        infoPanel.add(maDvField, gbcInfo);

        // Row 5
        gbcInfo.gridx = 0; gbcInfo.gridy = 4;
        infoPanel.add(new JLabel("Trưởng nhóm:"), gbcInfo);
        gbcInfo.gridx = 1;
        infoPanel.add(maTruongNhomField, gbcInfo);

        // Save button
        JButton saveInfoButton = new JButton("Lưu thay đổi");
        saveInfoButton.setBackground(new Color(40, 167, 69));
        saveInfoButton.setForeground(Color.WHITE);
        saveInfoButton.setFocusPainted(false);
        gbcInfo.gridx = 1;
        gbcInfo.gridy = 5;
        gbcInfo.anchor = GridBagConstraints.EAST;
        infoPanel.add(saveInfoButton, gbcInfo);

        saveInfoButton.addActionListener(e -> {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                String updateSql = "UPDATE NhanVienThuGom SET TenNvtg = ?, Sdt = ?, GioiTinh = ? WHERE MaNvtg = ?";
                
                try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setString(1, tenNvtgField.getText().trim());
                    pstmt.setString(2, sdtField.getText().trim());
                    pstmt.setString(3, (String)gioiTinhCombo.getSelectedItem());
                    pstmt.setString(4, maNvtg);
                    
                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(this,
                            "Cập nhật thông tin thành công!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                }
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi cập nhật thông tin: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Password Change Panel
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Đổi mật khẩu",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        JPasswordField oldPassField = new JPasswordField(20);
        JPasswordField newPassField = new JPasswordField(20);
        JPasswordField confirmPassField = new JPasswordField(20);

        GridBagConstraints gbcPass = new GridBagConstraints();
        gbcPass.insets = new Insets(5, 5, 5, 5);
        gbcPass.fill = GridBagConstraints.HORIZONTAL;

        // Row 1
        gbcPass.gridx = 0; gbcPass.gridy = 0;
        passwordPanel.add(new JLabel("Mật khẩu cũ:"), gbcPass);
        gbcPass.gridx = 1;
        passwordPanel.add(oldPassField, gbcPass);

        // Row 2
        gbcPass.gridx = 0; gbcPass.gridy = 1;
        passwordPanel.add(new JLabel("Mật khẩu mới:"), gbcPass);
        gbcPass.gridx = 1;
        passwordPanel.add(newPassField, gbcPass);

        // Row 3
        gbcPass.gridx = 0; gbcPass.gridy = 2;
        passwordPanel.add(new JLabel("Xác nhận mật khẩu:"), gbcPass);
        gbcPass.gridx = 1;
        passwordPanel.add(confirmPassField, gbcPass);

        // Change password button
        JButton changePassButton = new JButton("Đổi mật khẩu");
        changePassButton.setBackground(new Color(0, 123, 255));
        changePassButton.setForeground(Color.WHITE);
        changePassButton.setFocusPainted(false);
        gbcPass.gridx = 1;
        gbcPass.gridy = 3;
        gbcPass.anchor = GridBagConstraints.EAST;
        passwordPanel.add(changePassButton, gbcPass);

        changePassButton.addActionListener(e -> {
            String oldPass = new String(oldPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng điền đầy đủ thông tin!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this,
                    "Mật khẩu mới không khớp!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = ConnectionJDBC.getConnection();
                // First verify old password
                String checkSql = "SELECT Password FROM NhanVienThuGom WHERE MaNvtg = ? AND Password = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, maNvtg);
                    checkStmt.setString(2, oldPass);
                    ResultSet rs = checkStmt.executeQuery();
                    
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(this,
                            "Mật khẩu cũ không đúng!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                // Update new password
                String updateSql = "UPDATE NhanVienThuGom SET Password = ? WHERE MaNvtg = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, newPass);
                    updateStmt.setString(2, maNvtg);
                    
                    int result = updateStmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(this,
                            "Đổi mật khẩu thành công!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                        oldPassField.setText("");
                        newPassField.setText("");
                        confirmPassField.setText("");
                    }
                }
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi đổi mật khẩu: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add panels to main content panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        contentPanel.add(infoPanel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.5;
        contentPanel.add(passwordPanel, gbc);

        // Add components to main panel
        panel.add(title, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            NvtgUI nvtgUI = new NvtgUI("NV001", "Nguyễn Văn A");
            nvtgUI.setVisible(true);
        });
    }
}
