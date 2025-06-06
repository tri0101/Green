// File: GiaoDienDangNhap.java
package doan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.border.EmptyBorder;

public class GiaoDienDangNhap extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JPanel loginPanel;
    private JPanel forgotPasswordPanel;
    private JPanel resetPasswordPanel;
    private JPanel newPasswordPanel;
    private JPanel registerPanel;
    private CardLayout cardLayout;
    private String currentEmail;
    private JTextField emailField;
    private JTextField otpFieldReset;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;

    public GiaoDienDangNhap() {
        setTitle("Đăng nhập hệ thống");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(new Color(50, 202, 50));
                g.setColor(Color.YELLOW);
                addLogo(g, 25, 50);

                g.setFont(new Font("SansSerif", Font.BOLD, 20));
                g.setColor(new Color(255,215,0));
                g.drawString("GREEN CITY", 65, 280);

                g.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g.setColor(Color.WHITE);
                g.drawString("DỊCH VỤ THU GOM RÁC TẠI NHÀ", 40, 305);
            }
        };
        leftPanel.setPreferredSize(new Dimension(250, 400));
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Panel chính bên phải
        JPanel rightPanel = new JPanel();
        cardLayout = new CardLayout();
        rightPanel.setLayout(cardLayout);
        rightPanel.setBackground(new Color(240, 240, 240));

        // Login panel
        loginPanel = new JPanel(null);
        loginPanel.setBackground(new Color(240, 240, 240));

        JLabel titleLabel = new JLabel("ĐĂNG NHẬP");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setBounds(150, 30, 200, 30);
        loginPanel.add(titleLabel);

        JLabel userLabel = new JLabel("Tài khoản");
        userLabel.setBounds(80, 80, 100, 25);
        loginPanel.add(userLabel);

        userField = new JTextField();
        userField.setBounds(80, 105, 250, 30);
        userField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        loginPanel.add(userField);

        JLabel passLabel = new JLabel("Mật khẩu");
        passLabel.setBounds(80, 145, 100, 25);
        loginPanel.add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(80, 170, 250, 30);
        loginPanel.add(passField);

        // Thêm link quên mật khẩu
        JLabel forgotPassLabel = new JLabel("Quên mật khẩu?");
        forgotPassLabel.setBounds(80, 210, 100, 20);
        forgotPassLabel.setForeground(new Color(0, 102, 204));
        forgotPassLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPassLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clearAllFields();
                cardLayout.show(rightPanel, "FORGOT_PASSWORD");
            }
        });
        loginPanel.add(forgotPassLabel);

        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setBounds(80, 240, 250, 35);
        loginButton.setBackground(new Color(20, 22, 58));
        loginButton.setForeground(Color.WHITE);
        loginPanel.add(loginButton);

        JButton registerButton = new JButton("Đăng ký");
        registerButton.setBounds(80, 285, 250, 35);
        registerButton.setBackground(new Color(20, 22, 58));
        registerButton.setForeground(Color.WHITE);
        loginPanel.add(registerButton);

        // Forgot Password Panel
        forgotPasswordPanel = new JPanel(null);
        forgotPasswordPanel.setBackground(new Color(240, 240, 240));

        JLabel forgotTitleLabel = new JLabel("QUÊN MẬT KHẨU");
        forgotTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        forgotTitleLabel.setBounds(120, 30, 200, 30);
        forgotPasswordPanel.add(forgotTitleLabel);

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setBounds(80, 80, 100, 25);
        forgotPasswordPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(80, 105, 250, 30);
        emailField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        forgotPasswordPanel.add(emailField);

        JButton continueButton = new JButton("Tiếp tục");
        continueButton.setBounds(80, 150, 120, 35);
        continueButton.setBackground(new Color(20, 22, 58));
        continueButton.setForeground(Color.WHITE);
        forgotPasswordPanel.add(continueButton);

        JButton backButton = new JButton("Quay lại");
        backButton.setBounds(210, 150, 120, 35);
        backButton.setBackground(new Color(20, 22, 58));
        backButton.setForeground(Color.WHITE);
        forgotPasswordPanel.add(backButton);

        // Reset Password Panel
        resetPasswordPanel = new JPanel(null);
        resetPasswordPanel.setBackground(new Color(240, 240, 240));

        JLabel resetTitleLabel = new JLabel("ĐẶT LẠI MẬT KHẨU");
        resetTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        resetTitleLabel.setBounds(100, 30, 200, 30);
        resetPasswordPanel.add(resetTitleLabel);

        JLabel otpLabelReset = new JLabel("Nhập mã OTP");
        otpLabelReset.setBounds(80, 80, 100, 25);
        resetPasswordPanel.add(otpLabelReset);

        otpFieldReset = new JTextField();
        otpFieldReset.setBounds(80, 105, 250, 30);
        otpFieldReset.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        resetPasswordPanel.add(otpFieldReset);

        JButton verifyButton = new JButton("Xác nhận");
        verifyButton.setBounds(80, 150, 120, 35);
        verifyButton.setBackground(new Color(20, 22, 58));
        verifyButton.setForeground(Color.WHITE);
        resetPasswordPanel.add(verifyButton);

        JButton backToLoginButton = new JButton("Quay lại");
        backToLoginButton.setBounds(210, 150, 120, 35);
        backToLoginButton.setBackground(new Color(20, 22, 58));
        backToLoginButton.setForeground(Color.WHITE);
        resetPasswordPanel.add(backToLoginButton);

        // New Password Panel
        newPasswordPanel = new JPanel(null);
        newPasswordPanel.setBackground(new Color(240, 240, 240));

        JLabel newPassTitleLabel = new JLabel("ĐẶT MẬT KHẨU MỚI");
        newPassTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        newPassTitleLabel.setBounds(100, 30, 200, 30);
        newPasswordPanel.add(newPassTitleLabel);

        JLabel newPassLabel = new JLabel("Mật khẩu mới:");
        newPassLabel.setBounds(80, 80, 100, 25);
        newPasswordPanel.add(newPassLabel);

        newPassField = new JPasswordField();
        newPassField.setBounds(80, 105, 250, 30);
        newPassField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        newPasswordPanel.add(newPassField);

        JLabel confirmPassLabel = new JLabel("Xác nhận mật khẩu:");
        confirmPassLabel.setBounds(80, 145, 150, 25);
        newPasswordPanel.add(confirmPassLabel);

        confirmPassField = new JPasswordField();
        confirmPassField.setBounds(80, 170, 250, 30);
        confirmPassField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        newPasswordPanel.add(confirmPassField);

        JButton saveButton = new JButton("Lưu");
        saveButton.setBounds(80, 220, 120, 35);
        saveButton.setBackground(new Color(20, 22, 58));
        saveButton.setForeground(Color.WHITE);
        newPasswordPanel.add(saveButton);

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBounds(210, 220, 120, 35);
        cancelButton.setBackground(new Color(20, 22, 58));
        cancelButton.setForeground(Color.WHITE);
        newPasswordPanel.add(cancelButton);

        // Register Panel
        registerPanel = new JPanel(new CardLayout());
        registerPanel.setBackground(new Color(240, 240, 240));

        // Step 1 Panel
        JPanel step1Panel = new JPanel(null);
        step1Panel.setBackground(new Color(240, 240, 240));

        JLabel step1TitleLabel = new JLabel("THÔNG TIN CÁ NHÂN");
        step1TitleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        step1TitleLabel.setBounds(120, 30, 250, 30);
        step1Panel.add(step1TitleLabel);

        // Họ tên
        JLabel hoTenLabel = new JLabel("Họ tên:");
        hoTenLabel.setBounds(50, 80, 100, 25);
        step1Panel.add(hoTenLabel);

        JTextField hoTenField = new JTextField();
        hoTenField.setBounds(150, 80, 250, 30);
        hoTenField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        step1Panel.add(hoTenField);

        // Số điện thoại
        JLabel sdtLabel = new JLabel("Số điện thoại:");
        sdtLabel.setBounds(50, 130, 100, 25);
        step1Panel.add(sdtLabel);

        JTextField sdtField = new JTextField();
        sdtField.setBounds(150, 130, 250, 30);
        sdtField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        step1Panel.add(sdtField);

        // Địa chỉ
        JLabel diaChiLabel = new JLabel("Địa chỉ:");
        diaChiLabel.setBounds(50, 180, 100, 25);
        step1Panel.add(diaChiLabel);

        JTextField diaChiField = new JTextField();
        diaChiField.setBounds(150, 180, 250, 30);
        diaChiField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        step1Panel.add(diaChiField);

        // Loại chủ thải
        JLabel loaiChuThaiLabel = new JLabel("Loại chủ thải:");
        loaiChuThaiLabel.setBounds(50, 230, 100, 25);
        step1Panel.add(loaiChuThaiLabel);

        String[] loaiChuThai = {"Cá nhân", "Doanh nghiệp"};
        JComboBox<String> loaiChuThaiCombo = new JComboBox<>(loaiChuThai);
        loaiChuThaiCombo.setBounds(150, 230, 250, 30);
        step1Panel.add(loaiChuThaiCombo);

        // Buttons Step 1
        JButton step1NextButton = new JButton("Tiếp tục");
        step1NextButton.setBounds(150, 290, 120, 35);
        step1NextButton.setBackground(new Color(20, 22, 58));
        step1NextButton.setForeground(Color.WHITE);
        step1Panel.add(step1NextButton);

        JButton step1BackButton = new JButton("Quay lại");
        step1BackButton.setBounds(280, 290, 120, 35);
        step1BackButton.setBackground(new Color(20, 22, 58));
        step1BackButton.setForeground(Color.WHITE);
        step1Panel.add(step1BackButton);

        // Step 2 Panel
        JPanel step2Panel = new JPanel(null);
        step2Panel.setBackground(new Color(240, 240, 240));

        JLabel step2TitleLabel = new JLabel("THÔNG TIN TÀI KHOẢN");
        step2TitleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        step2TitleLabel.setBounds(120, 30, 250, 30);
        step2Panel.add(step2TitleLabel);

        // Email
        JLabel emailRegLabel = new JLabel("Email:");
        emailRegLabel.setBounds(50, 80, 100, 25);
        step2Panel.add(emailRegLabel);

        JTextField emailRegField = new JTextField();
        emailRegField.setBounds(150, 80, 250, 30);
        emailRegField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        step2Panel.add(emailRegField);

        // Username
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setBounds(50, 130, 100, 25);
        step2Panel.add(usernameLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(150, 130, 250, 30);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        step2Panel.add(usernameField);

        // Password
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setBounds(50, 180, 100, 25);
        step2Panel.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 180, 250, 30);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        step2Panel.add(passwordField);

        // Confirm Password
        JLabel confirmPasswordLabel = new JLabel("Xác nhận ");
        confirmPasswordLabel.setBounds(50, 230, 150, 25);
        step2Panel.add(confirmPasswordLabel);

        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(150, 230, 250, 30);
        confirmPasswordField.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        step2Panel.add(confirmPasswordField);

        // Buttons Step 2
        JButton step2NextButton = new JButton("Tiếp tục");
        step2NextButton.setBounds(150, 290, 120, 35);
        step2NextButton.setBackground(new Color(20, 22, 58));
        step2NextButton.setForeground(Color.WHITE);
        step2Panel.add(step2NextButton);

        JButton step2BackButton = new JButton("Quay lại");
        step2BackButton.setBounds(280, 290, 120, 35);
        step2BackButton.setBackground(new Color(20, 22, 58));
        step2BackButton.setForeground(Color.WHITE);
        step2Panel.add(step2BackButton);

        // Step 3 Panel
        JPanel step3Panel = new JPanel(null);
        step3Panel.setBackground(new Color(240, 240, 240));

        JLabel step3TitleLabel = new JLabel("XÁC NHẬN OTP");
        step3TitleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        step3TitleLabel.setBounds(150, 30, 250, 30);
        step3Panel.add(step3TitleLabel);

        JLabel otpLabel = new JLabel("Nhập mã OTP:");
        otpLabel.setBounds(50, 80, 100, 25);
        step3Panel.add(otpLabel);

        JTextField otpFieldRegister = new JTextField();
        otpFieldRegister.setBounds(150, 80, 250, 30);
        otpFieldRegister.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        step3Panel.add(otpFieldRegister);

        // Buttons Step 3
        JButton step3NextButton = new JButton("Tiếp tục");
        step3NextButton.setBounds(150, 290, 120, 35);
        step3NextButton.setBackground(new Color(20, 22, 58));
        step3NextButton.setForeground(Color.WHITE);
        step3Panel.add(step3NextButton);

        JButton step3BackButton = new JButton("Quay lại");
        step3BackButton.setBounds(280, 290, 120, 35);
        step3BackButton.setBackground(new Color(20, 22, 58));
        step3BackButton.setForeground(Color.WHITE);
        step3Panel.add(step3BackButton);

        // Add panels to card layout
        registerPanel.add(step1Panel, "STEP1");
        registerPanel.add(step2Panel, "STEP2");
        registerPanel.add(step3Panel, "STEP3");

        // Add action listeners
        step1NextButton.addActionListener(e -> {
            String hoTen = hoTenField.getText().trim();
            String sdt = sdtField.getText().trim();
            String diaChi = diaChiField.getText().trim();
            String loaiChuThaiSelected = (String) loaiChuThaiCombo.getSelectedItem();

            if (hoTen.isEmpty() || sdt.isEmpty() || diaChi.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng điền đầy đủ thông tin!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = ConnectionJDBC.getConnection();
                String checkPhoneSql = "SELECT COUNT(*) FROM ChuThai WHERE Sdt = ?";
                PreparedStatement checkPhoneStmt = conn.prepareStatement(checkPhoneSql);
                checkPhoneStmt.setString(1, sdt);
                ResultSet phoneRs = checkPhoneStmt.executeQuery();
                phoneRs.next();
                if (phoneRs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Số điện thoại đã được đăng ký!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                CardLayout cl = (CardLayout) registerPanel.getLayout();
                cl.show(registerPanel, "STEP2");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        step1BackButton.addActionListener(e -> {
            setSize(700, 400);
            clearRegisterFields(hoTenField, diaChiField, sdtField, emailRegField, 
                              usernameField, passwordField, confirmPasswordField);
            cardLayout.show(rightPanel, "LOGIN");
        });

        step2NextButton.addActionListener(e -> {
            String email = emailRegField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng điền đầy đủ thông tin!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate email format
            if (!email.matches("^[A-Za-z0-9+_.-]+@(email\\.com|gmail\\.com)$")) {
                JOptionPane.showMessageDialog(this,
                    "Email không hợp lệ! Email phải kết thúc bằng email.com hoặc gmail.com",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                    "Mật khẩu không khớp!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = ConnectionJDBC.getConnection();
                
                // Check for duplicate email
                String checkEmailSql = "SELECT COUNT(*) FROM ChuThai WHERE Email = ?";
                PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailSql);
                checkEmailStmt.setString(1, email);
                ResultSet emailRs = checkEmailStmt.executeQuery();
                emailRs.next();
                if (emailRs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Email đã được sử dụng!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check for duplicate username
                String checkUsernameSql = "SELECT COUNT(*) FROM ChuThai WHERE Username = ?";
                PreparedStatement checkUsernameStmt = conn.prepareStatement(checkUsernameSql);
                checkUsernameStmt.setString(1, username);
                ResultSet usernameRs = checkUsernameStmt.executeQuery();
                usernameRs.next();
                if (usernameRs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Tên đăng nhập đã tồn tại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                CardLayout cl = (CardLayout) registerPanel.getLayout();
                cl.show(registerPanel, "STEP3");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        step2BackButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) registerPanel.getLayout();
            cl.show(registerPanel, "STEP1");
        });

        step3NextButton.addActionListener(e -> {
            String otp = otpFieldRegister.getText().trim();
            if (otp.equals("123456")) {
                try {
                    Connection conn = ConnectionJDBC.getConnection();
                    
                    // Get next MaChuThai
                    String getNextIdSql = "SELECT MAX(MaChuThai) + 1 FROM ChuThai";
                    PreparedStatement getNextIdStmt = conn.prepareStatement(getNextIdSql);
                    ResultSet nextIdRs = getNextIdStmt.executeQuery();
                    nextIdRs.next();
                    int nextId = nextIdRs.getInt(1);

                    // Insert new user
                    String insertSql = "INSERT INTO ChuThai (MaChuThai, HoTen, DiaChi, Sdt, Email, LoaiChuThai, Username, Password) " +
                                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                    insertStmt.setInt(1, nextId);
                    insertStmt.setString(2, hoTenField.getText().trim());
                    insertStmt.setString(3, diaChiField.getText().trim());
                    insertStmt.setString(4, sdtField.getText().trim());
                    insertStmt.setString(5, emailRegField.getText().trim());
                    insertStmt.setString(6, (String) loaiChuThaiCombo.getSelectedItem());
                    insertStmt.setString(7, usernameField.getText().trim());
                    insertStmt.setString(8, new String(passwordField.getPassword()));
                    insertStmt.executeUpdate();

                    JOptionPane.showMessageDialog(this,
                        "Tạo tài khoản thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);

                    setSize(700, 400);
                    clearRegisterFields(hoTenField, diaChiField, sdtField, emailRegField, 
                                      usernameField, passwordField, confirmPasswordField);
                    cardLayout.show(rightPanel, "LOGIN");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Lỗi khi tạo tài khoản: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "OTP không đúng!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        step3BackButton.addActionListener(e -> {
            CardLayout cl = (CardLayout) registerPanel.getLayout();
            cl.show(registerPanel, "STEP2");
        });

        registerButton.addActionListener(e -> {
            setSize(700, 400);
            CardLayout cl = (CardLayout) registerPanel.getLayout();
            cl.show(registerPanel, "STEP1");
            cardLayout.show(rightPanel, "REGISTER");
        });

        // Add panels to card layout
        rightPanel.add(loginPanel, "LOGIN");
        rightPanel.add(forgotPasswordPanel, "FORGOT_PASSWORD");
        rightPanel.add(resetPasswordPanel, "RESET_PASSWORD");
        rightPanel.add(newPasswordPanel, "NEW_PASSWORD");
        rightPanel.add(registerPanel, "REGISTER");

        mainPanel.add(rightPanel, BorderLayout.CENTER);
        add(mainPanel);

        loginButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = ConnectionJDBC.getConnection();
                
                // Kiểm tra prefix của username
                if (username.startsWith("ct_")) {
                    // Kiểm tra đăng nhập chủ thải
                    String sql = "SELECT MaChuThai, HoTen FROM ChuThai WHERE Username = ? AND Password = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    ResultSet rs = pstmt.executeQuery();
                    
                    if (rs.next()) {
                        String maKh = rs.getString("MaChuThai");
                        String tenKh = rs.getString("HoTen");
                        new ChuThaiUI(tenKh, maKh).setVisible(true);
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Tên đăng nhập hoặc mật khẩu không đúng!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else if (username.startsWith("nvtg_")) {
                    // Kiểm tra đăng nhập nhân viên thu gom
                    String sql = "SELECT MaNvtg, TenNvtg, MaTruongNhom FROM NhanVienThuGom WHERE Username = ? AND Password = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    ResultSet rs = pstmt.executeQuery();
                    
                    if (rs.next()) {
                        String ma = rs.getString("MaNvtg");
                        String ten = rs.getString("TenNvtg");
                        Integer maTruongNhom = rs.getInt("MaTruongNhom");
                        
                        if (maTruongNhom == 0) { // Nếu là trưởng nhóm
                            new TruongNhomUI(ma, ten).setVisible(true);
                        } else { // Nếu là nhân viên thu gom thường
                            new NvtgUI(ma, ten).setVisible(true);
                        }
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Tên đăng nhập hoặc mật khẩu không đúng!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else if (username.startsWith("nvdp")) {
                    // Kiểm tra đăng nhập nhân viên điều phối
                    String dbUsername = username;
                    if (username.endsWith("_demo")) {
                        // Remove "_demo" suffix for database check
                        dbUsername = username.substring(0, username.length() - 5);
                    }
                    
                    String sql = "SELECT MaNvdp, TenNvdp FROM NhanVienDieuPhoi WHERE Username = ? AND Password = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, dbUsername);
                    pstmt.setString(2, password);
                    ResultSet rs = pstmt.executeQuery();
                    
                    if (rs.next()) {
                        String ma = rs.getString("MaNvdp");
                        String ten = rs.getString("TenNvdp");
                        if (username.endsWith("_demo")) {
                            new XuLyDongThoiUI(ma, ten).setVisible(true);
                        } else {
                            new QuanLyRacThaiUI(ma, ten).setVisible(true);
                        }
                        this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Tên đăng nhập hoặc mật khẩu không đúng!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Tên đăng nhập không hợp lệ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Thêm action listener cho nút tiếp tục trong forgotPasswordPanel
        continueButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập email!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "SELECT COUNT(*) FROM ChuThai WHERE Email = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, email);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                
                if (rs.getInt(1) > 0) {
                    currentEmail = email; // Lưu email để sử dụng ở bước tiếp theo
                    cardLayout.show(rightPanel, "RESET_PASSWORD");
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Email chưa được đăng ký!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Thêm action listener cho nút quay lại trong forgotPasswordPanel
        backButton.addActionListener(e -> {
            emailField.setText("");
            cardLayout.show(rightPanel, "LOGIN");
        });

        // Thêm action listener cho nút xác nhận trong resetPasswordPanel
        verifyButton.addActionListener(e -> {
            String otp = otpFieldReset.getText().trim();
            if (otp.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập mã OTP!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (otp.equals("123456")) {
                cardLayout.show(rightPanel, "NEW_PASSWORD");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Mã OTP không đúng!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Thêm action listener cho nút quay lại trong resetPasswordPanel
        backToLoginButton.addActionListener(e -> {
            otpFieldReset.setText("");
            cardLayout.show(rightPanel, "FORGOT_PASSWORD");
        });

        // Thêm action listener cho nút lưu trong newPasswordPanel
        saveButton.addActionListener(e -> {
            String newPassword = new String(newPassField.getPassword());
            String confirmPassword = new String(confirmPassField.getPassword());

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this,
                    "Mật khẩu không khớp!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = ConnectionJDBC.getConnection();
                // Lấy mật khẩu cũ từ database
                String getOldPassSql = "SELECT Password FROM ChuThai WHERE Email = ?";
                PreparedStatement getOldPassStmt = conn.prepareStatement(getOldPassSql);
                getOldPassStmt.setString(1, currentEmail);
                ResultSet rs = getOldPassStmt.executeQuery();
                if (rs.next()) {
                    String oldPassword = rs.getString("Password");
                    if (newPassword.equals(oldPassword)) {
                        JOptionPane.showMessageDialog(this,
                            "Mật khẩu này đã được đặt trước đó!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                String sql = "UPDATE ChuThai SET Password = ? WHERE Email = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newPassword);
                pstmt.setString(2, currentEmail);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                    "Đổi mật khẩu thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);

                // Reset các trường và quay về màn hình đăng nhập
                clearAllFields();
                cardLayout.show(rightPanel, "LOGIN");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Lỗi khi đổi mật khẩu: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Thêm action listener cho nút hủy trong newPasswordPanel
        cancelButton.addActionListener(e -> {
            clearAllFields();
            cardLayout.show(rightPanel, "LOGIN");
        });
    }

    private void clearAllFields() {
        emailField.setText("");
        otpFieldReset.setText("");
        newPassField.setText("");
        confirmPassField.setText("");
    }

    private void clearRegisterFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    public void addLogo(Graphics g, int x, int y) {
        URL url = GiaoDienDangNhap.class.getResource("/Image/logo.png");
        if (url == null) {
            System.err.println("Logo image file not found!");
            return;
        }
        ImageIcon logoIcon = new ImageIcon(url);
        Image logoImage = logoIcon.getImage();
        g.drawImage(logoImage, x, y, 200, 200, null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GiaoDienDangNhap().setVisible(true));
    }
}
