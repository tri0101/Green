package doan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class DichVuPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private Color primaryColor = new Color(25, 42, 86);    // Xanh đậm
    private Color secondaryColor = new Color(46, 64, 83);  // Xanh nhạt
    private Color accentColor = new Color(46, 204, 113);   // Xanh lá
    private Color textColor = Color.WHITE;
    private Font titleFont = new Font("Arial", Font.BOLD, 24);
    private Font normalFont = new Font("Arial", Font.PLAIN, 14);

    public DichVuPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Quản lý dịch vụ");
        titleLabel.setFont(titleFont);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Thêm");
        JButton deleteButton = new JButton("Xóa");
        JButton editButton = new JButton("Sửa");
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        
        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(editButton);
        styleButton(searchButton);
        styleButton(refreshButton);
        
        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = {
            "Mã dịch vụ",
            "Tên dịch vụ",
            "Đơn vị tính",
            "Đơn giá"
        };
        
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        // Load dữ liệu từ database
        loadData();
        
        // Thêm action listener cho các nút
        addButton.addActionListener(e -> showAddDialog());
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showDeleteDialog(selectedRow);
        });
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showEditDialog(selectedRow);
        });
        searchButton.addActionListener(e -> showSearchDialog());
        refreshButton.addActionListener(e -> loadData());
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void styleButton(JButton button) {
        button.setBackground(accentColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT * FROM DichVu ORDER BY MaDichVu";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("MaDichVu"),
                        rs.getString("TenDichVu"),
                        rs.getString("DonViTinh"),
                        rs.getDouble("DonGia")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu dịch vụ: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm dịch vụ mới", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField tenDichVuField = new JTextField();
        JTextField donViTinhField = new JTextField();
        JTextField donGiaField = new JTextField();

        formPanel.add(new JLabel("Tên dịch vụ:"));
        formPanel.add(tenDichVuField);
        formPanel.add(new JLabel("Đơn vị tính:"));
        formPanel.add(donViTinhField);
        formPanel.add(new JLabel("Đơn giá:"));
        formPanel.add(donGiaField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        
        styleButton(saveButton);
        styleButton(cancelButton);
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                String tenDichVu = tenDichVuField.getText().trim();
                String donViTinh = donViTinhField.getText().trim();
                double donGia = Double.parseDouble(donGiaField.getText().trim());

                if (tenDichVu.isEmpty() || donViTinh.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Vui lòng điền đầy đủ thông tin!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Connection conn = ConnectionJDBC.getConnection();
                String sql = "INSERT INTO DichVu (TenDichVu, DonViTinh, DonGia) VALUES (?, ?, ?)";
                
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, tenDichVu);
                    pstmt.setString(2, donViTinh);
                    pstmt.setDouble(3, donGia);
                    
                    pstmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(dialog,
                        "Thêm dịch vụ thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    loadData();
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Đơn giá phải là số!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Lỗi khi thêm dịch vụ: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showDeleteDialog(int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn dịch vụ cần xóa!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa dịch vụ này?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int maDichVu = (int) model.getValueAt(selectedRow, 0);
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "DELETE FROM DichVu WHERE MaDichVu = ?";
                
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, maDichVu);
                    pstmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(this,
                        "Xóa dịch vụ thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    loadData();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi xóa dịch vụ: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void showEditDialog(int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn dịch vụ cần sửa!");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa dịch vụ", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        int maDichVu = (int) model.getValueAt(selectedRow, 0);
        String tenDichVu = (String) model.getValueAt(selectedRow, 1);
        String donViTinh = (String) model.getValueAt(selectedRow, 2);
        double donGia = (double) model.getValueAt(selectedRow, 3);

        JTextField tenDichVuField = new JTextField(tenDichVu);
        JTextField donViTinhField = new JTextField(donViTinh);
        JTextField donGiaField = new JTextField(String.valueOf(donGia));

        formPanel.add(new JLabel("Tên dịch vụ:"));
        formPanel.add(tenDichVuField);
        formPanel.add(new JLabel("Đơn vị tính:"));
        formPanel.add(donViTinhField);
        formPanel.add(new JLabel("Đơn giá:"));
        formPanel.add(donGiaField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        
        styleButton(saveButton);
        styleButton(cancelButton);
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                String newTenDichVu = tenDichVuField.getText().trim();
                String newDonViTinh = donViTinhField.getText().trim();
                double newDonGia = Double.parseDouble(donGiaField.getText().trim());

                if (newTenDichVu.isEmpty() || newDonViTinh.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Vui lòng điền đầy đủ thông tin!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Connection conn = ConnectionJDBC.getConnection();
                String sql = "UPDATE DichVu SET TenDichVu = ?, DonViTinh = ?, DonGia = ? WHERE MaDichVu = ?";
                
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, newTenDichVu);
                    pstmt.setString(2, newDonViTinh);
                    pstmt.setDouble(3, newDonGia);
                    pstmt.setInt(4, maDichVu);
                    
                    pstmt.executeUpdate();
                    
                    JOptionPane.showMessageDialog(dialog,
                        "Cập nhật dịch vụ thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    loadData();
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Đơn giá phải là số!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Lỗi khi cập nhật dịch vụ: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showSearchDialog() {
    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tìm kiếm dịch vụ", true);
    dialog.setLayout(new BorderLayout(10, 10));
    dialog.setSize(420, 250);
    dialog.setLocationRelativeTo(this);

    JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));
    formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    // Combo box tiêu chí tìm kiếm
    String[] searchOptions = {"Mã dịch vụ", "Tên dịch vụ", "Đơn vị tính", "Đơn giá"};
    JComboBox<String> searchCombo = new JComboBox<>(searchOptions);
    formPanel.add(searchCombo);

    // Panel cho input dạng text
    JPanel textPanel = new JPanel(new BorderLayout());
    JTextField textField = new JTextField();
    textPanel.add(textField, BorderLayout.CENTER);

    // Panel cho khoảng giá
    JPanel pricePanel = new JPanel(new GridLayout(1, 4, 1, 1));
    pricePanel.add(new JLabel("Từ:"));
    JTextField minPriceField = new JTextField();
    pricePanel.add(minPriceField);
    pricePanel.add(new JLabel("Đến:"));
    JTextField maxPriceField = new JTextField();
    pricePanel.add(maxPriceField);

    // CardLayout cho input
    JPanel inputPanel = new JPanel(new CardLayout());
    inputPanel.add(textPanel, "text");
    inputPanel.add(pricePanel, "price");

    formPanel.add(inputPanel);

    // Xử lý chuyển đổi giao diện theo tiêu chí
    searchCombo.addActionListener(e -> {
        String selected = (String) searchCombo.getSelectedItem();
        CardLayout cl = (CardLayout) inputPanel.getLayout();
        if ("Đơn giá".equals(selected)) {
            cl.show(inputPanel, "price");
        } else {
            cl.show(inputPanel, "text");
        }
    });

    // Nút
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton searchButton = new JButton("Tìm kiếm");
    JButton cancelButton = new JButton("Hủy");
    styleButton(searchButton);
    styleButton(cancelButton);
    buttonPanel.add(cancelButton);
    buttonPanel.add(searchButton);

    searchButton.addActionListener(e -> {
        try {
            Connection conn = ConnectionJDBC.getConnection();
            StringBuilder sql = new StringBuilder("SELECT * FROM DichVu WHERE 1=1");
            List<Object> params = new ArrayList<>();

            String selected = (String) searchCombo.getSelectedItem();
            switch (selected) {
                case "Mã dịch vụ":
                    sql.append(" AND MaDichVu = ?");
                    params.add(Integer.parseInt(textField.getText().trim()));
                    break;
                case "Tên dịch vụ":
                    sql.append(" AND TenDichVu LIKE ?");
                    params.add("%" + textField.getText().trim() + "%");
                    break;
                case "Đơn vị tính":
                    sql.append(" AND DonViTinh LIKE ?");
                    params.add("%" + textField.getText().trim() + "%");
                    break;
                case "Đơn giá":
                    String minStr = minPriceField.getText().trim().replace(",", ".").replaceAll("[^\\d.]", "");
                    String maxStr = maxPriceField.getText().trim().replace(",", ".").replaceAll("[^\\d.]", "");

                    double min = minStr.isEmpty() ? 0 : Double.parseDouble(minStr);
                    double max = maxStr.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxStr);

                    if (min > max) {
                        JOptionPane.showMessageDialog(dialog, "Giá trị 'Từ' phải nhỏ hơn hoặc bằng giá trị 'Đến'!");
                        return;
                    }

                    sql.append(" AND DonGia BETWEEN ? AND ?");
                    params.add(min);
                    params.add(max);
                    break;
            }

            sql.append(" ORDER BY MaDichVu");

            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }

                ResultSet rs = pstmt.executeQuery();
                model.setRowCount(0);
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("MaDichVu"),
                        rs.getString("TenDichVu"),
                        rs.getString("DonViTinh"),
                        rs.getDouble("DonGia")
                    });
                }
                dialog.dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog, "Vui lòng nhập số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi khi tìm kiếm dịch vụ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);
}

} 