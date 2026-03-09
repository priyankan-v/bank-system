package com.bank.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.bank.model.User;

public class UserDAO {

    //  FIND USER BY ACCOUNT NUMBER 
    public User findByAccountNumber(Connection conn, String accountNumber) throws SQLException {

        String sql = "SELECT * FROM users WHERE account_number = ? AND active = TRUE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }

        return null;
    }

    //  UPDATE BALANCE 
    public void updateBalance(Connection conn, String accountNumber, BigDecimal newBalance) throws SQLException {

        String sql = "UPDATE users SET balance = ? WHERE account_number = ? AND active = TRUE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();
        }
    }

    //  UPDATE FAILED ATTEMPTS 
    public void updateFailedAttempts(Connection conn, String accountNumber, int attempts) throws SQLException {

        String sql = "UPDATE users SET failed_attempts = ? WHERE account_number = ? AND active = TRUE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, attempts);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();

        }
    }

    //  LOCK ACCOUNT 
    public void lockAccount(Connection conn, String accountNumber) throws SQLException {

        String sql = "UPDATE users SET locked = TRUE WHERE account_number = ? AND active = TRUE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.executeUpdate();
        }
    }

    // UNLOCK ACCOUNT
    public void unlockAccount(Connection conn, String accountNumber) throws SQLException {

        String sql = "UPDATE users SET locked = FALSE, failed_attempts = 0 WHERE account_number = ? AND active = TRUE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.executeUpdate();

        }
    }
    
    //  UPDATE PIN 
    public void updatePin(Connection conn, String accountNumber, String newPinHash) throws SQLException {

        String sql = "UPDATE users SET pin_hash = ? WHERE account_number = ? AND active = TRUE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPinHash);
            stmt.setString(2, accountNumber);

            stmt.executeUpdate();

        }
    }

    // CREATE ACCOUNT
    public void createUser(Connection conn, String name,
                       String accountNumber,
                       String pinHash,
                       java.math.BigDecimal balance) throws Exception {

        String sql = """
                INSERT INTO users (name, account_number, pin_hash, balance)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, accountNumber);
            stmt.setString(3, pinHash);
            stmt.setBigDecimal(4, balance);

            stmt.executeUpdate();
        }
    }

    // DELETE ACCOUNT
    public void deleteUser(Connection conn, String accountNumber) throws Exception {

        String sql = "UPDATE users SET active = FALSE WHERE account_number = ? AND active = TRUE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            stmt.executeUpdate();
        }
    }

    //  RESULTSET MAPPER 
    private User mapResultSetToUser(ResultSet rs) throws SQLException {

        User user = new User();

        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setAccountNumber(rs.getString("account_number"));
        user.setPinHash(rs.getString("pin_hash"));
        user.setActive(rs.getBoolean("active"));
        user.setBalance(rs.getBigDecimal("balance"));
        user.setFailedAttempts(rs.getInt("failed_attempts"));
        user.setLocked(rs.getBoolean("locked"));

        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            user.setCreatedAt(timestamp.toLocalDateTime());
        }

        return user;
    }
}