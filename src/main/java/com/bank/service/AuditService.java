package com.bank.service;

import java.sql.Connection;

import com.bank.dao.AdminLogDAO;
import com.bank.dao.AuditLogDAO;
import com.bank.model.AdminLog;
import com.bank.model.AuditLog;


public class AuditService {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();
    private final AdminLogDAO adminLogDAO = new AdminLogDAO();

    public void logUserEvent(Connection conn, String accountNumber, String event, String description) throws Exception {

        AuditLog log = new AuditLog(accountNumber, null, event, description);

        auditLogDAO.save(conn, log);
    }

    public void logAdminEvent(Connection conn, String accountNumber, String adminId, String event, String description) throws Exception {

        AuditLog log = new AuditLog(accountNumber, adminId, event, description);

        auditLogDAO.save(conn, log);
    }

    public void logAdmin(Connection conn, String performedId, String targetId, String event, String description) throws Exception {

        AdminLog log = new AdminLog(performedId, targetId, event, description);

        adminLogDAO.save(conn, log);
    }
}