package com.bank.service;

import java.math.BigDecimal;
import java.sql.Connection;

import com.bank.dao.AdminDAO;
import com.bank.dao.UserDAO;
import com.bank.model.Admin;
import com.bank.model.User;
import com.bank.util.AccountNumberGenerator;
import com.bank.util.AdminUsernameGenerator;
import com.bank.util.DBConnection;
import com.bank.util.PasswordUtil;
import com.bank.util.PinGenerator;

public class AdminService {

    private final AdminDAO adminDAO = new AdminDAO();
    private final UserDAO userDAO = new UserDAO();
    private final AuditService auditService = new AuditService();
    private final AccountService accountService = new AccountService();

    // ADMIN DEPOSIT/ WITHDRAW
    public void depositToUser(Admin admin, User user, BigDecimal amount) {

        accountService.depositByAdmin(user, admin, amount);
    }

    public void withdrawFromUser(Admin admin, User user, BigDecimal amount) {

        accountService.withdrawByAdmin(user, admin, amount);
    }

    // UNLOCK USER ACCOUNT
    public void unlockUser(Admin admin, String accountNumber) {

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {

                User user = userDAO.findByAccountNumber(conn, accountNumber);

                if (user == null) {
                    throw new RuntimeException("User account not found.");
                }

                userDAO.unlockAccount(conn, accountNumber);

                auditService.logAdminEvent(
                        conn,
                        accountNumber,
                        admin.getUsername(),
                        "ACCOUNT_UNLOCKED",
                        "Admin unlocked user account"
                );

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Unlock failed", e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // CREATE USER ACCOUNT
    public void createUser(Admin admin, String name, BigDecimal initialBalance) {

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {

                String accountNumber;

                do {
                    accountNumber = AccountNumberGenerator.generate();
                } while (userDAO.findByAccountNumber(conn, accountNumber) != null);

                String rawPin = PinGenerator.generate();
                String pinHash = PasswordUtil.hash(rawPin);

                userDAO.createUser(
                        conn,
                        name,
                        accountNumber,
                        pinHash,
                        initialBalance
                );

                auditService.logAdminEvent(
                        conn,
                        accountNumber,
                        admin.getUsername(),
                        "USER_CREATED",
                        "Admin created user account"
                );

                conn.commit();

            } catch (Exception e) {

                conn.rollback();
                throw new RuntimeException("User creation failed", e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // DELETE USER ACCOUNT
    public void deleteUser(Admin admin, String accountNumber) {

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {

                User user = userDAO.findByAccountNumber(conn, accountNumber);

                if (user == null) {
                    throw new RuntimeException("User account not found.");
                }

                userDAO.deleteUser(conn, accountNumber);

                auditService.logAdminEvent(
                        conn,
                        accountNumber,
                        admin.getUsername(),
                        "USER_DELETED",
                        "Admin deleted user account"
                );

                conn.commit();

            } catch (Exception e) {

                conn.rollback();
                throw new RuntimeException("User deletion failed", e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // CREATE ADMIN ACCOUNT
    public void createAdmin(Admin creator, String name, String password, String role) {

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {

                int superAdminCount = adminDAO.countSuperAdmins(conn);
                String username = AdminUsernameGenerator.generate();

                Admin newAdmin = new Admin();
                newAdmin.setName(name);
                newAdmin.setUsername(username);
                newAdmin.setPasswordHash(PasswordUtil.hash(password));

                // FIRST ADMIN CREATION
                if (superAdminCount == 0) {

                    newAdmin.setRole("SUPER_ADMIN");

                    adminDAO.createAdmin(conn, newAdmin);

                    String creatorName = (creator == null) ? "SYSTEM" : creator.getUsername();

                    auditService.logAdmin(
                            conn,
                            creatorName,
                            username,
                            "ADMIN_CREATED",
                            "Admin created with role: " + role
                    );

                    conn.commit();
                    return;
                }

                // AFTER FIRST ADMIN EXISTS
                if (creator == null || !creator.isSuperAdmin()) {
                    throw new RuntimeException("Only super admin can create admins.");
                }

                if (!role.equals("ADMIN") && !role.equals("SUPER_ADMIN")) {
                    throw new RuntimeException("Invalid role.");
                }

                newAdmin.setRole(role);

                adminDAO.createAdmin(conn, newAdmin);

                auditService.logAdmin(
                        conn,
                        creator.getUsername(),
                        username,
                        "ADMIN_CREATED",
                        "Admin created with role: " + role
                );

                conn.commit();

            } catch (Exception e) {

                conn.rollback();
                throw new RuntimeException("Admin creation failed", e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // PROMOTE TO SUPER ADMIN
    public void promoteToSuperAdmin(Admin currentAdmin, String targetAdmin) {

        if (!currentAdmin.isSuperAdmin()) {
            throw new RuntimeException("Only super admin can promote admins.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {

                adminDAO.updateRole(conn, targetAdmin, "SUPER_ADMIN");

                auditService.logAdmin(
                        conn,
                        currentAdmin.getUsername(),
                        targetAdmin,
                        "ADMIN_PROMOTED",
                        "Admin promoted to SUPER_ADMIN"
                );

                conn.commit();

            } catch (Exception e) {

                conn.rollback();
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // DEMOTE SUPER ADMIN
    public void demoteSuperAdmin(Admin currentAdmin, String username) {

        if (!currentAdmin.isSuperAdmin()) {
            throw new RuntimeException("Only super admin can demote admins.");
        }

        if (currentAdmin.getUsername().equals(username)) {
            throw new RuntimeException("Admin cannot demote themselves.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            Admin target = adminDAO.findByUsername(conn, username);

            if (target == null) {
                throw new RuntimeException("Admin not found.");
            }
            
            if (target.isSuperAdmin() && adminDAO.countSuperAdmins(conn) <= 1) {
                throw new RuntimeException("Cannot demote the last super admin.");
            }

            try {

                adminDAO.updateRole(conn, target.getUsername(), "ADMIN");

                auditService.logAdmin(
                        conn,
                        currentAdmin.getUsername(),
                        username,
                        "ADMIN_DEMOTED",
                        "Super admin demoted to ADMIN"
                );

                conn.commit();

            } catch (Exception e) {

                conn.rollback();
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // DELETE ADMIN
    public void deleteAdmin(Admin admin, String username) {

        if (!admin.isSuperAdmin()) {
            throw new RuntimeException("Only super admin can delete admins.");
        }

        if (admin.getUsername().equals(username)) {
            throw new RuntimeException("Admin cannot delete themselves.");
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            Admin target = adminDAO.findByUsername(conn, username);

            if (target == null) {
                throw new RuntimeException("Admin not found.");
            }

            if (target.isSuperAdmin() && adminDAO.countSuperAdmins(conn) <= 1) {
                throw new RuntimeException("Cannot delete the last super admin.");
            }


            try {

                adminDAO.deleteAdmin(conn, username);

                auditService.logAdmin(
                        conn,
                        admin.getUsername(),
                        username,
                        "ADMIN_DELETED",
                        "Super admin deleted admin"
                );

                conn.commit();

            } catch (Exception e) {

                conn.rollback();
                throw new RuntimeException("Admin deletion failed", e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}