package com.bank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public List<AuditLog> getUserAudit(Connection conn, String accountNumber, int limit) throws SQLException {

        List<AuditLog> list = new ArrayList<>();

        String sql = "SELECT * FROM audit_logs WHERE account_number = ? ORDER BY created_at DESC LIMIT ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            stmt.setInt(2, limit);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                AuditLog a = new AuditLog();

                a.setId(rs.getLong("id"));
                a.setAccountNumber(rs.getString("account_number"));
                a.setAdminId(rs.getString("admin_id"));
                a.setEvent(rs.getString("event"));
                a.setDescription(rs.getString("description"));
                a.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                list.add(a);
            }

        return list;
        }
    }
}