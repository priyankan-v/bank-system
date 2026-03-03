package com.bank.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.bank.model.User;
import com.bank.util.DBConnection;

public class UserDAO {

    //  FIND USER BY ACCOUNT NUMBER 
    public User findByAccountNumber(String accountNumber) {

        String sql = "SELECT * FROM users WHERE account_number = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    //  UPDATE BALANCE 
    public void updateBalance(Long userId, BigDecimal newBalance) {

        String sql = "UPDATE users SET balance = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, newBalance);
            stmt.setLong(2, userId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  UPDATE FAILED ATTEMPTS 
    public void updateFailedAttempts(Long userId, int attempts) {

        String sql = "UPDATE users SET failed_attempts = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, attempts);
            stmt.setLong(2, userId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  LOCK ACCOUNT 
    public void lockAccount(Long userId) {

        String sql = "UPDATE users SET locked = TRUE WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  UPDATE PIN 
    public void updatePin(Long userId, String newPinHash) {

        String sql = "UPDATE users SET pin_hash = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPinHash);
            stmt.setLong(2, userId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //  RESULTSET MAPPER 
    private User mapResultSetToUser(ResultSet rs) throws SQLException {

        User user = new User();

        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setAccountNumber(rs.getString("account_number"));
        user.setPinHash(rs.getString("pin_hash"));
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