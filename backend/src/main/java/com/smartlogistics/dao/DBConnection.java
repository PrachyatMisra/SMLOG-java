package com.smartlogistics.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * JDBC Connection Utility
 * Loads the MySQL driver, ensures the schema exists, and returns a Connection object.
 */
public class DBConnection {

    private static final String ROOT_URL = "jdbc:mysql://localhost:3306/?serverTimezone=UTC";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/smart_logistics?serverTimezone=UTC";
    private static final String USER = System.getenv().getOrDefault("SMART_LOGISTICS_DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("SMART_LOGISTICS_DB_PASSWORD", "Harshbabu@2004");

    // Load driver once when class is loaded
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            ensureSchema();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize smart_logistics schema", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
    }

    private static void ensureSchema() throws SQLException {
        try (Connection conn = DriverManager.getConnection(ROOT_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS smart_logistics");
        }

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS shipments (" +
                    "id VARCHAR(20) PRIMARY KEY," +
                    "origin VARCHAR(100) NOT NULL," +
                    "destination VARCHAR(100) NOT NULL," +
                    "cargo VARCHAR(100) NOT NULL," +
                    "weight DECIMAL(10,2) NOT NULL," +
                    "status ENUM('Pending','In Transit','Delivered','Delayed') DEFAULT 'Pending'," +
                    "eta VARCHAR(20)," +
                    "carrier VARCHAR(100)," +
                    "created_at VARCHAR(50)" +
                ")"
            );
        }

        if (isSeedDataPresent()) {
            return;
        }

        String insertSql =
            "INSERT INTO shipments (id, origin, destination, cargo, weight, status, eta, carrier, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String[][] seedRows = {
            {"SHP-1001", "Mumbai", "Delhi", "Electronics", "420.00", "In Transit", "2025-06-12", "BlueDart", "2025-06-08 09:14"},
            {"SHP-1002", "Chennai", "Bangalore", "Pharmaceuticals", "180.00", "Delivered", "2025-06-09", "DTDC", "2025-06-07 14:32"},
            {"SHP-1003", "Kolkata", "Hyderabad", "Textiles", "650.00", "Pending", "2025-06-15", "Delhivery", "2025-06-08 11:05"},
            {"SHP-1004", "Pune", "Ahmedabad", "Auto Parts", "900.00", "Delayed", "2025-06-13", "Gati KWE", "2025-06-06 08:00"},
            {"SHP-1005", "Jaipur", "Surat", "FMCG Goods", "300.00", "In Transit", "2025-06-11", "Ecom Express", "2025-06-09 07:45"}
        };

        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            for (String[] row : seedRows) {
                stmt.setString(1, row[0]);
                stmt.setString(2, row[1]);
                stmt.setString(3, row[2]);
                stmt.setString(4, row[3]);
                stmt.setDouble(5, Double.parseDouble(row[4]));
                stmt.setString(6, row[5]);
                stmt.setString(7, row[6]);
                stmt.setString(8, row[7]);
                stmt.setString(9, row[8]);
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private static boolean isSeedDataPresent() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM shipments");
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
