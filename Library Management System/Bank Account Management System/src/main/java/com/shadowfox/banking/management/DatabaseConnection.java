package com.shadowfox.banking.management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/banking_db";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password";

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public void saveTransaction(String accountId, Transaction transaction) {
        String sql = "INSERT INTO transactions (account_id, type, amount, timestamp) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountId);
            pstmt.setString(2, transaction.getType());
            pstmt.setDouble(3, transaction.getAmount());
            pstmt.setObject(4, transaction.getTimestamp());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            // For testing/mocking purposes, we might not have a real DB.
            System.err.println("Database error saving transaction: " + e.getMessage());
        }
    }

    public Map<String, Double> getAllAccounts() {
        Map<String, Double> accounts = new HashMap<>();
        String sql = "SELECT account_id, SUM(CASE WHEN type = 'DEPOSIT' THEN amount ELSE -amount END) as calculated_balance " +
                     "FROM transactions GROUP BY account_id";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                accounts.put(rs.getString("account_id"), rs.getDouble("calculated_balance"));
            }
        } catch (SQLException e) {
            System.err.println("Database error fetching accounts: " + e.getMessage());
        }
        return accounts;
    }
}
