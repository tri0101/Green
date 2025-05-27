package doan;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.EmptyBorder;
import java.text.SimpleDateFormat;
import java.util.Date;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BoxLayout;
import javax.swing.Box;
import java.awt.Component;
import javax.swing.JSplitPane;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.util.Calendar;
import javax.swing.SpinnerDateModel;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridBagLayout;
import javax.swing.JTextArea;
import java.sql.Clob;
import java.util.HashMap;
import java.util.Map;
import java.sql.Timestamp;
import java.sql.Types;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.sql.Statement;
import java.util.function.Consumer;
import javax.swing.table.TableColumnModel;

public class ChuThaiUI extends JFrame {
    private JPanel sideBar, mainPanel;
    private CardLayout cardLayout;
    private String tenKhachHang;
    private String maKhachHang;
    private JTable invoiceTable;
    private double maxWeight; // Add this as a class field
    private JLabel totalLabel; // Add totalLabel as a class field

    public ChuThaiUI(String tenKhachHang, String maKhachHang) {
        this.tenKhachHang = tenKhachHang;
        this.maKhachHang = maKhachHang;
        // Set maxWeight based on user type
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT LoaiChuThai FROM ChuThai WHERE MaChuThai = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maKhachHang);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String loaiChuThai = rs.getString("LoaiChuThai");
                this.maxWeight = "Cá nhân".equals(loaiChuThai) ? 100.0 : 1000.0; // 100kg for individual, 1000kg for business
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            this.maxWeight = 100.0; // Default to individual limit if error
        }
        initComponents();
    }

    private void initComponents() {
        setTitle("Hệ thống quản lý rác thải - Giao diện chủ thải");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tạo sidebar
        sideBar = new JPanel();
        sideBar.setLayout(new GridLayout(9, 1, 0, 10));
        sideBar.setPreferredSize(new Dimension(200, 0));
        sideBar.setBackground(new Color(25, 42, 86));
        sideBar.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Panel thông tin người dùng
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new GridLayout(2, 1));
        userInfoPanel.setBackground(new Color(25, 42, 86));
        JLabel nameLabel = new JLabel("Xin chào, " + tenKhachHang);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel idLabel = new JLabel("Mã KH: " + maKhachHang);
        idLabel.setForeground(Color.WHITE);
        idLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        userInfoPanel.add(nameLabel);
        userInfoPanel.add(idLabel);
        sideBar.add(userInfoPanel);

        // Các nút menu
        String[] menuItems = {
            "Đặt lịch thu gom",
            "Lịch sử đặt lịch",
            "Gửi phản ánh",
            "Xem hóa đơn",
            "Danh sách hợp đồng",
            "Hướng dẫn phân loại",
            "Cài đặt",
            "Đăng xuất"
        };

        for (String item : menuItems) {
            JButton button = createMenuButton(item);
            sideBar.add(button);
        }

        // Main content
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // Thêm các panel chức năng
        mainPanel.add(createDatLichPanel(), "Đặt lịch thu gom");
        mainPanel.add(createLichSuPanel(), "Lịch sử đặt lịch");
        mainPanel.add(createPhanAnhPanel(), "Gửi phản ánh");
        mainPanel.add(createHoaDonPanel(), "Xem hóa đơn");
        mainPanel.add(createHopDongPanel(), "Danh sách hợp đồng");
        mainPanel.add(createHuongDanPanel(), "Hướng dẫn phân loại");
        mainPanel.add(createSettingsPanel(), "Cài đặt");
        mainPanel.add(createDangXuatPanel(), "Đăng xuất");

        add(sideBar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(46, 64, 83));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.addActionListener(e -> cardLayout.show(mainPanel, text));
        return button;
    }

    private JPanel createDatLichPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel titleLabel = new JLabel("Đặt lịch thu gom rác", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form đặt lịch
        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // Chọn quận
        formPanel.add(new JLabel("Chọn quận:"));
        JComboBox<String> districtBox = new JComboBox<>();
        districtBox.addItem("-- Chọn quận --");
        // Load danh sách quận
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT TenQuan FROM Quan ORDER BY TenQuan";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                districtBox.addItem(rs.getString("TenQuan"));
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải danh sách quận: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
        formPanel.add(districtBox);

        // Chọn tuyến đường
        formPanel.add(new JLabel("Chọn tuyến đường:"));
        JComboBox<String> routeBox = new JComboBox<>();
        routeBox.addItem("-- Chọn tuyến đường --");
        routeBox.setEnabled(false);
        formPanel.add(routeBox);

        // Tạo các trường nhập liệu
        JSpinner dateSpinner = createDateSpinner();
        JComboBox<String> timeBox = new JComboBox<>(new String[]{
            "7:00", "7:30", "8:00", "8:30", "9:00", "9:30", "10:00", "10:30",
            "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
            "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00", "18:30",
            "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00", "22:30", "23:00"
        });
        JComboBox<String> serviceBox = new JComboBox<>();
        JTextField weightField = new JTextField("0,0");
        totalLabel = new JLabel("0 đ");
        JTextField noteField = new JTextField();
        JTextField addressDetailField = new JTextField();
        SpinnerNumberModel durationModel = new SpinnerNumberModel(1, 1, 60, 1);
        JSpinner durationSpinner = new JSpinner(durationModel);
        JComboBox<String> timeUnitBox = new JComboBox<>(new String[]{"Tháng", "Năm"});
        JLabel endDateLabel = new JLabel("");

        // Disable tất cả các trường nhập liệu ban đầu
        dateSpinner.setEnabled(false);
        timeBox.setEnabled(false);
        serviceBox.setEnabled(false);
        weightField.setEnabled(false);
        noteField.setEnabled(false);
        addressDetailField.setEnabled(false);
        durationSpinner.setEnabled(false);
        timeUnitBox.setEnabled(false);

        // Load dịch vụ
        Map<String, Double> servicePrices = new HashMap<>();
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MaDichVu, TenDichVu, DonViTinh, DonGia FROM DICHVU";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String serviceName = rs.getString("TenDichVu");
                String unit = rs.getString("DonViTinh");
                double price = rs.getDouble("DonGia");
                servicePrices.put(serviceName, price);
                serviceBox.addItem(serviceName + " - " + String.format("%,d", (int) price) + "đ/" + unit);
            }
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải danh sách dịch vụ: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }

        // Thêm listener cho combobox quận
        districtBox.addActionListener(e -> {
            String selectedDistrict = (String) districtBox.getSelectedItem();
            routeBox.removeAllItems();
            routeBox.addItem("-- Chọn tuyến đường --");
            
            if (selectedDistrict != null && !selectedDistrict.equals("-- Chọn quận --")) {
                try {
                    Connection conn = ConnectionJDBC.getConnection();
                    String sql = "SELECT t.TenTuyen FROM TuyenDuongThuGom t " +
                               "JOIN Quan q ON t.KhuVuc = q.MaQuan " +
                               "WHERE q.TenQuan = ? " +
                               "ORDER BY t.TenTuyen";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, selectedDistrict);
                    ResultSet rs = pstmt.executeQuery();
                    boolean hasRoutes = false;
                    while (rs.next()) {
                        routeBox.addItem(rs.getString("TenTuyen"));
                        hasRoutes = true;
                    }
                    routeBox.setEnabled(hasRoutes);
                    rs.close();
                    pstmt.close();
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Lỗi khi tải danh sách tuyến đường: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                routeBox.setEnabled(false);
                // Disable tất cả các trường nhập liệu
                dateSpinner.setEnabled(false);
                timeBox.setEnabled(false);
                serviceBox.setEnabled(false);
                weightField.setEnabled(false);
                noteField.setEnabled(false);
                durationSpinner.setEnabled(false);
                timeUnitBox.setEnabled(false);
            }
        });

        // Thêm listener cho combobox tuyến đường
        routeBox.addActionListener(e -> {
            String selectedRoute = (String) routeBox.getSelectedItem();
            boolean enableFields = selectedRoute != null && !selectedRoute.equals("-- Chọn tuyến đường --");
            
            // Enable/disable các trường nhập liệu
            dateSpinner.setEnabled(enableFields);
            timeBox.setEnabled(enableFields);
            serviceBox.setEnabled(enableFields);
            weightField.setEnabled(enableFields);
            noteField.setEnabled(enableFields);
            durationSpinner.setEnabled(enableFields);
            timeUnitBox.setEnabled(enableFields);
            addressDetailField.setEnabled(enableFields);
            
            if (!enableFields) {
                // Reset các giá trị
                weightField.setText("0,0");
                noteField.setText("");
                addressDetailField.setText("");
                totalLabel.setText("0 đ");
            }
        });

        // Thêm các trường vào form
        formPanel.add(new JLabel("Ngày bắt đầu thu gom:"));
        formPanel.add(dateSpinner);

        formPanel.add(new JLabel("Giờ thu gom:"));
        formPanel.add(timeBox);

        formPanel.add(new JLabel("Địa điểm chi tiết:"));
        formPanel.add(addressDetailField);

        formPanel.add(new JLabel("Chọn dịch vụ:"));
        formPanel.add(serviceBox);

        formPanel.add(new JLabel("Khối lượng (kg):"));
        formPanel.add(weightField);

        formPanel.add(new JLabel("Thành tiền:"));
        formPanel.add(totalLabel);

        formPanel.add(new JLabel("Ghi chú:"));
        formPanel.add(noteField);

        formPanel.add(new JLabel("Thời hạn:"));
        // Tạo panel chứa các components thời hạn với BorderLayout
        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
        durationSpinner.setPreferredSize(new Dimension(60, 25));
        durationPanel.add(durationSpinner);
        durationPanel.add(timeUnitBox);
        durationPanel.add(endDateLabel);
        durationPanel.setOpaque(false);
        formPanel.add(durationPanel);

        // Thêm xử lý tính tiền khi thay đổi khối lượng hoặc dịch vụ
        Runnable updateTotal = () -> {
            try {
                String selectedService = (String) serviceBox.getSelectedItem();
                if (selectedService != null) {
                    String weightText = weightField.getText().trim().replace(',', '.');
                    if (!weightText.isEmpty()) {
                        double weight = Double.parseDouble(weightText);
                        String serviceName = selectedService.split(" - ")[0];
                        double pricePerUnit = servicePrices.get(serviceName);
                        double total = weight * pricePerUnit;
                        totalLabel.setText(String.format("%,.0f đ", total));
                    }
                }
            } catch (NumberFormatException ex) {
                // Không làm gì khi có lỗi parse số
            }
        };

        // Thêm các listeners cho việc tính tiền
        weightField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = weightField.getText();
                if (!((c >= '0' && c <= '9') || c == ',' || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                    return;
                }
                if (c == ',' && currentText.contains(",")) {
                    e.consume();
                    return;
                }
            }
            
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                try {
                    String weightText = weightField.getText().trim().replace(',', '.');
                    if (!weightText.isEmpty()) {
                        double weight = Double.parseDouble(weightText);
                        String selectedService = (String) serviceBox.getSelectedItem();
                        if (selectedService != null) {
                            String serviceName = selectedService.split(" - ")[0];
                            double pricePerUnit = servicePrices.get(serviceName);
                            double total = weight * pricePerUnit;
                            totalLabel.setText(String.format("%,.0f đ", total));
                        }
                    } else {
                        totalLabel.setText("0 đ");
                    }
                } catch (NumberFormatException ex) {
                    // Không làm gì khi có lỗi parse số
                }
            }
        });

        weightField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                String text = weightField.getText().trim();
                if (text.isEmpty()) {
                    weightField.setText("0,0");
                    totalLabel.setText("0 đ");
                    return;
                }
                if (text.startsWith(",")) {
                    text = "0" + text;
                }
                if (text.endsWith(",")) {
                    text = text + "0";
                }
                if (!text.contains(",")) {
                    text = text + ",0";
                }
                if (text.contains(",")) {
                    String[] parts = text.split(",");
                    if (parts.length > 1 && parts[1].length() > 1) {
                        text = parts[0] + "," + parts[1].substring(0, 1);
                    }
                }
                weightField.setText(text);
                
                // Cập nhật thành tiền khi format lại text
                try {
                    String weightText = text.replace(',', '.');
                    double weight = Double.parseDouble(weightText);
                    String selectedService = (String) serviceBox.getSelectedItem();
                    if (selectedService != null) {
                        String serviceName = selectedService.split(" - ")[0];
                        double pricePerUnit = servicePrices.get(serviceName);
                        double total = weight * pricePerUnit;
                        totalLabel.setText(String.format("%,.0f đ", total));
                    }
                } catch (NumberFormatException ex) {
                    totalLabel.setText("0 đ");
                }
            }
        });

        serviceBox.addActionListener(e -> updateTotal.run());

        // Xử lý tính ngày kết thúc
        Runnable updateEndDate = () -> {
            try {
                Date startDate = (Date) dateSpinner.getValue();
                int duration = (Integer) durationSpinner.getValue();
                String unit = (String) timeUnitBox.getSelectedItem();
                
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                
                if ("Năm".equals(unit)) {
                    cal.add(Calendar.YEAR, duration);
                } else {
                    cal.add(Calendar.MONTH, duration);
                }
                // Trừ đi 1 ngày
                cal.add(Calendar.DAY_OF_MONTH, -1);
                
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                endDateLabel.setText("(Đến ngày: " + sdf.format(cal.getTime()) + ")");
                endDateLabel.setForeground(new Color(128, 128, 128));
            } catch (Exception ex) {
                endDateLabel.setText("");
            }
        };

        // Thêm listeners cho việc tính ngày kếtthúc
        durationSpinner.addChangeListener(e -> updateEndDate.run());
        timeUnitBox.addActionListener(e -> {
            String selectedUnit = (String) timeUnitBox.getSelectedItem();
            if ("Năm".equals(selectedUnit)) {
                durationModel.setMaximum(5);
                if ((Integer)durationSpinner.getValue() > 5) {
                    durationSpinner.setValue(5);
                }
            } else {
                durationModel.setMaximum(60);
            }
            updateEndDate.run();
        });
        dateSpinner.addChangeListener(e -> updateEndDate.run());

        // Panel cho nút submit
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("Xác nhận");  // Add this line
        submitButton.setBackground(new Color(46, 204, 113));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> {
            // Validate các trường bắt buộc
            String selectedDistrict = (String) districtBox.getSelectedItem();
            String selectedRoute = (String) routeBox.getSelectedItem();
            String addressDetail = addressDetailField.getText().trim();
            
            if (selectedDistrict == null || selectedDistrict.equals("-- Chọn quận --")) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn quận!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedRoute == null || selectedRoute.equals("-- Chọn tuyến đường --")) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn tuyến đường!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (addressDetail.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập địa điểm chi tiết!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedService = (String) serviceBox.getSelectedItem();
            if (selectedService == null) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn dịch vụ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Validate khối lượng
                String weightText = weightField.getText().trim().replace(',', '.');
                if (weightText.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập khối lượng!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double weight = Double.parseDouble(weightText);
                if (weight < 1) {
                    JOptionPane.showMessageDialog(this,
                        "Khối lượng phải lớn hơn 1 kg!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Kiểm tra giới hạn khối lượng dựa trên loại chủ thải
                if (weight > maxWeight) {
                    JOptionPane.showMessageDialog(this,
                        "Khối lượng vượt quá giới hạn cho phép (" + maxWeight + " kg)!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Tính ngày kết thúc
                Date startDate = (Date) dateSpinner.getValue();
                int duration = (Integer) durationSpinner.getValue();
                String unit = (String) timeUnitBox.getSelectedItem();
                
                Calendar cal = Calendar.getInstance();
                cal.setTime(startDate);
                
                if ("Năm".equals(unit)) {
                    cal.add(Calendar.YEAR, duration);
                } else {
                    cal.add(Calendar.MONTH, duration);
                }
                cal.add(Calendar.DAY_OF_MONTH, -1);
                Date endDate = cal.getTime();

                // Tạo địa chỉ đầy đủ từ tuyến đường, quận và địa điểm chi tiết
                String fullAddress = String.format("%s - %s - %s", addressDetail, selectedRoute, selectedDistrict);

                // Gọi hàm submit với các thông tin đã validate
                submitBooking(
                    startDate,
                    (String) timeBox.getSelectedItem(),
                    selectedService,
                    fullAddress,
                    weight,
                    noteField.getText().trim(),
                    totalLabel.getText(),
                    endDate
                );
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Khối lượng không hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(submitButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private void submitBooking(Date startDate, String time, String service, 
                             String address, double weight, String note, 
                             String total, Date endDate) {
        // Kiểm tra ngày đặt lịch phải sau ngày hiện tại ít nhất 1 ngày
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_MONTH, 1);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar bookingDate = Calendar.getInstance();
        bookingDate.setTime(startDate);
        bookingDate.set(Calendar.HOUR_OF_DAY, 0);
        bookingDate.set(Calendar.MINUTE, 0);
        bookingDate.set(Calendar.SECOND, 0);
        bookingDate.set(Calendar.MILLISECOND, 0);

        if (bookingDate.before(today)) {
            JOptionPane.showMessageDialog(this, 
                "Ngày đặt lịch phải sau ngày hiện tại ít nhất 1 ngày!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = null;
        try {
            conn = ConnectionJDBC.getConnection();
            conn.setAutoCommit(false);

            // Thêm yêu cầu đặt lịch
            String insertYeuCauSql = "INSERT INTO YeuCauDatLich (MaChuThai, MaLich, ThoiGianYc, GhiChu, TrangThai) " +
                                   "VALUES (?, NULL, ?, ?, 'Đang xử lý')";
            
            try (PreparedStatement pstmt = conn.prepareStatement(insertYeuCauSql)) {
                pstmt.setString(1, maKhachHang);
                pstmt.setDate(2, new java.sql.Date(new Date().getTime())); // Thời gian hiện tại
                pstmt.setString(3, note);
                
                int result = pstmt.executeUpdate();
                if (result <= 0) {
                    throw new SQLException("Không thể tạo yêu cầu đặt lịch");
                }
            }

            // 1. Create HopDong record
            String insertHopDongSql = "INSERT INTO HopDong (MaChuThai, LoaiHopDong, NgBatDau, NgKetThuc, DiaChiThuGom, TrangThai) " +
                                    "VALUES (?, ?, ?, ?, ?, 'Chờ duyệt')";
            
            int maHopDongValue;
            try (PreparedStatement pstmt = conn.prepareStatement(insertHopDongSql, new String[]{"MaHopDong"})) {
                pstmt.setString(1, maKhachHang);
                long durationInDays = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
                String contractType = durationInDays > 180 ? "Dài hạn" : "Ngắn hạn";
                pstmt.setString(2, contractType);
                pstmt.setDate(3, new java.sql.Date(startDate.getTime()));
                pstmt.setDate(4, new java.sql.Date(endDate.getTime()));
                pstmt.setString(5, address);
                
                int result = pstmt.executeUpdate();
                if (result <= 0) {
                    throw new SQLException("Không thể tạo hợp đồng");
                }
                
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    maHopDongValue = rs.getInt(1);
                } else {
                    throw new SQLException("Không thể lấy mã hợp đồng");
                }
            }

            // 2. Get MaDichVu
            String findServiceSql = "SELECT MaDichVu FROM DichVu WHERE TenDichVu = ?";
            int maDichVu;
            try (PreparedStatement pstmt = conn.prepareStatement(findServiceSql)) {
                String serviceName = service.split(" - ")[0];
                pstmt.setString(1, serviceName);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    maDichVu = rs.getInt("MaDichVu");
                } else {
                    throw new SQLException("Không tìm thấy dịch vụ");
                }
            }

            // 3. Create ChiTietHopDong record
            String insertChiTietSql = "INSERT INTO ChiTietHopDong (MaHopDong, MaDichVu, KhoiLuong, ThanhTien, GhiChu) " +
                                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertChiTietSql)) {
                String totalAmount = total.replaceAll("[^0-9]", "");
                double totalValue = Double.parseDouble(totalAmount);
                
                pstmt.setInt(1, maHopDongValue);
                pstmt.setInt(2, maDichVu);
                pstmt.setDouble(3, weight);
                pstmt.setDouble(4, totalValue);
                pstmt.setString(5, note);
                
                int result = pstmt.executeUpdate();
                if (result <= 0) {
                    throw new SQLException("Không thể thêm chi tiết hợp đồng");
                }
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, 
                "Đăng ký hợp đồng thành công!", 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
            clearBookingForm();

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tạo hợp đồng: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void clearBookingForm() {
        // Reset all form fields to default values
        // This method will be called after successful booking
        // Implementation will depend on how you want to handle form reset
    }

    private JPanel createLichSuPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Lịch sử đặt lịch thu gom");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton deleteButton = new JButton("Xóa");
        
        // Style buttons
        searchButton.setBackground(new Color(46, 64, 83));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        
        refreshButton.setBackground(new Color(46, 64, 83));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        deleteButton.setBackground(new Color(231, 76, 60)); // Màu đỏ cho nút xóa
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setEnabled(false); // Ban đầu disable nút xóa
        
        buttonPanel.add(searchButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Trạng thái:");
        String[] statuses = {"Tất cả", "Đang xử lí", "Đã duyệt", "Từ chối"};
        JComboBox<String> statusBox = new JComboBox<>(statuses);
        filterPanel.add(statusLabel);
        filterPanel.add(statusBox);

        // Combine header and filter
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        // Table with columns matching the database
        String[] columns = {
            "Mã yêu cầu",
            "Mã lịch",
            "Thời gian yêu cầu",
            "Ghi chú",
            "Trạng thái"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load initial data
        loadHistoryData(model, "Tất cả");

        // Add selection listener to enable/disable delete button
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String status = table.getValueAt(selectedRow, 4).toString();
                    deleteButton.setEnabled("Đang xử lí".equals(status));
                } else {
                    deleteButton.setEnabled(false);
                }
            }
        });

        // Add action listeners
        statusBox.addActionListener(e -> {
            String selectedStatus = (String) statusBox.getSelectedItem();
            loadHistoryData(model, selectedStatus);
        });

        searchButton.addActionListener(e -> showSearchHistoryDialog(model));
        
        refreshButton.addActionListener(e -> loadHistoryData(model, (String) statusBox.getSelectedItem()));

        // Add delete button action listener
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String maYc = table.getValueAt(selectedRow, 0).toString();
                String status = table.getValueAt(selectedRow, 4).toString();
                
                if ("Đang xử lí".equals(status)) {
                    int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc chắn muốn xóa yêu cầu đặt lịch này không?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteBookingRequest(maYc, model, (String) statusBox.getSelectedItem());
                    }
                }
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Thêm phương thức xóa yêu cầu đặt lịch
    private void deleteBookingRequest(String maYc, DefaultTableModel model, String currentFilter) {
        try {
            Connection conn = ConnectionJDBC.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            
            try {
                String sql = "DELETE FROM YeuCauDatLich WHERE MaYc = ? AND TrangThai = 'Đang xử lí'";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, maYc);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    conn.commit();
                    JOptionPane.showMessageDialog(this,
                        "Xóa yêu cầu đặt lịch thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh table data
                    loadHistoryData(model, currentFilter);
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this,
                        "Không thể xóa yêu cầu đặt lịch!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
                
                pstmt.close();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi xóa yêu cầu đặt lịch: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadHistoryData(DefaultTableModel model, String statusFilter) {
        model.setRowCount(0); // Xóa dữ liệu cũ

        try {
            Connection conn = ConnectionJDBC.getConnection();
            StringBuilder sql = new StringBuilder(
                "SELECT yc.MaYc, yc.MaLich, yc.ThoiGianYc, yc.GhiChu, yc.TrangThai " +
                "FROM YeuCauDatLich yc " +
                "WHERE yc.MaChuThai = ? "
            );

            if (!statusFilter.equals("Tất cả")) {
                sql.append("AND yc.TrangThai = ? ");
            }
            sql.append("ORDER BY yc.ThoiGianYc DESC");

            System.out.println("SQL Query: " + sql.toString());
            System.out.println("MaChuThai: " + maKhachHang);
            System.out.println("Status Filter: " + statusFilter);

            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, maKhachHang);
            if (!statusFilter.equals("Tất cả")) {
                pstmt.setString(2, statusFilter);
            }

            ResultSet rs = pstmt.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                String maYc = rs.getString("MaYc");
                String maLich = rs.getString("MaLich");
                Timestamp thoiGianYc = rs.getTimestamp("ThoiGianYc");
                String ghiChu = rs.getString("GhiChu");
                String trangThai = rs.getString("TrangThai");

                System.out.println("Loading row " + rowCount + ": MaYc=" + maYc + 
                    ", MaLich=" + maLich + ", ThoiGianYc=" + thoiGianYc + 
                    ", TrangThai=" + trangThai);

                model.addRow(new Object[]{
                    maYc,
                    maLich,
                    dateFormat.format(thoiGianYc),
                    ghiChu,
                    trangThai
                });
            }

            System.out.println("Total rows loaded: " + rowCount);

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("Error loading data: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải danh sách yêu cầu: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSearchHistoryDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm lịch sử đặt lịch", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        // Main panel with GridBagLayout for better control
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Mã yêu cầu
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Mã yêu cầu:"), gbc);

        JTextField maYcField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        mainPanel.add(maYcField, gbc);

        // Ngày yêu cầu
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Ngày yêu cầu:"), gbc);

        JSpinner dateSpinner = createSearchDateSpinner();
        JPanel datePanel = (JPanel)dateSpinner.getClientProperty("parent.panel");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        mainPanel.add(datePanel, gbc);

        // Checkbox for date search
        JCheckBox dateCheckBox = new JCheckBox("Tìm theo ngày", false);
        dateCheckBox.addActionListener(e -> {
            dateSpinner.setEnabled(dateCheckBox.isSelected());
            datePanel.getComponent(1).setEnabled(dateCheckBox.isSelected());
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(dateCheckBox, gbc);

        // Initially disable date spinner
        dateSpinner.setEnabled(false);
        datePanel.getComponent(1).setEnabled(false);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchBtn = new JButton("Tìm kiếm");
        JButton cancelBtn = new JButton("Hủy");

        // Style buttons
        searchBtn.setBackground(new Color(46, 64, 83));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);

        cancelBtn.setBackground(new Color(46, 64, 83));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);

        searchBtn.addActionListener(evt -> {
            try {
                String maYc = maYcField.getText().trim();
                Date ngayYc = dateCheckBox.isSelected() ? 
                    (Date) ((SpinnerDateModel)dateSpinner.getModel()).getValue() : null;

                // Validate input - either ID or date must be provided
                if (maYc.isEmpty() && !dateCheckBox.isSelected()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập mã yêu cầu hoặc chọn ngày để tìm kiếm.",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Tạo câu truy vấn SQL
                StringBuilder sql = new StringBuilder(
                    "SELECT MaYc, MaLich, ThoiGianYc, GhiChu, TrangThai " +
                    "FROM YeuCauDatLich " +
                    "WHERE MaChuThai = ? ");

                if (!maYc.isEmpty()) {
                    sql.append("AND MaYc = ? ");
                }
                // Add date condition if date is selected
                if (ngayYc != null) {
                    sql.append("AND TRUNC(ThoiGianYc) = TRUNC(?) ");
                }

                sql.append("ORDER BY ThoiGianYc DESC");

                model.setRowCount(0);
                Connection conn = ConnectionJDBC.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString());
                
                int paramIndex = 1;
                pstmt.setString(paramIndex++, maKhachHang);
                if (!maYc.isEmpty()) {
                    pstmt.setString(paramIndex++, maYc);
                }
                if (ngayYc != null) {
                    pstmt.setTimestamp(paramIndex++, new Timestamp(ngayYc.getTime()));
                }

                ResultSet rs = pstmt.executeQuery();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("MaYc"),
                        rs.getString("MaLich"),
                        dateFormat.format(rs.getTimestamp("ThoiGianYc")),
                        rs.getString("GhiChu"),
                        rs.getString("TrangThai")
                    });
                }

                rs.close();
                pstmt.close();
                conn.close();

                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(dialog,
                        "Không tìm thấy kết quả nào.",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                dialog.dispose();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Lỗi khi tìm kiếm: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(evt -> dialog.dispose());

        buttonPanel.add(searchBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createPhanAnhPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Phản ánh dịch vụ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton deleteButton = new JButton("Xóa");
        
        // Style buttons
        searchButton.setBackground(new Color(46, 64, 83));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        
        refreshButton.setBackground(new Color(46, 64, 83));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setEnabled(false); // Ban đầu disable nút xóa
        
        buttonPanel.add(searchButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(deleteButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Trạng thái:");
        String[] statuses = {"Tất cả", "Đang xử lý", "Đã xử lý"};
        JComboBox<String> statusBox = new JComboBox<>(statuses);
        filterPanel.add(statusLabel);
        filterPanel.add(statusBox);

        // Combine header and filter
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        // Feedback table
        String[] columns = {
            "Mã phản ánh",
            "Nội dung",
            "Thời gian gửi",
            "Trạng thái"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        
        // Set custom renderer for content column to support word wrap
        table.getColumnModel().getColumn(1).setCellRenderer(new MultiLineTableCellRenderer());
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);  // Mã phản ánh
        table.getColumnModel().getColumn(1).setPreferredWidth(400);  // Nội dung
        table.getColumnModel().getColumn(2).setPreferredWidth(150);  // Thời gian gửi
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Trạng thái

        JScrollPane scrollPane = new JScrollPane(table);

        // Add selection listener to enable/disable delete button
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    String status = table.getValueAt(selectedRow, 3).toString();
                    deleteButton.setEnabled("Đang xử lý".equals(status));
                } else {
                    deleteButton.setEnabled(false);
                }
            }
        });

        // Add feedback form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thêm phản ánh mới"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextArea contentArea = new JTextArea(5, 40);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(contentArea);
        
        formPanel.add(new JLabel("Nội dung:"), gbc);
        gbc.gridx = 1;
        formPanel.add(contentScroll, gbc);

        JButton submitButton = new JButton("Gửi phản ánh");
        submitButton.setBackground(new Color(46, 204, 113));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(submitButton, gbc);

        // Load initial data
        loadFeedbackHistory(model);

        // Add action listeners
        submitButton.addActionListener(e -> {
            String content = contentArea.getText().trim();
            if (!content.isEmpty()) {
                submitFeedback(content);
                contentArea.setText("");
                loadFeedbackHistory(model);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập nội dung phản ánh!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        searchButton.addActionListener(e -> showSearchFeedbackDialog(model));
        refreshButton.addActionListener(e -> loadFeedbackHistory(model));
        statusBox.addActionListener(e -> {
            String status = statusBox.getSelectedItem().toString();
            loadFeedbackHistory(model, status);
        });

        // Add delete button action listener
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String maPa = table.getValueAt(selectedRow, 0).toString();
                String status = table.getValueAt(selectedRow, 3).toString();
                
                if ("Đang xử lý".equals(status)) {
                    int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc chắn muốn xóa phản ánh này không?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteFeedback(maPa, model, statusBox.getSelectedItem().toString());
                    }
                }
            }
        });

        // Layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(scrollPane);
        splitPane.setBottomComponent(formPanel);
        splitPane.setResizeWeight(0.7);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private void deleteFeedback(String maPa, DefaultTableModel model, String currentFilter) {
        try {
            Connection conn = ConnectionJDBC.getConnection();
            conn.setAutoCommit(false);
            
            try {
                String sql = "DELETE FROM PhanAnh WHERE MaPA = ? AND TrangThai = 'Đang xử lý'";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, maPa);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    conn.commit();
                    JOptionPane.showMessageDialog(this,
                        "Xóa phản ánh thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh table data
                    loadFeedbackHistory(model, currentFilter);
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this,
                        "Không thể xóa phản ánh!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
                
                pstmt.close();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi xóa phản ánh: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void submitFeedback(String content) {
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "INSERT INTO PhanAnh (MaChuThai, NoiDung, ThoiGianGui, TrangThai) VALUES (?, ?, SYSDATE, 'Đang xử lý')";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maKhachHang);
            pstmt.setString(2, content);
            pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            JOptionPane.showMessageDialog(this,
                "Gửi phản ánh thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi gửi phản ánh: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFeedbackHistory(DefaultTableModel model) {
        loadFeedbackHistory(model, "Tất cả");
    }

    private void loadFeedbackHistory(DefaultTableModel model, String status) {
        model.setRowCount(0);

        try {
            Connection conn = ConnectionJDBC.getConnection();
            StringBuilder sql = new StringBuilder(
                "SELECT MaPA, NoiDung, ThoiGianGui, TrangThai " +
                "FROM PhanAnh " +
                "WHERE MaChuThai = ? "
            );

            if (!status.equals("Tất cả")) {
                sql.append("AND TrangThai = ? ");
            }
            sql.append("ORDER BY ThoiGianGui DESC");

            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, maKhachHang);
            if (!status.equals("Tất cả")) {
                pstmt.setString(2, status);
            }

            ResultSet rs = pstmt.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("MaPA"),
                    rs.getString("NoiDung"),
                    dateFormat.format(rs.getTimestamp("ThoiGianGui")),
                    rs.getString("TrangThai")
                });
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải lịch sử phản ánh: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom renderer for multi-line table cells
    private class MultiLineTableCellRenderer extends JTextArea implements TableCellRenderer {
        public MultiLineTableCellRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setSize(table.getColumnModel().getColumn(column).getWidth(),
                    getPreferredSize().height);
            if (table.getRowHeight(row) != getPreferredSize().height) {
                table.setRowHeight(row, getPreferredSize().height);
            }
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            return this;
        }
    }

    private void showSearchFeedbackDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm phản ánh", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        // Main panel with GridBagLayout for better control
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Mã phản ánh
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Mã phản ánh:"), gbc);

        JTextField maPaField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        mainPanel.add(maPaField, gbc);

        // Ngày gửi
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Ngày gửi:"), gbc);

        JSpinner dateSpinner = createSearchDateSpinner();
        JPanel datePanel = (JPanel)dateSpinner.getClientProperty("parent.panel");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        mainPanel.add(datePanel, gbc);

        // Checkbox for date search
        JCheckBox dateCheckBox = new JCheckBox("Tìm theo ngày", false);
        dateCheckBox.addActionListener(e -> {
            dateSpinner.setEnabled(dateCheckBox.isSelected());
            datePanel.getComponent(1).setEnabled(dateCheckBox.isSelected());
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(dateCheckBox, gbc);

        // Initially disable date spinner
        dateSpinner.setEnabled(false);
        datePanel.getComponent(1).setEnabled(false);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchBtn = new JButton("Tìm kiếm");
        JButton cancelBtn = new JButton("Hủy");

        // Style buttons
        searchBtn.setBackground(new Color(46, 64, 83));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);

        cancelBtn.setBackground(new Color(46, 64, 83));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);

        searchBtn.addActionListener(evt -> {
            try {
                String maPa = maPaField.getText().trim();
                Date ngayGui = dateCheckBox.isSelected() ? 
                    (Date) ((SpinnerDateModel)dateSpinner.getModel()).getValue() : null;

                // Validate input - either ID or date must be provided
                if (maPa.isEmpty() && !dateCheckBox.isSelected()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập mã phản ánh hoặc chọn ngày để tìm kiếm.",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                StringBuilder sql = new StringBuilder(
                    "SELECT MaPA, NoiDung, ThoiGianGui, TrangThai " +
                    "FROM PhanAnh WHERE MaChuThai = ? ");

                List<Object> params = new ArrayList<>();
                List<Integer> types = new ArrayList<>();
                params.add(maKhachHang);
                types.add(Types.VARCHAR);

                // Only add ID condition if ID is provided
                if (!maPa.isEmpty()) {
                    sql.append("AND MaPA = ? ");
                    params.add(maPa);
                    types.add(Types.VARCHAR);
                }
                // Add date condition if date is selected
                if (ngayGui != null) {
                    sql.append("AND TRUNC(ThoiGianGui) = TRUNC(?) ");
                    params.add(new Timestamp(ngayGui.getTime()));
                    types.add(Types.TIMESTAMP);
                }

                sql.append("ORDER BY ThoiGianGui DESC");

                model.setRowCount(0);
                Connection conn = ConnectionJDBC.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString());
                
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i), types.get(i));
                }

                ResultSet rs = pstmt.executeQuery();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("MaPA"),
                        rs.getString("NoiDung"),
                        dateFormat.format(rs.getTimestamp("ThoiGianGui")),
                        rs.getString("TrangThai")
                    });
                }

                rs.close();
                pstmt.close();
                conn.close();

                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(dialog,
                        "Không tìm thấy kết quả nào.",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                dialog.dispose();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Lỗi khi tìm kiếm: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(evt -> dialog.dispose());

        buttonPanel.add(searchBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showPaymentDialog(String maHoaDon, String soTien) {
        JDialog dialog = new JDialog(this, "Thanh toán hóa đơn", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        contentPanel.add(new JLabel("Mã hóa đơn:"));
        contentPanel.add(new JLabel(maHoaDon));
        contentPanel.add(new JLabel("Số tiền:"));
        contentPanel.add(new JLabel(soTien + " đ"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmBtn = new JButton("Xác nhận");
        JButton cancelBtn = new JButton("Hủy");

        confirmBtn.setBackground(new Color(46, 204, 113));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);

        cancelBtn.setBackground(new Color(231, 76, 60));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);

        confirmBtn.addActionListener(e -> {
            updatePaymentStatus(maHoaDon);
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(confirmBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updatePaymentStatus(String maHoaDon) {
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "UPDATE HoaDon SET TinhTrang = 'Đã thanh toán' WHERE MaHoaDon = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maHoaDon);
            pstmt.executeUpdate();

            pstmt.close();
            conn.close();

            JOptionPane.showMessageDialog(this,
                "Cập nhật trạng thái thanh toán thành công!",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);

            // Refresh bảng hóa đơn
            DefaultTableModel model = (DefaultTableModel) invoiceTable.getModel();
            loadInvoiceData(model, "Tất cả");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi cập nhật trạng thái thanh toán: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSearchInvoiceDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm hóa đơn", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        // Main panel with GridBagLayout for better control
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Mã hóa đơn
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Mã hóa đơn:"), gbc);

        JTextField maHdField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        mainPanel.add(maHdField, gbc);

        // Ngày lập
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Ngày lập:"), gbc);

        JSpinner dateSpinner = createSearchDateSpinner();
        JPanel datePanel = (JPanel)dateSpinner.getClientProperty("parent.panel");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        mainPanel.add(datePanel, gbc);

        // Checkbox for date search
        JCheckBox dateCheckBox = new JCheckBox("Tìm theo ngày", false);
        dateCheckBox.addActionListener(e -> {
            dateSpinner.setEnabled(dateCheckBox.isSelected());
            datePanel.getComponent(1).setEnabled(dateCheckBox.isSelected());
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(dateCheckBox, gbc);

        // Initially disable date spinner
        dateSpinner.setEnabled(false);
        datePanel.getComponent(1).setEnabled(false);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchBtn = new JButton("Tìm kiếm");
        JButton cancelBtn = new JButton("Hủy");

        // Style buttons
        searchBtn.setBackground(new Color(46, 64, 83));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);

        cancelBtn.setBackground(new Color(46, 64, 83));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);

        searchBtn.addActionListener(evt -> {
            try {
                String maHd = maHdField.getText().trim();
                Date ngayLap = dateCheckBox.isSelected() ? 
                    (Date) ((SpinnerDateModel)dateSpinner.getModel()).getValue() : null;

                // Validate input - either ID or date must be provided
                if (maHd.isEmpty() && !dateCheckBox.isSelected()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập mã hóa đơn hoặc chọn ngày để tìm kiếm.",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                StringBuilder sql = new StringBuilder(
                    "SELECT hd.MaHoaDon, hd.MaHopDong, hd.NgLap, hd.SoTien, " +
                    "nvdp.TenNvdp, hd.TinhTrang " +
                    "FROM HoaDon hd " +
                    "JOIN HopDong h ON hd.MaHopDong = h.MaHopDong " +
                    "JOIN NhanVienDieuPhoi nvdp ON hd.MaNvdp = nvdp.MaNvdp " +
                    "WHERE h.MaChuThai = ? ");

                List<Object> params = new ArrayList<>();
                List<Integer> types = new ArrayList<>();
                params.add(maKhachHang);
                types.add(Types.VARCHAR);

                // Only add ID condition if ID is provided
                if (!maHd.isEmpty()) {
                    sql.append("AND hd.MaHoaDon = ? ");
                    params.add(maHd);
                    types.add(Types.VARCHAR);
                }
                // Add date condition if date is selected
                if (ngayLap != null) {
                    sql.append("AND TRUNC(hd.NgLap) = TRUNC(?) ");
                    params.add(new Timestamp(ngayLap.getTime()));
                    types.add(Types.TIMESTAMP);
                }

                sql.append("ORDER BY hd.NgLap DESC");

                model.setRowCount(0);
                Connection conn = ConnectionJDBC.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString());
                
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i), types.get(i));
                }

                ResultSet rs = pstmt.executeQuery();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("MaHoaDon"),
                        rs.getString("MaHopDong"),
                        dateFormat.format(rs.getDate("NgLap")),
                        String.format("%,d", rs.getLong("SoTien")),
                        rs.getString("TenNvdp"),
                        rs.getString("TinhTrang")
                    });
                }

                rs.close();
                pstmt.close();
                conn.close();

                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(dialog,
                        "Không tìm thấy kết quả nào.",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                dialog.dispose();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Lỗi khi tìm kiếm: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(evt -> dialog.dispose());

        buttonPanel.add(searchBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadInvoiceData(DefaultTableModel model, String status) {
        model.setRowCount(0); // Xóa dữ liệu cũ

        try {
            Connection conn = ConnectionJDBC.getConnection();
            StringBuilder sql = new StringBuilder(
                "SELECT hd.MaHoaDon, hd.MaHopDong, hd.NgLap, hd.SoTien, hd.TinhTrang, " +
                "nvdp.TenNvdp " +
                "FROM HoaDon hd " +
                "JOIN HopDong h ON hd.MaHopDong = h.MaHopDong " +
                "JOIN NhanVienDieuPhoi nvdp ON hd.MaNvdp = nvdp.MaNvdp " +
                "WHERE h.MaChuThai = ? "
            );

            if (!status.equals("Tất cả")) {
                sql.append("AND hd.TinhTrang = ? ");
            }
            sql.append("ORDER BY hd.NgLap DESC");

            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, maKhachHang);
            if (!status.equals("Tất cả")) {
                pstmt.setString(2, status);
            }

            ResultSet rs = pstmt.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("MaHoaDon"),
                    rs.getString("MaHopDong"),
                    dateFormat.format(rs.getDate("NgLap")),
                    String.format("%,d", rs.getLong("SoTien")),
                    rs.getString("TenNvdp"),
                    rs.getString("TinhTrang")
                });
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải danh sách hóa đơn: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createHopDongPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Quản lý hợp đồng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton addDetailButton = new JButton("Thêm chi tiết");
        JButton editContractButton = new JButton("Sửa hợp đồng");
        JButton deleteButton = new JButton("Xóa hợp đồng");
        
        // Style buttons
        searchButton.setBackground(new Color(46, 64, 83));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        
        refreshButton.setBackground(new Color(46, 64, 83));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        addDetailButton.setBackground(new Color(46, 204, 113));
        addDetailButton.setForeground(Color.WHITE);
        addDetailButton.setFocusPainted(false);
        addDetailButton.setEnabled(false);

        editContractButton.setBackground(new Color(241, 196, 15));
        editContractButton.setForeground(Color.WHITE);
        editContractButton.setFocusPainted(false);
        editContractButton.setEnabled(false);

        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.setEnabled(false);
        
        buttonPanel.add(searchButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addDetailButton);
        buttonPanel.add(editContractButton);
        buttonPanel.add(deleteButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Trạng thái:");
        String[] statuses = {"Tất cả", "Hoạt động", "Tạm dừng", "Kết thúc", "Chờ duyệt"};
        JComboBox<String> statusBox = new JComboBox<>(statuses);
        filterPanel.add(statusLabel);
        filterPanel.add(statusBox);

        // Combine header and filter
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        // Split pane for contract list and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // Contract table
        String[] contractColumns = {
            "Mã hợp đồng",
            "Loại hợp đồng",
            "Địa chỉ thu gom",
            "Ngày bắt đầu",
            "Ngày kết thúc",
            "Mô tả",
            "Trạng thái"
        };
        DefaultTableModel contractModel = new DefaultTableModel(contractColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable contractTable = new JTable(contractModel);
        JScrollPane contractScrollPane = new JScrollPane(contractTable);

        // Contract details panel
        JPanel detailPanel = new JPanel(new BorderLayout(10, 10));
        JPanel detailHeaderPanel = new JPanel(new BorderLayout(5, 5));
        JLabel detailTitle = new JLabel("Chi Tiết Hợp Đồng");
        detailTitle.setFont(new Font("Arial", Font.BOLD, 16));
        detailTitle.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Detail button panel
        JPanel detailButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editDetailButton = new JButton("Sửa chi tiết");
        JButton deleteDetailButton = new JButton("Xóa chi tiết"); // Add delete button
        editDetailButton.setBackground(new Color(241, 196, 15));
        editDetailButton.setForeground(Color.WHITE);
        editDetailButton.setFocusPainted(false);
        editDetailButton.setEnabled(false);
        
        deleteDetailButton.setBackground(new Color(231, 76, 60)); // Style delete button
        deleteDetailButton.setForeground(Color.WHITE);
        deleteDetailButton.setFocusPainted(false);
        deleteDetailButton.setEnabled(false);
        
        detailButtonPanel.add(editDetailButton);
        detailButtonPanel.add(deleteDetailButton);

        detailHeaderPanel.add(detailTitle, BorderLayout.WEST);
        detailHeaderPanel.add(detailButtonPanel, BorderLayout.EAST);

        String[] detailColumns = {
            "Tên dịch vụ",
            "Đơn vị tính",
            "Khối lượng",
            "Đơn giá",
            "Thành tiền",
            "Ghi chú"
        };
        DefaultTableModel detailModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable detailTable = new JTable(detailModel);
        JScrollPane detailScrollPane = new JScrollPane(detailTable);

        detailPanel.add(detailHeaderPanel, BorderLayout.NORTH);
        detailPanel.add(detailScrollPane, BorderLayout.CENTER);

        // Add tables to split pane
        splitPane.setTopComponent(contractScrollPane);
        splitPane.setBottomComponent(detailPanel);

        // Load initial data
        loadContractData(contractModel, "Tất cả");

        // Add selection listener to contract table
        contractTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = contractTable.getSelectedRow();
                if (row >= 0) {
                    String maHopDong = contractTable.getValueAt(row, 0).toString();
                    String trangThai = contractTable.getValueAt(row, 6).toString();
                    loadContractDetails(maHopDong, detailModel);
                    
                    // Chỉ enable các nút nếu hợp đồng đang ở trạng thái "Chờ duyệt"
                    boolean isChoDuyet = "Chờ duyệt".equals(trangThai);
                    addDetailButton.setEnabled(isChoDuyet);
                    editContractButton.setEnabled(isChoDuyet);
                    deleteButton.setEnabled(isChoDuyet);
                    
                    // Disable nút sửa chi tiết khi không có dòng nào được chọn
                    editDetailButton.setEnabled(false);
                } else {
                    addDetailButton.setEnabled(false);
                    editContractButton.setEnabled(false);
                    editDetailButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        });

        // Add selection listener for detail table
        detailTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int contractRow = contractTable.getSelectedRow();
                int detailRow = detailTable.getSelectedRow();
                
                if (contractRow >= 0 && detailRow >= 0) {
                    String trangThai = contractTable.getValueAt(contractRow, 6).toString();
                    boolean isChoDuyet = "Chờ duyệt".equals(trangThai);
                    editDetailButton.setEnabled(isChoDuyet);
                    deleteDetailButton.setEnabled(isChoDuyet);
                } else {
                    editDetailButton.setEnabled(false);
                    deleteDetailButton.setEnabled(false);
                }
            }
        });

        // Add action listeners
        searchButton.addActionListener(e -> showSearchContractDialog(contractModel));
        refreshButton.addActionListener(e -> loadContractData(contractModel, statusBox.getSelectedItem().toString()));
        statusBox.addActionListener(e -> loadContractData(contractModel, statusBox.getSelectedItem().toString()));
        
        // Add action listener for add detail button
        addDetailButton.addActionListener(e -> {
            int selectedRow = contractTable.getSelectedRow();
            if (selectedRow >= 0) {
                String maHopDong = contractTable.getValueAt(selectedRow, 0).toString();
                String trangThai = contractTable.getValueAt(selectedRow, 6).toString();
                
                if ("Chờ duyệt".equals(trangThai)) {
                    showAddContractDetailDialog(maHopDong, detailModel);
                }
            }
        });

        // Add action listener for edit contract button
        editContractButton.addActionListener(e -> {
            int selectedRow = contractTable.getSelectedRow();
            if (selectedRow >= 0) {
                String maHopDong = contractTable.getValueAt(selectedRow, 0).toString();
                String trangThai = contractTable.getValueAt(selectedRow, 6).toString();
                
                if ("Chờ duyệt".equals(trangThai)) {
                    showEditContractDialog(maHopDong, contractModel);
                }
            }
        });

        // Add action listener for edit detail button
        editDetailButton.addActionListener(e -> {
            int contractRow = contractTable.getSelectedRow();
            int detailRow = detailTable.getSelectedRow();
            
            if (contractRow >= 0 && detailRow >= 0) {
                String maHopDong = contractTable.getValueAt(contractRow, 0).toString();
                String trangThai = contractTable.getValueAt(contractRow, 6).toString();
                
                if ("Chờ duyệt".equals(trangThai)) {
                    // Lấy thông tin chi tiết hợp đồng được chọn
                    String dichVu = detailTable.getValueAt(detailRow, 0).toString();
                    String khoiLuongStr = detailTable.getValueAt(detailRow, 2).toString().replace(",", "");
                    double khoiLuong = Double.parseDouble(khoiLuongStr);
                    String ghiChu = detailTable.getValueAt(detailRow, 5).toString();
                    
                    showEditDetailDialog(maHopDong, dichVu, khoiLuong, ghiChu, detailModel);
                }
            }
        });

        // Add action listener for delete button
        deleteButton.addActionListener(e -> {
            int selectedRow = contractTable.getSelectedRow();
            if (selectedRow >= 0) {
                String maHopDong = contractTable.getValueAt(selectedRow, 0).toString();
                String trangThai = contractTable.getValueAt(selectedRow, 6).toString();
                
                if ("Chờ duyệt".equals(trangThai)) {
                    int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc chắn muốn xóa hợp đồng này và tất cả chi tiết của nó không?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        deleteContract(maHopDong, contractModel, detailModel);
                    }
                }
            }
        });

        // Add action listener for delete detail button
        deleteDetailButton.addActionListener(e -> {
            int contractRow = contractTable.getSelectedRow();
            int detailRow = detailTable.getSelectedRow();
            
            if (contractRow >= 0 && detailRow >= 0) {
                String maHopDong = contractTable.getValueAt(contractRow, 0).toString();
                String trangThai = contractTable.getValueAt(contractRow, 6).toString();
                String dichVu = detailTable.getValueAt(detailRow, 0).toString();
                
                if ("Chờ duyệt".equals(trangThai)) {
                    // Check if this is the only detail
                    if (detailTable.getRowCount() <= 1) {
                        JOptionPane.showMessageDialog(this,
                            "Không thể xóa chi tiết hợp đồng cuối cùng. Hợp đồng phải có ít nhất một chi tiết.",
                            "Cảnh báo",
                            JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Get MaDichVu from the service name
                    try {
                        Connection conn = ConnectionJDBC.getConnection();
                        String sql = "SELECT MaDichVu FROM DichVu WHERE TenDichVu = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, dichVu);
                        ResultSet rs = pstmt.executeQuery();
                        
                        if (rs.next()) {
                            String maDichVu = rs.getString("MaDichVu");
                            deleteContractDetail(maHopDong, maDichVu, detailModel);
                        }
                        
                        conn.close();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this,
                            "Lỗi khi xóa chi tiết hợp đồng: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    // Thêm phương thức xóa hợp đồng
    private void deleteContract(String maHopDong, DefaultTableModel contractModel, DefaultTableModel detailModel) {
        try {
            Connection conn = ConnectionJDBC.getConnection();
            conn.setAutoCommit(false);
            
            try {
                // Xóa chi tiết hợp đồng trước
                String deleteDetailsSql = "DELETE FROM ChiTietHopDong WHERE MaHopDong = ?";
                PreparedStatement deleteDetailsStmt = conn.prepareStatement(deleteDetailsSql);
                deleteDetailsStmt.setString(1, maHopDong);
                deleteDetailsStmt.executeUpdate();
                
                // Sau đó xóa hợp đồng
                String deleteContractSql = "DELETE FROM HopDong WHERE MaHopDong = ? AND TrangThai = 'Chờ duyệt'";
                PreparedStatement deleteContractStmt = conn.prepareStatement(deleteContractSql);
                deleteContractStmt.setString(1, maHopDong);
                
                int result = deleteContractStmt.executeUpdate();
                
                if (result > 0) {
                    conn.commit();
                    JOptionPane.showMessageDialog(this,
                        "Xóa hợp đồng thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh both tables
                    loadContractData(contractModel, "Tất cả");
                    detailModel.setRowCount(0); // Clear detail table
                } else {
                    throw new SQLException("Không thể xóa hợp đồng");
                }
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi xóa hợp đồng: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Thêm phương thức để hiển thị dialog thêm chi tiết hợp đồng
    private void showAddContractDetailDialog(String maHopDong, DefaultTableModel detailModel) {
        JDialog dialog = new JDialog(this, "Thêm chi tiết hợp đồng", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Chọn dịch vụ
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Chọn dịch vụ:"), gbc);

        JComboBox<String> serviceBox = new JComboBox<>();
        loadServices(serviceBox);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(serviceBox, gbc);

        // Khối lượng
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Khối lượng (kg):"), gbc);

        JTextField weightField = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(weightField, gbc);

        // Thành tiền
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Thành tiền:"), gbc);

        JLabel totalLabel = new JLabel("0 đ");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(totalLabel, gbc);

        // Ghi chú
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Ghi chú:"), gbc);

        JTextField noteField = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(noteField, gbc);

        // Add listeners for total calculation
        serviceBox.addActionListener(e -> calculateTotal(serviceBox, weightField, totalLabel));
        weightField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = weightField.getText();
                if (!((c >= '0' && c <= '9') || c == ',' || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                    return;
                }
                if (c == ',' && currentText.contains(",")) {
                    e.consume();
                    return;
                }
            }
            
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                calculateTotal(serviceBox, weightField, totalLabel);
            }
        });

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (serviceBox.getSelectedItem() == null || 
                    serviceBox.getSelectedItem().toString().equals("-- Chọn dịch vụ --")) {
                    JOptionPane.showMessageDialog(dialog,
                        "Vui lòng chọn dịch vụ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String weightText = weightField.getText().trim().replace(',', '.');
                if (weightText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập khối lượng!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double weight = Double.parseDouble(weightText);
                if (weight <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                        "Khối lượng phải lớn hơn 0!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                addContractDetail(
                    maHopDong,
                    null, // district is no longer needed
                    null, // route is no longer needed
                    serviceBox.getSelectedItem().toString(),
                    weightField.getText(),
                    totalLabel.getText(),
                    noteField.getText(),
                    detailModel
                );
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Khối lượng không hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadDistricts(JComboBox<String> districtBox) {
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT TenQuan FROM Quan ORDER BY TenQuan";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                districtBox.addItem(rs.getString("TenQuan"));
            }
            
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải danh sách quận: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRoutes(JComboBox<String> routeBox, String district) {
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT t.TenTuyen FROM TuyenDuongThuGom t " +
                        "JOIN Quan q ON t.KhuVuc = q.MaQuan " +
                        "WHERE q.TenQuan = ? " +
                        "ORDER BY t.TenTuyen";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, district);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                routeBox.addItem(rs.getString("TenTuyen"));
            }
            
            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải danh sách tuyến đường: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadServices(JComboBox<String> serviceBox) {
        serviceBox.removeAllItems();
        
        try (Connection conn = ConnectionJDBC.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT TenDichVu, DonViTinh, DonGia FROM DichVu ORDER BY TenDichVu")) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String service = String.format("%s - %s - %,d VNĐ/%s",
                    rs.getString("TenDichVu"),
                    rs.getString("DonViTinh"),
                    rs.getLong("DonGia"),
                    rs.getString("DonViTinh"));
                serviceBox.addItem(service);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải danh sách dịch vụ: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateContractDetailInput(JComboBox<String> districtBox, 
                                              JComboBox<String> routeBox,
                                              JComboBox<String> serviceBox,
                                              JTextField weightField) {
        if (districtBox.getSelectedItem() == null || 
            districtBox.getSelectedItem().toString().equals("-- Chọn quận --")) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn quận!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (routeBox.getSelectedItem() == null || 
            routeBox.getSelectedItem().toString().equals("-- Chọn tuyến đường --")) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn tuyến đường!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (serviceBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn dịch vụ!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            String weightText = weightField.getText().trim().replace(',', '.');
            if (weightText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập khối lượng!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            double weight = Double.parseDouble(weightText);
            if (weight <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Khối lượng phải lớn hơn 0!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Khối lượng không hợp lệ!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void addContractDetail(String maHopDong, 
                                 String district,
                                 String route,
                                 String service,
                                 String weight,
                                 String total,
                                 String note,
                                 DefaultTableModel detailModel) {
        try {
            Connection conn = ConnectionJDBC.getConnection();
            conn.setAutoCommit(false);

            try {
                String serviceName = service.split(" - ")[0];
                
                // Get MaDichVu
                String findServiceSql = "SELECT MaDichVu FROM DichVu WHERE TenDichVu = ?";
                PreparedStatement findServiceStmt = conn.prepareStatement(findServiceSql);
                findServiceStmt.setString(1, serviceName);
                ResultSet rs = findServiceStmt.executeQuery();
                
                if (rs.next()) {
                    int maDichVu = rs.getInt("MaDichVu");
                    
                    // Insert chi tiết hợp đồng
                    String insertSql = "INSERT INTO ChiTietHopDong (MaHopDong, MaDichVu, KhoiLuong, ThanhTien, GhiChu) " +
                                     "VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(insertSql);
                    
            pstmt.setString(1, maHopDong);
                    pstmt.setInt(2, maDichVu);
                    pstmt.setDouble(3, Double.parseDouble(weight.replace(",", ".")));
                    pstmt.setDouble(4, Double.parseDouble(total.replaceAll("[^0-9]", "")));
                    pstmt.setString(5, note);
                    
                    int result = pstmt.executeUpdate();
                    
                    if (result > 0) {
                        conn.commit();
                        JOptionPane.showMessageDialog(this,
                            "Thêm chi tiết hợp đồng thành công!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Refresh detail table
                        loadContractDetails(maHopDong, detailModel);
                    } else {
                        throw new SQLException("Không thể thêm chi tiết hợp đồng");
                    }
                } else {
                    throw new SQLException("Không tìm thấy dịch vụ: " + serviceName);
                }
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException | NumberFormatException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi thêm chi tiết hợp đồng: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadContractDetails(String maHopDong, DefaultTableModel model) {
        model.setRowCount(0);
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT dv.TenDichVu, dv.DonViTinh, ct.KhoiLuong, dv.DonGia, ct.ThanhTien, ct.GhiChu " +
                        "FROM ChiTietHopDong ct " +
                        "JOIN DichVu dv ON ct.MaDichVu = dv.MaDichVu " +
                        "WHERE ct.MaHopDong = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maHopDong);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("TenDichVu"),
                    rs.getString("DonViTinh"),
                    String.format("%,d", rs.getInt("KhoiLuong")),
                    String.format("%,.0f đ", rs.getDouble("DonGia")),
                    String.format("%,.0f đ", rs.getDouble("ThanhTien")),
                    rs.getString("GhiChu")
                });
            }

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải chi tiết hợp đồng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteContractDetail(String maHopDong, String maDichVu, DefaultTableModel detailModel) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa chi tiết hợp đồng này không?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                conn.setAutoCommit(false);
                
                try {
                    String sql = "DELETE FROM ChiTietHopDong WHERE MaHopDong = ? AND MaDichVu = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, maHopDong);
                    pstmt.setString(2, maDichVu);
                    
                    int result = pstmt.executeUpdate();
                    
                    if (result > 0) {
                        conn.commit();
                        JOptionPane.showMessageDialog(this,
                            "Xóa chi tiết hợp đồng thành công!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                            
                        // Refresh detail table
                        loadContractDetails(maHopDong, detailModel);
                    } else {
                        throw new SQLException("Không thể xóa chi tiết hợp đồng");
                    }
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                    conn.close();
                }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xóa chi tiết hợp đồng: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createContractDetailTable(String maHopDong, JTable detailTable) {
        DefaultTableModel detailModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Chỉ cho phép edit cột nút xóa
            }
        };
        
        detailModel.addColumn("Tên dịch vụ");
        detailModel.addColumn("Đơn vị tính");
        detailModel.addColumn("Khối lượng");
        detailModel.addColumn("Đơn giá");
        detailModel.addColumn("Thành tiền");
        detailModel.addColumn("Ghi chú");
        
        detailTable.setModel(detailModel);
        

        
        // Set column widths
        detailTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Tên dịch vụ
        detailTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Đơn vị tính
        detailTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Khối lượng
        detailTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Đơn giá
        detailTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Thành tiền
        detailTable.getColumnModel().getColumn(5).setPreferredWidth(200); // Ghi chú
        
        // Load data
        loadContractDetails(maHopDong, detailModel);
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
            setOpaque(true);
            setBackground(new Color(231, 76, 60));
            setForeground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private ActionListener actionListener;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox, String label, Consumer<Integer> action) {
            super(checkBox);
            this.label = label;
            button = new JButton(label);
            button.setOpaque(true);
            button.setBackground(new Color(231, 76, 60));
            button.setForeground(Color.WHITE);
            
            button.addActionListener(e -> {
                fireEditingStopped();
                action.accept(currentRow);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private void showSearchContractDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm hợp đồng", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        // Main panel with GridBagLayout for better control
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Mã hợp đồng
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Mã hợp đồng:"), gbc);

        JTextField maHdField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        mainPanel.add(maHdField, gbc);

        // Ngày bắt đầu
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Ngày bắt đầu:"), gbc);

        JSpinner dateSpinner = createSearchDateSpinner();
        JPanel datePanel = (JPanel)dateSpinner.getClientProperty("parent.panel");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        mainPanel.add(datePanel, gbc);

        // Checkbox for date search
        JCheckBox dateCheckBox = new JCheckBox("Tìm theo ngày", false);
        dateCheckBox.addActionListener(e -> {
            dateSpinner.setEnabled(dateCheckBox.isSelected());
            datePanel.getComponent(1).setEnabled(dateCheckBox.isSelected());
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(dateCheckBox, gbc);

        // Initially disable date spinner
        dateSpinner.setEnabled(false);
        datePanel.getComponent(1).setEnabled(false);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchBtn = new JButton("Tìm kiếm");
        JButton cancelBtn = new JButton("Hủy");

        // Style buttons
        searchBtn.setBackground(new Color(46, 64, 83));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);

        cancelBtn.setBackground(new Color(46, 64, 83));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);

        searchBtn.addActionListener(evt -> {
            try {
                String maHd = maHdField.getText().trim();
                Date ngayBd = dateCheckBox.isSelected() ? 
                    (Date) ((SpinnerDateModel)dateSpinner.getModel()).getValue() : null;

                // Validate input - either ID or date must be provided
                if (maHd.isEmpty() && !dateCheckBox.isSelected()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập mã hợp đồng hoặc chọn ngày để tìm kiếm.",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }

                StringBuilder sql = new StringBuilder(
                    "SELECT h.MaHopDong, h.LoaiHopDong, h.DiaChiThuGom, h.NgBatDau, h.NgKetThuc, " +
                    "DBMS_LOB.SUBSTR(h.MoTa, 4000, 1) as MoTa, h.TrangThai " +
                    "FROM HopDong h " +
                    "WHERE h.MaChuThai = ? ");

                List<Object> params = new ArrayList<>();
                List<Integer> types = new ArrayList<>();
                params.add(maKhachHang);
                types.add(Types.VARCHAR);

                // Only add ID condition if ID is provided
                if (!maHd.isEmpty()) {
                    sql.append("AND h.MaHopDong = ? ");
                    params.add(maHd);
                    types.add(Types.VARCHAR);
                }
                // Add date condition if date is selected
                if (ngayBd != null) {
                    sql.append("AND TRUNC(h.NgBatDau) = TRUNC(?) ");
                    params.add(new Timestamp(ngayBd.getTime()));
                    types.add(Types.TIMESTAMP);
                }

                sql.append("ORDER BY h.NgBatDau DESC");

                model.setRowCount(0);
                Connection conn = ConnectionJDBC.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql.toString());
                
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i), types.get(i));
                }

                ResultSet rs = pstmt.executeQuery();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("MaHopDong"),
                        rs.getString("LoaiHopDong"),
                        rs.getString("DiaChiThuGom"),
                        dateFormat.format(rs.getDate("NgBatDau")),
                        dateFormat.format(rs.getDate("NgKetThuc")),
                        rs.getString("MoTa"),
                        rs.getString("TrangThai")
                    });
                }

                rs.close();
                pstmt.close();
                conn.close();

                if (model.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(dialog,
                        "Không tìm thấy kết quả nào.",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                dialog.dispose();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Lỗi khi tìm kiếm: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(evt -> dialog.dispose());

        buttonPanel.add(searchBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadContractData(DefaultTableModel model, String status) {
        model.setRowCount(0); // Xóa dữ liệu cũ
        
        try {
            Connection conn = ConnectionJDBC.getConnection();
            StringBuilder sql = new StringBuilder(
                "SELECT h.MaHopDong, h.LoaiHopDong, h.DiaChiThuGom, h.NgBatDau, h.NgKetThuc, " +
                "DBMS_LOB.SUBSTR(h.MoTa, 4000, 1) as MoTa, h.TrangThai " +
                "FROM HopDong h " +
                "WHERE h.MaChuThai = ? "
            );

            if (!status.equals("Tất cả")) {
                sql.append(" AND h.TrangThai = ?");
            }
            sql.append(" ORDER BY h.NgBatDau DESC");

            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, maKhachHang);
            if (!status.equals("Tất cả")) {
                pstmt.setString(2, status);
            }

            ResultSet rs = pstmt.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("MaHopDong"),
                    rs.getString("LoaiHopDong"),
                    rs.getString("DiaChiThuGom"),
                    dateFormat.format(rs.getDate("NgBatDau")),
                    dateFormat.format(rs.getDate("NgKetThuc")),
                    rs.getString("MoTa"),
                    rs.getString("TrangThai")
                });
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải danh sách hợp đồng: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createDangXuatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Bạn có chắc chắn muốn đăng xuất?", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton logoutButton = new JButton("Xác nhận đăng xuất");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> {
            dispose();
            new GiaoDienDangNhap().setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(logoutButton);

        panel.add(label, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    // Helper method to create a date spinner for searching (allows past dates)
    private JSpinner createSearchDateSpinner() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date initDate = calendar.getTime(); // Current date as initial value
        
        // Set minimum date to 10 years in the past
        Calendar minCal = Calendar.getInstance();
        minCal.add(Calendar.YEAR, -10);
        Date minDate = minCal.getTime();
        
        // Set maximum date to current date
        Date maxDate = calendar.getTime();
        
        SpinnerDateModel dateModel = new SpinnerDateModel(initDate, minDate, maxDate, Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(dateModel);
        
        // Custom date editor that allows editing year
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(dateEditor);
        
        // Allow direct text editing
        JFormattedTextField ftf = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        ftf.setEditable(true);
        ftf.setHorizontalAlignment(JTextField.LEFT);
        ftf.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add clear button
        JButton clearButton = new JButton("X");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 10));
        clearButton.setMargin(new Insets(1, 4, 1, 4));
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> {
            dateModel.setValue(initDate); // Reset to current date instead of null
            ftf.setValue(initDate);
        });
        
        // Create a panel to hold both spinner and clear button
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(spinner, BorderLayout.CENTER);
        panel.add(clearButton, BorderLayout.EAST);
        
        // Store the panel as a client property of the spinner
        spinner.putClientProperty("parent.panel", panel);
        
        return spinner;
    }

    // Helper method to create a standardized date spinner for booking
    private JSpinner createDateSpinner() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date initDate = calendar.getTime();
        Date minDate = calendar.getTime(); // Minimum date is tomorrow
        
        Calendar maxCal = Calendar.getInstance();
        maxCal.add(Calendar.YEAR, 10); // Allow dates up to 10 years in the future
        Date maxDate = maxCal.getTime();
        
        SpinnerDateModel dateModel = new SpinnerDateModel(initDate, minDate, maxDate, Calendar.DAY_OF_MONTH);
        JSpinner spinner = new JSpinner(dateModel);
        
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(dateEditor);
        
        // Make the text field non-editable to prevent invalid input
        JFormattedTextField ftf = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        ftf.setEditable(false);
        ftf.setHorizontalAlignment(JTextField.LEFT);
        ftf.setFont(new Font("Arial", Font.PLAIN, 14));
        
        return spinner;
    }

    

    private JPanel createHoaDonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Xem hóa đơn");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton paymentButton = new JButton("Thanh toán");
        
        // Style buttons
        searchButton.setBackground(new Color(46, 64, 83));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        
        refreshButton.setBackground(new Color(46, 64, 83));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);

        paymentButton.setBackground(new Color(46, 204, 113));
        paymentButton.setForeground(Color.WHITE);
        paymentButton.setFocusPainted(false);
        paymentButton.setEnabled(false); // Ban đầu disable nút thanh toán
        
        buttonPanel.add(searchButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(paymentButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Trạng thái:");
        String[] statuses = {"Tất cả", "Chưa thanh toán", "Đã thanh toán"};
        JComboBox<String> statusBox = new JComboBox<>(statuses);
        filterPanel.add(statusLabel);
        filterPanel.add(statusBox);

        // Combine header and filter
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        // Table with columns matching the database
        String[] columns = {
            "Mã hóa đơn",
            "Mã hợp đồng",
            "Ngày lập",
            "Số tiền",
            "Nhân viên lập",
            "Tình trạng"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        invoiceTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        
        // Load dữ liệu vào bảng
        loadInvoiceData(model, "Tất cả");

        // Add selection listener to enable/disable payment button
        invoiceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = invoiceTable.getSelectedRow();
                if (selectedRow != -1) {
                    String status = invoiceTable.getValueAt(selectedRow, 5).toString();
                    paymentButton.setEnabled("Chưa thanh toán".equals(status));
                } else {
                    paymentButton.setEnabled(false);
                }
            }
        });

        // Add action listeners
        searchButton.addActionListener(e -> showSearchInvoiceDialog(model));
        refreshButton.addActionListener(e -> loadInvoiceData(model, "Tất cả"));
        statusBox.addActionListener(e -> loadInvoiceData(model, statusBox.getSelectedItem().toString()));

        // Add payment button action listener
        paymentButton.addActionListener(e -> {
            int selectedRow = invoiceTable.getSelectedRow();
            if (selectedRow != -1) {
                String maHoaDon = invoiceTable.getValueAt(selectedRow, 0).toString();
                String soTien = invoiceTable.getValueAt(selectedRow, 3).toString();
                String status = invoiceTable.getValueAt(selectedRow, 5).toString();
                
                if ("Chưa thanh toán".equals(status)) {
                    showQRPaymentDialog(maHoaDon, soTien, model, statusBox);
                }
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void showQRPaymentDialog(String maHoaDon, String soTien, DefaultTableModel model, JComboBox<String> statusBox) {
        JDialog dialog = new JDialog(this, "Thanh toán hóa đơn", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        // Panel chứa thông tin hóa đơn
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        infoPanel.add(new JLabel("Mã hóa đơn:"));
        infoPanel.add(new JLabel(maHoaDon));
        
        infoPanel.add(new JLabel("Số tiền:"));
        infoPanel.add(new JLabel(soTien + " đ"));
        
        infoPanel.add(new JLabel("Phương thức:"));
        infoPanel.add(new JLabel("Quét mã QR"));

        // Panel chứa hình QR
        JPanel qrPanel = new JPanel(new BorderLayout());
        qrPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Load hình QR từ resources
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("/Image/QR.jpg"));
            Image originalImage = originalIcon.getImage();
            
            // Scale hình QR
            int qrSize = 250;
            Image scaledImage = originalImage.getScaledInstance(qrSize, qrSize, Image.SCALE_SMOOTH);
            JLabel qrLabel = new JLabel(new ImageIcon(scaledImage));
            qrLabel.setHorizontalAlignment(JLabel.CENTER);
            
            qrPanel.add(qrLabel, BorderLayout.CENTER);
        } catch (Exception ex) {
            qrPanel.add(new JLabel("Không thể tải hình QR"), BorderLayout.CENTER);
        }

        // Panel chứa nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton confirmButton = new JButton("Xác nhận đã thanh toán");
        JButton cancelButton = new JButton("Hủy");

        confirmButton.setBackground(new Color(46, 204, 113));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);

        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        confirmButton.addActionListener(evt -> {
            int confirm = JOptionPane.showConfirmDialog(
                dialog,
                "Xác nhận đã thanh toán hóa đơn này?",
                "Xác nhận thanh toán",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                updatePaymentStatus(maHoaDon, model, statusBox);
                dialog.dispose();
            }
        });

        cancelButton.addActionListener(evt -> dialog.dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        // Thêm các panel vào dialog
        dialog.add(infoPanel, BorderLayout.NORTH);
        dialog.add(qrPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void updatePaymentStatus(String maHoaDon, DefaultTableModel model, JComboBox<String> statusBox) {
        try {
            Connection conn = ConnectionJDBC.getConnection();
            conn.setAutoCommit(false);
            
            try {
                String sql = "UPDATE HoaDon SET TinhTrang = 'Đã thanh toán' WHERE MaHoaDon = ? AND TinhTrang = 'Chưa thanh toán'";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, maHoaDon);
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    conn.commit();
                    JOptionPane.showMessageDialog(this,
                        "Cập nhật trạng thái thanh toán thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh table data
                    loadInvoiceData(model, statusBox.getSelectedItem().toString());
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this,
                        "Không thể cập nhật trạng thái thanh toán!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
                
                pstmt.close();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi cập nhật trạng thái thanh toán: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Tiêu đề
        JLabel titleLabel = new JLabel("Cài đặt", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel chính chứa cả thông tin và form đổi mật khẩu
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 50, 20, 50));

        // Panel thông tin chủ thải
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(46, 64, 83), 1),
            "Thông tin chủ thải",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Tạo các trường thông tin có thể chỉnh sửa
        JTextField hoTenField = new JTextField(20);
        JTextField diaChiField = new JTextField(20);
        JTextField sdtField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JLabel loaiChuThaiLabel = new JLabel();

        // Thêm các trường vào panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Họ tên:"), gbc);

        gbc.gridx = 1;
        infoPanel.add(hoTenField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("Địa chỉ:"), gbc);

        gbc.gridx = 1;
        infoPanel.add(diaChiField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(new JLabel("Số điện thoại:"), gbc);

        gbc.gridx = 1;
        infoPanel.add(sdtField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        infoPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        infoPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        infoPanel.add(new JLabel("Loại chủ thải:"), gbc);

        gbc.gridx = 1;
        infoPanel.add(loaiChuThaiLabel, gbc);

        // Nút cập nhật thông tin
        JButton updateInfoButton = new JButton("Cập nhật thông tin");
        updateInfoButton.setBackground(new Color(46, 204, 113));
        updateInfoButton.setForeground(Color.WHITE);
        updateInfoButton.setFocusPainted(false);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        infoPanel.add(updateInfoButton, gbc);

        // Load thông tin hiện tại
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT HoTen, DiaChi, Sdt, Email, LoaiChuThai FROM ChuThai WHERE MaChuThai = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maKhachHang);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                hoTenField.setText(rs.getString("HoTen"));
                diaChiField.setText(rs.getString("DiaChi"));
                sdtField.setText(rs.getString("Sdt"));
                emailField.setText(rs.getString("Email"));
                loaiChuThaiLabel.setText(rs.getString("LoaiChuThai"));
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải thông tin chủ thải: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }

        // Thêm action listener cho nút cập nhật
        updateInfoButton.addActionListener(e -> {
            // Validate input
            String hoTen = hoTenField.getText().trim();
            String diaChi = diaChiField.getText().trim();
            String sdt = sdtField.getText().trim();
            String email = emailField.getText().trim();

            if (hoTen.isEmpty() || diaChi.isEmpty() || sdt.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng điền đầy đủ thông tin!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate số điện thoại
            if (!sdt.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this,
                    "Số điện thoại không hợp lệ! Vui lòng nhập 10 chữ số.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate email
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(this,
                    "Email không hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cập nhật thông tin
            try {
                Connection conn = ConnectionJDBC.getConnection();
                String updateSql = "UPDATE ChuThai SET HoTen = ?, DiaChi = ?, Sdt = ?, Email = ? WHERE MaChuThai = ?";
                PreparedStatement pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, hoTen);
                pstmt.setString(2, diaChi);
                pstmt.setString(3, sdt);
                pstmt.setString(4, email);
                pstmt.setString(5, maKhachHang);

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Cập nhật thông tin thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Cập nhật tên hiển thị trên sidebar
                    tenKhachHang = hoTen;
                    updateSidebarUserInfo();
                }

                pstmt.close();
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi cập nhật thông tin: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(20)); // Tạo khoảng cách

        // Panel đổi mật khẩu (giữ nguyên code cũ)
        JPanel passwordPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        passwordPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(46, 64, 83), 1),
            "Đổi mật khẩu",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14)
        ));

        // Mật khẩu cũ
        JLabel oldPassLabel = new JLabel("Mật khẩu cũ:");
        JPasswordField oldPassField = new JPasswordField();
        passwordPanel.add(oldPassLabel);
        passwordPanel.add(oldPassField);

        // Mật khẩu mới
        JLabel newPassLabel = new JLabel("Mật khẩu mới:");
        JPasswordField newPassField = new JPasswordField();
        passwordPanel.add(newPassLabel);
        passwordPanel.add(newPassField);

        // Xác nhận mật khẩu mới
        JLabel confirmPassLabel = new JLabel("Xác nhận mật khẩu mới:");
        JPasswordField confirmPassField = new JPasswordField();
        passwordPanel.add(confirmPassLabel);
        passwordPanel.add(confirmPassField);

        // Nút đổi mật khẩu
        JButton changePassButton = new JButton("Đổi mật khẩu");
        changePassButton.setBackground(new Color(46, 204, 113));
        changePassButton.setForeground(Color.WHITE);
        
        // Thêm sự kiện cho nút đổi mật khẩu
        changePassButton.addActionListener(evt -> {
            String oldPass = new String(oldPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());

            // Kiểm tra các trường không được để trống
            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng điền đầy đủ thông tin!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra mật khẩu mới và xác nhận mật khẩu
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this,
                    "Mật khẩu mới và xác nhận mật khẩu không khớp!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = ConnectionJDBC.getConnection();
                // Kiểm tra mật khẩu cũ
                String checkSql = "SELECT * FROM TaiKhoan WHERE MaTaiKhoan = ? AND MatKhau = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, maKhachHang);
                    checkStmt.setString(2, oldPass);
                    ResultSet rs = checkStmt.executeQuery();

                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(this,
                            "Mật khẩu cũ không đúng!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Cập nhật mật khẩu mới
                    String updateSql = "UPDATE TaiKhoan SET MatKhau = ? WHERE MaTaiKhoan = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, newPass);
                        updateStmt.setString(2, maKhachHang);
                        int result = updateStmt.executeUpdate();

                        if (result > 0) {
                            JOptionPane.showMessageDialog(this,
                                "Đổi mật khẩu thành công!",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE);
                            // Clear các trường
                            oldPassField.setText("");
                            newPassField.setText("");
                            confirmPassField.setText("");
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi đổi mật khẩu: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Panel cho nút
        JPanel buttonPanelPass = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanelPass.add(changePassButton);
        passwordPanel.add(new JLabel()); // Placeholder
        passwordPanel.add(buttonPanelPass);

        mainPanel.add(passwordPanel);

        // Thêm JScrollPane để có thể cuộn nếu nội dung quá dài
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null); // Xóa border của ScrollPane
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Thêm phương thức để cập nhật thông tin người dùng trên sidebar
    private void updateSidebarUserInfo() {
        // Tìm panel thông tin người dùng trong sidebar
        for (Component comp : sideBar.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel userInfoPanel = (JPanel) comp;
                for (Component label : userInfoPanel.getComponents()) {
                    if (label instanceof JLabel) {
                        JLabel nameLabel = (JLabel) label;
                        if (nameLabel.getText().startsWith("Xin chào")) {
                            nameLabel.setText("Xin chào, " + tenKhachHang);
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    private JPanel createHuongDanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Hướng dẫn phân loại rác", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel chứa danh sách loại rác
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel hiển thị chi tiết
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tạo scroll pane cho danh sách
        JScrollPane listScrollPane = new JScrollPane(listPanel);
        listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        listScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        listScrollPane.setPreferredSize(new Dimension(200, 0));

        // Tạo scroll pane cho panel chi tiết với chỉ thanh cuộn dọc
        JScrollPane detailScrollPane = new JScrollPane(detailPanel);
        detailScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        detailScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        detailScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        detailScrollPane.setPreferredSize(new Dimension(750, 0));

        // Label mặc định cho panel chi tiết
        JLabel defaultLabel = new JLabel("Chọn một loại rác để xem chi tiết", JLabel.CENTER);
        defaultLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        detailPanel.add(defaultLabel);

        // Danh sách các loại rác cố định với màu sắc
        String[][] loaiRac = {
            {"1", "Rác Hữu Cơ", "#4CAF50"},     // Green
            {"2", "Rác Vô Cơ", "#2196F3"},     // Blue
            {"3", "Rác Tái Chế", "#FF9800"},    // Orange
            {"4", "Rác Nguy Hại", "#F44336"},   // Red
            {"5", "Các loại rác khác", "#9C27B0"} // Purple
        };

        for (String[] loai : loaiRac) {
            JPanel buttonPanel = new JPanel(new BorderLayout());
            buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            buttonPanel.setOpaque(false);
            
            JButton itemButton = new JButton(loai[1]);
            itemButton.setHorizontalAlignment(SwingConstants.LEFT);
            itemButton.setFont(new Font("Arial", Font.BOLD, 14));
            itemButton.setBorderPainted(false);
            itemButton.setContentAreaFilled(true);
            itemButton.setFocusPainted(false);
            itemButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            itemButton.setBackground(Color.decode(loai[2]));
            itemButton.setForeground(Color.WHITE);
            
            // Add hover effect
            itemButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    itemButton.setBackground(itemButton.getBackground().brighter());
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    itemButton.setBackground(Color.decode(loai[2]));
                }
            });
            
            final String maLoai = loai[0];
            
            // Add click event to show details
            itemButton.addActionListener(evt -> {
                showWasteDetails(detailPanel, Integer.parseInt(maLoai));
            });
            
            buttonPanel.add(itemButton);
            listPanel.add(buttonPanel);
            listPanel.add(Box.createVerticalStrut(5));
        }

        // Tạo JSplitPane để chia màn hình thành 2 phần
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                            listScrollPane, detailScrollPane);
        splitPane.setDividerLocation(200);
        splitPane.setEnabled(false);
        splitPane.setOneTouchExpandable(false);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private void showWasteDetails(JPanel detailPanel, int maLoai) {
        detailPanel.removeAll();
        
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MoTa, HdPhanLoai FROM TtPhanLoaiRac WHERE MaLoaiRac = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maLoai);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    // Panel chứa tất cả nội dung
                    JPanel contentPanel = new JPanel();
                    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                    contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                    
                    // Tạo panel cho tiêu đề
                    String tenLoai = "";
                    switch(maLoai) {
                        case 1: tenLoai = "Rác Hữu Cơ"; break;
                        case 2: tenLoai = "Rác Vô Cơ"; break;
                        case 3: tenLoai = "Rác Tái Chế"; break;
                        case 4: tenLoai = "Rác Nguy Hại"; break;
                        case 5: tenLoai = "Các loại rác khác"; break;
                    }
                    
                    JLabel titleLabel = new JLabel(tenLoai);
                    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
                    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentPanel.add(titleLabel);
                    contentPanel.add(Box.createVerticalStrut(20));
                    
                    // Hiển thị hình ảnh từ package Image
                    String imageName = "";
                    switch(maLoai) {
                        case 1: imageName = "RacHuuCo.jpg"; break;
                        case 2: imageName = "RacVoCo.jpg"; break;
                        case 3: imageName = "RacTaiChe.jpg"; break;
                        case 4: imageName = "RacNguyHai.jpg"; break;
                        case 5: imageName = "RacKhac.jpg"; break;
                    }
                    
                    try {
                        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/Image/" + imageName));
                        Image originalImage = originalIcon.getImage();
                        
                        // Tạo BufferedImage mới với chất lượng cao
                        BufferedImage resizedImage = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = resizedImage.createGraphics();
                        
                        // Thiết lập các thuộc tính để render hình ảnh chất lượng cao
                        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Vẽ hình ảnh với chất lượng cao
                        g2d.drawImage(originalImage, 0, 0, 400, 300, null);
                        g2d.dispose();
                        
                        JLabel imageLabel = new JLabel(new ImageIcon(resizedImage));
                        imageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        contentPanel.add(imageLabel);
                        contentPanel.add(Box.createVerticalStrut(20));
                    } catch (Exception ex) {
                        System.err.println("Không thể tải hình ảnh: " + imageName);
                    }
                    
                    // Hiển thị mô tả
                    JLabel descLabel = new JLabel("Mô tả:");
                    descLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentPanel.add(descLabel);
                    
                    JTextArea descArea = new JTextArea(rs.getString("MoTa"));
                    descArea.setWrapStyleWord(true);
                    descArea.setLineWrap(true);
                    descArea.setOpaque(false);
                    descArea.setEditable(false);
                    descArea.setFont(new Font("Arial", Font.PLAIN, 14));
                    descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentPanel.add(descArea);
                    contentPanel.add(Box.createVerticalStrut(20));
                    
                    // Hiển thị hướng dẫn phân loại
                    JLabel guideLabel = new JLabel("Hướng dẫn phân loại:");
                    guideLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    guideLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentPanel.add(guideLabel);
                    
                    JTextArea guideArea = new JTextArea(rs.getString("HdPhanLoai"));
                    guideArea.setWrapStyleWord(true);
                    guideArea.setLineWrap(true);
                    guideArea.setOpaque(false);
                    guideArea.setEditable(false);
                    guideArea.setFont(new Font("Arial", Font.PLAIN, 14));
                    guideArea.setAlignmentX(Component.LEFT_ALIGNMENT);
                    contentPanel.add(guideArea);
                    
                    // Thêm contentPanel vào detailPanel
                    detailPanel.add(contentPanel);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Lỗi khi tải thông tin phân loại rác: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        
        detailPanel.revalidate();
        detailPanel.repaint();
    }

    // Thêm phương thức tính tổng tiền
    private void calculateTotal(JComboBox<String> serviceBox, JTextField weightField, JLabel totalLabel) {
        try {
            String selectedService = (String) serviceBox.getSelectedItem();
            if (selectedService == null || weightField.getText().trim().isEmpty()) {
                totalLabel.setText("0 VNĐ");
                return;
            }

            String[] parts = selectedService.split(" - ");
            String priceStr = parts[2].replaceAll("[^0-9]", "");
            double price = Double.parseDouble(priceStr);
            String weightStr = weightField.getText().trim().replace(".", "").replace(",", ".");
            double weight = Double.parseDouble(weightStr);
            
            double total = price * weight;
            totalLabel.setText(String.format("%,d VNĐ", (long)total));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            totalLabel.setText("0 VNĐ");
        }
    }

    private void showEditContractDialog(String maHopDong, DefaultTableModel contractModel) {
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT NgBatDau, NgKetThuc, MoTa, DiaChiThuGom FROM HopDong WHERE MaHopDong = ? AND TrangThai = 'Chờ duyệt'";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, maHopDong);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                JDialog dialog = new JDialog(this, "Sửa hợp đồng", true);
                dialog.setLayout(new BorderLayout(10, 10));
                dialog.setSize(400, 300);
                dialog.setLocationRelativeTo(this);

                JPanel mainPanel = new JPanel(new GridBagLayout());
                mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);

                // Địa chỉ thu gom
                gbc.gridx = 0;
                gbc.gridy = 0;
                mainPanel.add(new JLabel("Địa chỉ thu gom:"), gbc);

                JTextField addressField = new JTextField(rs.getString("DiaChiThuGom"));
                gbc.gridx = 1;
                mainPanel.add(addressField, gbc);

                // Ngày bắt đầu
                gbc.gridx = 0;
                gbc.gridy = 1;
                mainPanel.add(new JLabel("Ngày bắt đầu:"), gbc);

                JSpinner startDateSpinner = createDateSpinner();
                startDateSpinner.setValue(rs.getDate("NgBatDau"));
                gbc.gridx = 1;
                mainPanel.add(startDateSpinner, gbc);

                // Ngày kết thúc
                gbc.gridx = 0;
                gbc.gridy = 2;
                mainPanel.add(new JLabel("Ngày kết thúc:"), gbc);

                JSpinner endDateSpinner = createDateSpinner();
                endDateSpinner.setValue(rs.getDate("NgKetThuc"));
                gbc.gridx = 1;
                mainPanel.add(endDateSpinner, gbc);

                // Mô tả
                gbc.gridx = 0;
                gbc.gridy = 3;
                mainPanel.add(new JLabel("Mô tả:"), gbc);

                JTextField descField = new JTextField(rs.getString("MoTa"));
                gbc.gridx = 1;
                mainPanel.add(descField, gbc);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton saveButton = new JButton("Lưu");
                JButton cancelButton = new JButton("Hủy");

                saveButton.setBackground(new Color(46, 204, 113));
                saveButton.setForeground(Color.WHITE);
                saveButton.setFocusPainted(false);

                cancelButton.setBackground(new Color(231, 76, 60));
                cancelButton.setForeground(Color.WHITE);
                cancelButton.setFocusPainted(false);

                saveButton.addActionListener(evt -> {
                    try {
                        Date startDate = (Date) startDateSpinner.getValue();
                        Date endDate = (Date) endDateSpinner.getValue();
                        String desc = descField.getText().trim();

                        // Validate dates
                        if (startDate.after(endDate)) {
                            JOptionPane.showMessageDialog(dialog,
                                "Ngày bắt đầu không thể sau ngày kết thúc!",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Calculate duration in days
                        long durationInDays = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
                        String contractType = durationInDays > 180 ? "Dài hạn" : "Ngắn hạn";

                        // Update contract with new type
                        String updateSql = "UPDATE HopDong SET NgBatDau = ?, NgKetThuc = ?, MoTa = ?, DiaChiThuGom = ?, LoaiHopDong = ? " +
                                        "WHERE MaHopDong = ? AND TrangThai = 'Chờ duyệt'";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setDate(1, new java.sql.Date(startDate.getTime()));
                        updateStmt.setDate(2, new java.sql.Date(endDate.getTime()));
                        updateStmt.setString(3, desc);
                        updateStmt.setString(4, addressField.getText().trim());
                        updateStmt.setString(5, contractType);
                        updateStmt.setString(6, maHopDong);

                        int result = updateStmt.executeUpdate();
                        if (result > 0) {
                            JOptionPane.showMessageDialog(dialog,
                                "Cập nhật hợp đồng thành công!",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE);
                            loadContractData(contractModel, "Tất cả");
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                "Không thể cập nhật hợp đồng!",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(dialog,
                            "Lỗi khi cập nhật hợp đồng: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });

                cancelButton.addActionListener(evt -> dialog.dispose());

                buttonPanel.add(saveButton);
                buttonPanel.add(cancelButton);

                dialog.add(mainPanel, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                dialog.setVisible(true);
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải thông tin hợp đồng: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditDetailDialog(String maHopDong, String currentDichVu, 
                                    double currentKhoiLuong, String currentGhiChu, 
                                    DefaultTableModel detailModel) {
        JDialog dialog = new JDialog(this, "Sửa chi tiết hợp đồng", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Chọn dịch vụ
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Chọn dịch vụ:"), gbc);

        JComboBox<String> serviceBox = new JComboBox<>();
        loadServices(serviceBox);
        // Tìm và chọn dịch vụ hiện tại
        for (int i = 0; i < serviceBox.getItemCount(); i++) {
            String item = serviceBox.getItemAt(i);
            if (item.startsWith(currentDichVu)) {
                serviceBox.setSelectedIndex(i);
                break;
            }
        }
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(serviceBox, gbc);

        // Khối lượng
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Khối lượng (kg):"), gbc);

        JTextField weightField = new JTextField(String.format("%.1f", currentKhoiLuong).replace('.', ','));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(weightField, gbc);

        // Thành tiền
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Thành tiền:"), gbc);

        JLabel totalLabel = new JLabel("0 đ");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(totalLabel, gbc);

        // Ghi chú
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        mainPanel.add(new JLabel("Ghi chú:"), gbc);

        JTextField noteField = new JTextField(currentGhiChu);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        mainPanel.add(noteField, gbc);

        // Add listeners for total calculation
        serviceBox.addActionListener(e -> calculateTotal(serviceBox, weightField, totalLabel));
        weightField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                String currentText = weightField.getText();
                if (!((c >= '0' && c <= '9') || c == ',' || c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE)) {
                    e.consume();
                    return;
                }
                if (c == ',' && currentText.contains(",")) {
                    e.consume();
                    return;
                }
            }
            
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                calculateTotal(serviceBox, weightField, totalLabel);
            }
        });

        // Calculate initial total
        calculateTotal(serviceBox, weightField, totalLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (serviceBox.getSelectedItem() == null || 
                    serviceBox.getSelectedItem().toString().equals("-- Chọn dịch vụ --")) {
                    JOptionPane.showMessageDialog(dialog,
                        "Vui lòng chọn dịch vụ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String weightText = weightField.getText().trim().replace(',', '.');
                if (weightText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập khối lượng!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double weight = Double.parseDouble(weightText);
                if (weight <= 0) {
                    JOptionPane.showMessageDialog(dialog,
                        "Khối lượng phải lớn hơn 0!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Connection conn = ConnectionJDBC.getConnection();
                conn.setAutoCommit(false);

                try {
                    String serviceName = serviceBox.getSelectedItem().toString().split(" - ")[0];
                    
                    // Get MaDichVu
                    String findServiceSql = "SELECT MaDichVu FROM DichVu WHERE TenDichVu = ?";
                    PreparedStatement findServiceStmt = conn.prepareStatement(findServiceSql);
                    findServiceStmt.setString(1, serviceName);
                    ResultSet rs = findServiceStmt.executeQuery();
                    
                    if (rs.next()) {
                        int maDichVu = rs.getInt("MaDichVu");
                        
                        // Update chi tiết hợp đồng
                        String updateSql = "UPDATE ChiTietHopDong " +
                                        "SET KhoiLuong = ?, ThanhTien = ?, GhiChu = ? " +
                                        "WHERE MaHopDong = ? AND MaDichVu = ?";
                        PreparedStatement pstmt = conn.prepareStatement(updateSql);
                        
                        pstmt.setDouble(1, weight);
                        pstmt.setDouble(2, Double.parseDouble(totalLabel.getText().replaceAll("[^0-9]", "")));
                        pstmt.setString(3, noteField.getText());
                        pstmt.setString(4, maHopDong);
                        pstmt.setInt(5, maDichVu);
                        
                        int result = pstmt.executeUpdate();
                        
                        if (result > 0) {
                            conn.commit();
                            JOptionPane.showMessageDialog(dialog,
                                "Cập nhật chi tiết hợp đồng thành công!",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE);
                            
                            // Refresh detail table
                            loadContractDetails(maHopDong, detailModel);
                            dialog.dispose();
                        } else {
                            throw new SQLException("Không thể cập nhật chi tiết hợp đồng");
                        }
                    } else {
                        throw new SQLException("Không tìm thấy dịch vụ: " + serviceName);
                    }
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException | NumberFormatException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Lỗi khi cập nhật chi tiết hợp đồng: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
} 