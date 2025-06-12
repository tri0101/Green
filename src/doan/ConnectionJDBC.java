package doan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionJDBC {
    private static final String HOSTNAME = "LAPTOP-GLGO9P0T";
    private static final String PORT = "1521";
    private static final String SID = "orcl";
    private static final String USERNAME = "citygreen126";
    private static final String PASSWORD = "abc";

    // Kết nối mặc định
    public static Connection getConnection() throws SQLException {
        String connectionURL = String.format("jdbc:oracle:thin:@%s:%s:%s", HOSTNAME, PORT, SID);
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(connectionURL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC Driver not found.", e);
        }
    }

    // 🔐 Kết nối với mức cô lập cao nhất: Serializable
    public static Connection getSerializableConnection() throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
        conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        return conn;
    }

    // 🟡 Kết nối với mức cô lập READ_COMMITTED (mức mặc định của Oracle)
    public static Connection getReadCommittedConnection() throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        return conn;
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
