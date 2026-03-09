package com.bank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
}