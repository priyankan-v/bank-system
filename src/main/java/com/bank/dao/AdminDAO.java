package com.bank.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.bank.model.Admin;


public class AdminDAO {

    // FIND BY USERNAME
    public Admin findByUsername(Connection conn, String username) throws SQLException {

        String sql = "SELECT * FROM admins WHERE username = ? AND active = TRUE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        } 

        return null;
    }

    public void createAdmin(Connection conn, Admin admin) throws Exception {

        String sql = """
                INSERT INTO admins (name, username, password_hash, role)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, admin.getName());
            stmt.setString(2, admin.getUsername());
            stmt.setString(3, admin.getPasswordHash());
            stmt.setString(4, admin.getRole());

            stmt.executeUpdate();
        }
    }

    public void deleteAdmin(Connection conn, String username) throws Exception {

        String sql = "UPDATE admins SET active = FALSE WHERE username = ? AND active = TRUE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    // UPDATE ROLE
    public void updateRole(Connection conn, String username, String role) throws Exception {

        String sql = "UPDATE admins SET role = ? WHERE username = ? AND active = TRUE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role);
            stmt.setString(2, username);

            stmt.executeUpdate();
        }
    }

    public int countSuperAdmins(Connection conn) throws Exception {

        String sql = "SELECT COUNT(*) FROM admins WHERE role = 'SUPER_ADMIN' AND active = TRUE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        }

        return 0;
    }

    //  RESULTSET MAPPER 
    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {

        Admin admin = new Admin();

        admin.setId(rs.getLong("id"));
        admin.setName(rs.getString("name"));
        admin.setUsername(rs.getString("username"));
        admin.setPasswordHash(rs.getString("password_hash"));
        admin.setActive(rs.getBoolean("active"));
        admin.setRole(rs.getString("role"));

        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            admin.setCreatedAt(timestamp.toLocalDateTime());
        }

        return admin;
    }
}