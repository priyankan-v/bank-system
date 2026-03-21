package com.bank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bank.model.AdminLog;

public class AdminLogDAO {

    public void save(Connection conn, AdminLog log) throws SQLException {

        String sql = """
                INSERT INTO admin_logs (performedId, targetId, event, description)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, log.getPerformedId());
            stmt.setObject(2, log.getTargetId());
            stmt.setString(3, log.getEvent());
            stmt.setString(4, log.getDescription());

            stmt.executeUpdate();
        }
    }

    public List<AdminLog> getAdminAudit(Connection conn, String username, int limit) throws SQLException {

        List<AdminLog> list = new ArrayList<>();

        String sql = "SELECT * FROM admin_logs WHERE performedId = ? ORDER BY created_at DESC LIMIT ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, username);
            stmt.setInt(2, limit);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                AdminLog a = new AdminLog();

                a.setId(rs.getLong("id"));
                a.setPerformedId(rs.getString("performedId"));
                a.setTargetId(rs.getString("targetId"));
                a.setEvent(rs.getString("event"));
                a.setDescription(rs.getString("description"));
                a.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                list.add(a);
            }
        }
        return list;
    }
}