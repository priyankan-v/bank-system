package com.bank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bank.model.Transaction;

public class TransactionDAO {

    public void save(Connection conn, Transaction transaction) throws SQLException {

        String sql = "INSERT INTO transactions (account_number, type, amount, balance_after) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transaction.getAccountNumber());
            stmt.setString(2, transaction.getType());
            stmt.setBigDecimal(3, transaction.getAmount());
            stmt.setBigDecimal(4, transaction.getBalanceAfter());

            stmt.executeUpdate();
        }
    }

    public List<Transaction> getTransactions(Connection conn, String accountNumber, int limit) throws SQLException {

        List<Transaction> list = new ArrayList<>();

        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY created_at DESC LIMIT ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            stmt.setInt(2, limit);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Transaction t = new Transaction();

                t.setId(rs.getLong("id"));
                t.setAccountNumber(rs.getString("account_number"));
                t.setType(rs.getString("type"));
                t.setAmount(rs.getBigDecimal("amount"));
                t.setBalanceAfter(rs.getBigDecimal("balance_after"));
                t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                list.add(t);
            }
        }
        return list;
    }
}