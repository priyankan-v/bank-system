package com.bank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.bank.model.AuditLog;

public class AuditLogDAO {

    public void save(Connection conn, AuditLog log) throws SQLException {

        String sql = """
                INSERT INTO audit_logs (account_number, admin_id, event, description)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, log.getAccountNumber());
            stmt.setObject(2, log.getAdminId());
            stmt.setString(3, log.getEvent());
            stmt.setString(4, log.getDescription());

            stmt.executeUpdate();
        }
    }
}