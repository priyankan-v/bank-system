package com.bank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.bank.model.Transaction;

public class TransactionDAO {

    public void save(Connection conn, Transaction transaction) throws SQLException {

        String sql = "INSERT INTO transactions (account_id, type, amount) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, transaction.getAccountId());
            stmt.setString(2, transaction.getType());
            stmt.setBigDecimal(3, transaction.getAmount());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}