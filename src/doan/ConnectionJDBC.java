/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package doan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author ASUS
 */
public class ConnectionJDBC {
    private static final String HOSTNAME = "LAPTOP-GLGO9P0T";
    private static final String PORT = "1521";
    private static final String SID = "orcl";
    private static final String USERNAM = "citygreen2805";
    private static final String PASSWORD = "abc";
    
    public static Connection getConnection() throws SQLException {
        String connectionURL = String.format("jdbc:oracle:thin:@%s:%s:%s", HOSTNAME, PORT, SID);
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(connectionURL, USERNAM, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC Driver not found.", e);
        }
    }
    
    // Test connection
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Kết nối database thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
