package doan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import java.text.SimpleDateFormat;
import java.util.Date;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javax.swing.SpinnerDateModel;
import java.util.Calendar;
import java.util.ArrayList;
import java.awt.CardLayout;
import javax.swing.table.TableColumnModel;
import java.sql.Statement;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.Types;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import java.io.FileOutputStream;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
// Thêm imports cho JFreeChart
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.axis.NumberAxis;
import java.text.DecimalFormat;
// Thêm imports cho JDateChooser
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.border.TitledBorder;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;

public class QuanLyRacThaiUI extends JFrame {

    private JPanel sideBar, mainPanel, headerPanel;
    private CardLayout cardLayout;
    private Color primaryColor = new Color(25, 42, 86);    // Xanh đậm
    private Color secondaryColor = new Color(46, 64, 83);  // Xanh nhạt
    private Color accentColor = new Color(46, 204, 113);   // Xanh lá
    private Color textColor = Color.WHITE;
    private Font titleFont = new Font("Arial", Font.BOLD, 24);
    private Font normalFont = new Font("Arial", Font.PLAIN, 14);
    private JTable table; // Add this line
    private String maNvdp;
    private String tenNvdp;
    private int currentMonth = 1; // Mặc định là tháng 1
    private int currentYear = Calendar.getInstance().get(Calendar.YEAR); // Năm hiện tại
    private JDateChooser dateChooser; // Add date chooser as class field

    public QuanLyRacThaiUI() {
        this(null, null);
    }

    public QuanLyRacThaiUI(String maNvdp, String tenNvdp) {
        this.maNvdp = maNvdp;
        this.tenNvdp = tenNvdp;
        setTitle("Hệ thống Quản lý Thu gom Rác thải");
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
        JLabel titleLabel = new JLabel("QUẢN LÝ THU GOM RÁC THẢI");
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

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Đổi mật khẩu", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Mật khẩu hiện tại:"));
        JPasswordField currentPassField = new JPasswordField();
        formPanel.add(currentPassField);

        formPanel.add(new JLabel("Mật khẩu mới:"));
        JPasswordField newPassField = new JPasswordField();
        formPanel.add(newPassField);

        formPanel.add(new JLabel("Xác nhận mật khẩu mới:"));
        JPasswordField confirmPassField = new JPasswordField();
        formPanel.add(confirmPassField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        styleButton(saveButton);
        styleButton(cancelButton);

        saveButton.addActionListener(e -> {
            String currentPass = new String(currentPassField.getPassword());
            String newPass = new String(newPassField.getPassword());
            String confirmPass = new String(confirmPassField.getPassword());

            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(dialog, "Mật khẩu mới không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Connection conn = ConnectionJDBC.getConnection();
                // Kiểm tra mật khẩu hiện tại
                String checkSql = "SELECT * FROM NhanVienDieuPhoi WHERE MaNvdp = ? AND Password = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, maNvdp);
                checkStmt.setString(2, currentPass);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Mật khẩu hiện tại không đúng!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Cập nhật mật khẩu mới
                String updateSql = "UPDATE NhanVienDieuPhoi SET Password = ? WHERE MaNvdp = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, newPass);
                updateStmt.setString(2, maNvdp);
                updateStmt.executeUpdate();

                JOptionPane.showMessageDialog(dialog,
                        "Đổi mật khẩu thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi khi cập nhật mật khẩu: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void createSideBar() {
        sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        sideBar.setBackground(secondaryColor);
        sideBar.setPreferredSize(new Dimension(250, 0));
        sideBar.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Các nút menu
        String[] menuItems = {
            "Tổng quan",
//            "Quản lý nhân viên điều phối",
            "Chủ thải",
            "Đơn vị thu gom",
            "Nhân viên thu gom",
            "Lịch thu gom",
            "Tuyến thu gom",
            "Hợp đồng",
            "Hóa đơn",
            "Phân công",
            "Chấm công",
            "Phản ánh",
            "Yêu cầu đặt lịch",
            "Quận",
            "Dịch vụ",
            "Thống kê phân công",
            "Thống kê báo cáo"
        };

        for (String item : menuItems) {
            JButton button = createMenuButton(item);
            sideBar.add(button);
            sideBar.add(Box.createVerticalStrut(5));
        }
    }

    private void createMainPanel() {
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        mainPanel.setBackground(Color.WHITE);

        // Thêm các panel chức năng
        mainPanel.add(createTongQuanPanel(), "Tổng quan");
//        mainPanel.add(createNvdpPanel(), "Quản lý nhân viên điều phối");
        mainPanel.add(createChuThaiPanel(), "Chủ thải"); 
        mainPanel.add(createDvtgPanel(), "Đơn vị thu gom");
        mainPanel.add(createNvtgPanel(), "Nhân viên thu gom");
        mainPanel.add(createLichThuGomPanel(), "Lịch thu gom");
        mainPanel.add(createTuyenThuGomPanel(), "Tuyến thu gom");
        mainPanel.add(createHopDongPanel(), "Hợp đồng");
        mainPanel.add(createHoaDonPanel(), "Hóa đơn");
        mainPanel.add(createPhanCongPanel(), "Phân công");
        mainPanel.add(createChamCongPanel(), "Chấm công");
        mainPanel.add(createPhanAnhPanel(), "Phản ánh");
        mainPanel.add(createYeuCauDatLichPanel(), "Yêu cầu đặt lịch");
        mainPanel.add(createQuanPanel(), "Quận");
        mainPanel.add(new DichVuPanel(), "Dịch vụ");
        mainPanel.add(createThongKePhanCongPanel(), "Thống kê phân công");
        mainPanel.add(createThongKePanel(), "Thống kê báo cáo");

        // Hiển thị panel Tổng quan mặc định
        cardLayout.show(mainPanel, "Tổng quan");
    }

    private JPanel createThongKePhanCongPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý phân công");
        titleLabel.setFont(titleFont);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        JButton addButton = new JButton("Thêm");
//        JButton deleteButton = new JButton("Xóa");
//        JButton editButton = new JButton("Sửa");
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton exportButton = new JButton("Xuất excel");

//        styleButton(addButton);
//        styleButton(deleteButton);
//        styleButton(editButton);
        styleButton(searchButton);
        styleButton(refreshButton);
        styleButton(exportButton);

        buttonPanel.add(searchButton);
//        buttonPanel.add(deleteButton);
//        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);
//        buttonPanel.add(addButton);
        buttonPanel.add(exportButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = {
            "Mã nhân viên thu gom",
            "Số lịch đã nhận",};

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load dữ liệu từ database
        loadThongKePhanCongData(model);

        // Thêm action listener cho các nút
//        addButton.addActionListener(e -> showAddPhanCongDialog(model));
//        deleteButton.addActionListener(e -> {
//            int selectedRow = table.getSelectedRow();
//            showDeletePhanCongDialog(model, selectedRow);
//        });
//        editButton.addActionListener(e -> {
//            int selectedRow = table.getSelectedRow();
//            showEditPhanCongDialog(model, selectedRow);
//        });
//              
        searchButton.addActionListener(e -> showSearchThongKePhanCongDialog(model));
        refreshButton.addActionListener(e -> loadThongKePhanCongData(model));
        exportButton.addActionListener(e -> exportThongKePhanCongExcel(model));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void showSearchThongKePhanCongDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm thống kê phân công", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 210);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Combo box tiêu chí tìm kiếm
        String[] searchCriteria = {
            "Mã nhân viên thu gom",
            "Số lịch đã nhận"
        };
        JComboBox<String> criteriaCombo = new JComboBox<>(searchCriteria);
        criteriaCombo.setPreferredSize(new Dimension(150, 35));
        formPanel.add(criteriaCombo);

        // Panel cho input text
        JPanel valuePanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(150, 35));
        valuePanel.add(searchField, BorderLayout.CENTER);

        // Panel chứa input
        JPanel inputPanel = new JPanel(new CardLayout());
        inputPanel.add(valuePanel, "text");
        formPanel.add(inputPanel);

        // Xử lý chuyển đổi input nếu có thêm loại khác (hiện tại chỉ dùng text)
        criteriaCombo.addActionListener(e -> {
            CardLayout cl = (CardLayout) inputPanel.getLayout();
            cl.show(inputPanel, "text");
        });

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = new JButton("Tìm kiếm");
        JButton cancelButton = new JButton("Hủy");
        styleButton(searchButton);
        styleButton(cancelButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(searchButton);

        // Sự kiện tìm kiếm
        searchButton.addActionListener(e -> {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                StringBuilder sql = new StringBuilder("SELECT * FROM ThongKePhanCong WHERE 1=1");
                List<Object> params = new ArrayList<>();

                String selected = (String) criteriaCombo.getSelectedItem();
                switch (selected) {
                    case "Mã nhân viên thu gom":
                        sql.append(" AND MaNvtg = ?");
                        params.add(Integer.parseInt(searchField.getText().trim()));
                        break;
                    case "Số tuyến đã nhận":
                        sql.append(" AND SoTuyenDaNhan = ?");
                        params.add(Integer.parseInt(searchField.getText().trim()));
                        break;
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    ResultSet rs = pstmt.executeQuery();
                    model.setRowCount(0); // Xóa dữ liệu cũ

                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("MaNvtg"),
                            rs.getInt("SoTuyenDaNhan")
                        });
                    }

                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập số hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi truy vấn: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadThongKePhanCongData(DefaultTableModel model) {
        model.setRowCount(0); // Xóa dữ liệu cũ

        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MaNvtg,SoTuyenDaNhan FROM ThongKePhanCong ";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
//                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("MaNvtg"),
                        rs.getInt("SoTuyenDaNhan"),};
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi tải dữ liệu phản ánh: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(230, 40));
        button.setFont(normalFont);
        button.setForeground(textColor);
        button.setBackground(secondaryColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);

        // Add icon for "Quản lý nhân viên thu gom" button
        if (text.equals("Nhân viên thu gom")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/nvtg.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về đúng kích thước mong muốn
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển màu đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu là màu gần đen (giá trị RGB thấp), đổi sang trắng
                        if (color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 50 && color.getAlpha() > 0) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (text.equals("Phản ánh")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/compl.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về đúng kích thước mong muốn
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển màu đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu là màu gần đen (giá trị RGB thấp), đổi sang trắng
                        if (color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 50 && color.getAlpha() > 0) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (text.equals("Tuyến thu gom")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/tuyen.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về kích thước mới
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển các pixel màu gần đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu pixel là đen hoặc gần đen, chuyển sang trắng
                        if (color.getAlpha() > 0 && color.getRed() < 60 && color.getGreen() < 60 && color.getBlue() < 60) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (text.equals("Thống kê phân công")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/ess.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về kích thước mới
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển các pixel màu gần đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu pixel là đen hoặc gần đen, chuyển sang trắng
                        if (color.getAlpha() > 0 && color.getRed() < 60 && color.getGreen() < 60 && color.getBlue() < 60) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (text.equals("Hợp đồng")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/hopdong.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về đúng kích thước mong muốn
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển màu đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu là màu gần đen (giá trị RGB thấp), đổi sang trắng
                        if (color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 50 && color.getAlpha() > 0) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (text.equals("Hóa đơn")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/hoadon.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về đúng kích thước mong muốn
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển màu đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu là màu gần đen (giá trị RGB thấp), đổi sang trắng
                        if (color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 50 && color.getAlpha() > 0) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (text.equals("Chủ thải")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/customer.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về đúng kích thước mong muốn
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển màu đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu là màu gần đen (giá trị RGB thấp), đổi sang trắng
                        if (color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 50 && color.getAlpha() > 0) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (text.equals("Đơn vị thu gom")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/donvithugom.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về đúng kích thước mong muốn
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển màu đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu là màu gần đen (giá trị RGB thấp), đổi sang trắng
                        if (color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 50 && color.getAlpha() > 0) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (text.equals("Chấm công")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/chamcong.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về đúng kích thước mong muốn
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển màu đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu là màu gần đen (giá trị RGB thấp), đổi sang trắng
                        if (color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 50 && color.getAlpha() > 0) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (text.equals("Phân công")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/phanc.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về đúng kích thước mong muốn
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển màu đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu là màu gần đen (giá trị RGB thấp), đổi sang trắng
                        if (color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 50 && color.getAlpha() > 0) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (text.equals("Yêu cầu đặt lịch")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/booking.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về đúng kích thước mong muốn
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển màu đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu là màu gần đen (giá trị RGB thấp), đổi sang trắng
                        if (color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 50 && color.getAlpha() > 0) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (text.equals("Quận")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/qq.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về đúng kích thước mong muốn
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển màu đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu là màu gần đen (giá trị RGB thấp), đổi sang trắng
                        if (color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 50 && color.getAlpha() > 0) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (text.equals("Lịch thu gom")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/cld.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về đúng kích thước mong muốn
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();

                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển màu đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu là màu gần đen (giá trị RGB thấp), đổi sang trắng
                        if (color.getRed() < 50 && color.getGreen() < 50 && color.getBlue() < 50 && color.getAlpha() > 0) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (text.equals("Dịch vụ")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/binn.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về kích thước mới
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển các pixel màu gần đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu pixel là đen hoặc gần đen, chuyển sang trắng
                        if (color.getAlpha() > 0 && color.getRed() < 60 && color.getGreen() < 60 && color.getBlue() < 60) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (text.equals("Thống kê báo cáo")) {
            try {
                BufferedImage originalImage = ImageIO.read(new File("src/Icon/statt.png"));

                int width = 25;
                int height = 25;

                // Resize ảnh gốc về kích thước mới
                BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = resizedImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.drawImage(originalImage, 0, 0, width, height, null);
                g2d.dispose();

                // Chuyển các pixel màu gần đen sang trắng
                for (int y = 0; y < resizedImage.getHeight(); y++) {
                    for (int x = 0; x < resizedImage.getWidth(); x++) {
                        int rgba = resizedImage.getRGB(x, y);
                        Color color = new Color(rgba, true);

                        // Nếu pixel là đen hoặc gần đen, chuyển sang trắng
                        if (color.getAlpha() > 0 && color.getRed() < 60 && color.getGreen() < 60 && color.getBlue() < 60) {
                            resizedImage.setRGB(x, y, new Color(255, 255, 255, color.getAlpha()).getRGB());
                        }
                    }
                }

                button.setIcon(new ImageIcon(resizedImage));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        button.addActionListener(e -> cardLayout.show(mainPanel, text));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(primaryColor);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(secondaryColor);
            }
        });
        return button;
    }

    private void styleButton(JButton button) {
        button.setBackground(accentColor);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
    }

    private JPanel createTongQuanPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Tổng quan hệ thống", JLabel.LEFT);
        titleLabel.setFont(titleFont);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel thống kê với 2 dòng 3 cột
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBackground(Color.WHITE);

        int tongChuThai = getCount("SELECT COUNT(*) FROM ChuThai");
        int tongHopDong = getCount("SELECT COUNT(*) FROM HopDong");
        double tongDoanhThu = getDoanhThuThangHienTai();

        // Thêm 3 card vào dòng 1
        addStatCard(statsPanel, "Tổng số chủ thải", String.valueOf(tongChuThai), new Color(41, 128, 185));
        addStatCard(statsPanel, "Tổng số hợp đồng", String.valueOf(tongHopDong), new Color(39, 174, 96));
        addStatCard(statsPanel, "Tổng doanh thu tháng", String.format("%,.2f VND", tongDoanhThu), new Color(243, 156, 18));

        // Thêm 3 panel trống vào dòng 2
        for (int i = 0; i < 3; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(Color.WHITE);
            statsPanel.add(emptyPanel);
        }

        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }

    private int getCount(String query) {
        try (Connection conn = ConnectionJDBC.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private double getDoanhThuThangHienTai() {
        String query = "SELECT SUM(SoTien) "
                + "FROM HoaDon "
                + "WHERE TinhTrang = 'Đã thanh toán' "
                + "AND EXTRACT(MONTH FROM NgLap) = ? "
                + "AND EXTRACT(YEAR FROM NgLap) = ?";

        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        try (Connection conn = ConnectionJDBC.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, currentMonth);
            stmt.setInt(2, currentYear);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private void addStatCard(JPanel parent, String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(200, 200)); // Đặt kích thước cố định cho card

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        parent.add(card);
    }

    private JPanel createNhanVienPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý nhân viên");
        titleLabel.setFont(titleFont);

        JButton addButton = new JButton("Thêm nhân viên mới");
        styleButton(addButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);

        // Table
        String[] columns = {"Mã NV", "Họ tên", "Chức vụ", "SĐT", "Email", "Trạng thái"};
        Object[][] data = {};

        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTuyenThuGomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý tuyến thu gom");
        titleLabel.setFont(titleFont);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Thêm");
        JButton deleteButton = new JButton("Xóa");
        JButton editButton = new JButton("Sửa");
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton exportButton = new JButton("Xuất excel");
        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(editButton);
        styleButton(searchButton);
        styleButton(refreshButton);
        styleButton(exportButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(exportButton);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = {
            "Mã tuyến",
            "Mã đơn vị",
            "Tên tuyến",
            "Khu vực"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load dữ liệu từ database
        loadTuyenThuGomData(model);

        // Thêm action listener cho các nút
        addButton.addActionListener(e -> showAddTuyenThuGomDialog(model));
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showDeleteTuyenThuGomDialog(model, selectedRow);
        });
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showEditTuyenThuGomDialog(model, selectedRow);
        });
        searchButton.addActionListener(e -> showSearchTuyenThuGomDialog(model));
        refreshButton.addActionListener(e -> loadTuyenThuGomData(model));
        exportButton.addActionListener(e -> exportTuyenThuGomExcel(model));
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ... existing code ...
    private void showAddTuyenThuGomDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Thêm tuyến thu gom mới", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Mã đơn vị:"));
        JTextField maDvField = new JTextField();
        formPanel.add(maDvField);

        formPanel.add(new JLabel("Tên tuyến:"));
        JTextField tenTuyenField = new JTextField();
        formPanel.add(tenTuyenField);

        formPanel.add(new JLabel("Khu vực:"));
        JComboBox<String> khuVucComboBox = new JComboBox<>();
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MaQuan, TenQuan FROM Quan ORDER BY MaQuan";
            try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String item = rs.getInt("MaQuan") + " - " + rs.getString("TenQuan");
                    khuVucComboBox.addItem(item);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi khi tải danh sách quận: " + ex.getMessage());
        }
        formPanel.add(khuVucComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        styleButton(saveButton);
        styleButton(cancelButton);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (maDvField.getText().trim().isEmpty()
                        || tenTuyenField.getText().trim().isEmpty()
                        || khuVucComboBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                // Insert into database
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "INSERT INTO TuyenDuongThuGom (MaDv, TenTuyen, KhuVuc) VALUES (?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, Integer.parseInt(maDvField.getText().trim()));
                    pstmt.setString(2, tenTuyenField.getText().trim());
                    // Lấy mã quận từ combo box (phần trước dấu " - ")
                    String selectedKhuVuc = (String) khuVucComboBox.getSelectedItem();
                    int maQuan = Integer.parseInt(selectedKhuVuc.split(" - ")[0]);
                    pstmt.setInt(3, maQuan);

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Thêm tuyến thu gom thành công!");
                    loadTuyenThuGomData(model);
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Mã đơn vị không hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm tuyến thu gom: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
// ... existing code ...

    private void showDeleteTuyenThuGomDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tuyến thu gom muốn xóa!");
            return;
        }
        String maTuyen = table.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            Connection conn = ConnectionJDBC.getConnection();
            // Kiểm tra xem tuyến có tồn tại không
            String checkSql = "SELECT COUNT(*) FROM TuyenDuongThuGom WHERE MaTuyen = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, Integer.parseInt(maTuyen.trim()));
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy tuyến thu gom với mã " + maTuyen);
                    return;
                }
            }
            // Thực hiện xóa
            String deleteSql = "DELETE FROM TuyenDuongThuGom WHERE MaTuyen = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setInt(1, Integer.parseInt(maTuyen.trim()));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Xóa tuyến thu gom thành công!");
                loadTuyenThuGomData(model);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã tuyến không hợp lệ!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xóa tuyến thu gom: " + ex.getMessage());
        }
    }

    // ... existing code ...
    private void showEditTuyenThuGomDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tuyến thu gom muốn sửa!");
            return;
        }

        // Lấy dữ liệu từ model
        int maTuyen = (int) model.getValueAt(selectedRow, 0);
        int maDv = (int) model.getValueAt(selectedRow, 1);
        String tenTuyen = (String) model.getValueAt(selectedRow, 2);
        int khuVuc = (int) model.getValueAt(selectedRow, 3);

        // Create edit dialog
        JDialog dialog = new JDialog(this, "Sửa tuyến thu gom", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Mã đơn vị
        formPanel.add(new JLabel("Mã đơn vị:"));
        JTextField maDvField = new JTextField(String.valueOf(maDv));
        formPanel.add(maDvField);

        // Tên tuyến
        formPanel.add(new JLabel("Tên tuyến:"));
        JTextField tenTuyenField = new JTextField(tenTuyen);
        formPanel.add(tenTuyenField);

        // Khu vực - ComboBox cho MaQuan
        formPanel.add(new JLabel("Khu vực:"));
        JComboBox<String> khuVucComboBox = new JComboBox<>();
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MaQuan, TenQuan FROM Quan ORDER BY MaQuan";
            try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String item = rs.getInt("MaQuan") + " - " + rs.getString("TenQuan");
                    khuVucComboBox.addItem(item);
                    // Nếu là khu vực hiện tại của tuyến, chọn nó
                    if (rs.getInt("MaQuan") == khuVuc) {
                        khuVucComboBox.setSelectedItem(item);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi khi tải danh sách quận: " + ex.getMessage());
        }
        formPanel.add(khuVucComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        styleButton(saveButton);
        styleButton(cancelButton);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (maDvField.getText().trim().isEmpty()
                        || tenTuyenField.getText().trim().isEmpty()
                        || khuVucComboBox.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                // Update database
                String updateSql = "UPDATE TuyenDuongThuGom SET MaDv = ?, TenTuyen = ?, KhuVuc = ? WHERE MaTuyen = ?";

                try (Connection conn = ConnectionJDBC.getConnection(); PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

                    updateStmt.setInt(1, Integer.parseInt(maDvField.getText().trim()));
                    updateStmt.setString(2, tenTuyenField.getText().trim());
                    // Lấy mã quận từ combo box (phần trước dấu " - ")
                    String selectedKhuVuc = (String) khuVucComboBox.getSelectedItem();
                    int maQuan = Integer.parseInt(selectedKhuVuc.split(" - ")[0]);
                    updateStmt.setInt(3, maQuan);
                    updateStmt.setInt(4, maTuyen);

                    updateStmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Cập nhật tuyến thu gom thành công!");
                    loadTuyenThuGomData(model);
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Giá trị không hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật tuyến thu gom: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
// ... existing code ...

    private void loadLichThuGomData(DefaultTableModel model) {
        model.setRowCount(0); // Xóa dữ liệu cũ

        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MaLich, MaNvdp,MaTuyen, NgThu, GioThu, TrangThai FROM LichThuGom ORDER BY NgThu, GioThu";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("MaLich"),
                        rs.getInt("MaNvdp"),
                        rs.getInt("MaTuyen"),
                        rs.getString("NgThu"),
                        rs.getString("GioThu"),
                        rs.getString("TrangThai")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi tải dữ liệu lịch thu gom: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JPanel createLichThuGomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý lịch thu gom");
        titleLabel.setFont(titleFont);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Thêm");
        JButton deleteButton = new JButton("Xóa");
        JButton editButton = new JButton("Sửa");
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton exportButton = new JButton("Xuất excel");

        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(editButton);
        styleButton(searchButton);
        styleButton(refreshButton);
        styleButton(exportButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(exportButton);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = {
            "Mã lịch",
            "Mã nhân viên điều phối",
            "Mã Tuyến",
            "Ngày thu",
            "Giờ thu",
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

        // Load dữ liệu từ database
        loadLichThuGomData(model);

        // Thêm action listener cho các nút
        addButton.addActionListener(e -> showAddLichThuGomDialog(model));
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showDeleteLichThuGomDialog(model, selectedRow);
        });

        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showEditLichThuGomDialog(model, selectedRow);
        });
        searchButton.addActionListener(e -> showSearchLichThuGomDialog(model));
        refreshButton.addActionListener(e -> loadLichThuGomData(model));
        exportButton.addActionListener(e -> exportLichThuGomExcel(model));
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void showAddLichThuGomDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Thêm lịch thu gom mới", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Mã NVĐP (không cho sửa)
        formPanel.add(new JLabel("Mã NVĐP:"));
        JTextField maNvdpField = new JTextField(this.maNvdp);
        maNvdpField.setEditable(false);
        maNvdpField.setBackground(new Color(220, 220, 220));
        formPanel.add(maNvdpField);

        // Mã tuyến (giả sử bạn có combo box hoặc text field, ví dụ dùng text field)
        formPanel.add(new JLabel("Mã tuyến:"));
        JTextField maTuyenField = new JTextField();
        formPanel.add(maTuyenField);

        // Ngày thu (2-6)
        formPanel.add(new JLabel("Ngày thu:"));
        String[] ngayThuOptions = {"2", "3", "4", "5", "6", "7", "8"};
        JComboBox<String> ngayThuCombo = new JComboBox<>(ngayThuOptions);
        formPanel.add(ngayThuCombo);

        // Giờ thu (7:00 đến 23:00, mỗi 30 phút)
        formPanel.add(new JLabel("Giờ thu:"));
        java.util.List<String> gioThuList = new java.util.ArrayList<>();
        for (int h = 7; h <= 23; h++) {
            gioThuList.add(String.format("%d:00", h));
            if (h != 23) {
                gioThuList.add(String.format("%d:30", h));
            }
        }
        JComboBox<String> gioThuCombo = new JComboBox<>(gioThuList.toArray(new String[0]));
        formPanel.add(gioThuCombo);

        // Trạng thái
        formPanel.add(new JLabel("Trạng thái:"));
        String[] trangThaiOptions = {"Hoạt động", "Tạm dừng"};
        JComboBox<String> trangThaiCombo = new JComboBox<>(trangThaiOptions);
        formPanel.add(trangThaiCombo);

        // Nút lưu và hủy
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        styleButton(saveButton);
        styleButton(cancelButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (maTuyenField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập mã tuyến!");
                    return;
                }
                // Thêm vào database
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "INSERT INTO LichThuGom (MaNvdp, MaTuyen, NgThu, GioThu, TrangThai) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, this.maNvdp);
                pstmt.setString(2, maTuyenField.getText().trim());
                pstmt.setString(3, (String) ngayThuCombo.getSelectedItem());
                pstmt.setString(4, (String) gioThuCombo.getSelectedItem());
                pstmt.setString(5, (String) trangThaiCombo.getSelectedItem());
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Thêm lịch thu gom thành công!");
                // loadLichThuGomData(model); // Gọi lại hàm load dữ liệu nếu có
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm lịch thu gom: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showDeleteLichThuGomDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch thu gom cần xóa!");
            return;
        }

        int maLich = (int) model.getValueAt(selectedRow, 0);
        int maNvdp = (int) model.getValueAt(selectedRow, 1);
        int maTuyen = (int) model.getValueAt(selectedRow, 2);
        String ngayThu = model.getValueAt(selectedRow, 3).toString();
        String gioThu = model.getValueAt(selectedRow, 4).toString();
        String trangThai = model.getValueAt(selectedRow, 5).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa lịch thu gom này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM LichThuGom WHERE MaLich = ?";
                try (Connection conn = ConnectionJDBC.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, maLich);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Xóa lịch thu gom thành công!");
                    loadLichThuGomData(model);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa lịch thu gom: " + ex.getMessage());
            }
        }
    }

    // ... existing code ...
    private void showEditLichThuGomDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch thu gom muốn sửa!");
            return;
        }

        // Lấy dữ liệu từ model
        int maLich = (int) model.getValueAt(selectedRow, 0);
        int maNvdp = (int) model.getValueAt(selectedRow, 1);
        int maTuyen = (int) model.getValueAt(selectedRow, 2);
        String ngThu = (String) model.getValueAt(selectedRow, 3);
        String gioThu = (String) model.getValueAt(selectedRow, 4);
        String trangThai = (String) model.getValueAt(selectedRow, 5);

        // Create edit dialog
        JDialog dialog = new JDialog(this, "Sửa lịch thu gom", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Mã NVĐP (không cho sửa)
        formPanel.add(new JLabel("Mã NVĐP:"));
        JTextField maNvdpField = new JTextField(String.valueOf(maNvdp));
        maNvdpField.setEditable(false);
        maNvdpField.setBackground(new Color(220, 220, 220));
        formPanel.add(maNvdpField);

        // Mã tuyến
        formPanel.add(new JLabel("Mã tuyến:"));
        JTextField maTuyenField = new JTextField(String.valueOf(maTuyen));
        formPanel.add(maTuyenField);

        // Ngày thu (2-8)
        formPanel.add(new JLabel("Ngày thu:"));
        String[] ngayThuOptions = {"2", "3", "4", "5", "6", "7", "8"};
        JComboBox<String> ngayThuCombo = new JComboBox<>(ngayThuOptions);
        ngayThuCombo.setSelectedItem(ngThu);
        formPanel.add(ngayThuCombo);

        // Giờ thu (7:00 đến 23:00, mỗi 30 phút)
        formPanel.add(new JLabel("Giờ thu:"));
        List<String> timeValues = new ArrayList<>();
        for (int hour = 7; hour <= 23; hour++) {
            timeValues.add(String.format("%02d:00", hour));
            if (hour < 23) {
                timeValues.add(String.format("%02d:30", hour));
            }
        }
        JComboBox<String> gioThuCombo = new JComboBox<>(timeValues.toArray(new String[0]));
        gioThuCombo.setSelectedItem(gioThu);
        formPanel.add(gioThuCombo);

        // Trạng thái
        formPanel.add(new JLabel("Trạng thái:"));
        String[] trangThaiOptions = {"Hoạt động", "Tạm ngưng"};
        JComboBox<String> trangThaiCombo = new JComboBox<>(trangThaiOptions);
        trangThaiCombo.setSelectedItem(trangThai);
        formPanel.add(trangThaiCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        styleButton(saveButton);
        styleButton(cancelButton);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (maTuyenField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                // Update database
                String updateSql = "UPDATE LichThuGom SET MaTuyen = ?, NgThu = ?, GioThu = ?, TrangThai = ? WHERE MaLich = ?";

                try (Connection conn = ConnectionJDBC.getConnection(); PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

                    updateStmt.setInt(1, Integer.parseInt(maTuyenField.getText().trim()));
                    updateStmt.setString(2, (String) ngayThuCombo.getSelectedItem());
                    updateStmt.setString(3, (String) gioThuCombo.getSelectedItem());
                    updateStmt.setString(4, (String) trangThaiCombo.getSelectedItem());
                    updateStmt.setInt(5, maLich);

                    updateStmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Cập nhật lịch thu gom thành công!");
                    loadLichThuGomData(model);
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Giá trị không hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật lịch thu gom: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
// ... existing code ...
// ... existing code ...

    private void showSearchLichThuGomDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm lịch thu gom", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 210);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Tiêu chí tìm kiếm
        String[] searchCriteria = {"Mã lịch", "Mã nhân viên điều phối", "Mã tuyến", "Ngày thu", "Giờ thu", "Trạng thái"};
        JComboBox<String> criteriaCombo = new JComboBox<>(searchCriteria);
        formPanel.add(criteriaCombo);

        // Panel nhập text
        JPanel textPanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        textPanel.add(searchField, BorderLayout.CENTER);

        // Panel chọn ngày thu (thứ trong tuần)
        JPanel datePanel = new JPanel(new BorderLayout(5, 5));
        String[] dateValues = {"2", "3", "4", "5", "6", "7", "8"};
        JComboBox<String> dateCombo = new JComboBox<>(dateValues);
        datePanel.add(dateCombo, BorderLayout.CENTER);

        // Panel chọn giờ thu
        JPanel timePanel = new JPanel(new BorderLayout(5, 5));
        List<String> timeValues = new ArrayList<>();
        for (int hour = 7; hour <= 23; hour++) {
            timeValues.add(String.format("%02d:00", hour));
            if (hour < 23) {
                timeValues.add(String.format("%02d:30", hour));
            }
        }
        JComboBox<String> timeCombo = new JComboBox<>(timeValues.toArray(new String[0]));
        timePanel.add(timeCombo, BorderLayout.CENTER);

        // Panel chọn trạng thái
        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        String[] statusValues = {"", "Hoạt động", "Tạm dừng"};
        JComboBox<String> statusCombo = new JComboBox<>(statusValues);
        statusPanel.add(statusCombo, BorderLayout.CENTER);

        // CardLayout cho phần nhập
        JPanel inputPanel = new JPanel(new CardLayout());
        inputPanel.add(textPanel, "text");
        inputPanel.add(datePanel, "date");
        inputPanel.add(timePanel, "time");
        inputPanel.add(statusPanel, "status");

        formPanel.add(inputPanel);
        dialog.add(formPanel, BorderLayout.CENTER);

        // Nút chức năng
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Hủy");
        JButton searchButton = new JButton("Tìm kiếm");
        styleButton(searchButton);
        styleButton(cancelButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(searchButton);

        // Xử lý sự kiện khi thay đổi tiêu chí tìm kiếm
        criteriaCombo.addActionListener(e -> {
            String selected = (String) criteriaCombo.getSelectedItem();
            CardLayout cl = (CardLayout) inputPanel.getLayout();
            switch (selected) {
                case "Ngày thu":
                    cl.show(inputPanel, "date");
                    break;
                case "Giờ thu":
                    cl.show(inputPanel, "time");
                    break;
                case "Trạng thái":
                    cl.show(inputPanel, "status");
                    break;
                default:
                    cl.show(inputPanel, "text");
                    break;
            }
        });

        // Xử lý nút tìm kiếm
        searchButton.addActionListener(e -> {
            String selected = (String) criteriaCombo.getSelectedItem();
            String searchValue = "";

            switch (selected) {
                case "Ngày thu":
                    searchValue = (String) dateCombo.getSelectedItem();
                    break;
                case "Giờ thu":
                    searchValue = (String) timeCombo.getSelectedItem();
                    break;
                case "Trạng thái":
                    searchValue = (String) statusCombo.getSelectedItem();
                    break;
                default:
                    searchValue = searchField.getText().trim();
                    break;
            }

            if (searchValue.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập giá trị tìm kiếm!");
                return;
            }

            try {
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "SELECT * FROM LichThuGom WHERE ";

                switch (selected) {
                    case "Mã lịch":
                        sql += "MaLich = ?";
                        break;
                    case "Mã nhân viên điều phối":
                        sql += "MaNvdp = ?";
                        break;
                    case "Mã tuyến":
                        sql += "MaTuyen = ?";
                        break;
                    case "Ngày thu":
                        sql += "NgThu = ?";
                        break;
                    case "Giờ thu":
                        sql += "GioThu = ?";
                        break;
                    case "Trạng thái":
                        sql += "TrangThai = ?";
                        break;
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    if (selected.equals("Mã lịch") || selected.equals("Mã nhân viên điều phối")
                            || selected.equals("Mã tuyến")) {
                        pstmt.setInt(1, Integer.parseInt(searchValue));
                    } else {
                        pstmt.setString(1, searchValue);
                    }

                    ResultSet rs = pstmt.executeQuery();
                    model.setRowCount(0);

                    while (rs.next()) {
                        Object[] row = {
                            rs.getInt("MaLich"),
                            rs.getInt("MaNvdp"),
                            rs.getInt("MaTuyen"),
                            rs.getString("NgThu"),
                            rs.getString("GioThu"),
                            rs.getString("TrangThai")
                        };
                        model.addRow(row);
                    }

                    if (model.getRowCount() == 0) {
                        dialog.dispose();
                    }
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Giá trị tìm kiếm không hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi tìm kiếm: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
// ... existing code ...

    private void showAddPhanCongDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Thêm phân công mới", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 210);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Mã nhân viên điều phối:"));
        JTextField maNvdpField = new JTextField(this.maNvdp);
        maNvdpField.setEditable(false);
        maNvdpField.setBackground(new Color(220, 220, 220));
        formPanel.add(maNvdpField);

        formPanel.add(new JLabel("Mã lịch:"));
        JTextField maLichField = new JTextField();
        formPanel.add(maLichField);

        formPanel.add(new JLabel("Mã nhân viên thu gom:"));
        JTextField maNvtgField = new JTextField();
        formPanel.add(maNvtgField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        styleButton(saveButton);
        styleButton(cancelButton);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (maNvdpField.getText().trim().isEmpty()
                        || maLichField.getText().trim().isEmpty()
                        || maNvtgField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                // Insert into database
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "INSERT INTO PhanCong (MaNvdp, MaLich, MaNvtg) VALUES (?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, Integer.parseInt(maNvdpField.getText().trim()));
                    pstmt.setInt(2, Integer.parseInt(maLichField.getText().trim()));
                    pstmt.setInt(3, Integer.parseInt(maNvtgField.getText().trim()));

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Thêm phân công thành công!");
                    loadPhanCongData(model);
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Mã không hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm phân công: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showDeletePhanCongDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phân công cần xóa!");
            return;
        }

        String maPC = model.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa phân công này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "DELETE FROM PhanCong WHERE MaPC = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, Integer.parseInt(maPC));
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Xóa phân công thành công!");
                    loadPhanCongData(model);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa phân công: " + ex.getMessage());
            }
        }
    }

    private void showEditPhanCongDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phân công cần sửa!");
            return;
        }

        String maPC = model.getValueAt(selectedRow, 0).toString();
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT * FROM PhanCong WHERE MaPC = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, Integer.parseInt(maPC.trim()));
                ResultSet rs = pstmt.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy phân công với mã " + maPC);
                    return;
                }

                // Create edit dialog
                JDialog dialog = new JDialog(this, "Sửa thông tin phân công", true);
                dialog.setLayout(new BorderLayout(10, 10));
                dialog.setSize(400, 300);
                dialog.setLocationRelativeTo(this);

                JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
                formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                formPanel.add(new JLabel("Mã nhân viên điều phối:"));
                JTextField maNvdpField = new JTextField(String.valueOf(rs.getInt("MaNvdp")));
                formPanel.add(maNvdpField);

                formPanel.add(new JLabel("Mã lịch:"));
                JTextField maLichField = new JTextField(String.valueOf(rs.getInt("MaLich")));
                formPanel.add(maLichField);

                formPanel.add(new JLabel("Mã nhân viên thu gom:"));
                JTextField maNvtgField = new JTextField(String.valueOf(rs.getInt("MaNvtg")));
                formPanel.add(maNvtgField);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton saveButton = new JButton("Lưu");
                JButton cancelButton = new JButton("Hủy");

                styleButton(saveButton);
                styleButton(cancelButton);

                buttonPanel.add(cancelButton);
                buttonPanel.add(saveButton);

                saveButton.addActionListener(e -> {
                    try {
                        // Validate input
                        if (maNvdpField.getText().trim().isEmpty()
                                || maLichField.getText().trim().isEmpty()
                                || maNvtgField.getText().trim().isEmpty()) {
                            JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                            return;
                        }

                        // Update database
                        String updateSql = "UPDATE PhanCong SET MaNvdp = ?, MaLich = ?, MaNvtg = ? WHERE MaPC = ?";

                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, Integer.parseInt(maNvdpField.getText().trim()));
                            updateStmt.setInt(2, Integer.parseInt(maLichField.getText().trim()));
                            updateStmt.setInt(3, Integer.parseInt(maNvtgField.getText().trim()));
                            updateStmt.setInt(4, Integer.parseInt(maPC.trim()));

                            updateStmt.executeUpdate();
                            JOptionPane.showMessageDialog(dialog, "Cập nhật phân công thành công!");
                            loadPhanCongData(model);
                            dialog.dispose();
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "Mã không hợp lệ!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật phân công: " + ex.getMessage());
                    }
                });

                cancelButton.addActionListener(e -> dialog.dispose());

                dialog.add(formPanel, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                dialog.setVisible(true);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã phân công không hợp lệ!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm phân công: " + ex.getMessage());
        }
    }

//    private void showSearchPhanCongDialog(DefaultTableModel model) {
//        String maPC = JOptionPane.showInputDialog(this, "Nhập mã phân công cần tìm:");
//        if (maPC == null || maPC.trim().isEmpty()) {
//            loadPhanCongData(model); // Just reload data if user cancels or input is empty
//            return;
//        }
//
//        try {
//            Connection conn = ConnectionJDBC.getConnection();
//            String sql = "SELECT * FROM PhanCong WHERE MaPC = ?";
//            
//            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//                pstmt.setInt(1, Integer.parseInt(maPC.trim()));
//                ResultSet rs = pstmt.executeQuery();
//                
//                if (rs.next()) {
//                    // Clear existing table data
//                    model.setRowCount(0);
//                    
//                    // Add the found record to the table
//                    model.addRow(new Object[]{
//                        rs.getInt("MaPC"),
//                        rs.getInt("MaNvdp"),
//                        rs.getInt("MaLich"),
//                        rs.getInt("MaNvtg")
//                    });
//                } else {
//                    JOptionPane.showMessageDialog(this, "Không tìm thấy phân công với mã " + maPC);
//                    loadPhanCongData(model);
//                }
//            }
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Mã phân công không hợp lệ!");
//            loadPhanCongData(model);
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm phân công: " + ex.getMessage());
//            loadPhanCongData(model);
//        }
//    }
    private void showSearchPhanCongDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm phân công", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Tiêu chí tìm kiếm
        String[] searchCriteria = {
            "Mã phân công",
            "Mã nhân viên điều phối",
            "Mã lịch",
            "Mã nhân viên thu gom"
        };
        JComboBox<String> criteriaCombo = new JComboBox<>(searchCriteria);
        criteriaCombo.setPreferredSize(new Dimension(150, 35));
        formPanel.add(criteriaCombo);

        // Input tìm kiếm
        JPanel valuePanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(150, 35));
        valuePanel.add(searchField, BorderLayout.CENTER);

        formPanel.add(valuePanel);

        // Nút thao tác
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
                StringBuilder sql = new StringBuilder("SELECT * FROM PhanCong WHERE 1=1");
                List<Object> params = new ArrayList<>();

                String searchText = searchField.getText().trim();
                if (!searchText.isEmpty()) {
                    String selected = (String) criteriaCombo.getSelectedItem();
                    try {
                        switch (selected) {
                            case "Mã phân công":
                                sql.append(" AND MaPC = ?");
                                params.add(Integer.parseInt(searchText));
                                break;
                            case "Mã nhân viên điều phối":
                                sql.append(" AND MaNvdp = ?");
                                params.add(Integer.parseInt(searchText));
                                break;
                            case "Mã lịch":
                                sql.append(" AND MaLich = ?");
                                params.add(Integer.parseInt(searchText));
                                break;
                            case "Mã nhân viên thu gom":
                                sql.append(" AND MaNvtg = ?");
                                params.add(Integer.parseInt(searchText));
                                break;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Vui lòng nhập số cho trường tìm kiếm",
                                "Lỗi định dạng",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    ResultSet rs = pstmt.executeQuery();
                    model.setRowCount(0); // clear table

                    boolean found = false;
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("MaPC"),
                            rs.getInt("MaNvdp"),
                            rs.getInt("MaLich"),
                            rs.getInt("MaNvtg")
                        });
                        found = true;
                    }

                    if (!found) {
                        dialog.dispose();
                    }

                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập giá trị số hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi tìm kiếm: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createPhanCongPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý phân công");
        titleLabel.setFont(titleFont);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Thêm");
        JButton deleteButton = new JButton("Xóa");
        JButton editButton = new JButton("Sửa");
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton exportButton = new JButton("Xuất excel");

        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(editButton);
        styleButton(searchButton);
        styleButton(refreshButton);
        styleButton(exportButton);

        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(exportButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = {
            "Mã phân công",
            "Mã NVĐP",
            "Mã lịch",
            "Mã NVTG"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load dữ liệu từ database
        loadPhanCongData(model);

        // Thêm action listener cho các nút
        addButton.addActionListener(e -> showAddPhanCongDialog(model));
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showDeletePhanCongDialog(model, selectedRow);
        });
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showEditPhanCongDialog(model, selectedRow);
        });

        searchButton.addActionListener(e -> showSearchPhanCongDialog(model));
        refreshButton.addActionListener(e -> loadPhanCongData(model));
        exportButton.addActionListener(e -> exportPhanCongExcel(model));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadPhanCongData(DefaultTableModel model) {
        model.setRowCount(0); // Xóa dữ liệu cũ

        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MaPC, MaNvdp, MaLich, MaNvtg FROM PhanCong ORDER BY MaPC";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("MaPC"),
                        rs.getInt("MaNvdp"),
                        rs.getInt("MaLich"),
                        rs.getInt("MaNvtg")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi tải dữ liệu phân công: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JPanel createKhachHangPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý khách hàng");
        titleLabel.setFont(titleFont);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Customer table
        String[] columns = {"Mã KH", "Họ tên", "Địa chỉ", "SĐT", "Loại KH", "Trạng thái"};
        Object[][] data = {};

        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPhuongTienPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý phương tiện");
        titleLabel.setFont(titleFont);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Vehicle table
        String[] columns = {"Mã PT", "Loại xe", "Biển số", "Tải trọng", "Năm sản xuất", "Trạng thái"};
        Object[][] data = {};

        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPhanAnhPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý phản ánh");
        titleLabel.setFont(titleFont);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteButton = new JButton("Xóa");
        JButton editButton = new JButton("Sửa");
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton exportButton = new JButton("Xuất excel");

        styleButton(deleteButton);
        styleButton(editButton);
        styleButton(searchButton);
        styleButton(refreshButton);
        styleButton(exportButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = {
            "Mã phản ánh",
            "Mã chủ thải",
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

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load dữ liệu từ database
        loadPhanAnhData(model);

        // Thêm action listener cho các nút
        deleteButton.addActionListener(e -> showDeletePhanAnhDialog(model));
        editButton.addActionListener(e -> showEditPhanAnhDialog(model));
        searchButton.addActionListener(e -> showSearchPhanAnhDialog(model));
        refreshButton.addActionListener(e -> loadPhanAnhData(model));
        exportButton.addActionListener(e -> exportPhanAnhExcel(model));
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadPhanAnhData(DefaultTableModel model) {
        model.setRowCount(0); // Xóa dữ liệu cũ

        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MaPA, MaChuThai, NoiDung, ThoiGianGui, TrangThai FROM PhanAnh ORDER BY ThoiGianGui DESC";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("MaPA"),
                        rs.getInt("MaChuThai"),
                        rs.getString("NoiDung"),
                        dateFormat.format(rs.getTimestamp("ThoiGianGui")),
                        rs.getString("TrangThai")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi tải dữ liệu phản ánh: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JPanel createYeuCauDatLichPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý yêu cầu đặt lịch");
        titleLabel.setFont(titleFont);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteButton = new JButton("Xóa");
        JButton editButton = new JButton("Sửa");
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton viewDetailsButton = new JButton("Xem chi tiết");
        JButton exportButton = new JButton("Xuất excel");
        styleButton(viewDetailsButton);

        styleButton(deleteButton);
        styleButton(editButton);
        styleButton(searchButton);
        styleButton(refreshButton);
        styleButton(exportButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = {
            "Mã yêu cầu",
            "Mã chủ thải",
            "Mã lịch thu gom",
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

        // Load dữ liệu từ database
        loadYeuCauDatLichData(model);

        // Thêm action listener cho các nút
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showDeleteYeuCauDatLichDialog(model, selectedRow);
        });
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showEditYeuCauDatLichDialog(model, selectedRow);
        });
        viewDetailsButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn yêu cầu đặt lịch cần xem chi tiết!");
                return;
            }
            showYeuCauDatLichDetailsDialog(model, selectedRow);
        });
        searchButton.addActionListener(e -> showSearchYeuCauDatLichDialog(model));
        refreshButton.addActionListener(e -> loadYeuCauDatLichData(model));
        exportButton.addActionListener(e -> exportYeuCauDatLichExcel(model));
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void showYeuCauDatLichDetailsDialog(DefaultTableModel model, int selectedRow) {
        try {
            int maYC = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
            int maFile = maYC - 1;
            String filePath = "src/DatLichImg/" + maFile + ".txt";
            java.util.LinkedHashMap<String, String> data = new java.util.LinkedHashMap<>();
            java.io.File file = new java.io.File(filePath);
            if (!file.exists()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Không tìm thấy file: " + filePath, "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    int idx = line.indexOf(":");
                    if (idx > 0) {
                        String key = line.substring(0, idx).trim();
                        String value = line.substring(idx + 1).trim();
                        data.put(key, value);
                    }
                }
            }
            javax.swing.JDialog dialog = new javax.swing.JDialog(this, "Chi tiết yêu cầu đặt lịch", true);
            dialog.setLayout(new java.awt.BorderLayout(10, 10));
            dialog.setSize(600, 600);
            dialog.setLocationRelativeTo(this);
            javax.swing.JPanel formPanel = new javax.swing.JPanel(new java.awt.GridLayout(data.size(), 2, 10, 10));
            formPanel.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));
            for (java.util.Map.Entry<String, String> entry : data.entrySet()) {
                formPanel.add(new javax.swing.JLabel(entry.getKey() + ":"));
                javax.swing.JTextField tf = new javax.swing.JTextField(entry.getValue());
                tf.setEditable(false);
                formPanel.add(tf);
            }
            dialog.add(formPanel, java.awt.BorderLayout.CENTER);
            javax.swing.JButton closeBtn = new javax.swing.JButton("Đóng");
            closeBtn.addActionListener(e -> dialog.dispose());
            javax.swing.JPanel btnPanel = new javax.swing.JPanel();
            btnPanel.add(closeBtn);
            dialog.add(btnPanel, java.awt.BorderLayout.SOUTH);
            dialog.setVisible(true);
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi xem chi tiết: " + ex.getMessage(), "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadYeuCauDatLichData(DefaultTableModel model) {
        model.setRowCount(0); // Xóa dữ liệu cũ

        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MaYc, MaChuThai, MaLich, ThoiGianYc, GhiChu, TrangThai FROM YeuCauDatLich ORDER BY ThoiGianYc DESC";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("MaYc"),
                        rs.getInt("MaChuThai"),
                        rs.getInt("MaLich"),
                        dateFormat.format(rs.getDate("ThoiGianYc")),
                        rs.getString("GhiChu"),
                        rs.getString("TrangThai")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi tải dữ liệu yêu cầu đặt lịch: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JPanel createNvdpPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý nhân viên điều phối");
        titleLabel.setFont(titleFont);

        JButton addButton = new JButton("Thêm nhân viên điều phối");
        styleButton(addButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);

        // Table
        String[] columns = {"Mã NVĐP", "Họ tên", "Ngày sinh", "SĐT", "Email", "Địa chỉ", "Trạng thái"};
        Object[][] data = {};

        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createChuThaiPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE); // giống với createLichThuGomPanel

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE); // đồng bộ màu nền

        JLabel titleLabel = new JLabel("Quản lý chủ thải");
        titleLabel.setFont(titleFont); // sử dụng font chung như createLichThuGomPanel

        // Button panel (bên phải)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE); // đồng bộ màu nền

        JButton addButton = new JButton("Thêm");
        JButton deleteButton = new JButton("Xóa");
        JButton editButton = new JButton("Sửa");
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton exportButton = new JButton("Xuất Excel");

        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(editButton);
        styleButton(searchButton);
        styleButton(refreshButton);
        styleButton(exportButton);

        buttonPanel.add(searchButton);

        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(exportButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = {
            "Mã chủ thải", "Username", "Password", "Họ tên",
            "Địa chỉ", "Số điện thoại", "Email", "Loại chủ thải"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Gắn sự kiện cho các nút
        addButton.addActionListener(e -> showAddChuThaiDialog(model));
        deleteButton.addActionListener(e -> showDeleteChuThaiDialog(model, table.getSelectedRow()));
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showEditChuThaiDialog(model, selectedRow);
        });
        searchButton.addActionListener(e -> showSearchChuThaiDialog(model));
        refreshButton.addActionListener(e -> loadChuThaiData(model));
        exportButton.addActionListener(e -> exportChuThaiExcel(model));

        // Load dữ liệu ban đầu
        loadChuThaiData(model);

        // Thêm thành phần vào panel chính
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void exportChuThaiExcel(DefaultTableModel model) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("D:/ExcelSwing");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = "D:/ExcelSwing/ChuThai.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Chủ Thải");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Create data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất file Excel thành công!\nĐường dẫn: " + filePath,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportDonViExcel(DefaultTableModel model) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("D:/ExcelSwing");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = "D:/ExcelSwing/DonVi.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Đơn vị");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Create data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất file Excel thành công!\nĐường dẫn: " + filePath,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportHoaDonExcel(DefaultTableModel model) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("D:/ExcelSwing");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = "D:/ExcelSwing/HoaDon.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Hóa Đơn");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Create data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất file Excel thành công!\nĐường dẫn: " + filePath,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportNhanVienThuGomExcel(DefaultTableModel model) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("D:/ExcelSwing");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = "D:/ExcelSwing/NhanVienThuGom.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Nhân Viên Thu Gom");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Create data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất file Excel thành công!\nĐường dẫn: " + filePath,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportLichThuGomExcel(DefaultTableModel model) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("D:/ExcelSwing");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = "D:/ExcelSwing/LichThuGom.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Lịch thu gom");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Create data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất file Excel thành công!\nĐường dẫn: " + filePath,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportTuyenThuGomExcel(DefaultTableModel model) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("D:/ExcelSwing");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = "D:/ExcelSwing/TuyenThuGom.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Tuyến đường thu gom");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Create data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất file Excel thành công!\nĐường dẫn: " + filePath,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportPhanCongExcel(DefaultTableModel model) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("D:/ExcelSwing");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = "D:/ExcelSwing/PhanCong.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Phân công");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Create data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất file Excel thành công!\nĐường dẫn: " + filePath,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportChamCongExcel(DefaultTableModel model) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("D:/ExcelSwing");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = "D:/ExcelSwing/ChamCong.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Chấm công");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Create data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất file Excel thành công!\nĐường dẫn: " + filePath,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportHopDongExcel(DefaultTableModel model) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("D:/ExcelSwing");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = "D:/ExcelSwing/HopDong.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Hợp đồng");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Create data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất file Excel thành công!\nĐường dẫn: " + filePath,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportQuanExcel(DefaultTableModel model) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("D:/ExcelSwing");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = "D:/ExcelSwing/Quan.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Quận");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Create data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất file Excel thành công!\nĐường dẫn: " + filePath,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportChiTietHopDongExcel(DefaultTableModel model) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("D:/ExcelSwing");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = "D:/ExcelSwing/ChiTietHopDong.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Chi tiết hợp đồng");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Create data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất file Excel thành công!\nĐường dẫn: " + filePath,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportThongKePhanCongExcel(DefaultTableModel model) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("D:/ExcelSwing");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = "D:/ExcelSwing/ThongKePhanCong.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Thống kê phân công");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Create data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất file Excel thành công!\nĐường dẫn: " + filePath,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportPhanAnhExcel(DefaultTableModel model) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("D:/ExcelSwing");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = "D:/ExcelSwing/PhanAnh.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Phản ánh");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Create data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất file Excel thành công!\nĐường dẫn: " + filePath,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportYeuCauDatLichExcel(DefaultTableModel model) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File("D:/ExcelSwing");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = "D:/ExcelSwing/YeuCauDatLich.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Yêu cầu đặt lịch");

            // Create header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < model.getColumnCount(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(model.getColumnName(i));
            }

            // Create data rows
            for (int i = 0; i < model.getRowCount(); i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = model.getValueAt(i, j);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto size columns
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            workbook.close();

            JOptionPane.showMessageDialog(this,
                    "Xuất file Excel thành công!\nĐường dẫn: " + filePath,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file Excel: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showAddChuThaiDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Thêm chủ thải mới", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create form fields
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JTextField hoTenField = new JTextField();
        JTextField diaChiField = new JTextField();
        JTextField sdtField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField loaiChuThaiField = new JTextField();

        // Add components to form
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Họ tên:"));
        formPanel.add(hoTenField);
        formPanel.add(new JLabel("Địa chỉ:"));
        formPanel.add(diaChiField);
        formPanel.add(new JLabel("Số điện thoại:"));
        formPanel.add(sdtField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Loại chủ thải:"));
        formPanel.add(loaiChuThaiField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        styleButton(saveButton);
        styleButton(cancelButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (usernameField.getText().trim().isEmpty()
                        || passwordField.getText().trim().isEmpty()
                        || hoTenField.getText().trim().isEmpty()
                        || diaChiField.getText().trim().isEmpty()
                        || sdtField.getText().trim().isEmpty()
                        || emailField.getText().trim().isEmpty()
                        || loaiChuThaiField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                Connection conn = ConnectionJDBC.getConnection();
                String sql = """
                    INSERT INTO ChuThai (Username, Password, HoTen, DiaChi, Sdt, Email, LoaiChuThai)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, usernameField.getText().trim());
                    pstmt.setString(2, passwordField.getText().trim());
                    pstmt.setString(3, hoTenField.getText().trim());
                    pstmt.setString(4, diaChiField.getText().trim());
                    pstmt.setString(5, sdtField.getText().trim());
                    pstmt.setString(6, emailField.getText().trim());
                    pstmt.setString(7, loaiChuThaiField.getText().trim());

                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(dialog, "Thêm chủ thải thành công!");
                        loadChuThaiData(model);
                        dialog.dispose();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi khi thêm chủ thải: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadChuThaiData(DefaultTableModel model) {
        model.setRowCount(0); // Clear existing data

        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT * FROM ChuThai ORDER BY MaChuThai";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("MaChuThai"),
                    rs.getString("Username"),
                    rs.getString("Password"),
                    rs.getString("HoTen"),
                    rs.getString("DiaChi"),
                    rs.getString("Sdt"),
                    rs.getString("Email"),
                    rs.getString("LoaiChuThai")
                };
                model.addRow(row);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu chủ thải: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDvtgDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Thêm đơn vị thu gom mới", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Tên đơn vị:"));
        JTextField tenDvField = new JTextField();
        formPanel.add(tenDvField);

        formPanel.add(new JLabel("Khu vực phụ trách:"));
        JTextField khuVucField = new JTextField();
        formPanel.add(khuVucField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        styleButton(saveButton);
        styleButton(cancelButton);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (tenDvField.getText().trim().isEmpty()
                        || khuVucField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                // Insert into database
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "INSERT INTO DonViThuGom (TenDv, KhuVucPhuTrach) VALUES (?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, tenDvField.getText().trim());
                    pstmt.setString(2, khuVucField.getText().trim());

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Thêm đơn vị thu gom thành công!");
                    loadDvtgData(model);
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm đơn vị thu gom: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showDeleteDvtgDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn vị cần xóa!");
            return;
        }

        int maDv = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa đơn vị này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "DELETE FROM DonViThuGom WHERE MaDv = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(sql)) {
                    deleteStmt.setInt(1, maDv);
                    int result = deleteStmt.executeUpdate();

                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, "Xóa đơn vị thu gom thành công!");
                        loadDvtgData(model);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa đơn vị thu gom: " + ex.getMessage());
            }
        }
    }

    private void showEditDvtgDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn vị cần sửa!");
            return;
        }

        int maDv = (int) model.getValueAt(selectedRow, 0);
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT * FROM DonViThuGom WHERE MaDv = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maDv);
                ResultSet rs = pstmt.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy đơn vị với mã " + maDv);
                    return;
                }

                // Create edit dialog
                JDialog dialog = new JDialog(this, "Sửa thông tin đơn vị thu gom", true);
                dialog.setLayout(new BorderLayout(10, 10));
                dialog.setSize(400, 300);
                dialog.setLocationRelativeTo(this);

                JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
                formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                formPanel.add(new JLabel("Tên đơn vị:"));
                JTextField tenDvField = new JTextField(rs.getString("TenDv"));
                formPanel.add(tenDvField);

                formPanel.add(new JLabel("Khu vực phụ trách:"));
                JTextField khuVucField = new JTextField(rs.getString("KhuVucPhuTrach"));
                formPanel.add(khuVucField);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton saveButton = new JButton("Lưu");
                JButton cancelButton = new JButton("Hủy");

                styleButton(saveButton);
                styleButton(cancelButton);

                buttonPanel.add(cancelButton);
                buttonPanel.add(saveButton);

                saveButton.addActionListener(e -> {
                    try {
                        // Validate input
                        if (tenDvField.getText().trim().isEmpty()
                                || khuVucField.getText().trim().isEmpty()) {
                            JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                            return;
                        }

                        // Update database
                        String updateSql = "UPDATE DonViThuGom SET TenDv = ?, KhuVucPhuTrach = ? WHERE MaDv = ?";

                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, tenDvField.getText().trim());
                            updateStmt.setString(2, khuVucField.getText().trim());
                            updateStmt.setInt(3, maDv);

                            updateStmt.executeUpdate();
                            JOptionPane.showMessageDialog(dialog, "Cập nhật đơn vị thu gom thành công!");
                            loadDvtgData(model);
                            dialog.dispose();
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật đơn vị thu gom: " + ex.getMessage());
                    }
                });

                cancelButton.addActionListener(e -> dialog.dispose());

                dialog.add(formPanel, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                dialog.setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm đơn vị thu gom: " + ex.getMessage());
        }
    }

    private void showSearchDvtgDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm đơn vị thu gom", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 210);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Combo box cho tiêu chí tìm kiếm
        String[] searchCriteria = {
            "Mã đơn vị",
            "Tên đơn vị",
            "Khu vực phụ trách"
        };
        JComboBox<String> criteriaCombo = new JComboBox<>(searchCriteria);
        criteriaCombo.setPreferredSize(new Dimension(150, 35));
        formPanel.add(criteriaCombo);

        // Panel cho giá trị tìm kiếm
        JPanel valuePanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(150, 35));
        valuePanel.add(searchField, BorderLayout.CENTER);

        // CardLayout để mở rộng về sau
        JPanel inputPanel = new JPanel(new CardLayout());
        inputPanel.add(valuePanel, "text");
        formPanel.add(inputPanel);

        // Nút tìm kiếm và hủy
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = new JButton("Tìm kiếm");
        JButton cancelButton = new JButton("Hủy");
        styleButton(searchButton);
        styleButton(cancelButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(searchButton);

        // Xử lý nút Tìm kiếm
        searchButton.addActionListener(e -> {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                StringBuilder sql = new StringBuilder("SELECT * FROM DonViThuGom WHERE 1=1");
                List<Object> params = new ArrayList<>();

                String selected = (String) criteriaCombo.getSelectedItem();
                String searchValue = searchField.getText().trim();

                if (selected.equals("Mã đơn vị")) {
                    sql.append(" AND MaDv = ?");
                    params.add(Integer.parseInt(searchValue));
                } else if (selected.equals("Tên đơn vị")) {
                    sql.append(" AND TenDv LIKE ?");
                    params.add("%" + searchValue + "%");
                } else if (selected.equals("Khu vực phụ trách")) {
                    sql.append(" AND KhuVucPhuTrach LIKE ?");
                    params.add("%" + searchValue + "%");
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    ResultSet rs = pstmt.executeQuery();
                    model.setRowCount(0); // Clear bảng

                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("MaDv"),
                            rs.getString("TenDv"),
                            rs.getString("KhuVucPhuTrach")
                        });
                    }

                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập số nguyên hợp lệ cho mã đơn vị.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi tìm kiếm: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createDvtgPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý đơn vị thu gom");
        titleLabel.setFont(titleFont);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Thêm");
        JButton deleteButton = new JButton("Xóa");
        JButton editButton = new JButton("Sửa");
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton exportButton = new JButton("Xuất Excel");

        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(editButton);
        styleButton(searchButton);
        styleButton(refreshButton);
        styleButton(exportButton);

        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);

        buttonPanel.add(addButton);
        buttonPanel.add(exportButton);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = {
            "Mã đơn vị",
            "Tên đơn vị",
            "Khu vực phụ trách"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load dữ liệu từ database
        loadDvtgData(model);

        // Thêm action listener cho các nút
        addButton.addActionListener(e -> showAddDvtgDialog(model));
        deleteButton.addActionListener(e -> showDeleteDvtgDialog(model, table.getSelectedRow()));
        editButton.addActionListener(e -> showEditDvtgDialog(model, table.getSelectedRow()));
        searchButton.addActionListener(e -> showSearchDvtgDialog(model));
        refreshButton.addActionListener(e -> loadDvtgData(model));
        exportButton.addActionListener(e -> exportDonViExcel(model));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadDvtgData(DefaultTableModel model) {
        model.setRowCount(0); // Xóa dữ liệu cũ

        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MaDv, TenDv, KhuVucPhuTrach FROM DonViThuGom ORDER BY MaDv";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("MaDv"),
                        rs.getString("TenDv"),
                        rs.getString("KhuVucPhuTrach")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi tải dữ liệu đơn vị thu gom: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JPanel createNvtgPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý nhân viên thu gom");
        titleLabel.setFont(titleFont);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Thêm");
        JButton deleteButton = new JButton("Xóa");
        JButton editButton = new JButton("Sửa");
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton exportButton = new JButton("Xuất excel");

        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(editButton);
        styleButton(searchButton);
        styleButton(refreshButton);
        styleButton(exportButton);

        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(exportButton);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = {
            "Mã nhân viên thu gom",
            "Username",
            "Password",
            "Mã đơn vị",
            "Tên nhân viên thu gom",
            "Giới tính",
            "Số điện thoại",
            "Mã trưởng nhóm",
            "Tên trưởng nhóm"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load dữ liệu từ database
        loadNvtgData(model);

        // Thêm action listener cho các nút
        addButton.addActionListener(e -> showAddNvtgDialog(model));
        deleteButton.addActionListener(e -> showDeleteNvtgDialog(model, table.getSelectedRow()));
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showEditNvtgDialog(model, selectedRow);
        });
        searchButton.addActionListener(e -> showSearchNvtgDialog(model));
        refreshButton.addActionListener(e -> loadNvtgData(model));
        exportButton.addActionListener(e -> exportNhanVienThuGomExcel(model));
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void showDeleteNvtgDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn nhân viên thu gom cần xóa");
            return;
        }

        String maNvtg = model.getValueAt(selectedRow, 0) != null ? model.getValueAt(selectedRow, 0).toString() : "";
        String tenNvtg = model.getValueAt(selectedRow, 4) != null ? model.getValueAt(selectedRow, 4).toString() : "";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nhân viên thu gom này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "DELETE FROM NhanVienThuGom WHERE MaNvtg = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, Integer.parseInt(maNvtg));
                    int result = pstmt.executeUpdate();

                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, "Xóa nhân viên thu gom thành công!");
                        loadNvtgData(model);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi xóa nhân viên thu gom: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditNvtgDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn nhân viên thu gom cần sửa");
            return;
        }

        JDialog dialog = new JDialog(this, "Sửa thông tin nhân viên thu gom", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Lấy dữ liệu hiện tại từ dòng được chọn
        String maNvtg = model.getValueAt(selectedRow, 0) != null ? model.getValueAt(selectedRow, 0).toString() : "";
        String username = model.getValueAt(selectedRow, 1) != null ? model.getValueAt(selectedRow, 1).toString() : "";
        String password = model.getValueAt(selectedRow, 2) != null ? model.getValueAt(selectedRow, 2).toString() : "";
        String maDv = model.getValueAt(selectedRow, 3) != null ? model.getValueAt(selectedRow, 3).toString() : "";
        String tenNvtg = model.getValueAt(selectedRow, 4) != null ? model.getValueAt(selectedRow, 4).toString() : "";
        String gioiTinh = model.getValueAt(selectedRow, 5) != null ? model.getValueAt(selectedRow, 5).toString() : "";
        String sdt = model.getValueAt(selectedRow, 6) != null ? model.getValueAt(selectedRow, 6).toString() : "";
        String maTruongNhomCurrent = model.getValueAt(selectedRow, 7) != null ? model.getValueAt(selectedRow, 7).toString() : "";

        // Tạo các field nhập liệu
        JTextField usernameField = new JTextField(username);
        JTextField passwordField = new JTextField(password);
        JTextField maDvField = new JTextField(maDv);
        JTextField tenNvtgField = new JTextField(tenNvtg);
        JComboBox<String> gioiTinhCombo = new JComboBox<>(new String[]{"Nam", "Nữ"});
        gioiTinhCombo.setSelectedItem(gioiTinh);
        JTextField sdtField = new JTextField(sdt);
        JComboBox<Integer> maTruongNhomCombo = new JComboBox<>();
        JTextField tenTruongNhomField = new JTextField();
        tenTruongNhomField.setEditable(false);

        // Load danh sách trưởng nhóm có MaTruongNhom IS NULL
        Map<Integer, String> truongNhomMap = new HashMap<>();
        try (Connection conn = ConnectionJDBC.getConnection()) {
            String sql = "SELECT MaNvtg, TenNvtg FROM NhanVienThuGom WHERE MaTruongNhom IS NULL";
            try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
                maTruongNhomCombo.addItem(null); // Cho phép bỏ chọn trưởng nhóm
                while (rs.next()) {
                    int ma = rs.getInt("MaNvtg");
                    String ten = rs.getString("TenNvtg");
                    truongNhomMap.put(ma, ten);
                    maTruongNhomCombo.addItem(ma);
                }
            }

            // Gán lại giá trị cũ nếu có
            if (!maTruongNhomCurrent.isEmpty()) {
                try {
                    int maTruongNhomInt = Integer.parseInt(maTruongNhomCurrent);
                    if (!truongNhomMap.containsKey(maTruongNhomInt)) {
                        // Nếu mã hiện tại không trong danh sách (do không thỏa điều kiện IS NULL), vẫn thêm tạm
                        maTruongNhomCombo.addItem(maTruongNhomInt);
                        truongNhomMap.put(maTruongNhomInt, getTenTruongNhomByMa(maTruongNhomInt));
                    }
                    maTruongNhomCombo.setSelectedItem(maTruongNhomInt);
                    tenTruongNhomField.setText(truongNhomMap.get(maTruongNhomInt));
                } catch (NumberFormatException ignore) {
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi khi tải danh sách trưởng nhóm: " + ex.getMessage());
        }

        // Cập nhật tên trưởng nhóm khi thay đổi mã trưởng nhóm
        maTruongNhomCombo.addActionListener(e -> {
            Integer selectedMa = (Integer) maTruongNhomCombo.getSelectedItem();
            if (selectedMa == null) {
                tenTruongNhomField.setText("");
            } else {
                tenTruongNhomField.setText(truongNhomMap.getOrDefault(selectedMa, ""));
            }
        });

        // Thêm các component
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Mã đơn vị:"));
        formPanel.add(maDvField);
        formPanel.add(new JLabel("Tên nhân viên:"));
        formPanel.add(tenNvtgField);
        formPanel.add(new JLabel("Giới tính:"));
        formPanel.add(gioiTinhCombo);
        formPanel.add(new JLabel("Số điện thoại:"));
        formPanel.add(sdtField);
        formPanel.add(new JLabel("Mã trưởng nhóm:"));
        formPanel.add(maTruongNhomCombo);
        formPanel.add(new JLabel("Tên trưởng nhóm:"));
        formPanel.add(tenTruongNhomField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        styleButton(saveButton);
        styleButton(cancelButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Sự kiện lưu
        saveButton.addActionListener(e -> {
            try (Connection conn = ConnectionJDBC.getConnection()) {
                String sql = """
                UPDATE NhanVienThuGom 
                SET Username = ?, Password = ?, MaDv = ?, TenNvtg = ?, 
                    GioiTinh = ?, Sdt = ?, MaTruongNhom = ?
                WHERE MaNvtg = ?
                """;
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, usernameField.getText().trim());
                    pstmt.setString(2, passwordField.getText().trim());
                    pstmt.setInt(3, Integer.parseInt(maDvField.getText().trim()));
                    pstmt.setString(4, tenNvtgField.getText().trim());
                    pstmt.setString(5, gioiTinhCombo.getSelectedItem().toString());
                    pstmt.setString(6, sdtField.getText().trim());

                    Integer selectedMaTruongNhom = (Integer) maTruongNhomCombo.getSelectedItem();
                    if (selectedMaTruongNhom == null) {
                        pstmt.setNull(7, Types.INTEGER);
                    } else {
                        pstmt.setInt(7, selectedMaTruongNhom);
                    }

                    pstmt.setInt(8, Integer.parseInt(maNvtg));

                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!");
                        loadNvtgData(model);
                        dialog.dispose();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

// Hàm phụ để lấy tên trưởng nhóm từ CSDL nếu không có trong Map
    private String getTenTruongNhomByMa(int ma) {
        try (Connection conn = ConnectionJDBC.getConnection()) {
            String sql = "SELECT TenNvtg FROM NhanVienThuGom WHERE MaNvtg = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, ma);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("TenNvtg");
                    }
                }
            }
        } catch (SQLException ignored) {
        }
        return "";
    }

    private void showSearchNvtgDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm nhân viên thu gom", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 210);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] criteria = {
            "Mã nhân viên thu gom", "Username", "Mã đơn vị", "Tên nhân viên thu gom",
            "Giới tính", "Số điện thoại", "Mã trưởng nhóm"
        };
        JComboBox<String> criteriaCombo = new JComboBox<>(criteria);
        criteriaCombo.setPreferredSize(new Dimension(150, 35));

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(150, 35));

        formPanel.add(criteriaCombo);
        formPanel.add(searchField);

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
                StringBuilder sql = new StringBuilder("""
                    SELECT nv.MaNvtg, nv.Username, nv.Password, nv.MaDv, nv.TenNvtg, 
                           nv.GioiTinh, nv.Sdt, nv.MaTruongNhom, tn.TenNvtg as TenTruongNhom
                FROM NhanVienThuGom nv
                LEFT JOIN NhanVienThuGom tn ON nv.MaTruongNhom = tn.MaNvtg
                    WHERE 1=1
                """);

                String selected = (String) criteriaCombo.getSelectedItem();
                String input = searchField.getText().trim();
                List<Object> params = new ArrayList<>();

                switch (selected) {
                    case "Mã nhân viên thu gom":
                        sql.append(" AND nv.MaNvtg = ?");
                        params.add(Integer.parseInt(input));
                        break;
                    case "Username":
                        sql.append(" AND LOWER(nv.Username) LIKE ?");
                        params.add("%" + input.toLowerCase() + "%");
                        break;
                    case "Mã đơn vị":
                        sql.append(" AND nv.MaDv = ?");
                        params.add(Integer.parseInt(input));
                        break;
                    case "Tên nhân viên thu gom":
                        sql.append(" AND LOWER(nv.TenNvtg) LIKE ?");
                        params.add("%" + input.toLowerCase() + "%");
                        break;
                    case "Giới tính":
                        sql.append(" AND LOWER(nv.GioiTinh) LIKE ?");
                        params.add("%" + input.toLowerCase() + "%");
                        break;
                    case "Số điện thoại":
                        sql.append(" AND nv.Sdt LIKE ?");
                        params.add("%" + input + "%");
                        break;
                    case "Mã trưởng nhóm":
                        sql.append(" AND nv.MaTruongNhom = ?");
                        params.add(Integer.parseInt(input));
                        break;
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    ResultSet rs = pstmt.executeQuery();
                    model.setRowCount(0);
                    while (rs.next()) {
                        Integer maTruongNhom = rs.getObject("MaTruongNhom", Integer.class);
                        String displayMaTruongNhom = maTruongNhom != null ? maTruongNhom.toString() : "Không có";
                        String tenTruongNhom = rs.getString("TenTruongNhom");
                        String displayTenTruongNhom = tenTruongNhom != null ? tenTruongNhom : "Không có";

                        model.addRow(new Object[]{
                            rs.getString("MaNvtg"),
                            rs.getString("Username"),
                            rs.getString("Password"),
                            rs.getString("MaDv"),
                            rs.getString("TenNvtg"),
                            rs.getString("GioiTinh"),
                            rs.getString("Sdt"),
                            displayMaTruongNhom,
                            displayTenTruongNhom
                        });
                    }

                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập số hợp lệ cho tiêu chí đã chọn.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi truy vấn: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showAddNvtgDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Thêm nhân viên thu gom mới", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();
        JTextField maDvField = new JTextField();
        JTextField tenNvtgField = new JTextField();
        JComboBox<String> gioiTinhCombo = new JComboBox<>(new String[]{"Nam", "Nữ"});
        JTextField sdtField = new JTextField();
        JComboBox<Integer> maTruongNhomCombo = new JComboBox<>();
        JTextField tenTruongNhomField = new JTextField();
        tenTruongNhomField.setEditable(false);

        // Load mã trưởng nhóm (những người có MaTruongNhom IS NULL)
        try (Connection conn = ConnectionJDBC.getConnection()) {
            String sql = "SELECT MaNvtg, TenNvtg FROM NhanVienThuGom WHERE MaTruongNhom IS NULL";
            try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
                maTruongNhomCombo.addItem(null); // Cho phép không có trưởng nhóm
                while (rs.next()) {
                    maTruongNhomCombo.addItem(rs.getInt("MaNvtg"));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi khi load mã trưởng nhóm: " + ex.getMessage());
        }

        // Xử lý thay đổi mã trưởng nhóm → load tên trưởng nhóm
        maTruongNhomCombo.addActionListener(e -> {
            Integer selectedMa = (Integer) maTruongNhomCombo.getSelectedItem();
            if (selectedMa == null) {
                tenTruongNhomField.setText("");
                return;
            }
            try (Connection conn = ConnectionJDBC.getConnection()) {
                String sql = "SELECT TenNvtg FROM NhanVienThuGom WHERE MaNvtg = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, selectedMa);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            tenTruongNhomField.setText(rs.getString("TenNvtg"));
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi load tên trưởng nhóm: " + ex.getMessage());
            }
        });

        // Add components
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Mã đơn vị:"));
        formPanel.add(maDvField);
        formPanel.add(new JLabel("Tên nhân viên thu gom:"));
        formPanel.add(tenNvtgField);
        formPanel.add(new JLabel("Giới tính:"));
        formPanel.add(gioiTinhCombo);
        formPanel.add(new JLabel("Số điện thoại:"));
        formPanel.add(sdtField);
        formPanel.add(new JLabel("Mã trưởng nhóm:"));
        formPanel.add(maTruongNhomCombo);
        formPanel.add(new JLabel("Tên trưởng nhóm:"));
        formPanel.add(tenTruongNhomField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        styleButton(saveButton);
        styleButton(cancelButton);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                if (usernameField.getText().trim().isEmpty()
                        || passwordField.getText().trim().isEmpty()
                        || maDvField.getText().trim().isEmpty()
                        || tenNvtgField.getText().trim().isEmpty()
                        || sdtField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                Connection conn = ConnectionJDBC.getConnection();
                String sql = """
                INSERT INTO NhanVienThuGom (Username, Password, MaDv, TenNvtg, GioiTinh, Sdt, MaTruongNhom)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, usernameField.getText().trim());
                    pstmt.setString(2, passwordField.getText().trim());
                    pstmt.setInt(3, Integer.parseInt(maDvField.getText().trim()));
                    pstmt.setString(4, tenNvtgField.getText().trim());
                    pstmt.setString(5, gioiTinhCombo.getSelectedItem().toString());
                    pstmt.setString(6, sdtField.getText().trim());

                    Integer selectedMaTruongNhom = (Integer) maTruongNhomCombo.getSelectedItem();
                    if (selectedMaTruongNhom == null) {
                        pstmt.setNull(7, Types.INTEGER);
                    } else {
                        pstmt.setInt(7, selectedMaTruongNhom);
                    }

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Thêm nhân viên thu gom thành công!");
                    loadNvtgData(model);
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: Mã đơn vị không hợp lệ.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm nhân viên: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadNvtgData(DefaultTableModel model) {
        model.setRowCount(0); // Clear existing data

        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = """
                SELECT nv.MaNvtg, nv.Username, nv.Password, nv.MaDv, nv.TenNvtg, 
                       nv.GioiTinh, nv.Sdt, nv.MaTruongNhom, tn.TenNvtg as TenTruongNhom
                FROM NhanVienThuGom nv
                LEFT JOIN NhanVienThuGom tn ON nv.MaTruongNhom = tn.MaNvtg
                ORDER BY nv.MaNvtg
                """;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getString("MaNvtg"),
                    rs.getString("Username"),
                    rs.getString("Password"),
                    rs.getString("MaDv"),
                    rs.getString("TenNvtg"),
                    rs.getString("GioiTinh"),
                    rs.getString("Sdt"),
                    rs.getString("MaTruongNhom"),
                    rs.getString("TenTruongNhom")
                };
                model.addRow(row);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu nhân viên thu gom: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createHopDongPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý hợp đồng");
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
//        buttonPanel.add(addButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Main content panel with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300); // Set initial divider location

        // HopDong Table
        String[] hopDongColumns = {
            "Mã hợp đồng",
            "Mã chủ thải",
            "Loại hợp đồng",
            "Ngày bắt đầu",
            "Ngày kết thúc",
            "Địa chỉ thu gom",
            "Mô tả",
            "Trạng thái"
        };

        DefaultTableModel hopDongModel = new DefaultTableModel(hopDongColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable hopDongTable = new JTable(hopDongModel);
        hopDongTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane hopDongScrollPane = new JScrollPane(hopDongTable);

        // ChiTietHopDong Table
        String[] chiTietColumns = {
            "Mã hợp đồng",
            "Mã dịch vụ",
            "Khối lượng",
            "Thành tiền",
            "Ghi chú"
        };

        DefaultTableModel chiTietModel = new DefaultTableModel(chiTietColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable chiTietTable = new JTable(chiTietModel);
        JScrollPane chiTietScrollPane = new JScrollPane(chiTietTable);

        // Chi tiết panel with its own buttons
        JPanel chiTietPanel = new JPanel(new BorderLayout());
        JPanel chiTietButtonPanel = new JPanel(new BorderLayout());

        JLabel chiTietLabel = new JLabel("Chi tiết hợp đồng");
        chiTietLabel.setFont(new Font("Arial", Font.BOLD, 20));
        chiTietLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); // Add left padding
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton chiTietAddButton = new JButton("Thêm");
        JButton chiTietDeleteButton = new JButton("Xóa");
        JButton chiTietEditButton = new JButton("Sửa");
        JButton chiTietSearchButton = new JButton("Tìm kiếm");
        JButton chiTietRefreshButton = new JButton("Làm mới");

        styleButton(chiTietAddButton);
        styleButton(chiTietDeleteButton);
        styleButton(chiTietEditButton);
        styleButton(chiTietSearchButton);
        styleButton(chiTietRefreshButton);

        buttonContainer.add(chiTietSearchButton);
        buttonContainer.add(chiTietDeleteButton);
        buttonContainer.add(chiTietEditButton);
        buttonContainer.add(chiTietRefreshButton);
//        buttonContainer.add(chiTietAddButton);

        chiTietButtonPanel.add(chiTietLabel, BorderLayout.WEST);
        chiTietButtonPanel.add(buttonContainer, BorderLayout.EAST);

        chiTietPanel.add(chiTietButtonPanel, BorderLayout.NORTH);
        chiTietPanel.add(chiTietScrollPane, BorderLayout.CENTER);

        // Add tables to split pane
        splitPane.setTopComponent(hopDongScrollPane);
        splitPane.setBottomComponent(chiTietPanel);

        // Load initial data
        loadHopDongData(hopDongModel);

        // Add selection listener to hopDongTable
        hopDongTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = hopDongTable.getSelectedRow();
                if (selectedRow != -1) {
                    String maHopDong = hopDongTable.getValueAt(selectedRow, 0).toString();
                    loadChiTietHopDongData(chiTietModel, maHopDong);
                }
            }
        });

        // Add action listeners for HopDong buttons
        addButton.addActionListener(e -> showAddHopDongDialog(hopDongModel));
        deleteButton.addActionListener(e -> {
            int selectedRow = hopDongTable.getSelectedRow();
            showDeleteHopDongDialog(hopDongModel, selectedRow);
        });
        editButton.addActionListener(e -> {
            int selectedRow = hopDongTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hợp đồng cần sửa!");
                return;
            }
            showEditHopDongDialog(hopDongModel, selectedRow);
        });
        searchButton.addActionListener(e -> showSearchHopDongDialog(hopDongModel));
        refreshButton.addActionListener(e -> loadHopDongData(hopDongModel));

        // Add action listeners for ChiTietHopDong buttons
        chiTietAddButton.addActionListener(e -> {
            int selectedRow = hopDongTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hợp đồng để thêm chi tiết!");
                return;
            }
            showAddChiTietHopDongDialog(chiTietModel, hopDongTable);
        });
        chiTietDeleteButton.addActionListener(e -> {
            int selectedRow = chiTietTable.getSelectedRow();
            showDeleteChiTietHopDongDialog(chiTietModel, selectedRow);
        });
        chiTietEditButton.addActionListener(e -> {
            int selectedRow = chiTietTable.getSelectedRow();
            showEditChiTietHopDongDialog(chiTietModel, selectedRow);
        });
        chiTietSearchButton.addActionListener(e -> showSearchChiTietHopDongDialog(chiTietModel));
        chiTietRefreshButton.addActionListener(e -> {
            int selectedRow = hopDongTable.getSelectedRow();
            if (selectedRow != -1) {
                String maHopDong = hopDongTable.getValueAt(selectedRow, 0).toString();
                loadChiTietHopDongData(chiTietModel, maHopDong);
            }
        });

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadChiTietHopDongData(DefaultTableModel model, String maHopDong) {
        model.setRowCount(0); // Xóa dữ liệu cũ
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT * FROM ChiTietHopDong WHERE MaHopDong = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, Integer.parseInt(maHopDong));
                ResultSet rs = pstmt.executeQuery();

//                while (rs.next()) {
//                    Object[] row = {
//                        rs.getString("MaHopDong"),
//                        rs.getString("DiaChiThuGom"),
//                        rs.getString("TenDichVu"),
//                        rs.getString("DonViTinh"),
//                        String.format("%,.2f", rs.getDouble("DonGia")),
//                        rs.getInt("SoLuong"),
//                        String.format("%,.2f", rs.getDouble("ThanhTien")),
//                        rs.getString("GhiChu")
//                    };
//                    model.addRow(row);
//                }
                while (rs.next()) {
                    Object[] row = {
                        rs.getString("MaHopDong"),
                        rs.getString("MaDichVu"),
                        rs.getInt("KhoiLuong"),
                        String.format("%,.2f", rs.getDouble("ThanhTien")),
                        rs.getString("GhiChu")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu chi tiết hợp đồng: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showAddChiTietHopDongDialog(DefaultTableModel model, JTable hopDongTable) {
        if (hopDongTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hợp đồng cần thêm chi tiết!");
            return;
        }

        // Lấy mã hợp đồng từ bảng hợp đồng
        int maHopDong = Integer.parseInt(hopDongTable.getValueAt(hopDongTable.getSelectedRow(), 0).toString());

        JDialog dialog = new JDialog(this, "Thêm chi tiết hợp đồng", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Mã hợp đồng (không cho sửa)
        formPanel.add(new JLabel("Mã hợp đồng:"));
        JTextField maHopDongField = new JTextField(String.valueOf(maHopDong));
        maHopDongField.setEditable(false);
        formPanel.add(maHopDongField);

        // ComboBox cho mã dịch vụ
        formPanel.add(new JLabel("Mã dịch vụ:"));
        JComboBox<String> maDichVuCombo = new JComboBox<>();
        java.util.Map<String, Double> donGiaMap = new java.util.HashMap<>();
        try (Connection conn = ConnectionJDBC.getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT MaDichVu, TenDichVu, DonGia FROM DichVu ORDER BY MaDichVu"); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String display = rs.getInt("MaDichVu") + " - " + rs.getString("TenDichVu");
                maDichVuCombo.addItem(display);
                donGiaMap.put(display, rs.getDouble("DonGia"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi khi tải dịch vụ: " + ex.getMessage());
            dialog.dispose();
            return;
        }
        formPanel.add(maDichVuCombo);

//    formPanel.add(new JLabel("Địa chỉ thu gom:"));
//    JTextField diaChiField = new JTextField();
//    formPanel.add(diaChiField);
        formPanel.add(new JLabel("Khối lượng:"));
        JTextField khoiLuongField = new JTextField();
        formPanel.add(khoiLuongField);

        formPanel.add(new JLabel("Thành tiền:"));
        JTextField thanhTienField = new JTextField();
        thanhTienField.setEditable(false);
        formPanel.add(thanhTienField);

        formPanel.add(new JLabel("Ghi chú:"));
        JTextField ghiChuField = new JTextField();
        formPanel.add(ghiChuField);

        // Hàm tính thành tiền
        Runnable updateThanhTien = () -> {
            try {
                String selected = (String) maDichVuCombo.getSelectedItem();
                double donGia = donGiaMap.get(selected);
                double kl = Double.parseDouble(khoiLuongField.getText().trim().replace(",", "."));
                kl = Math.round(kl * 10.0) / 10.0; // Làm tròn 1 chữ số
                double tt = kl * donGia;
                thanhTienField.setText(String.format("%,.2f", tt).replace(",", "X").replace(".", ",").replace("X", "."));
            } catch (Exception ex) {
                thanhTienField.setText("");
            }
        };

        // Thêm listener cho combo box và khối lượng
        maDichVuCombo.addActionListener(e -> updateThanhTien.run());
        khoiLuongField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateThanhTien.run();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateThanhTien.run();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateThanhTien.run();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        styleButton(saveButton);
        styleButton(cancelButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
//            if (diaChiField.getText().trim().isEmpty()) {
//                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập địa chỉ thu gom!");
//                return;
//            }

                if (khoiLuongField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập khối lượng!");
                    return;
                }

                double khoiLuong = Double.parseDouble(khoiLuongField.getText().trim().replace(",", "."));
                khoiLuong = Math.round(khoiLuong * 10.0) / 10.0; // Làm tròn 1 chữ số
                if (khoiLuong <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Khối lượng phải lớn hơn 0!");
                    return;
                }

                String selectedDichVu = (String) maDichVuCombo.getSelectedItem();
                int maDichVu = Integer.parseInt(selectedDichVu.split(" - ")[0]);
                double thanhTien = Double.parseDouble(thanhTienField.getText().trim().replace(".", "").replace(",", "."));

                // Kiểm tra xem chi tiết hợp đồng đã tồn tại chưa
                try (Connection conn = ConnectionJDBC.getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM ChiTietHopDong WHERE MaHopDong = ? AND MaDichVu = ?")) {
                    pstmt.setInt(1, maHopDong);
                    pstmt.setInt(2, maDichVu);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(dialog, "Chi tiết hợp đồng này đã tồn tại!");
                        return;
                    }
                }

                // Thêm chi tiết hợp đồng mới
                String sql = "INSERT INTO ChiTietHopDong (MaHopDong, MaDichVu,  KhoiLuong, ThanhTien, GhiChu) VALUES (?, ?,  ?, ?, ?)";
                try (Connection conn = ConnectionJDBC.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, maHopDong);
                    pstmt.setInt(2, maDichVu);
//                pstmt.setString(3, diaChiField.getText().trim());
                    pstmt.setDouble(3, khoiLuong);
                    pstmt.setDouble(4, thanhTien);
                    pstmt.setString(5, ghiChuField.getText().trim());

                    pstmt.executeUpdate();

                    // Thêm vào bảng
                    model.addRow(new Object[]{
                        maHopDong,
                        maDichVu,
                        //                    diaChiField.getText().trim(),
                        khoiLuong,
                        String.format("%,.2f", thanhTien).replace(",", "X").replace(".", ",").replace("X", "."),
                        ghiChuField.getText().trim()
                    });

                    JOptionPane.showMessageDialog(dialog, "Thêm chi tiết hợp đồng thành công!");
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập giá trị số hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm chi tiết hợp đồng: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showDeleteChiTietHopDongDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chi tiết hợp đồng cần xóa!");
            return;
        }

        String maHopDong = model.getValueAt(selectedRow, 0).toString();
        String diaChiThuGom = model.getValueAt(selectedRow, 1).toString();
        String tenDichVu = model.getValueAt(selectedRow, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa chi tiết hợp đồng này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "DELETE FROM ChiTietHopDong WHERE MaHopDong = ? AND DiaChiThuGom = ? AND TenDichVu = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, maHopDong);
                    pstmt.setString(2, diaChiThuGom);
                    pstmt.setString(3, tenDichVu);

                    pstmt.executeUpdate();
                    model.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(this, "Xóa chi tiết hợp đồng thành công!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa chi tiết hợp đồng: " + ex.getMessage());
            }
        }
    }

    private void showEditChiTietHopDongDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chi tiết hợp đồng cần sửa!");
            return;
        }

        String maHopDongStr = model.getValueAt(selectedRow, 0).toString();
        String maDichVuStr = model.getValueAt(selectedRow, 1).toString();
//    String diaChiThuGom = model.getValueAt(selectedRow, 2).toString();
        String khoiLuongStr = model.getValueAt(selectedRow, 2).toString();
        String thanhTienStr = model.getValueAt(selectedRow, 3).toString();
        String ghiChu = model.getValueAt(selectedRow, 4).toString();

        int maHopDong = Integer.parseInt(maHopDongStr);
        int maDichVu = Integer.parseInt(maDichVuStr);
        int khoiLuong = Integer.parseInt(khoiLuongStr);

        double thanhTien = 0;
        try {
            NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
            Number number = format.parse(thanhTienStr);
            thanhTien = number.doubleValue();
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Lỗi định dạng số tiền: " + e.getMessage());
            return;
        }

        JDialog dialog = new JDialog(this, "Sửa chi tiết hợp đồng", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Mã hợp đồng:"));
        JTextField maHopDongField = new JTextField(String.valueOf(maHopDong));
        maHopDongField.setEditable(false);
        formPanel.add(maHopDongField);

        formPanel.add(new JLabel("Mã dịch vụ:"));
        JComboBox<String> maDichVuCombo = new JComboBox<>();
        java.util.Map<String, Double> donGiaMap = new java.util.HashMap<>();
        try (Connection conn = ConnectionJDBC.getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT MaDichVu, TenDichVu, DonGia FROM DichVu ORDER BY MaDichVu"); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String display = rs.getInt("MaDichVu") + " - " + rs.getString("TenDichVu");
                maDichVuCombo.addItem(display);
                donGiaMap.put(display, rs.getDouble("DonGia"));
                if (rs.getInt("MaDichVu") == maDichVu) {
                    maDichVuCombo.setSelectedItem(display);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi khi tải dịch vụ: " + ex.getMessage());
            dialog.dispose();
            return;
        }
        formPanel.add(maDichVuCombo);
//
//    formPanel.add(new JLabel("Địa chỉ thu gom:"));
//    JTextField diaChiField = new JTextField(diaChiThuGom);
//    formPanel.add(diaChiField);

        formPanel.add(new JLabel("Khối lượng:"));
        JTextField khoiLuongField = new JTextField(String.valueOf(khoiLuong));
        formPanel.add(khoiLuongField);

        formPanel.add(new JLabel("Thành tiền:"));
        JTextField thanhTienField = new JTextField();
        thanhTienField.setEditable(false);
        formPanel.add(thanhTienField);

        formPanel.add(new JLabel("Ghi chú:"));
        JTextField ghiChuField = new JTextField(ghiChu);
        formPanel.add(ghiChuField);

        Runnable updateThanhTien = () -> {
            try {
                String selected = (String) maDichVuCombo.getSelectedItem();
                double donGia = donGiaMap.get(selected);
                int kl = Integer.parseInt(khoiLuongField.getText().trim());
                double tt = kl * donGia;

                NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
                thanhTienField.setText(format.format(tt));
            } catch (Exception ex) {
                thanhTienField.setText("");
            }
        };
        maDichVuCombo.addActionListener(e -> updateThanhTien.run());
        khoiLuongField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateThanhTien.run();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateThanhTien.run();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateThanhTien.run();
            }
        });
        updateThanhTien.run();

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        styleButton(saveButton);
        styleButton(cancelButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
//            if (diaChiField.getText().trim().isEmpty()) {
//                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập địa chỉ thu gom!");
//                return;
//            }
                int newKhoiLuong = Integer.parseInt(khoiLuongField.getText().trim());
                if (newKhoiLuong <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Khối lượng phải lớn hơn 0!");
                    return;
                }

                String selectedDichVu = (String) maDichVuCombo.getSelectedItem();
                int newMaDichVu = Integer.parseInt(selectedDichVu.split(" - ")[0]);

                NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
                Number number = format.parse(thanhTienField.getText().trim());
                double newThanhTien = number.doubleValue();

                String sql = "UPDATE ChiTietHopDong SET "
                        + "MaDichVu = ?, "
                        + "DiaChiThuGom = ?, "
                        + "KhoiLuong = ?, "
                        + "ThanhTien = ?, "
                        + "GhiChu = ? "
                        + "WHERE MaHopDong = ? AND MaDichVu = ?";
                try (Connection conn = ConnectionJDBC.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, newMaDichVu);
//                pstmt.setString(2, diaChiField.getText().trim());
                    pstmt.setInt(2, newKhoiLuong);
                    pstmt.setDouble(3, newThanhTien);
                    pstmt.setString(4, ghiChuField.getText().trim());
                    pstmt.setInt(5, maHopDong);
                    pstmt.setInt(6, maDichVu);
                    pstmt.executeUpdate();

                    model.setValueAt(newMaDichVu, selectedRow, 1);
//                model.setValueAt(diaChiField.getText().trim(), selectedRow, 2);
                    model.setValueAt(newKhoiLuong, selectedRow, 2);
                    model.setValueAt(format.format(newThanhTien), selectedRow, 3);
                    model.setValueAt(ghiChuField.getText().trim(), selectedRow, 4);

                    JOptionPane.showMessageDialog(dialog, "Cập nhật chi tiết hợp đồng thành công!");
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập giá trị số hợp lệ!");
            } catch (SQLException | ParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật chi tiết hợp đồng: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showSearchChiTietHopDongDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm chi tiết hợp đồng", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 250);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Tiêu chí tìm kiếm
        String[] searchCriteria = {
            "Mã hợp đồng",
            "Mã dịch vụ",
            //            "Địa chỉ thu gom",
            "Khối lượng",
            "Thành tiền"
        };
        JComboBox<String> criteriaCombo = new JComboBox<>(searchCriteria);
        criteriaCombo.setPreferredSize(new Dimension(150, 35));
        formPanel.add(criteriaCombo);

        // Panel cho input dạng text
        JPanel textPanel = new JPanel(new BorderLayout());
        JTextField textField = new JTextField();
        textPanel.add(textField, BorderLayout.CENTER);

        // Panel cho khoảng giá trị
        JPanel rangePanel = new JPanel(new GridLayout(1, 4, 1, 1));
        rangePanel.add(new JLabel("Từ:"));
        JTextField minField = new JTextField();
        rangePanel.add(minField);
        rangePanel.add(new JLabel("Đến:"));
        JTextField maxField = new JTextField();
        rangePanel.add(maxField);

        // CardLayout cho input
        JPanel inputPanel = new JPanel(new CardLayout());
        inputPanel.add(textPanel, "text");
        inputPanel.add(rangePanel, "range");

        // Thay đổi khi chọn tiêu chí
        criteriaCombo.addActionListener(e -> {
            CardLayout cl = (CardLayout) inputPanel.getLayout();
            String selected = (String) criteriaCombo.getSelectedItem();
            if (selected.equals("Khối lượng") || selected.equals("Thành tiền")) {
                cl.show(inputPanel, "range");
            } else {
                cl.show(inputPanel, "text");
            }
        });

        formPanel.add(inputPanel);

        // Nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = new JButton("Tìm kiếm");
        JButton cancelButton = new JButton("Hủy");
        styleButton(searchButton);
        styleButton(cancelButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(searchButton);

        cancelButton.addActionListener(e -> dialog.dispose());

        searchButton.addActionListener(e -> {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                StringBuilder sql = new StringBuilder("SELECT * FROM ChiTietHopDong WHERE 1=1");
                java.util.List<Object> params = new ArrayList<>();

                String selected = (String) criteriaCombo.getSelectedItem();
                switch (selected) {
                    case "Mã hợp đồng":
                        sql.append(" AND MaHopDong = ?");
                        params.add(Integer.parseInt(textField.getText().trim()));
                        break;
                    case "Mã dịch vụ":
                        sql.append(" AND MaDichVu = ?");
                        params.add(Integer.parseInt(textField.getText().trim()));
                        break;
                    case "Địa chỉ thu gom":
                        sql.append(" AND LOWER(DiaChiThuGom) LIKE ?");
                        params.add("%" + textField.getText().trim().toLowerCase() + "%");
                        break;
                    case "Khối lượng":
                        String minKhoiLuong = minField.getText().trim().replace(",", ".").replaceAll("[^\\d.]", "");
                        String maxKhoiLuong = maxField.getText().trim().replace(",", ".").replaceAll("[^\\d.]", "");

                        double minKL = minKhoiLuong.isEmpty() ? 0 : Double.parseDouble(minKhoiLuong);
                        double maxKL = maxKhoiLuong.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxKhoiLuong);

                        if (minKL > maxKL) {
                            JOptionPane.showMessageDialog(dialog, "Giá trị 'Từ' phải nhỏ hơn hoặc bằng giá trị 'Đến'!");
                            return;
                        }

                        sql.append(" AND KhoiLuong BETWEEN ? AND ?");
                        params.add(minKL);
                        params.add(maxKL);
                        break;
                    case "Thành tiền":
                        String minThanhTien = minField.getText().trim().replace(",", ".").replaceAll("[^\\d.]", "");
                        String maxThanhTien = maxField.getText().trim().replace(",", ".").replaceAll("[^\\d.]", "");

                        double minTT = minThanhTien.isEmpty() ? 0 : Double.parseDouble(minThanhTien);
                        double maxTT = maxThanhTien.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxThanhTien);

                        if (minTT > maxTT) {
                            JOptionPane.showMessageDialog(dialog, "Giá trị 'Từ' phải nhỏ hơn hoặc bằng giá trị 'Đến'!");
                            return;
                        }

                        sql.append(" AND ThanhTien BETWEEN ? AND ?");
                        params.add(minTT);
                        params.add(maxTT);
                        break;
                }

                sql.append(" ORDER BY MaHopDong");

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    ResultSet rs = pstmt.executeQuery();
                    model.setRowCount(0);

                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("MaHopDong"),
                            rs.getInt("MaDichVu"),
                            //                            rs.getString("DiaChiThuGom"),
                            rs.getInt("KhoiLuong"),
                            String.format("%,.2f", rs.getDouble("ThanhTien")),
                            rs.getString("GhiChu")
                        });
                    }

                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập giá trị số hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi tìm kiếm: " + ex.getMessage());
            }
        });

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showAddHopDongDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Thêm hợp đồng mới", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Mã chủ thải:"));
        JTextField maChuThaiField = new JTextField();
        formPanel.add(maChuThaiField);

        formPanel.add(new JLabel("Loại hợp đồng:"));
        JTextField loaiHopDongField = new JTextField();
        formPanel.add(loaiHopDongField);

        // Tạo spinner cho ngày bắt đầu
        formPanel.add(new JLabel("Ngày bắt đầu:"));
        SpinnerDateModel startDateModel = new SpinnerDateModel();
        JSpinner ngayBatDauSpinner = new JSpinner(startDateModel);
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(ngayBatDauSpinner, "dd/MM/yyyy");
        ngayBatDauSpinner.setEditor(startDateEditor);
        formPanel.add(ngayBatDauSpinner);

        // Tạo spinner cho ngày kết thúc
        formPanel.add(new JLabel("Ngày kết thúc:"));
        SpinnerDateModel endDateModel = new SpinnerDateModel();
        JSpinner ngayKetThucSpinner = new JSpinner(endDateModel);
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(ngayKetThucSpinner, "dd/MM/yyyy");
        ngayKetThucSpinner.setEditor(endDateEditor);
        formPanel.add(ngayKetThucSpinner);

        formPanel.add(new JLabel("Địa chỉ thu gom:"));
        JTextField DiaChiField = new JTextField();
        formPanel.add(DiaChiField);

        formPanel.add(new JLabel("Mô tả:"));
        JTextField moTaField = new JTextField();
        formPanel.add(moTaField);

        formPanel.add(new JLabel("Trạng thái:"));
        JTextField trangThaiField = new JTextField();
        formPanel.add(trangThaiField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        styleButton(saveButton);
        styleButton(cancelButton);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (maChuThaiField.getText().trim().isEmpty()
                        || loaiHopDongField.getText().trim().isEmpty()
                        || DiaChiField.getText().trim().isEmpty()
                        || trangThaiField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                // Get dates from spinners
                Date ngayBatDau = (Date) ngayBatDauSpinner.getValue();
                Date ngayKetThuc = (Date) ngayKetThucSpinner.getValue();

//                // Parse giá trị
//                double giaTri = Double.parseDouble(giaTriField.getText().trim());
                // Insert into database
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "INSERT INTO HopDong (MaChuThai, LoaiHopDong, NgBatDau, NgKetThuc, DiaChiThuGom,  MoTa, TrangThai) VALUES (?, ?, ?,?,  ?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, Integer.parseInt(maChuThaiField.getText().trim()));
                    pstmt.setString(2, loaiHopDongField.getText().trim());
                    pstmt.setDate(3, new java.sql.Date(ngayBatDau.getTime()));
                    pstmt.setDate(4, new java.sql.Date(ngayKetThuc.getTime()));
                    pstmt.setString(5, DiaChiField.getText().trim());
                    pstmt.setString(6, moTaField.getText().trim());
                    pstmt.setString(7, trangThaiField.getText().trim());

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Thêm hợp đồng thành công!");
                    loadHopDongData(model);
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Giá trị không hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm hợp đồng: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showDeleteHopDongDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hợp đồng cần xóa!");
            return;
        }

        String maHopDong = model.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa hợp đồng này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: Thêm code xóa hợp đồng từ database
            model.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "Xóa hợp đồng thành công!");
        }
    }

    private void showEditHopDongDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hợp đồng cần sửa!");
            return;
        }

        String maHD = model.getValueAt(selectedRow, 0).toString();
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT * FROM HopDong WHERE MaHopDong = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, Integer.parseInt(maHD.trim()));
                ResultSet rs = pstmt.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy hợp đồng với mã " + maHD);
                    return;
                }

                // Tạo dialog sửa hợp đồng
                JDialog dialog = new JDialog(this, "Sửa hợp đồng", true);
                dialog.setLayout(new BorderLayout(10, 10));
                dialog.setSize(400, 500);
                dialog.setLocationRelativeTo(this);

                JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
                formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                formPanel.add(new JLabel("Mã chủ thải:"));
                JTextField maChuThaiField = new JTextField(String.valueOf(rs.getInt("MaChuThai")));
                formPanel.add(maChuThaiField);

                formPanel.add(new JLabel("Loại hợp đồng:"));
                JTextField loaiHopDongField = new JTextField(rs.getString("LoaiHopDong"));
                formPanel.add(loaiHopDongField);

                // Spinner ngày bắt đầu
                formPanel.add(new JLabel("Ngày bắt đầu:"));
                SpinnerDateModel startDateModel = new SpinnerDateModel();
                JSpinner ngayBatDauSpinner = new JSpinner(startDateModel);
                JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(ngayBatDauSpinner, "dd/MM/yyyy");
                ngayBatDauSpinner.setEditor(startDateEditor);
                ngayBatDauSpinner.setValue(rs.getDate("NgBatDau"));
                formPanel.add(ngayBatDauSpinner);

                // Spinner ngày kết thúc
                formPanel.add(new JLabel("Ngày kết thúc:"));
                SpinnerDateModel endDateModel = new SpinnerDateModel();
                JSpinner ngayKetThucSpinner = new JSpinner(endDateModel);
                JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(ngayKetThucSpinner, "dd/MM/yyyy");
                ngayKetThucSpinner.setEditor(endDateEditor);
                ngayKetThucSpinner.setValue(rs.getDate("NgKetThuc"));
                formPanel.add(ngayKetThucSpinner);

                formPanel.add(new JLabel("Địa chỉ thu gom:"));
                JTextField diaChiField = new JTextField(rs.getString("DiaChiThuGom"));
                formPanel.add(diaChiField);

                formPanel.add(new JLabel("Mô tả:"));
                JTextField moTaField = new JTextField(rs.getString("MoTa"));
                formPanel.add(moTaField);

                formPanel.add(new JLabel("Trạng thái:"));
                String[] trangThaiOptions = {"Hoạt động", "Chờ duyệt", "Tạm dừng"};
                JComboBox<String> trangThaiCombo = new JComboBox<>(trangThaiOptions);
                String trangThaiHienTai = rs.getString("TrangThai");
                if (trangThaiHienTai != null) {
                    trangThaiCombo.setSelectedItem(trangThaiHienTai);
                }
                formPanel.add(trangThaiCombo);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton saveButton = new JButton("Lưu");
                JButton cancelButton = new JButton("Hủy");

                styleButton(saveButton);
                styleButton(cancelButton);

                buttonPanel.add(cancelButton);
                buttonPanel.add(saveButton);

                saveButton.addActionListener(e -> {
                    try {
                        // Validate input
                        if (maChuThaiField.getText().trim().isEmpty()
                                || loaiHopDongField.getText().trim().isEmpty()
                                || trangThaiCombo.getSelectedItem() == null) {
                            JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                            return;
                        }

                        // Lấy ngày từ spinner
                        Date ngayBatDau = (Date) ngayBatDauSpinner.getValue();
                        Date ngayKetThuc = (Date) ngayKetThucSpinner.getValue();

                        // Cập nhật database
                        String updateSql = "UPDATE HopDong SET MaChuThai = ?, LoaiHopDong = ?, NgBatDau = ?, NgKetThuc = ?, DiaChiThuGom = ? ,MoTa = ?, TrangThai = ? WHERE MaHopDong = ?";

                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, Integer.parseInt(maChuThaiField.getText().trim()));
                            updateStmt.setString(2, loaiHopDongField.getText().trim());
                            updateStmt.setDate(3, new java.sql.Date(ngayBatDau.getTime()));
                            updateStmt.setDate(4, new java.sql.Date(ngayKetThuc.getTime()));
                            updateStmt.setString(5, diaChiField.getText().trim());
                            updateStmt.setString(6, moTaField.getText().trim());
                            updateStmt.setString(7, (String) trangThaiCombo.getSelectedItem());

                            updateStmt.setInt(8, Integer.parseInt(maHD.trim()));

                            updateStmt.executeUpdate();
                            JOptionPane.showMessageDialog(dialog, "Cập nhật hợp đồng thành công!");
                            loadHopDongData(model);
                            dialog.dispose();
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "Giá trị không hợp lệ!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật hợp đồng: " + ex.getMessage());
                    }
                });

                cancelButton.addActionListener(e -> dialog.dispose());

                dialog.add(formPanel, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                dialog.setVisible(true);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã hợp đồng không hợp lệ!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm hợp đồng: " + ex.getMessage());
        }
    }

    private void showSearchHopDongDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm hợp đồng", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 210);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Combo box cho tiêu chí tìm kiếm
        String[] searchCriteria = {
            "Mã hợp đồng",
            "Mã chủ thải",
            "Loại hợp đồng",
            "Ngày bắt đầu",
            "Ngày kết thúc",
            "Địa chỉ thu gom",
            "Trạng thái"
        };
        JComboBox<String> criteriaCombo = new JComboBox<>(searchCriteria);

        criteriaCombo.setPreferredSize(new Dimension(150, 35));

        formPanel.add(criteriaCombo);
        // Panel cho giá trị tìm kiếm
        JPanel valuePanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(150, 35));
        valuePanel.add(searchField, BorderLayout.CENTER);
        // ComboBox cho Loại hợp đồng
        String[] loaiHopDongOptions = {"", "Dài hạn", "Ngắn hạn"};
        JComboBox<String> loaiHopDongCombo = new JComboBox<>(loaiHopDongOptions);
        loaiHopDongCombo.setPreferredSize(new Dimension(150, 35));

        // ComboBox cho Trạng thái
        String[] trangThaiOptions = {"", "Hoạt động", "Chờ duyệt", "Tạm dừng"};
        JComboBox<String> trangThaiCombo = new JComboBox<>(trangThaiOptions);
        trangThaiCombo.setPreferredSize(new Dimension(150, 35));
        // Panel cho khoảng giá trị
        JPanel amountRangePanel = new JPanel(new GridLayout(1, 4, 1, 1));
        amountRangePanel.add(new JLabel("Từ:"));
        JTextField minAmountField = new JTextField();
        minAmountField.setPreferredSize(new Dimension(150, 35));
        amountRangePanel.add(minAmountField);
        amountRangePanel.add(new JLabel("Đến:"));
        JTextField maxAmountField = new JTextField();
        maxAmountField.setPreferredSize(new Dimension(150, 35));
        amountRangePanel.add(maxAmountField);
        amountRangePanel.setVisible(false);

        // Panel cho ngày
        JPanel datePanel = new JPanel(new BorderLayout(5, 5));
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(150, 35));
        datePanel.add(dateSpinner, BorderLayout.CENTER);
        datePanel.setVisible(false);

        // Panel chứa tất cả các loại input
        JPanel inputPanel = new JPanel(new CardLayout());
        inputPanel.add(valuePanel, "text");
//        inputPanel.add(amountRangePanel, "amount");
        inputPanel.add(datePanel, "date");
        inputPanel.add(loaiHopDongCombo, "loaiHopDong");
        inputPanel.add(trangThaiCombo, "trangThai");

        // Xử lý sự kiện khi thay đổi tiêu chí tìm kiếm
        criteriaCombo.addActionListener(e -> {
            String selected = (String) criteriaCombo.getSelectedItem();
            CardLayout cl = (CardLayout) inputPanel.getLayout();

            if (selected.equals("Ngày bắt đầu") || selected.equals("Ngày kết thúc")) {
                cl.show(inputPanel, "date");
            } else if (selected.equals("Loại hợp đồng")) {
                cl.show(inputPanel, "loaiHopDong");
            } else if (selected.equals("Trạng thái")) {
                cl.show(inputPanel, "trangThai");
            } else {
                cl.show(inputPanel, "text");
            }
        });

        formPanel.add(inputPanel, BorderLayout.CENTER);

        // Nút tìm kiếm và hủy
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
                StringBuilder sql = new StringBuilder("SELECT * FROM HopDong WHERE 1=1");
                java.util.List<Object> params = new ArrayList<>();

                String selected = (String) criteriaCombo.getSelectedItem();
                switch (selected) {
                    case "Mã hợp đồng":
                        sql.append(" AND MaHopDong = ?");
                        params.add(Integer.parseInt(searchField.getText().trim()));
                        break;
                    case "Mã chủ thải":
                        sql.append(" AND MaChuThai = ?");
                        params.add(Integer.parseInt(searchField.getText().trim()));
                        break;
                    case "Loại hợp đồng":
                        String selectedLoaiHopDong = (String) loaiHopDongCombo.getSelectedItem();
                        if (selectedLoaiHopDong != null && !selectedLoaiHopDong.isEmpty()) {
                            sql.append(" AND LoaiHopDong = ?");
                            params.add(selectedLoaiHopDong);
                        }
                        break;
                    case "Ngày bắt đầu":
                        sql.append(" AND TRUNC(NgBatDau) = ?");
                        params.add(new java.sql.Date(((Date) dateSpinner.getValue()).getTime()));
                        break;
                    case "Địa chỉ thu gom":
                        sql.append(" AND LOWER(DiaChiThuGom) LIKE ?");
                        params.add("%" + searchField.getText().trim().toLowerCase() + "%");
                        break;

                    case "Ngày kết thúc":
                        sql.append(" AND TRUNC(NgKetThuc) = ?");
                        params.add(new java.sql.Date(((Date) dateSpinner.getValue()).getTime()));
                        break;
//                    
                    case "Trạng thái":
                        String selectedTrangThai = (String) trangThaiCombo.getSelectedItem();
                        if (selectedTrangThai != null && !selectedTrangThai.isEmpty()) {
                            sql.append(" AND TrangThai = ?");
                            params.add(selectedTrangThai);
                        }
                        break;

                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    ResultSet rs = pstmt.executeQuery();
                    model.setRowCount(0);

                    while (rs.next()) {
                        // Format ngày tháng
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String ngayBatDau = rs.getDate("NgBatDau") != null
                                ? dateFormat.format(rs.getDate("NgBatDau")) : "";
                        String ngayKetThuc = rs.getDate("NgKetThuc") != null
                                ? dateFormat.format(rs.getDate("NgKetThuc")) : "";

                        // Format số tiền
//                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
//                        String formattedGiaTri = currencyFormat.format(rs.getDouble("GiaTri"));
                        model.addRow(new Object[]{
                            rs.getInt("MaHopDong"),
                            rs.getInt("MaChuThai"),
                            rs.getString("LoaiHopDong"),
                            ngayBatDau,
                            ngayKetThuc,
                            rs.getString("DiaChiThuGom"),
                            rs.getString("MoTa"),
                            rs.getString("TrangThai")
                        });
                    }

                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập giá trị số hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi tìm kiếm: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadHopDongData(DefaultTableModel model) {
        model.setRowCount(0); // Xóa dữ liệu cũ

        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MaHopDong, MaChuThai, LoaiHopDong, NgBatDau, NgKetThuc, DiaChiThuGom,  MoTa, TrangThai FROM HopDong ORDER BY MaHopDong";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    // Format giá trị tiền tệ
//                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
//                    String formattedGiaTri = currencyFormat.format(rs.getDouble("GiaTri"));

                    // Format ngày tháng
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String ngBatDau = rs.getDate("NgBatDau") != null ? dateFormat.format(rs.getDate("NgBatDau")) : "";
                    String ngKetThuc = rs.getDate("NgKetThuc") != null ? dateFormat.format(rs.getDate("NgKetThuc")) : "";

                    // Thêm dòng mới vào bảng
                    model.addRow(new Object[]{
                        rs.getInt("MaHopDong"),
                        rs.getInt("MaChuThai"),
                        rs.getString("LoaiHopDong"),
                        ngBatDau,
                        ngKetThuc,
                        rs.getString("DiaChiThuGom"),
                        //                        formattedGiaTri,
                        rs.getString("MoTa"),
                        rs.getString("TrangThai")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi tải dữ liệu hợp đồng: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private JPanel createHoaDonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý hóa đơn");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Thêm");
        JButton deleteButton = new JButton("Xóa");
        JButton editButton = new JButton("Sửa");
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton exportButton = new JButton("Xuất excel");

        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(editButton);
        styleButton(searchButton);
        styleButton(refreshButton);
        styleButton(exportButton);
        buttonPanel.add(searchButton);

        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(exportButton);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Mã hóa đơn", "Mã hợp đồng", "Mã NVĐP", "Ngày lập", "Số tiền", "Tình trạng"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadHoaDonData(model);

        // Add button action
        addButton.addActionListener(e -> showAddHoaDonDialog(model));

        // Delete button action
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xóa!");
                return;
            }
            showDeleteHoaDonDialog(model, selectedRow);
        });

        // Edit button action
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần sửa!");
                return;
            }
            showEditHoaDonDialog(model, selectedRow);
        });

        // Search button action
        searchButton.addActionListener(e -> showSearchHoaDonDialog(model));

        // Refresh button action
        refreshButton.addActionListener(e -> loadHoaDonData(model));
        exportButton.addActionListener(e -> exportHoaDonExcel(model));
        return panel;
    }

    private void showAddHoaDonDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tạo hóa đơn mới", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Mã hợp đồng:"));
        JComboBox<String> maHopDongCombo = new JComboBox<>();
        formPanel.add(maHopDongCombo);

        // Load mã hợp đồng từ database
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MaHopDong FROM HopDong";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                maHopDongCombo.addItem(rs.getString("MaHopDong"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải danh sách hợp đồng: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        formPanel.add(new JLabel("Mã NVĐP:"));
        JTextField maNvdpField = new JTextField(this.maNvdp);
        maNvdpField.setEditable(false);
        maNvdpField.setBackground(new Color(220, 220, 220));
        formPanel.add(maNvdpField);

        // Tạo spinner cho ngày lập
        formPanel.add(new JLabel("Ngày lập:"));
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner ngayLapSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(ngayLapSpinner, "dd/MM/yyyy");
        ngayLapSpinner.setEditor(dateEditor);
        formPanel.add(ngayLapSpinner);

        formPanel.add(new JLabel("Số tiền:"));
        JTextField soTienField = new JTextField();
        soTienField.setEditable(false);
        soTienField.setBackground(new Color(220, 220, 220));
        formPanel.add(soTienField);

        formPanel.add(new JLabel("Tình trạng:"));
        String[] tinhTrangOptions = {"Chưa thanh toán", "Đã thanh toán"};
        JComboBox<String> tinhTrangCombo = new JComboBox<>(tinhTrangOptions);
        formPanel.add(tinhTrangCombo);

        // Tự động cập nhật số tiền khi chọn hợp đồng
        maHopDongCombo.addActionListener(e -> {
            String selectedMaHopDong = (String) maHopDongCombo.getSelectedItem();
            if (selectedMaHopDong != null) {
                try {
                    Connection conn = ConnectionJDBC.getConnection();
                    String sql = "SELECT SUM(ThanhTien) as TongTien FROM ChiTietHopDong WHERE MaHopDong = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, selectedMaHopDong);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        double tongTien = rs.getDouble("TongTien");
                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                        soTienField.setText(currencyFormat.format(tongTien));
                    } else {
                        soTienField.setText("0");
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Lỗi khi tính tổng tiền: " + ex.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        styleButton(saveButton);
        styleButton(cancelButton);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (maHopDongCombo.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn mã hợp đồng!");
                    return;
                }

                // Get date from spinner
                Date ngayLap = (Date) ngayLapSpinner.getValue();

                // Parse số tiền từ text field
                String soTienStr = soTienField.getText().replaceAll("[^\\d,]", "").replace(",", ".");
                double soTien = Double.parseDouble(soTienStr);

                // Insert into database
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "INSERT INTO HoaDon (MaHopDong, MaNvdp, NgLap, SoTien, TinhTrang) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, (String) maHopDongCombo.getSelectedItem());
                    pstmt.setString(2, this.maNvdp);
                    pstmt.setDate(3, new java.sql.Date(ngayLap.getTime()));
                    pstmt.setDouble(4, soTien);
                    pstmt.setString(5, (String) tinhTrangCombo.getSelectedItem());

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Thêm hóa đơn thành công!");
                    loadHoaDonData(model);
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Giá trị không hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm hóa đơn: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showDeleteHoaDonDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xóa!");
            return;
        }

        String maHoaDon = model.getValueAt(selectedRow, 0).toString();
        String maHopDong = model.getValueAt(selectedRow, 1).toString();
        String maNvdp = model.getValueAt(selectedRow, 2).toString();
        String ngayLap = (String) model.getValueAt(selectedRow, 3);
        String soTien = (String) model.getValueAt(selectedRow, 4);
        String tinhTrang = (String) model.getValueAt(selectedRow, 5);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa hóa đơn này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String deleteSql = "DELETE FROM HoaDon WHERE MaHoaDon = ?";
                try (Connection conn = ConnectionJDBC.getConnection(); PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {

                    deleteStmt.setString(1, maHoaDon);
                    deleteStmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công!");
                    loadHoaDonData(model);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa hóa đơn: " + ex.getMessage());
            }
        }
    }

    private void showEditHoaDonDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn hóa đơn cần sửa!");

            return;
        }

        try {
            // Lấy dữ liệu từ hàng được chọn
            int maHoaDon = (int) model.getValueAt(selectedRow, 0);
            int maHopDong = (int) model.getValueAt(selectedRow, 1);
            int maNvdp = (int) model.getValueAt(selectedRow, 2);
            String ngayLapStr = (String) model.getValueAt(selectedRow, 3);
            String soTienStr = (String) model.getValueAt(selectedRow, 4);
            String tinhTrang = (String) model.getValueAt(selectedRow, 5);

            // Chuyển đổi chuỗi số tiền thành số
            // Loại bỏ ký tự tiền tệ và dấu chấm phân cách hàng nghìn
            soTienStr = soTienStr.replaceAll("[^\\d,]", "").replace(",", ".");
            double soTien = Double.parseDouble(soTienStr);

            // Chuyển đổi chuỗi ngày thành Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date ngayLap = dateFormat.parse(ngayLapStr);

            // Create edit dialog
            JDialog dialog = new JDialog(this, "Sửa hóa đơn", true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setSize(400, 400);
            dialog.setLocationRelativeTo(this);

            JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
            formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

            formPanel.add(new JLabel("Mã hợp đồng:"));
            JTextField maHopDongField = new JTextField(String.valueOf(maHopDong));
            formPanel.add(maHopDongField);

            formPanel.add(new JLabel("Mã NVĐP:"));
            JTextField maNvdpField = new JTextField(String.valueOf(maNvdp));
            formPanel.add(maNvdpField);

            // Spinner cho ngày lập
            formPanel.add(new JLabel("Ngày lập:"));
            SpinnerDateModel dateModel = new SpinnerDateModel(ngayLap, null, null, Calendar.DAY_OF_MONTH);
            JSpinner ngayLapSpinner = new JSpinner(dateModel);
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(ngayLapSpinner, "dd/MM/yyyy");
            ngayLapSpinner.setEditor(dateEditor);
            formPanel.add(ngayLapSpinner);

            formPanel.add(new JLabel("Số tiền:"));
            JTextField soTienField = new JTextField(String.valueOf(soTien));
            formPanel.add(soTienField);

            formPanel.add(new JLabel("Tình trạng:"));
            String[] tinhTrangOptions = {"Đã thanh toán", "Chưa thanh toán"};
            JComboBox<String> tinhTrangCombo = new JComboBox<>(tinhTrangOptions);
            tinhTrangCombo.setSelectedItem(tinhTrang);
            formPanel.add(tinhTrangCombo);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Lưu");
            JButton cancelButton = new JButton("Hủy");

            styleButton(saveButton);
            styleButton(cancelButton);

            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);

            saveButton.addActionListener(e -> {
                try {
                    // Validate input
                    if (maHopDongField.getText().trim().isEmpty()
                            || maNvdpField.getText().trim().isEmpty()
                            || soTienField.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                        return;
                    }

                    // Get date from spinner
                    Date newNgayLap = (Date) ngayLapSpinner.getValue();

                    // Parse số tiền - xử lý cả dấu chấm và dấu phẩy
                    String newSoTienStr = soTienField.getText().trim().replaceAll("[^\\d,.]", "");
                    // Nếu có dấu phẩy, chuyển thành dấu chấm
                    if (newSoTienStr.contains(",")) {
                        newSoTienStr = newSoTienStr.replace(",", ".");
                    }
                    double newSoTien = Double.parseDouble(newSoTienStr);

                    // Update database
                    Connection conn = ConnectionJDBC.getConnection();
                    String updateSql = "UPDATE HoaDon SET MaHopDong = ?, MaNvdp = ?, NgLap = ?, SoTien = ?, TinhTrang = ? WHERE MaHoaDon = ?";

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, Integer.parseInt(maHopDongField.getText().trim()));
                        updateStmt.setInt(2, Integer.parseInt(maNvdpField.getText().trim()));
                        updateStmt.setDate(3, new java.sql.Date(newNgayLap.getTime()));
                        updateStmt.setDouble(4, newSoTien);
                        updateStmt.setString(5, (String) tinhTrangCombo.getSelectedItem());
                        updateStmt.setInt(6, maHoaDon);

                        updateStmt.executeUpdate();
                        JOptionPane.showMessageDialog(dialog, "Cập nhật hóa đơn thành công!");
                        loadHoaDonData(model);
                        dialog.dispose();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Giá trị số tiền không hợp lệ!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật hóa đơn: " + ex.getMessage());
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            dialog.add(formPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý ngày tháng: " + ex.getMessage());
        }
    }

    private void showSearchHoaDonDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm hóa đơn", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 210);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Combo box cho tiêu chí tìm kiếm
        String[] searchCriteria = {
            "Mã hóa đơn",
            "Mã hợp đồng",
            "Mã nhân viên điều phối",
            "Ngày lập",
            "Số tiền",
            "Tình trạng"
        };
        JComboBox<String> criteriaCombo = new JComboBox<>(searchCriteria);
        criteriaCombo.setPreferredSize(new Dimension(150, 35));

        formPanel.add(criteriaCombo);

        // Panel cho giá trị tìm kiếm
        JPanel valuePanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        String[] tinhTrangOptions = {"", "Chưa thanh toán", "Đã thanh toán"};
        JComboBox<String> tinhTrangCombo = new JComboBox<>(tinhTrangOptions);
        tinhTrangCombo.setPreferredSize(new Dimension(150, 35));
        searchField.setPreferredSize(new Dimension(150, 35));
        valuePanel.add(searchField, BorderLayout.CENTER);

        // Panel cho khoảng số tiền
        JPanel amountRangePanel = new JPanel(new GridLayout(1, 4, 1, 1));
        amountRangePanel.add(new JLabel("Từ:"));
        JTextField minAmountField = new JTextField();
        minAmountField.setPreferredSize(new Dimension(100, 35));
        amountRangePanel.add(minAmountField);
        amountRangePanel.add(new JLabel("Đến:"));
        JTextField maxAmountField = new JTextField();
        maxAmountField.setPreferredSize(new Dimension(100, 35));
        amountRangePanel.add(maxAmountField);
        amountRangePanel.setVisible(false);

        // Panel cho ngày
        JPanel datePanel = new JPanel(new BorderLayout(5, 5));
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(100, 25));
        datePanel.add(dateSpinner, BorderLayout.CENTER);
        datePanel.setVisible(false);

        // Panel chứa tất cả các loại input
        JPanel inputPanel = new JPanel(new CardLayout());
        inputPanel.add(valuePanel, "text");
        inputPanel.add(amountRangePanel, "amount");
        inputPanel.add(datePanel, "date");
        inputPanel.add(tinhTrangCombo, "combo");
        // Xử lý sự kiện khi thay đổi tiêu chí tìm kiếm
        criteriaCombo.addActionListener(e -> {
            String selected = (String) criteriaCombo.getSelectedItem();
            CardLayout cl = (CardLayout) inputPanel.getLayout();

            if (selected.equals("Ngày lập")) {
                cl.show(inputPanel, "date");
            } else if (selected.equals("Số tiền")) {
                cl.show(inputPanel, "amount");
            }
            if ("Tình trạng".equals(selected)) {
                cl.show(inputPanel, "combo");
            } else {
                cl.show(inputPanel, "text");
            }
        });

        formPanel.add(inputPanel, BorderLayout.CENTER);

        // Nút tìm kiếm và hủy
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
                StringBuilder sql = new StringBuilder("SELECT * FROM HoaDon WHERE 1=1");
                java.util.List<Object> params = new ArrayList<>();

                String selected = (String) criteriaCombo.getSelectedItem();
                switch (selected) {
                    case "Mã hóa đơn":
                        sql.append(" AND MaHoaDon = ?");
                        params.add(Integer.parseInt(searchField.getText().trim()));
                        break;
                    case "Mã hợp đồng":
                        sql.append(" AND MaHopDong = ?");
                        params.add(Integer.parseInt(searchField.getText().trim()));
                        break;
                    case "Mã nhân viên điều phối":
                        sql.append(" AND MaNvdp = ?");
                        params.add(Integer.parseInt(searchField.getText().trim()));
                        break;
                    case "Ngày lập":
                        sql.append(" AND NgLap = ?");
                        params.add(new java.sql.Date(((Date) dateSpinner.getValue()).getTime()));
                        break;
                    case "Số tiền":
                        // Xử lý số tiền từ và đến
                        String minAmountStr = minAmountField.getText().trim().replaceAll("[^\\d,.]", "");
                        String maxAmountStr = maxAmountField.getText().trim().replaceAll("[^\\d,.]", "");

                        // Chuyển dấu phẩy thành dấu chấm nếu có
                        if (minAmountStr.contains(",")) {
                            minAmountStr = minAmountStr.replace(",", ".");
                        }
                        if (maxAmountStr.contains(",")) {
                            maxAmountStr = maxAmountStr.replace(",", ".");
                        }

                        double minAmount = Double.parseDouble(minAmountStr);
                        double maxAmount = Double.parseDouble(maxAmountStr);

                        if (minAmount > maxAmount) {
                            JOptionPane.showMessageDialog(dialog, "Giá trị 'Từ' phải nhỏ hơn hoặc bằng giá trị 'Đến'!");
                            return;
                        }

                        sql.append(" AND SoTien BETWEEN ? AND ?");
                        params.add(minAmount);
                        params.add(maxAmount);
                        break;
                    case "Tình trạng":
                        sql.append(" AND TinhTrang = ?");
                        params.add(tinhTrangCombo.getSelectedItem());
                        break;
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    ResultSet rs = pstmt.executeQuery();
                    model.setRowCount(0);

                    while (rs.next()) {
                        // Format ngày tháng
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String ngLap = rs.getDate("NgLap") != null
                                ? dateFormat.format(rs.getDate("NgLap")) : "";

                        // Format số tiền
                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                        String formattedSoTien = currencyFormat.format(rs.getDouble("SoTien"));

                        model.addRow(new Object[]{
                            rs.getInt("MaHoaDon"),
                            rs.getInt("MaHopDong"),
                            rs.getInt("MaNvdp"),
                            ngLap,
                            formattedSoTien,
                            rs.getString("TinhTrang")
                        });
                    }

                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập giá trị số hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi tìm kiếm: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadHoaDonData(DefaultTableModel model) {
        model.setRowCount(0); // Xóa dữ liệu cũ

        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MaHoaDon, MaHopDong, MaNvdp, NgLap, SoTien, TinhTrang FROM HoaDon ORDER BY MaHoaDon";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    // Format giá trị tiền tệ
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                    String formattedSoTien = currencyFormat.format(rs.getDouble("SoTien"));

                    // Format ngày tháng
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String ngLap = rs.getDate("NgLap") != null ? dateFormat.format(rs.getDate("NgLap")) : "";

                    // Thêm dòng mới vào bảng
                    model.addRow(new Object[]{
                        rs.getInt("MaHoaDon"),
                        rs.getInt("MaHopDong"),
                        rs.getInt("MaNvdp"),
                        ngLap,
                        formattedSoTien,
                        rs.getString("TinhTrang")
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi tải dữ liệu hóa đơn: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
   private void loadThongKeChamCong(DefaultTableModel model, int month, int year) {
    model.setRowCount(0);
    try (Connection conn = ConnectionJDBC.getConnection()) {
        String query = "SELECT MaNvtg, COUNT(*) as SoNgayCong " +
                      "FROM ChamCong " +
                      "WHERE EXTRACT(MONTH FROM NgayCong) = ? " +
                      "AND EXTRACT(YEAR FROM NgayCong) = ? " +
                      "GROUP BY MaNvtg";
        
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                        model.addRow(new Object[]{
                        rs.getInt("MaNvtg"),
                        rs.getInt("SoNgayCong")
                        });
                }
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null,
                "Lỗi khi tải dữ liệu thống kê chấm công: " + ex.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}
    private JPanel createChamCongPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý chấm công");
        titleLabel.setFont(titleFont);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Thêm");
        JButton deleteButton = new JButton("Xóa");
        JButton editButton = new JButton("Sửa");
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton exportButton = new JButton("Xuất excel");

        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(editButton);
        styleButton(searchButton);
        styleButton(refreshButton);
        styleButton(exportButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(exportButton);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = {
            "Mã chấm công",
            "Mã nhân viên điều phối",
            "Mã nhân viên thu gom",
            "Ngày công",
            "Ghi chú",
            "Trạng thái"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model); // Initialize the class field
        JScrollPane scrollPane = new JScrollPane(table);

        // Load dữ liệu từ database
        loadChamCongData(model);

        // Thêm action listener cho các nút
        addButton.addActionListener(e -> showAddChamCongDialog(model));
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showDeleteChamCongDialog(model, selectedRow);
        });
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showEditChamCongDialog(model, selectedRow);
        });
        searchButton.addActionListener(e -> showSearchChamCongDialog(model));
        refreshButton.addActionListener(e -> loadChamCongData(model));
        exportButton.addActionListener(e -> exportChamCongExcel(model));
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        // Tạo panel thống kê
        JPanel thongKePanel = new JPanel(new BorderLayout(10, 10));
        // Tạo font mới với kích thước 16
        Font titleFont = new Font("Arial", Font.BOLD, 24);
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Thống kê chấm công theo tháng");
        titledBorder.setTitleFont(titleFont);
        thongKePanel.setBorder(titledBorder);
        thongKePanel.setBackground(Color.WHITE);

        // Panel chứa spinner và nút tìm kiếm
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);

        // Tạo spinner cho tháng (1-12)
        SpinnerNumberModel monthModel = new SpinnerNumberModel(1, 1, 12, 1);
        JSpinner monthSpinner = new JSpinner(monthModel);
        JSpinner.NumberEditor monthEditor = new JSpinner.NumberEditor(monthSpinner, "00");
        monthSpinner.setEditor(monthEditor);
        
        // Tạo spinner cho năm (2000-2100)
       // Tạo spinner cho năm (2000-2100)
        SpinnerNumberModel yearModel = new SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR), 2000, 2100, 1);
        JSpinner yearSpinner = new JSpinner(yearModel);
        // Thêm dòng này để bỏ dấu phẩy
        JSpinner.NumberEditor yearEditor = new JSpinner.NumberEditor(yearSpinner, "####");
        yearSpinner.setEditor(yearEditor);

        // Tạo nút thống kê
        JButton thongKeButton = new JButton("Thống kê");
        JButton searchThongKeButton = new JButton("Tìm kiếm");
        styleButton(thongKeButton);
        styleButton(searchThongKeButton);

        // Thêm các thành phần vào panel filter
        filterPanel.add(new JLabel("Tháng:"));
        filterPanel.add(monthSpinner);
        filterPanel.add(new JLabel("Năm:"));
        filterPanel.add(yearSpinner);
        filterPanel.add(thongKeButton);
        filterPanel.add(searchThongKeButton);

        // Tạo bảng thống kê
        String[] thongKeColumns = {"Mã nhân viên thu gom", "Số ngày công"};
        DefaultTableModel thongKeModel = new DefaultTableModel(thongKeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable thongKeTable = new JTable(thongKeModel);
        JScrollPane thongKeScrollPane = new JScrollPane(thongKeTable);
        thongKeScrollPane.setPreferredSize(new Dimension(800, 200));

        // Thêm các thành phần vào panel thống kê
        thongKePanel.add(filterPanel, BorderLayout.NORTH);
        thongKePanel.add(thongKeScrollPane, BorderLayout.CENTER);

        // Thêm sự kiện cho nút thống kê
        thongKeButton.addActionListener(e -> {
            int month = (Integer) monthSpinner.getValue();
            int year = (Integer) yearSpinner.getValue();
            loadThongKeChamCong(thongKeModel, month, year);
        });
        // Thêm nút tìm kiếm
        
        searchThongKeButton.addActionListener(e -> {
            int month = (Integer) monthSpinner.getValue();
            int year = (Integer) yearSpinner.getValue();
            showSearchThongKeChamCongDialog(thongKeModel, month, year);
        });
        // Tạo panel chính chứa cả bảng chấm công và thống kê
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);
        mainContentPanel.add(thongKePanel, BorderLayout.SOUTH);

        // Thay thế scrollPane bằng mainContentPanel trong panel chính
        panel.add(mainContentPanel, BorderLayout.CENTER);
        return panel;
    }
 private void showSearchThongKeChamCongDialog(DefaultTableModel model, int month, int year) {
    JDialog dialog = new JDialog(this, "Tìm kiếm thống kê chấm công", true);
    dialog.setLayout(new BorderLayout(10, 10));
    dialog.setSize(420, 210);
    dialog.setLocationRelativeTo(this);

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

    // ComboBox cho tiêu chí tìm kiếm (không còn label)
    JComboBox<String> searchCriteriaComboBox = new JComboBox<>();
    searchCriteriaComboBox.addItem("Mã nhân viên");
    searchCriteriaComboBox.addItem("Số ngày công");
    searchCriteriaComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

    // TextField cho giá trị tìm kiếm (không còn label)
    JTextField searchField = new JTextField();
    searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

    // Thêm thành phần vào panel
    panel.add(searchCriteriaComboBox);
    panel.add(Box.createVerticalStrut(10));
    panel.add(searchField);

    // Các nút
    JButton searchButton = new JButton("Tìm kiếm");
    styleButton(searchButton);
    JButton cancelButton = new JButton("Hủy");
    styleButton(cancelButton);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.add(cancelButton);
    buttonPanel.add(searchButton);

    cancelButton.addActionListener(e -> dialog.dispose());

    searchButton.addActionListener(e -> {
        String searchCriteria = (String) searchCriteriaComboBox.getSelectedItem();
        String searchValue = searchField.getText().trim();

        // Xóa dữ liệu cũ trong bảng
        model.setRowCount(0);

        try (Connection conn = ConnectionJDBC.getConnection()) {
            String query;
            if (searchCriteria.equals("Mã nhân viên")) {
                query = "SELECT MaNvtg, COUNT(*) as SoNgayCong " +
                        "FROM ChamCong " +
                        "WHERE EXTRACT(MONTH FROM NgayCong) = ? " +
                        "AND EXTRACT(YEAR FROM NgayCong) = ? " +
                        "AND MaNvtg = ? " +
                        "GROUP BY MaNvtg";
            } else {
                query = "SELECT MaNvtg, COUNT(*) as SoNgayCong " +
                        "FROM ChamCong " +
                        "WHERE EXTRACT(MONTH FROM NgayCong) = ? " +
                        "AND EXTRACT(YEAR FROM NgayCong) = ? " +
                        "GROUP BY MaNvtg " +
                        "HAVING COUNT(*) = ?";
            }

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, month);
                pstmt.setInt(2, year);

                if (searchCriteria.equals("Mã nhân viên")) {
                    pstmt.setString(3, searchValue);
                } else {
                    pstmt.setInt(3, Integer.parseInt(searchValue));
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getInt("MaNvtg"),
                                rs.getInt("SoNgayCong")
                        });
                    }
                }
            }
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(dialog,
                    "Lỗi khi tìm kiếm dữ liệu: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }

        dialog.dispose();
    });

    dialog.add(panel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);
}


    private void showAddChamCongDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Thêm chấm công mới", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Mã nhân viên điều phối:"));
        JTextField maNvdpField = new JTextField(this.maNvdp);
        maNvdpField.setEditable(false);
        maNvdpField.setBackground(new Color(220, 220, 220));
        formPanel.add(maNvdpField);

        formPanel.add(new JLabel("Mã nhân viên thu gom:"));
        JTextField maNvtgField = new JTextField();
        formPanel.add(maNvtgField);

        // Tạo spinner cho ngày công
        formPanel.add(new JLabel("Ngày công:"));
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner ngayCongSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(ngayCongSpinner, "dd/MM/yyyy");
        ngayCongSpinner.setEditor(dateEditor);
        formPanel.add(ngayCongSpinner);

        formPanel.add(new JLabel("Ghi chú:"));
        JTextField ghiChuField = new JTextField();
        formPanel.add(ghiChuField);

        formPanel.add(new JLabel("Trạng thái:"));
        String[] trangThaiOptions = {"Chưa chấm công", "Đã chấm công"};
        JComboBox<String> trangThaiCombo = new JComboBox<>(trangThaiOptions);
        formPanel.add(trangThaiCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        styleButton(saveButton);
        styleButton(cancelButton);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (maNvdpField.getText().trim().isEmpty()
                        || maNvtgField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                    return;
                }

                // Get date from spinner
                Date ngayCong = (Date) ngayCongSpinner.getValue();

                // Insert into database
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "INSERT INTO ChamCong (MaNvdp, MaNvtg, NgayCong, GhiChu, TrangThai) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, Integer.parseInt(maNvdpField.getText().trim()));
                    pstmt.setInt(2, Integer.parseInt(maNvtgField.getText().trim()));
                    pstmt.setDate(3, new java.sql.Date(ngayCong.getTime()));
                    pstmt.setString(4, ghiChuField.getText().trim());
                    pstmt.setString(5, (String) trangThaiCombo.getSelectedItem());

                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Thêm chấm công thành công!");
                    loadChamCongData(model);
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Mã nhân viên không hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm chấm công: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showDeleteChamCongDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chấm công cần xóa");
            return;
        }

        // Lấy MaCc từ dòng được chọn (giả sử MaCc nằm ở cột 0)
        String maCc = model.getValueAt(selectedRow, 0).toString();

        // Xác nhận xóa
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa chấm công này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                String deleteSql = "DELETE FROM ChamCong WHERE MaCc = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                    pstmt.setInt(1, Integer.parseInt(maCc));
                    int result = pstmt.executeUpdate();

                    if (result > 0) {
                        JOptionPane.showMessageDialog(this,
                                "Xóa chấm công thành công!",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadChamCongData(model); // Refresh lại bảng
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi xóa chấm công: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Mã chấm công không hợp lệ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditChamCongDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chấm công cần sửa");
            return;
        }

        // Lấy mã chấm công từ dòng đã chọn
        int maCc = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());

        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT * FROM ChamCong WHERE MaCc = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maCc);
                ResultSet rs = pstmt.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy chấm công với mã " + maCc);
                    return;
                }

                JDialog dialog = new JDialog(this, "Sửa thông tin chấm công", true);
                dialog.setLayout(new BorderLayout(10, 10));
                dialog.setSize(400, 450);
                dialog.setLocationRelativeTo(this);

                JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
                formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                formPanel.add(new JLabel("Mã nhân viên điều phối:"));
                JTextField maNvdpField = new JTextField(String.valueOf(rs.getInt("MaNvdp")));
                formPanel.add(maNvdpField);

                formPanel.add(new JLabel("Mã nhân viên thu gom:"));
                JTextField maNvtgField = new JTextField(String.valueOf(rs.getInt("MaNvtg")));
                formPanel.add(maNvtgField);

                formPanel.add(new JLabel("Ngày công:"));
                SpinnerDateModel dateModel = new SpinnerDateModel();
                JSpinner ngayCongSpinner = new JSpinner(dateModel);
                JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(ngayCongSpinner, "dd/MM/yyyy");
                ngayCongSpinner.setEditor(dateEditor);
                ngayCongSpinner.setValue(rs.getDate("NgayCong"));
                formPanel.add(ngayCongSpinner);

                formPanel.add(new JLabel("Ghi chú:"));
                JTextField ghiChuField = new JTextField(rs.getString("GhiChu"));
                formPanel.add(ghiChuField);

                formPanel.add(new JLabel("Trạng thái:"));
                String[] trangThaiOptions = {"Đã xác nhận", "Chưa xác nhận"};
                JComboBox<String> trangThaiCombo = new JComboBox<>(trangThaiOptions);
                trangThaiCombo.setSelectedItem(rs.getString("TrangThai"));
                formPanel.add(trangThaiCombo);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton saveButton = new JButton("Lưu");
                JButton cancelButton = new JButton("Hủy");

                styleButton(saveButton);
                styleButton(cancelButton);

                buttonPanel.add(cancelButton);
                buttonPanel.add(saveButton);

                saveButton.addActionListener(e -> {
                    try {
                        if (maNvdpField.getText().trim().isEmpty()
                                || maNvtgField.getText().trim().isEmpty()) {
                            JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                            return;
                        }

                        Date selectedDate = (Date) ngayCongSpinner.getValue();

                        String updateSql = """
                        UPDATE ChamCong 
                        SET MaNvdp = ?, MaNvtg = ?, NgayCong = ?, GhiChu = ?, TrangThai = ?
                        WHERE MaCc = ?
                    """;

                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, Integer.parseInt(maNvdpField.getText().trim()));
                            updateStmt.setInt(2, Integer.parseInt(maNvtgField.getText().trim()));
                            updateStmt.setDate(3, new java.sql.Date(selectedDate.getTime()));
                            updateStmt.setString(4, ghiChuField.getText().trim());
                            updateStmt.setString(5, (String) trangThaiCombo.getSelectedItem());
                            updateStmt.setInt(6, maCc);

                            updateStmt.executeUpdate();
                            JOptionPane.showMessageDialog(dialog, "Cập nhật chấm công thành công!");
        loadChamCongData(model);
                            dialog.dispose();
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(dialog, "Mã nhân viên không hợp lệ!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật chấm công: " + ex.getMessage());
                    }
                });

                cancelButton.addActionListener(e -> dialog.dispose());

                dialog.add(formPanel, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                dialog.setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm chấm công: " + ex.getMessage());
        }
    }

    private void showSearchChamCongDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm chấm công", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 210);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Combo box cho tiêu chí tìm kiếm
        String[] searchCriteria = {
            "Mã chấm công",
            "Mã nhân viên điều phối",
            "Mã nhân viên thu gom",
            "Ngày công",
            "Trạng thái"
        };
        JComboBox<String> criteriaCombo = new JComboBox<>(searchCriteria);
        criteriaCombo.setPreferredSize(new Dimension(150, 35));
        formPanel.add(criteriaCombo);

        // Panel cho input text
        JPanel valuePanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        String[] trangThaiOptions = {"", "Đã xác nhận", "Chưa xác nhận"};
        JComboBox<String> trangThaiCombo = new JComboBox<>(trangThaiOptions);
        trangThaiCombo.setPreferredSize(new Dimension(150, 35));
        searchField.setPreferredSize(new Dimension(150, 35));
        valuePanel.add(searchField, BorderLayout.CENTER);

        // Panel cho input ngày
        JPanel datePanel = new JPanel(new BorderLayout(5, 5));
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(100, 35));
        datePanel.add(dateSpinner, BorderLayout.CENTER);

        // Panel chứa các input dùng CardLayout
        JPanel inputPanel = new JPanel(new CardLayout());
        inputPanel.add(valuePanel, "text");
        inputPanel.add(datePanel, "date");
        inputPanel.add(trangThaiCombo, "combo");
        // Xử lý chuyển đổi khi đổi tiêu chí tìm kiếm
        criteriaCombo.addActionListener(e -> {
            String selected = (String) criteriaCombo.getSelectedItem();
            CardLayout cl = (CardLayout) inputPanel.getLayout();
            if ("Ngày công".equals(selected)) {
                cl.show(inputPanel, "date");
            } else if ("Trạng thái".equals(selected)) {
                cl.show(inputPanel, "combo");
            } else {
                cl.show(inputPanel, "text");
            }
        });
        formPanel.add(inputPanel);

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = new JButton("Tìm kiếm");
        JButton cancelButton = new JButton("Hủy");
        styleButton(searchButton);
        styleButton(cancelButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(searchButton);

        // Sự kiện tìm kiếm
        searchButton.addActionListener(e -> {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                StringBuilder sql = new StringBuilder("SELECT * FROM ChamCong WHERE 1=1");
                List<Object> params = new ArrayList<>();

                String selected = (String) criteriaCombo.getSelectedItem();
                switch (selected) {
                    case "Mã chấm công":
                        sql.append(" AND MaCc = ?");
                        params.add(Integer.parseInt(searchField.getText().trim()));
                        break;
                    case "Mã nhân viên điều phối":
                        sql.append(" AND MaNvdp = ?");
                        params.add(Integer.parseInt(searchField.getText().trim()));
                        break;
                    case "Mã nhân viên thu gom":
                        sql.append(" AND MaNvtg = ?");
                        params.add(Integer.parseInt(searchField.getText().trim()));
                        break;
                    case "Ngày công":
                        sql.append(" AND TRUNC(NgayCong) = ?");
                        params.add(new java.sql.Date(((Date) dateSpinner.getValue()).getTime()));
                        break;
                    case "Trạng thái":
                        sql.append(" AND LOWER(TrangThai) = ?");
                        params.add(trangThaiCombo.getSelectedItem() == null ? "" : ((String) trangThaiCombo.getSelectedItem()).toLowerCase());
                        break;
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    ResultSet rs = pstmt.executeQuery();
        model.setRowCount(0);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    while (rs.next()) {
                        model.addRow(new Object[]{
                            rs.getInt("MaCc"),
                            rs.getInt("MaNvdp"),
                            rs.getInt("MaNvtg"),
                            rs.getDate("NgayCong") != null ? sdf.format(rs.getDate("NgayCong")) : "",
                            rs.getString("GhiChu"),
                            rs.getString("TrangThai")
                        });
                    }

                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập số hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi truy vấn: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadChamCongData(DefaultTableModel model) {
        model.setRowCount(0); // Clear existing data

        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MaCc, MaNvdp, MaNvtg, NgayCong, GhiChu, TrangThai FROM ChamCong ORDER BY NgayCong DESC";

            try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("MaCc"),
                        rs.getInt("MaNvdp"),
                        rs.getInt("MaNvtg"),
                        dateFormat.format(rs.getDate("NgayCong")),
                        rs.getString("GhiChu"),
                        rs.getString("TrangThai")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu chấm công: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
    }
}

    private JPanel createThongKePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Thống kê báo cáo");
        titleLabel.setFont(titleFont);

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Chart type selector
        String[] chartTypes = {"Thống kê khối lượng rác", "Thống kê doanh thu", "Thống kê nhân viên", "Thống kê yêu cầu đặt lịch", "Thống kê khách hàng"};
        JComboBox<String> chartTypePicker = new JComboBox<>(chartTypes);

        dateChooser = new JDateChooser();
        // Set date format to only show month and year
        ((JTextFieldDateEditor) dateChooser.getDateEditor()).setDateFormatString("dd/MM/yyyy");
        dateChooser.setPreferredSize(new Dimension(100, 30));
        // Set default date to current date
        dateChooser.setDate(new Date());

        JButton exportButton = new JButton("Xuất báo cáo");
        styleButton(exportButton);

        controlPanel.add(new JLabel("Loại biểu đồ: "));
        controlPanel.add(chartTypePicker);
        controlPanel.add(new JLabel("Chọn tháng: "));
        controlPanel.add(dateChooser);
        controlPanel.add(exportButton);

        exportButton.addActionListener(e -> exportThongKeExcel(chartTypePicker.getSelectedItem().toString()));

        // Add property change listener to update charts when date changes
        dateChooser.addPropertyChangeListener("date", e -> {
            if (dateChooser.getDate() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateChooser.getDate());
                currentMonth = cal.get(Calendar.MONTH) + 1; // Calendar months are 0-based
                currentYear = cal.get(Calendar.YEAR);
                updateAllCharts();
            }
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(controlPanel, BorderLayout.EAST);

        // Content Panel - now only shows one chart
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        contentPanel.setBackground(Color.WHITE);

        // Create a card layout panel to switch between charts
        JPanel chartPanel = new JPanel(new CardLayout());
        chartPanel.setBackground(Color.WHITE);

        // Panel 2: Thống kê doanh thu (Bar Chart)
        DefaultCategoryDataset revenueDataset = new DefaultCategoryDataset();
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String revenueQuery = "SELECT SUM(SoTien) as DoanhThu "
                    + "FROM HoaDon "
                    + "WHERE EXTRACT(MONTH FROM NgLap) = ? "
                    + "AND EXTRACT(YEAR FROM NgLap) = ? "
                    + "AND TinhTrang = 'Đã thanh toán'";
            try (PreparedStatement pstmt = conn.prepareStatement(revenueQuery)) {
                pstmt.setInt(1, currentMonth);
                pstmt.setInt(2, currentYear);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    revenueDataset.addValue(rs.getDouble("DoanhThu"), "Doanh thu", "Tháng " + currentMonth + "/" + currentYear);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi lấy dữ liệu doanh thu: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }

        JFreeChart revenueChart = ChartFactory.createBarChart(
                "Thống kê doanh thu",
                "Tháng",
                "VND",
                revenueDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot revenuePlot = revenueChart.getCategoryPlot();
        revenuePlot.setBackgroundPaint(Color.WHITE);
        revenuePlot.setOutlinePaint(null);
        BarRenderer revenueRenderer = (BarRenderer) revenuePlot.getRenderer();
        revenueRenderer.setSeriesPaint(0, new Color(52, 152, 219));

        ChartPanel revenueChartPanel = new ChartPanel(revenueChart);
        revenueChartPanel.setBackground(Color.WHITE);

        JPanel revenuePanel = new JPanel(new BorderLayout());
        revenuePanel.setBackground(Color.WHITE);
        revenuePanel.add(revenueChartPanel);

        // Panel 3: Thống kê nhân viên (Bar Chart)
        DefaultCategoryDataset staffDataset = new DefaultCategoryDataset();
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String staffQuery = "SELECT 'Nhân viên điều phối' as LoaiNhanVien, COUNT(*) as SoLuong FROM NhanVienDieuPhoi "
                    + "UNION ALL "
                    + "SELECT 'Nhân viên thu gom' as LoaiNhanVien, COUNT(*) as SoLuong FROM NhanVienThuGom";
            try (PreparedStatement pstmt = conn.prepareStatement(staffQuery)) {
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    staffDataset.addValue(rs.getInt("SoLuong"), "Số lượng", rs.getString("LoaiNhanVien"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi lấy dữ liệu nhân viên: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }

        JFreeChart staffChart = ChartFactory.createBarChart(
                "Thống kê nhân viên",
                "Vị trí",
                "Số lượng",
                staffDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot staffPlot = staffChart.getCategoryPlot();
        staffPlot.setBackgroundPaint(Color.WHITE);
        staffPlot.setOutlinePaint(null);
        BarRenderer staffRenderer = (BarRenderer) staffPlot.getRenderer();
        staffRenderer.setSeriesPaint(0, new Color(46, 204, 113));

        ChartPanel staffChartPanel = new ChartPanel(staffChart);
        staffChartPanel.setBackground(Color.WHITE);

        JPanel staffPanel = new JPanel(new BorderLayout());
        staffPanel.setBackground(Color.WHITE);
        staffPanel.add(staffChartPanel);

        // Panel 4: Đánh giá dịch vụ (Pie Chart)
        DefaultPieDataset ratingDataset = new DefaultPieDataset();
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String requestQuery = "SELECT TrangThai, COUNT(*) as SoLuong "
                    + "FROM YeuCauDatLich "
                    + "WHERE EXTRACT(MONTH FROM ThoiGianYc) = ? "
                    + "AND EXTRACT(YEAR FROM ThoiGianYc) = ? "
                    + "GROUP BY TrangThai";
            try (PreparedStatement pstmt = conn.prepareStatement(requestQuery)) {
                pstmt.setInt(1, currentMonth);
                pstmt.setInt(2, currentYear);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    ratingDataset.setValue(rs.getString("TrangThai"), rs.getInt("SoLuong"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi lấy dữ liệu đánh giá: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }

        JFreeChart ratingChart = ChartFactory.createPieChart(
                "Đánh giá dịch vụ",
                ratingDataset,
                true,
                true,
                false
        );

        PiePlot ratingPlot = (PiePlot) ratingChart.getPlot();
        ratingPlot.setBackgroundPaint(Color.WHITE);
        ratingPlot.setOutlinePaint(null);
        ratingPlot.setSectionPaint("Đã duyệt", new Color(46, 204, 113));
        ratingPlot.setSectionPaint("Đang xử lý", new Color(255, 215, 0));
        ratingPlot.setSectionPaint("Từ chối", new Color(231, 76, 60));

        ChartPanel ratingChartPanel = new ChartPanel(ratingChart);
        ratingChartPanel.setBackground(Color.WHITE);

        JPanel ratingPanel = new JPanel(new BorderLayout());
        ratingPanel.setBackground(Color.WHITE);
        ratingPanel.add(ratingChartPanel);

        // Add charts to card layout
        chartPanel.add(createWasteChart(currentMonth, currentYear, dateChooser), "Thống kê khối lượng rác");
        chartPanel.add(createRevenueChart(currentMonth, currentYear), "Thống kê doanh thu");
        chartPanel.add(createStaffChart(), "Thống kê nhân viên");
        chartPanel.add(createRequestChart(currentMonth, currentYear), "Thống kê yêu cầu đặt lịch");
        chartPanel.add(createCustomerChart(currentMonth, currentYear), "Thống kê khách hàng");

        // Add listener to switch charts
        chartTypePicker.addActionListener(e -> {
            CardLayout cl = (CardLayout) chartPanel.getLayout();
            cl.show(chartPanel, (String) chartTypePicker.getSelectedItem());
        });

        // Add listener to update charts when date changes
        dateChooser.addPropertyChangeListener("date", e -> {
            if (dateChooser.getDate() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateChooser.getDate());
                currentMonth = cal.get(Calendar.MONTH) + 1;
                currentYear = cal.get(Calendar.YEAR);

                // Update all charts with new date
                chartPanel.removeAll();
                chartPanel.add(createWasteChart(currentMonth, currentYear, dateChooser), "Thống kê khối lượng rác");
                chartPanel.add(createRevenueChart(currentMonth, currentYear), "Thống kê doanh thu");
                chartPanel.add(createStaffChart(), "Thống kê nhân viên");
                chartPanel.add(createRequestChart(currentMonth, currentYear), "Thống kê yêu cầu đặt lịch");
                chartPanel.add(createCustomerChart(currentMonth, currentYear), "Thống kê khách hàng");

                // Show current selected chart
                CardLayout cl = (CardLayout) chartPanel.getLayout();
                cl.show(chartPanel, (String) chartTypePicker.getSelectedItem());
            }
        });

        contentPanel.add(chartPanel, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private void updateAllCharts() {
        try {
            // Get the content panel that contains the chart panel
            JPanel contentPanel = (JPanel) ((JPanel) mainPanel.getComponent(mainPanel.getComponentCount() - 1)).getComponent(1);
            JPanel chartPanel = (JPanel) contentPanel.getComponent(0);

            // Store the currently selected chart type
            String selectedChart = null;
            JPanel headerPanel = (JPanel) ((JPanel) mainPanel.getComponent(mainPanel.getComponentCount() - 1)).getComponent(0);
            JPanel controlPanel = (JPanel) headerPanel.getComponent(1);
            for (Component comp : controlPanel.getComponents()) {
                if (comp instanceof JComboBox) {
                    JComboBox<?> chartTypePicker = (JComboBox<?>) comp;
                    selectedChart = (String) chartTypePicker.getSelectedItem();
                    break;
                }
            }

            // Remove all existing charts
            chartPanel.removeAll();

            // Add updated charts
            chartPanel.add(createWasteChart(currentMonth, currentYear, dateChooser), "Thống kê khối lượng rác");
            chartPanel.add(createRevenueChart(currentMonth, currentYear), "Thống kê doanh thu");
            chartPanel.add(createStaffChart(), "Thống kê nhân viên");
            chartPanel.add(createRequestChart(currentMonth, currentYear), "Thống kê yêu cầu đặt lịch");
            chartPanel.add(createCustomerChart(currentMonth, currentYear), "Thống kê khách hàng");

            // Show the previously selected chart
            if (selectedChart != null) {
                CardLayout cl = (CardLayout) chartPanel.getLayout();
                cl.show(chartPanel, selectedChart);
            }

            // Revalidate and repaint
            chartPanel.revalidate();
            chartPanel.repaint();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi cập nhật biểu đồ: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createDangXuatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Panel chứa nội dung chính
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // Icon đăng xuất (có thể thay bằng ImageIcon thực tế)
        JLabel iconLabel = new JLabel("🚪", JLabel.CENTER);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label xác nhận
        JLabel confirmLabel = new JLabel("Bạn có chắc chắn muốn đăng xuất?", JLabel.CENTER);
        confirmLabel.setFont(new Font("Arial", Font.BOLD, 18));
        confirmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label thông tin phiên làm việc
        JLabel sessionLabel = new JLabel("Phiên làm việc sẽ kết thúc và bạn sẽ cần đăng nhập lại.", JLabel.CENTER);
        sessionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        sessionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel chứa các nút
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        // Nút Hủy
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setForeground(secondaryColor);
        cancelButton.setBorder(BorderFactory.createLineBorder(secondaryColor));
        cancelButton.setFocusPainted(false);

        // Nút Đăng xuất
        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.setPreferredSize(new Dimension(120, 40));
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorder(null);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> {
            dispose();
            new GiaoDienDangNhap().setVisible(true);
        });

        // Thêm các components vào panel
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(confirmLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(sessionLabel);
        contentPanel.add(Box.createVerticalStrut(30));

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(logoutButton);

        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalGlue());

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadTuyenThuGomData(DefaultTableModel model) {
        model.setRowCount(0); // Xóa dữ liệu cũ
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT * FROM TuyenDuongThuGom";

            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("MaTuyen"),
                        rs.getInt("MaDv"),
                        rs.getString("TenTuyen"),
                        rs.getInt("KhuVuc")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu tuyến thu gom: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ... existing code ...
    private void showSearchTuyenThuGomDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm tuyến thu gom", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 190);
        dialog.setLocationRelativeTo(this);

        JPanel searchPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Combo box cho tiêu chí tìm kiếm
        String[] searchCriteria = {
            "Mã tuyến",
            "Mã đơn vị",
            "Tên tuyến",
            "Khu vực"
        };
        JComboBox<String> criteriaComboBox = new JComboBox<>(searchCriteria);
        JTextField searchField = new JTextField();

        // ComboBox cho Khu vực (Mã quận)
        JComboBox<String> khuVucComboBox = new JComboBox<>();
        khuVucComboBox.addItem(""); // Thêm lựa chọn trống
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT MaQuan, TenQuan FROM Quan ORDER BY MaQuan";
            try (PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    khuVucComboBox.addItem(rs.getInt("MaQuan") + " - " + rs.getString("TenQuan"));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dialog, "Lỗi khi tải danh sách quận: " + ex.getMessage());
        }

        // Panel chứa các loại input
        JPanel inputPanel = new JPanel(new CardLayout());
        inputPanel.add(searchField, "text");
        inputPanel.add(khuVucComboBox, "khuVuc");

        searchPanel.add(criteriaComboBox);
        searchPanel.add(inputPanel);

        // Xử lý sự kiện khi thay đổi tiêu chí tìm kiếm
        criteriaComboBox.addActionListener(e -> {
            String selected = (String) criteriaComboBox.getSelectedItem();
            CardLayout cl = (CardLayout) inputPanel.getLayout();
            if (selected.equals("Khu vực")) {
                cl.show(inputPanel, "khuVuc");
            } else {
                cl.show(inputPanel, "text");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = new JButton("Tìm kiếm");
        JButton cancelButton = new JButton("Hủy");

        styleButton(searchButton);
        styleButton(cancelButton);

        buttonPanel.add(cancelButton);
        buttonPanel.add(searchButton);

        searchButton.addActionListener(e -> {
            String selectedCriteria = (String) criteriaComboBox.getSelectedItem();
            String searchValue = "";

            if (selectedCriteria.equals("Khu vực")) {
                String selected = (String) khuVucComboBox.getSelectedItem();
                if (selected.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng chọn khu vực!");
                    return;
                }
                searchValue = selected.split(" - ")[0]; // Lấy mã quận
            } else {
                searchValue = searchField.getText().trim();
                if (searchValue.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng nhập giá trị tìm kiếm!");
                    return;
                }
            }

            try {
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "SELECT * FROM TuyenDuongThuGom WHERE ";

                switch (selectedCriteria) {
                    case "Mã tuyến":
                        sql += "MaTuyen = ?";
                        break;
                    case "Mã đơn vị":
                        sql += "MaDv = ?";
                        break;
                    case "Tên tuyến":
                        sql += "UPPER(TenTuyen) LIKE UPPER(?)";
                        searchValue = "%" + searchValue + "%";
                        break;
                    case "Khu vực":
                        sql += "KhuVuc = ?";
                        break;
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    if (selectedCriteria.equals("Tên tuyến")) {
                        pstmt.setString(1, searchValue);
                    } else {
                        pstmt.setInt(1, Integer.parseInt(searchValue));
                    }

                    ResultSet rs = pstmt.executeQuery();
                    model.setRowCount(0);

                    while (rs.next()) {
                        Object[] row = {
                            rs.getInt("MaTuyen"),
                            rs.getInt("MaDv"),
                            rs.getString("TenTuyen"),
                            rs.getInt("KhuVuc")
                        };
                        model.addRow(row);
                    }

                    if (model.getRowCount() == 0) {
                        dialog.dispose();
                    }
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Giá trị tìm kiếm không hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi tìm kiếm: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(searchPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
// ... existing code ...

    private void showDeletePhanAnhDialog(DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phản ánh cần xóa!");
            return;
        }

        int maPA = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa phản ánh này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "DELETE FROM PhanAnh WHERE MaPA = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(sql)) {
                    deleteStmt.setInt(1, maPA);
                    int result = deleteStmt.executeUpdate();

                    if (result > 0) {
                        JOptionPane.showMessageDialog(this, "Xóa phản ánh thành công!");
                        loadPhanAnhData(model);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa phản ánh: " + ex.getMessage());
            }
        }
    }

    private void showEditPhanAnhDialog(DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phản ánh cần sửa!");
            return;
        }

        int maPA = (int) model.getValueAt(selectedRow, 0);
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT * FROM PhanAnh WHERE MaPA = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maPA);
                ResultSet rs = pstmt.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy phản ánh!");
                    return;
                }

                // Create edit dialog
                JDialog dialog = new JDialog(this, "Sửa thông tin phản ánh", true);
                dialog.setLayout(new BorderLayout(10, 10));
                dialog.setSize(400, 300);
                dialog.setLocationRelativeTo(this);

                JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
                formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                formPanel.add(new JLabel("Mã chủ thải:"));
                JTextField maChuThaiField = new JTextField(String.valueOf(rs.getInt("MaChuThai")));
                formPanel.add(maChuThaiField);

                formPanel.add(new JLabel("Nội dung:"));
                JTextField noiDungField = new JTextField(rs.getString("NoiDung"));
                formPanel.add(noiDungField);

                formPanel.add(new JLabel("Trạng thái:"));
                String[] trangThaiOptions = {"Đang xử lý", "Đã xử lý"};
                JComboBox<String> trangThaiCombo = new JComboBox<>(trangThaiOptions);
                trangThaiCombo.setSelectedItem(rs.getString("TrangThai"));
                formPanel.add(trangThaiCombo);

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
                    try {
                        String updateSql = "UPDATE PhanAnh SET MaChuThai = ?, NoiDung = ?, TrangThai = ? WHERE MaPA = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, Integer.parseInt(maChuThaiField.getText().trim()));
                            updateStmt.setString(2, noiDungField.getText().trim());
                            updateStmt.setString(3, (String) trangThaiCombo.getSelectedItem());
                            updateStmt.setInt(4, maPA);

                            int result = updateStmt.executeUpdate();
                            if (result > 0) {
                                JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!");
                                loadPhanAnhData(model);
                                dialog.dispose();
                            }
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật: " + ex.getMessage());
                    }
                });

                cancelButton.addActionListener(e -> dialog.dispose());
                dialog.setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi sửa phản ánh: " + ex.getMessage());
        }
    }

    private void showSearchPhanAnhDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm phản ánh", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 210);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Combo box cho tiêu chí tìm kiếm
        String[] searchCriteria = {
            "Mã phản ánh",
            "Mã chủ thải",
            "Thời gian gửi",
            "Trạng thái"
        };
        JComboBox<String> criteriaCombo = new JComboBox<>(searchCriteria);
        criteriaCombo.setPreferredSize(new Dimension(150, 35));

        formPanel.add(criteriaCombo);

        // Panel cho giá trị tìm kiếm
        JPanel valuePanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(150, 35));
        String[] trangThaiOptions = {"", "Đã xử lý", "Đang xử lý"};
        JComboBox<String> trangThaiCombo = new JComboBox<>(trangThaiOptions);
        trangThaiCombo.setPreferredSize(new Dimension(150, 35));
        valuePanel.add(searchField, BorderLayout.CENTER);

        // Panel cho ngày
        JPanel datePanel = new JPanel(new BorderLayout(5, 5));
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(100, 35));
        datePanel.add(dateSpinner, BorderLayout.CENTER);
        datePanel.setVisible(false);

        // Panel chứa tất cả các loại input
        JPanel inputPanel = new JPanel(new CardLayout());
        inputPanel.add(valuePanel, "text");
        inputPanel.add(datePanel, "date");
        inputPanel.add(trangThaiCombo, "combo");
        // Xử lý sự kiện khi thay đổi tiêu chí tìm kiếm
        criteriaCombo.addActionListener(e -> {
            String selected = (String) criteriaCombo.getSelectedItem();
            CardLayout cl = (CardLayout) inputPanel.getLayout();
            if (selected.equals("Thời gian gửi")) {
                cl.show(inputPanel, "date");
            } else if (selected.equals("Trạng thái")) {
                cl.show(inputPanel, "combo");
            } else {
                cl.show(inputPanel, "text");
            }
        });

        formPanel.add(inputPanel, BorderLayout.CENTER);

        // Nút tìm kiếm và hủy
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
                StringBuilder sql = new StringBuilder("SELECT * FROM PhanAnh WHERE 1=1");
                java.util.List<Object> params = new ArrayList<>();

                String selected = (String) criteriaCombo.getSelectedItem();
                switch (selected) {
                    case "Mã phản ánh":
                        sql.append(" AND MaPA = ?");
                        params.add(Integer.parseInt(searchField.getText().trim()));
                        break;
                    case "Mã chủ thải":
                        sql.append(" AND MaChuThai = ?");
                        params.add(Integer.parseInt(searchField.getText().trim()));
                        break;
                    case "Thời gian gửi":
                        sql.append(" AND TRUNC(ThoiGianGui) = ?");
                        params.add(new java.sql.Date(((Date) dateSpinner.getValue()).getTime()));
                        break;
                    case "Trạng thái":
                        sql.append(" AND TrangThai = ?");
                        params.add(trangThaiCombo.getSelectedItem());
                        break;
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    ResultSet rs = pstmt.executeQuery();
                    model.setRowCount(0);

                    while (rs.next()) {
                        // Format ngày tháng
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String thoiGianGui = rs.getDate("ThoiGianGui") != null
                                ? dateFormat.format(rs.getDate("ThoiGianGui")) : "";

                        model.addRow(new Object[]{
                            rs.getInt("MaPA"),
                            rs.getInt("MaChuThai"),
                            rs.getString("NoiDung"),
                            thoiGianGui,
                            rs.getString("TrangThai")
                        });
                    }

                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập giá trị số hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi tìm kiếm: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showDeleteYeuCauDatLichDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn yêu cầu đặt lịch cần xóa!");
            return;
        }

        int maYC = (int) model.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa yêu cầu đặt lịch này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "DELETE FROM YeuCauDatLich WHERE MaYc = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(sql)) {
                    deleteStmt.setInt(1, maYC);
                    int result = deleteStmt.executeUpdate();

                    if (result > 0) {
                        // Xóa file txt tương ứng
                        int maFile = maYC - 1;
                        String filePath = "src/DatLichImg/" + maFile + ".txt";
                        java.io.File file = new java.io.File(filePath);
                        if (file.exists()) {
                            file.delete();
                        }
                        JOptionPane.showMessageDialog(this, "Xóa yêu cầu đặt lịch thành công!");
                        loadYeuCauDatLichData(model);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa yêu cầu đặt lịch: " + ex.getMessage());
            }
        }
    }

    private void showEditYeuCauDatLichDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn yêu cầu đặt lịch cần sửa");
            return;
        }

        try {
            int maYC = (int) model.getValueAt(selectedRow, 0);

            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT * FROM YeuCauDatLich WHERE MaYc = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, maYC);
                ResultSet rs = pstmt.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy mã yêu cầu đặt lịch: " + maYC);
                    return;
                }

                // Tạo dialog sửa
                JDialog dialog = new JDialog(this, "Sửa yêu cầu đặt lịch", true);
                dialog.setLayout(new BorderLayout(10, 10));
                dialog.setSize(400, 300);
                dialog.setLocationRelativeTo(this);

                JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
                formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

                formPanel.add(new JLabel("Mã chủ thải:"));
                JTextField maChuThaiField = new JTextField(String.valueOf(rs.getInt("MaChuThai")));
                formPanel.add(maChuThaiField);

                formPanel.add(new JLabel("Mã lịch:"));
                JTextField maLichField = new JTextField(String.valueOf(rs.getInt("MaLich")));
                formPanel.add(maLichField);

                formPanel.add(new JLabel("Thời gian yêu cầu:"));
                SpinnerDateModel dateModel = new SpinnerDateModel();
                JSpinner dateSpinner = new JSpinner(dateModel);
                dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
                // Set initial value from database
                try {
                    java.sql.Date dbDate = rs.getDate("ThoiGianYc");
                    if (dbDate != null) {
                        dateSpinner.setValue(dbDate);
                    }
                } catch (SQLException ex) {
                    // If there's an error reading the date, use current date
                    dateSpinner.setValue(new Date());
                }
                formPanel.add(dateSpinner);

                formPanel.add(new JLabel("Ghi chú:"));
                JTextField ghiChuField = new JTextField(rs.getString("GhiChu"));
                formPanel.add(ghiChuField);

                formPanel.add(new JLabel("Trạng thái:"));
                String[] trangThaiOptions = {"Đang xử lý", "Đã duyệt", "Từ chối"};
                JComboBox<String> trangThaiCombo = new JComboBox<>(trangThaiOptions);
                trangThaiCombo.setSelectedItem(rs.getString("TrangThai"));
                formPanel.add(trangThaiCombo);

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
                    try {
                        String updateSql = "UPDATE YeuCauDatLich SET MaChuThai = ?, MaLich = ?, ThoiGianYc = ?, GhiChu = ?, TrangThai = ? WHERE MaYc = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, Integer.parseInt(maChuThaiField.getText().trim()));
                            updateStmt.setInt(2, Integer.parseInt(maLichField.getText().trim()));
                            // Convert spinner date to java.sql.Date
                            Date selectedDate = (Date) dateSpinner.getValue();
                            updateStmt.setDate(3, new java.sql.Date(selectedDate.getTime()));
                            updateStmt.setString(4, ghiChuField.getText().trim());
                            updateStmt.setString(5, (String) trangThaiCombo.getSelectedItem());
                            updateStmt.setInt(6, maYC);

                            int result = updateStmt.executeUpdate();
                            if (result > 0) {
                                JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!");
                                loadYeuCauDatLichData(model);
                                dialog.dispose();
                            }
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật: " + ex.getMessage());
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(dialog, "Thời gian không đúng định dạng (yyyy-MM-dd)!");
                    }
                });

                cancelButton.addActionListener(e -> dialog.dispose());
                dialog.setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi sửa yêu cầu đặt lịch: " + ex.getMessage());
        }
    }

//    private void showSearchYeuCauDatLichDialog(DefaultTableModel model) {
//        String maYC = JOptionPane.showInputDialog(this, "Nhập mã yêu cầu đặt lịch cần tìm:");
//        if (maYC == null || maYC.trim().isEmpty()) {
//            loadYeuCauDatLichData(model);
//            return;
//        }
//
//        try {
//            Connection conn = ConnectionJDBC.getConnection();
//            String sql = "SELECT * FROM YeuCauDatLich WHERE MaYC = ?";
//            
//            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//                pstmt.setInt(1, Integer.parseInt(maYC.trim()));
//                ResultSet rs = pstmt.executeQuery();
//                
//                if (rs.next()) {
//                    model.setRowCount(0);
//                    model.addRow(new Object[]{
//                        rs.getInt("MaYC"),
//                        rs.getInt("MaChuThai"),
//                        rs.getString("NgayThuGom"),
//                        rs.getString("GioThuGom"),
//                        rs.getString("LoaiRac"),
//                        rs.getString("TrangThai")
//                    });
//                } else {
//                    JOptionPane.showMessageDialog(this, "Không có mã yêu cầu đặt lịch " + maYC);
//                    loadYeuCauDatLichData(model);
//                }
//            }
//        } catch (NumberFormatException ex) {
//            JOptionPane.showMessageDialog(this, "Mã yêu cầu đặt lịch không hợp lệ!");
//        } catch (SQLException ex) {
//            JOptionPane.showMessageDialog(this, "Lỗi khi tìm yêu cầu đặt lịch: " + ex.getMessage());
//        }
//    }
    private void showSearchYeuCauDatLichDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm yêu cầu đặt lịch", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 210);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        // Các tiêu chí tìm kiếm
        String[] searchCriteria = {
            "Mã yêu cầu",
            "Mã chủ thải",
            "Mã lịch",
            "Trạng thái",
            "Thời gian yêu cầu"
        };
        JComboBox<String> criteriaCombo = new JComboBox<>(searchCriteria);
        criteriaCombo.setPreferredSize(new Dimension(150, 35));
        formPanel.add(criteriaCombo);

        // Input panel
        JPanel textPanel = new JPanel(new BorderLayout());
        JTextField textField = new JTextField();
        String[] trangThaiOptions = {"", "Đang xử lý", "Đã duyệt", "Từ chối"};
        JComboBox<String> trangThaiCombo = new JComboBox<>(trangThaiOptions);
        trangThaiCombo.setPreferredSize(new Dimension(150, 35));
        textField.setPreferredSize(new Dimension(150, 35));
        textPanel.add(textField, BorderLayout.CENTER);

        JPanel datePanel = new JPanel(new BorderLayout());
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        dateSpinner.setPreferredSize(new Dimension(150, 35));
        datePanel.add(dateSpinner, BorderLayout.CENTER);
        datePanel.setVisible(false);

        JPanel inputPanel = new JPanel(new CardLayout());
        inputPanel.add(textPanel, "text");
        inputPanel.add(datePanel, "date");
        inputPanel.add(trangThaiCombo, "combo");
        formPanel.add(inputPanel);

        // Chuyển đổi UI khi thay đổi tiêu chí
        criteriaCombo.addActionListener(e -> {
            String selected = (String) criteriaCombo.getSelectedItem();
            CardLayout cl = (CardLayout) inputPanel.getLayout();
            if ("Thời gian yêu cầu".equals(selected)) {
                cl.show(inputPanel, "date");
            }
            if ("Trạng thái".equals(selected)) {
                cl.show(inputPanel, "combo");
                        } else {
                cl.show(inputPanel, "text");
            }
        });

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = new JButton("Tìm kiếm");
        JButton cancelButton = new JButton("Hủy");
        styleButton(searchButton);
        styleButton(cancelButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(searchButton);

        // Xử lý sự kiện tìm kiếm
        searchButton.addActionListener(e -> {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                StringBuilder sql = new StringBuilder("SELECT * FROM YeuCauDatLich WHERE 1=1");
                List<Object> params = new ArrayList<>();

                String selected = (String) criteriaCombo.getSelectedItem();
                switch (selected) {
                    case "Mã yêu cầu":
                        sql.append(" AND MaYc = ?");
                        params.add(Integer.parseInt(textField.getText().trim()));
                        break;
                    case "Mã chủ thải":
                        sql.append(" AND MaChuThai = ?");
                        params.add(Integer.parseInt(textField.getText().trim()));
                        break;
                    case "Mã lịch":
                        sql.append(" AND MaLich = ?");
                        params.add(Integer.parseInt(textField.getText().trim()));
                        break;
                    case "Ghi chú":
                        sql.append(" AND GhiChu LIKE ?");
                        params.add("%" + textField.getText().trim() + "%");
                        break;
                    case "Trạng thái":
                        sql.append(" AND TrangThai = ?");
                        params.add(trangThaiCombo.getSelectedItem());
                        break;
                    case "Thời gian yêu cầu":
                        sql.append(" AND TRUNC(ThoiGianYc) = ?");
                        Date selectedDate = (Date) dateSpinner.getValue();
                        params.add(new java.sql.Date(selectedDate.getTime()));
                        break;
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    ResultSet rs = pstmt.executeQuery();
                    model.setRowCount(0);
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

                    while (rs.next()) {
                        String thoiGianStr = rs.getTimestamp("ThoiGianYc") != null
                                ? df.format(rs.getTimestamp("ThoiGianYc"))
                                : "";
                        model.addRow(new Object[]{
                            rs.getInt("MaYc"),
                            rs.getInt("MaChuThai"),
                            rs.getInt("MaLich"),
                            thoiGianStr,
                            rs.getString("GhiChu"),
                            rs.getString("TrangThai")
                        });
                    }

                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Giá trị nhập không hợp lệ!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi tìm kiếm: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JPanel createQuanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Quản lý quận");
        titleLabel.setFont(titleFont);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Thêm");
        JButton deleteButton = new JButton("Xóa");
        JButton editButton = new JButton("Sửa");
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        JButton exportButton = new JButton("Xuất excel");

        styleButton(addButton);
        styleButton(deleteButton);
        styleButton(editButton);
        styleButton(searchButton);
        styleButton(refreshButton);
        styleButton(exportButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(exportButton);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // Table
        String[] columns = {
            "Mã quận",
            "Tên quận"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Load dữ liệu từ database
        loadQuanData(model);

        // Thêm action listener cho các nút
        addButton.addActionListener(e -> showAddQuanDialog(model));
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showDeleteQuanDialog(model, selectedRow);
        });
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            showEditQuanDialog(model, selectedRow);
        });
        searchButton.addActionListener(e -> showSearchQuanDialog(model));
        refreshButton.addActionListener(e -> loadQuanData(model));
        exportButton.addActionListener(e -> exportQuanExcel(model));
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadQuanData(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String sql = "SELECT * FROM Quan ORDER BY MaQuan";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    Object[] row = {
                        rs.getString("MaQuan"),
                        rs.getString("TenQuan")
                    };
                    model.addRow(row);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi tải dữ liệu quận: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showAddQuanDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Thêm quận mới", true);
                dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
                dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
                formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField maQuanField = new JTextField();
        JTextField tenQuanField = new JTextField();

        formPanel.add(new JLabel("Mã quận:"));
        formPanel.add(maQuanField);
        formPanel.add(new JLabel("Tên quận:"));
        formPanel.add(tenQuanField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");

        styleButton(saveButton);
        styleButton(cancelButton);

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                String maQuan = maQuanField.getText().trim();
                String tenQuan = tenQuanField.getText().trim();

                if (maQuan.isEmpty() || tenQuan.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Vui lòng điền đầy đủ thông tin!",
                            "Cảnh báo",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Connection conn = ConnectionJDBC.getConnection();
                String sql = "INSERT INTO Quan (MaQuan, TenQuan) VALUES (?, ?)";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, maQuan);
                    pstmt.setString(2, tenQuan);

                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(dialog,
                                "Thêm quận thành công!",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadQuanData(model);
                        dialog.dispose();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi khi thêm quận: " + ex.getMessage(),
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

    private void showDeleteQuanDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn quận cần xóa!");
            return;
        }

        String maQuanStr = model.getValueAt(selectedRow, 0).toString();
        String tenQuan = (String) model.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa quận này" + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String deleteSql = "DELETE FROM Quan WHERE MaQuan = ?";
                try (Connection conn = ConnectionJDBC.getConnection(); PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {

                    deleteStmt.setString(1, maQuanStr);
                    deleteStmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Xóa quận thành công!");
                    loadQuanData(model);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa quận: " + ex.getMessage());
            }
        }
    }

    private void showEditQuanDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn quận cần sửa!");
            return;
        }

        // Lấy dữ liệu từ model
        String maQuanStr = model.getValueAt(selectedRow, 0).toString();
        String tenQuan = (String) model.getValueAt(selectedRow, 1);

        // Create edit dialog
        JDialog dialog = new JDialog(this, "Sửa quận", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 150);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tên quận
        formPanel.add(new JLabel("Tên quận:"));
        JTextField tenQuanField = new JTextField(tenQuan, 10);
        formPanel.add(tenQuanField);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton saveButton = new JButton("Lưu");
                JButton cancelButton = new JButton("Hủy");

                styleButton(saveButton);
                styleButton(cancelButton);

                buttonPanel.add(cancelButton);
                buttonPanel.add(saveButton);

                saveButton.addActionListener(e -> {
                    try {
                // Validate input
                if (tenQuanField.getText().trim().isEmpty()) {
                            JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!");
                            return;
                        }

                // Update database
                String updateSql = "UPDATE Quan SET TenQuan = ? WHERE MaQuan = ?";

                try (Connection conn = ConnectionJDBC.getConnection(); PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

                    updateStmt.setString(1, tenQuanField.getText().trim());
                    updateStmt.setString(2, maQuanStr);

                            updateStmt.executeUpdate();
                    JOptionPane.showMessageDialog(dialog, "Cập nhật quận thành công!");
                    loadQuanData(model);
                            dialog.dispose();
                        }
                    } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật quận: " + ex.getMessage());
                    }
                });

                cancelButton.addActionListener(e -> dialog.dispose());

                dialog.add(formPanel, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                dialog.setVisible(true);
            }

    private void showSearchQuanDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm quận", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 190);
        dialog.setLocationRelativeTo(this);

        JPanel searchPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Combo box cho tiêu chí tìm kiếm
        String[] searchCriteria = {
            "Mã quận",
            "Tên quận"
        };
        JComboBox<String> criteriaComboBox = new JComboBox<>(searchCriteria);
        JTextField searchField = new JTextField();

        searchPanel.add(criteriaComboBox);
        searchPanel.add(searchField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = new JButton("Tìm kiếm");
        JButton cancelButton = new JButton("Hủy");

        styleButton(searchButton);
        styleButton(cancelButton);

        buttonPanel.add(cancelButton);
        buttonPanel.add(searchButton);

        searchButton.addActionListener(e -> {
            String selectedCriteria = (String) criteriaComboBox.getSelectedItem();
            String searchValue = searchField.getText().trim();

            if (searchValue.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập giá trị tìm kiếm!");
                return;
            }

            try {
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "SELECT * FROM Quan WHERE ";

                switch (selectedCriteria) {
                    case "Mã quận":
                        sql += "MaQuan = ?";
                        break;
                    case "Tên quận":
                        sql += "UPPER(TenQuan) LIKE UPPER(?)";
                        searchValue = "%" + searchValue + "%";
                        break;
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    if (selectedCriteria.equals("Tên quận")) {
                        pstmt.setString(1, searchValue);
                    } else {
                        pstmt.setInt(1, Integer.parseInt(searchValue));
                    }

                    ResultSet rs = pstmt.executeQuery();
                    model.setRowCount(0);

                    while (rs.next()) {
                        Object[] row = {
                            rs.getInt("MaQuan"),
                            rs.getString("TenQuan")
                        };
                        model.addRow(row);
                    }

                    if (model.getRowCount() == 0) {

                        dialog.dispose();
                    }
                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Giá trị tìm kiếm không hợp lệ!");
        } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi tìm kiếm: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(searchPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showSearchChuThaiDialog(DefaultTableModel model) {
        JDialog dialog = new JDialog(this, "Tìm kiếm chủ thải", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(420, 180);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        // Combo box tiêu chí tìm kiếm
        String[] searchCriteria = {
            "Mã chủ thải",
            "Username",
            "Họ tên",
            "Địa chỉ",
            "Số điện thoại",
            "Email",
            "Loại chủ thải"
        };
        JComboBox<String> criteriaCombo = new JComboBox<>(searchCriteria);
        criteriaCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        formPanel.add(criteriaCombo);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // TextField và ComboBox nhập giá trị tìm kiếm
        JTextField searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        String[] loaiChuThaiOptions = {"", "Cá nhân", "Doanh nghiệp"};
        JComboBox<String> loaiChuThaiCombo = new JComboBox<>(loaiChuThaiOptions);
        loaiChuThaiCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        // CardLayout để hoán đổi giữa textField và comboBox
        JPanel inputPanel = new JPanel(new CardLayout());
        inputPanel.add(searchField, "text");
        inputPanel.add(loaiChuThaiCombo, "combo");
        formPanel.add(inputPanel);

        // Xử lý chuyển đổi input theo tiêu chí
        criteriaCombo.addActionListener(e -> {
            String selected = (String) criteriaCombo.getSelectedItem();
            CardLayout cl = (CardLayout) inputPanel.getLayout();
            if ("Loại chủ thải".equals(selected)) {
                cl.show(inputPanel, "combo");
            } else {
                cl.show(inputPanel, "text");
            }
        });

        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchButton = new JButton("Tìm kiếm");
        JButton cancelButton = new JButton("Hủy");
        styleButton(searchButton);
        styleButton(cancelButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(searchButton);

        // Gắn sự kiện (hàm xử lý bạn đã viết sẵn — giữ nguyên như bạn yêu cầu)
        searchButton.addActionListener(e -> {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                StringBuilder sql = new StringBuilder("""
                SELECT * FROM ChuThai 
                WHERE 1=1
            """);

                String selected = (String) criteriaCombo.getSelectedItem();
                String input = "";
                if (selected.equals("Loại chủ thải")) {
                    input = (String) loaiChuThaiCombo.getSelectedItem();
                } else {
                    input = searchField.getText().trim();
                }

                List<Object> params = new ArrayList<>();

                switch (selected) {
                    case "Mã chủ thải":
                        sql.append(" AND MaChuThai = ?");
                        params.add(Integer.parseInt(input));
                        break;
                    case "Username":
                        sql.append(" AND LOWER(Username) LIKE ?");
                        params.add("%" + input.toLowerCase() + "%");
                        break;
                    case "Họ tên":
                        sql.append(" AND LOWER(HoTen) LIKE ?");
                        params.add("%" + input.toLowerCase() + "%");
                        break;
                    case "Địa chỉ":
                        sql.append(" AND LOWER(DiaChi) LIKE ?");
                        params.add("%" + input.toLowerCase() + "%");
                        break;
                    case "Số điện thoại":
                        sql.append(" AND Sdt LIKE ?");
                        params.add("%" + input + "%");
                        break;
                    case "Email":
                        sql.append(" AND LOWER(Email) LIKE ?");
                        params.add("%" + input.toLowerCase() + "%");
                        break;
                    case "Loại chủ thải":
                        sql.append(" AND LOWER(LoaiChuThai) LIKE ?");
                        params.add("%" + input.toLowerCase() + "%");
                        break;
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }

                    ResultSet rs = pstmt.executeQuery();
                    model.setRowCount(0);
                    while (rs.next()) {
                        Object[] row = {
                            rs.getString("MaChuThai"),
                            rs.getString("Username"),
                            rs.getString("Password"),
                            rs.getString("HoTen"),
                            rs.getString("DiaChi"),
                            rs.getString("Sdt"),
                            rs.getString("Email"),
                            rs.getString("LoaiChuThai")
                        };
                        model.addRow(row);
                    }

                    if (model.getRowCount() == 0) {
                        dialog.dispose();
                    }

                    dialog.dispose();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập số hợp lệ cho mã chủ thải!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi truy vấn: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditChuThaiDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn chủ thải cần sửa");
            return;
        }

        JDialog dialog = new JDialog(this, "Sửa thông tin chủ thải", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Get current values from selected row with null checks
        String maChuThai = model.getValueAt(selectedRow, 0) != null ? model.getValueAt(selectedRow, 0).toString() : "";
        String username = model.getValueAt(selectedRow, 1) != null ? model.getValueAt(selectedRow, 1).toString() : "";
        String password = model.getValueAt(selectedRow, 2) != null ? model.getValueAt(selectedRow, 2).toString() : "";
        String hoTen = model.getValueAt(selectedRow, 3) != null ? model.getValueAt(selectedRow, 3).toString() : "";
        String diaChi = model.getValueAt(selectedRow, 4) != null ? model.getValueAt(selectedRow, 4).toString() : "";
        String sdt = model.getValueAt(selectedRow, 5) != null ? model.getValueAt(selectedRow, 5).toString() : "";
        String email = model.getValueAt(selectedRow, 6) != null ? model.getValueAt(selectedRow, 6).toString() : "";
        String loaiChuThai = model.getValueAt(selectedRow, 7) != null ? model.getValueAt(selectedRow, 7).toString() : "";

        // Create form fields
        JTextField usernameField = new JTextField(username);
        JTextField passwordField = new JTextField(password);
        JTextField hoTenField = new JTextField(hoTen);
        JTextField diaChiField = new JTextField(diaChi);
        JTextField sdtField = new JTextField(sdt);
        JTextField emailField = new JTextField(email);
//        JTextField loaiChuThaiField = new JTextField(loaiChuThai);
        // ComboBox cho Loại chủ thải
        String[] loaiChuThaiOptions = {"Cá nhân", "Doanh nghiệp"};
        JComboBox<String> loaiChuThaiCombo = new JComboBox<>(loaiChuThaiOptions);
        loaiChuThaiCombo.setSelectedItem(loaiChuThai); // Set giá trị hiện tại

        // Add components to form
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Họ tên:"));
        formPanel.add(hoTenField);
        formPanel.add(new JLabel("Địa chỉ:"));
        formPanel.add(diaChiField);
        formPanel.add(new JLabel("Số điện thoại:"));
        formPanel.add(sdtField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Loại chủ thải:"));
//        formPanel.add(loaiChuThaiField);
        formPanel.add(loaiChuThaiCombo);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Lưu");
        JButton cancelButton = new JButton("Hủy");
        styleButton(saveButton);
        styleButton(cancelButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (usernameField.getText().trim().isEmpty()
                        || passwordField.getText().trim().isEmpty()
                        || hoTenField.getText().trim().isEmpty()
                        || diaChiField.getText().trim().isEmpty()
                        || sdtField.getText().trim().isEmpty()
                        || emailField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Vui lòng điền đầy đủ thông tin!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Connection conn = ConnectionJDBC.getConnection();
                String sql = """
                    UPDATE ChuThai 
                    SET Username = ?, Password = ?, HoTen = ?, DiaChi = ?, 
                        Sdt = ?, Email = ?, LoaiChuThai = ?
                    WHERE MaChuThai = ?
                    """;

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, usernameField.getText().trim());
                    pstmt.setString(2, passwordField.getText().trim());
                    pstmt.setString(3, hoTenField.getText().trim());
                    pstmt.setString(4, diaChiField.getText().trim());
                    pstmt.setString(5, sdtField.getText().trim());
                    pstmt.setString(6, emailField.getText().trim());
                    String selectedLoaiChuThai = (String) loaiChuThaiCombo.getSelectedItem();
                    pstmt.setString(7, selectedLoaiChuThai);
                    pstmt.setInt(8, Integer.parseInt(maChuThai));

                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!");
                        loadChuThaiData(model);
                        dialog.dispose();
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi khi cập nhật: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Lỗi: Mã chủ thải không hợp lệ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showDeleteChuThaiDialog(DefaultTableModel model, int selectedRow) {
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn chủ thải cần xóa");
            return;
        }

        // Get the MaChuThai and HoTen from selected row
        String maChuThai = model.getValueAt(selectedRow, 0).toString();
        String hoTen = model.getValueAt(selectedRow, 3).toString();

        // Show confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa chủ thải này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = ConnectionJDBC.getConnection();
                String sql = "DELETE FROM ChuThai WHERE MaChuThai = ?";

                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, Integer.parseInt(maChuThai));

                    int result = pstmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(this,
                                "Xóa chủ thải thành công!",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadChuThaiData(model); // Refresh table data
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi xóa chủ thải: " + ex.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi: Mã chủ thải không hợp lệ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new QuanLyRacThaiUI().setVisible(true);
        });
    }

    private ChartPanel createCustomerChart(int month, int year) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String query = "SELECT q.TenQuan, COUNT(DISTINCT hd.MaChuThai) as SoLuongKhachHang "
                    + "FROM Quan q "
                    + "JOIN TuyenDuongThuGom t ON t.KhuVuc = q.MaQuan "
                    + "JOIN LichThuGom l ON l.MaTuyen = t.MaTuyen "
                    + "JOIN YeuCauDatLich y ON y.MaLich = l.MaLich "
                    + "JOIN ChuThai ct ON ct.MaChuThai = y.MaChuThai "
                    + "JOIN HopDong hd ON hd.MaChuThai = ct.MaChuThai "
                    + "WHERE hd.TrangThai = 'Hoạt động' "
                    + "AND hd.NgKetThuc >= TO_DATE(?, 'DD/MM/YYYY') "
                    + "GROUP BY q.MaQuan, q.TenQuan "
                    + "ORDER BY q.TenQuan";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                // Tạo chuỗi ngày từ tháng và năm được chọn
                String selectedDate = String.format("01/%02d/%04d", month, year);
                pstmt.setString(1, selectedDate);

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    dataset.setValue(rs.getString("TenQuan"), rs.getInt("SoLuongKhachHang"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi lấy dữ liệu khách hàng: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }

        JFreeChart chart = ChartFactory.createPieChart3D(
                "Thống kê khách hàng theo quận",
                dataset,
                true,
                true,
                false
        );

        // Customize the chart
        PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setStartAngle(290);
        plot.setDepthFactor(0.2);

        // Set a rainbow color scheme for different districts
        int index = 0;
        for (Object key : dataset.getKeys()) {
            float hue = index / (float) dataset.getItemCount();
            Color color = Color.getHSBColor(hue, 0.7f, 0.95f);
            plot.setSectionPaint((Comparable<?>) key, color);
            index++;
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(Color.WHITE);
        return chartPanel;
    }

    private ChartPanel createWasteChart(int month, int year, JDateChooser dateChooser) {
        DefaultPieDataset wasteDataset = new DefaultPieDataset();
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String wasteQuery = "WITH ContractMonths AS ("
                    + "  SELECT hd.MaHopDong, "
                    + "         dv.TenDichVu, "
                    + "         cthd.KhoiLuong, "
                    + "         MONTHS_BETWEEN(hd.NgKetThuc, hd.NgBatDau) + 1 as TotalMonths, "
                    + "         hd.NgBatDau, "
                    + "         hd.NgKetThuc "
                    + "  FROM ChiTietHopDong cthd "
                    + "  JOIN DichVu dv ON cthd.MaDichVu = dv.MaDichVu "
                    + "  JOIN HopDong hd ON cthd.MaHopDong = hd.MaHopDong "
                    + "  WHERE hd.TrangThai = 'Hoạt động' "
                    + "    AND TO_DATE(?, 'DD/MM/YYYY') BETWEEN hd.NgBatDau AND hd.NgKetThuc "
                    + ") "
                    + "SELECT TenDichVu, SUM(KhoiLuong / TotalMonths) as TongKL "
                    + "FROM ContractMonths "
                    + "GROUP BY TenDichVu";
            try (PreparedStatement pstmt = conn.prepareStatement(wasteQuery)) {
                // Get the exact selected date from JDateChooser
                Calendar cal = Calendar.getInstance();
                cal.set(year, month - 1, 1); // month is 0-based in Calendar
                Date selectedDate = dateChooser.getDate();
                if (selectedDate == null) {
                    selectedDate = cal.getTime(); // Use first day of month if no date selected
                }
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                pstmt.setString(1, sdf.format(selectedDate));
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    wasteDataset.setValue(rs.getString("TenDichVu"), rs.getDouble("TongKL"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi lấy dữ liệu khối lượng rác: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }

        JFreeChart wasteChart = ChartFactory.createPieChart(
                String.format("Thống kê khối lượng rác - Tháng %02d/%d", month, year),
                wasteDataset,
                true,
                true,
                false
        );

        PiePlot wastePlot = (PiePlot) wasteChart.getPlot();
        wastePlot.setBackgroundPaint(Color.WHITE);
        wastePlot.setOutlinePaint(null);
        wastePlot.setSectionPaint("Rác sinh hoạt", new Color(46, 204, 113));
        wastePlot.setSectionPaint("Rác tái chế", new Color(52, 152, 219));
        wastePlot.setSectionPaint("Rác nguy hại", new Color(231, 76, 60));

        // Configure labels to show percentages
        wastePlot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0}: {2}%", // Format: name: percent%
                NumberFormat.getNumberInstance(),
                NumberFormat.getPercentInstance()
        ));

        // Adjust label font and color for better visibility
        wastePlot.setLabelFont(new Font("Arial", Font.BOLD, 12));
        wastePlot.setLabelBackgroundPaint(new Color(255, 255, 255, 180)); // Semi-transparent white
        wastePlot.setLabelOutlinePaint(null);
        wastePlot.setLabelShadowPaint(null);

        ChartPanel chartPanel = new ChartPanel(wasteChart);
        chartPanel.setBackground(Color.WHITE);
        return chartPanel;
    }

    private ChartPanel createRevenueChart(int month, int year) {
        DefaultCategoryDataset revenueDataset = new DefaultCategoryDataset();
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String revenueQuery = "SELECT SUM(SoTien) as DoanhThu "
                    + "FROM HoaDon "
                    + "WHERE EXTRACT(MONTH FROM NgLap) = ? "
                    + "AND EXTRACT(YEAR FROM NgLap) = ? "
                    + "AND TinhTrang = 'Đã thanh toán'";
            try (PreparedStatement pstmt = conn.prepareStatement(revenueQuery)) {
                pstmt.setInt(1, month);
                pstmt.setInt(2, year);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    revenueDataset.addValue(rs.getDouble("DoanhThu"), "Doanh thu", "Tháng " + month + "/" + year);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi lấy dữ liệu doanh thu: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }

        JFreeChart revenueChart = ChartFactory.createBarChart(
                "Thống kê doanh thu",
                "Tháng",
                "VND",
                revenueDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot revenuePlot = revenueChart.getCategoryPlot();
        revenuePlot.setBackgroundPaint(Color.WHITE);
        revenuePlot.setOutlinePaint(null);
        BarRenderer revenueRenderer = (BarRenderer) revenuePlot.getRenderer();
        revenueRenderer.setSeriesPaint(0, new Color(52, 152, 219));

        ChartPanel chartPanel = new ChartPanel(revenueChart);
        chartPanel.setBackground(Color.WHITE);
        return chartPanel;
    }

    private ChartPanel createStaffChart() {
        DefaultCategoryDataset staffDataset = new DefaultCategoryDataset();
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String staffQuery = "SELECT 'Nhân viên điều phối' as LoaiNhanVien, COUNT(*) as SoLuong FROM NhanVienDieuPhoi "
                    + "UNION ALL "
                    + "SELECT 'Nhân viên thu gom' as LoaiNhanVien, COUNT(*) as SoLuong FROM NhanVienThuGom";
            try (PreparedStatement pstmt = conn.prepareStatement(staffQuery)) {
                ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                    staffDataset.addValue(rs.getInt("SoLuong"), "Số lượng", rs.getString("LoaiNhanVien"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi lấy dữ liệu nhân viên: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }

        JFreeChart staffChart = ChartFactory.createBarChart(
                "Thống kê nhân viên",
                "Vị trí",
                "Số lượng",
                staffDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot staffPlot = staffChart.getCategoryPlot();
        staffPlot.setBackgroundPaint(Color.WHITE);
        staffPlot.setOutlinePaint(null);
        BarRenderer staffRenderer = (BarRenderer) staffPlot.getRenderer();
        staffRenderer.setSeriesPaint(0, new Color(46, 204, 113));

        ChartPanel chartPanel = new ChartPanel(staffChart);
        chartPanel.setBackground(Color.WHITE);
        return chartPanel;
    }

    private ChartPanel createRequestChart(int month, int year) {
        DefaultPieDataset ratingDataset = new DefaultPieDataset();
        try {
            Connection conn = ConnectionJDBC.getConnection();
            String requestQuery = "SELECT TrangThai, COUNT(*) as SoLuong "
                    + "FROM YeuCauDatLich "
                    + "WHERE EXTRACT(MONTH FROM ThoiGianYc) = ? "
                    + "AND EXTRACT(YEAR FROM ThoiGianYc) = ? "
                    + "GROUP BY TrangThai";
            try (PreparedStatement pstmt = conn.prepareStatement(requestQuery)) {
                pstmt.setInt(1, month);
                pstmt.setInt(2, year);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    ratingDataset.setValue(rs.getString("TrangThai"), rs.getInt("SoLuong"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi lấy dữ liệu đánh giá: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }

        JFreeChart ratingChart = ChartFactory.createPieChart(
                "Thống kê yêu cầu đặt lịch",
                ratingDataset,
                true,
                true,
                false
        );

        PiePlot ratingPlot = (PiePlot) ratingChart.getPlot();
        ratingPlot.setBackgroundPaint(Color.WHITE);
        ratingPlot.setOutlinePaint(null);
        ratingPlot.setSectionPaint("Đã duyệt", new Color(46, 204, 113));
        ratingPlot.setSectionPaint("Đang xử lý", new Color(255, 215, 0));
        ratingPlot.setSectionPaint("Từ chối", new Color(231, 76, 60));

        ChartPanel chartPanel = new ChartPanel(ratingChart);
        chartPanel.setBackground(Color.WHITE);
        return chartPanel;
    }

    private void exportThongKeExcel(String selectedChart) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Chọn nơi lưu file");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().toString();
                if (!filePath.endsWith(".xlsx")) {
                    filePath += ".xlsx";
                }

                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = workbook.createSheet("Thống kê");

                // Tạo header
                Row headerRow = sheet.createRow(0);
                Cell headerCell = headerRow.createCell(0);
                headerCell.setCellValue("Thống kê " + selectedChart + " - Tháng " + currentMonth + "/" + currentYear);

                int rowNum = 2;
                Connection conn = ConnectionJDBC.getConnection();

                switch (selectedChart) {
                    case "Thống kê khối lượng rác":
                        String wasteQuery = "SELECT dv.TenDichVu, SUM(cthd.KhoiLuong) as TongKL "
                                + "FROM ChiTietHopDong cthd "
                                + "JOIN DichVu dv ON cthd.MaDichVu = dv.MaDichVu "
                                + "JOIN HopDong hd ON cthd.MaHopDong = hd.MaHopDong "
                                + "WHERE EXTRACT(MONTH FROM hd.NgBatDau) = ? "
                                + "AND EXTRACT(YEAR FROM hd.NgBatDau) = ? "
                                + "AND hd.TrangThai = 'Hoạt động' "
                                + "GROUP BY dv.TenDichVu";

                        Row wasteHeaderRow = sheet.createRow(rowNum++);
                        wasteHeaderRow.createCell(0).setCellValue("Loại rác");
                        wasteHeaderRow.createCell(1).setCellValue("Khối lượng (kg)");

                        try (PreparedStatement pstmt = conn.prepareStatement(wasteQuery)) {
                            pstmt.setInt(1, currentMonth);
                            pstmt.setInt(2, currentYear);
                            ResultSet rs = pstmt.executeQuery();

                            while (rs.next()) {
                                Row row = sheet.createRow(rowNum++);
                                row.createCell(0).setCellValue(rs.getString("TenDichVu"));
                                row.createCell(1).setCellValue(rs.getDouble("TongKL"));
                            }
                        }
                        break;

                    case "Thống kê doanh thu":
                        String revenueQuery = "SELECT SUM(SoTien) as DoanhThu "
                                + "FROM HoaDon "
                                + "WHERE EXTRACT(MONTH FROM NgLap) = ? "
                                + "AND EXTRACT(YEAR FROM NgLap) = ? "
                                + "AND TinhTrang = 'Đã thanh toán'";

                        Row revenueHeaderRow = sheet.createRow(rowNum++);
                        revenueHeaderRow.createCell(0).setCellValue("Tháng");
                        revenueHeaderRow.createCell(1).setCellValue("Doanh thu (VND)");

                        try (PreparedStatement pstmt = conn.prepareStatement(revenueQuery)) {
                            pstmt.setInt(1, currentMonth);
                            pstmt.setInt(2, currentYear);
                            ResultSet rs = pstmt.executeQuery();

                            if (rs.next()) {
                                Row row = sheet.createRow(rowNum++);
                                row.createCell(0).setCellValue(currentMonth + "/" + currentYear);
                                row.createCell(1).setCellValue(rs.getDouble("DoanhThu"));
                            }
                        }
                        break;

                    case "Thống kê nhân viên":
                        String staffQuery = "SELECT 'Nhân viên điều phối' as LoaiNhanVien, COUNT(*) as SoLuong FROM NhanVienDieuPhoi "
                                + "UNION ALL "
                                + "SELECT 'Nhân viên thu gom' as LoaiNhanVien, COUNT(*) as SoLuong FROM NhanVienThuGom";

                        Row staffHeaderRow = sheet.createRow(rowNum++);
                        staffHeaderRow.createCell(0).setCellValue("Loại nhân viên");
                        staffHeaderRow.createCell(1).setCellValue("Số lượng");

                        try (PreparedStatement pstmt = conn.prepareStatement(staffQuery)) {
                            ResultSet rs = pstmt.executeQuery();

                            while (rs.next()) {
                                Row row = sheet.createRow(rowNum++);
                                row.createCell(0).setCellValue(rs.getString("LoaiNhanVien"));
                                row.createCell(1).setCellValue(rs.getInt("SoLuong"));
                            }
                        }
                        break;

                    case "Thống kê yêu cầu đặt lịch":
                        String requestQuery = "SELECT TrangThai, COUNT(*) as SoLuong "
                                + "FROM YeuCauDatLich "
                                + "WHERE EXTRACT(MONTH FROM ThoiGianYc) = ? "
                                + "AND EXTRACT(YEAR FROM ThoiGianYc) = ? "
                                + "GROUP BY TrangThai";

                        Row requestHeaderRow = sheet.createRow(rowNum++);
                        requestHeaderRow.createCell(0).setCellValue("Trạng thái");
                        requestHeaderRow.createCell(1).setCellValue("Số lượng");

                        try (PreparedStatement pstmt = conn.prepareStatement(requestQuery)) {
                            pstmt.setInt(1, currentMonth);
                            pstmt.setInt(2, currentYear);
                            ResultSet rs = pstmt.executeQuery();

                            while (rs.next()) {
                                Row row = sheet.createRow(rowNum++);
                                row.createCell(0).setCellValue(rs.getString("TrangThai"));
                                row.createCell(1).setCellValue(rs.getInt("SoLuong"));
                            }
                        }
                        break;

                    case "Thống kê khách hàng":
                        String customerQuery = "SELECT q.TenQuan, COUNT(DISTINCT hd.MaChuThai) as SoLuongKhachHang "
                                + "FROM Quan q "
                                + "JOIN TuyenDuongThuGom t ON t.KhuVuc = q.MaQuan "
                                + "JOIN LichThuGom l ON l.MaTuyen = t.MaTuyen "
                                + "JOIN YeuCauDatLich y ON y.MaLich = l.MaLich "
                                + "JOIN ChuThai ct ON ct.MaChuThai = y.MaChuThai "
                                + "JOIN HopDong hd ON hd.MaChuThai = ct.MaChuThai "
                                + "WHERE hd.TrangThai = 'Hoạt động' "
                                + "AND hd.NgKetThuc >= TO_DATE(?, 'DD/MM/YYYY') "
                                + "GROUP BY q.MaQuan, q.TenQuan "
                                + "ORDER BY q.TenQuan";

                        Row customerHeaderRow = sheet.createRow(rowNum++);
                        customerHeaderRow.createCell(0).setCellValue("Quận");
                        customerHeaderRow.createCell(1).setCellValue("Số lượng khách hàng");

                        try (PreparedStatement pstmt = conn.prepareStatement(customerQuery)) {
                            String selectedDate = String.format("01/%02d/%04d", currentMonth, currentYear);
                            pstmt.setString(1, selectedDate);
                            ResultSet rs = pstmt.executeQuery();

                            while (rs.next()) {
                                Row row = sheet.createRow(rowNum++);
                                row.createCell(0).setCellValue(rs.getString("TenQuan"));
                                row.createCell(1).setCellValue(rs.getInt("SoLuongKhachHang"));
                            }
                        }
                        break;
                }

                // Auto-size columns
                for (int i = 0; i < 2; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Write to file
                try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                    workbook.write(fileOut);
                }
                workbook.close();

                JOptionPane.showMessageDialog(this,
                        "Xuất báo cáo thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất báo cáo: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
