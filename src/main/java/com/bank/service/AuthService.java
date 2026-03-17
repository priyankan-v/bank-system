package com.bank.service;
import java.sql.Connection;

import com.bank.dao.AdminDAO;
import com.bank.dao.UserDAO;
import com.bank.model.Admin;
import com.bank.model.User;
import com.bank.service.exceptions.AccountLockedException;
import com.bank.service.exceptions.AuthException;
import com.bank.service.exceptions.DeletedAccountException;
import com.bank.service.exceptions.InvalidCredentialsException;
import com.bank.util.DBConnection;
import com.bank.util.PasswordUtil;
import com.bank.util.SessionManager;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final AdminDAO adminDAO = new AdminDAO();
    private final AuditService auditService = new AuditService();

    private static final int MAX_FAILED_ATTEMPTS = 3;

    public User userLogin(String accountNumber, String rawPin) throws InvalidCredentialsException, AccountLockedException, DeletedAccountException {

        // Check account existence
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); 

            try {
                User user = userDAO.findByAccountNumber(conn, accountNumber);

                if (user == null) {
                    throw new InvalidCredentialsException("Invalid account number.");
                }

                if (!user.isActive()) {
                    throw new DeletedAccountException("Account is inactive.");
                }
                // Check if account locked
                if (user.isLocked()) {

                    auditService.logUserEvent(
                            conn,
                            user.getAccountNumber(),
                            "LOGIN_ATTEMPT_BLOCKED",
                            "Login attempted on locked account"
                    );

                    throw new AccountLockedException("Account is locked. Contact bank.");
                }

                // Verify PIN
                boolean valid = PasswordUtil.verify(rawPin, user.getPinHash());
                // System.out.println("user.getPinHash(): " + user.getPinHash() + " vs " + rawPin);

                if (!valid) {

                    auditService.logUserEvent(
                                conn,
                                user.getAccountNumber(),
                                "LOGIN_FAILED_INVALID_PIN",
                                "Account login failed due to invalid PIN");

                    int attempts = user.getFailedAttempts() + 1;

                    userDAO.updateFailedAttempts(conn, user.getAccountNumber(), attempts);
                    user.setFailedAttempts(attempts);

                    int remaining = MAX_FAILED_ATTEMPTS - attempts;

                    conn.commit();

                    if (attempts >= MAX_FAILED_ATTEMPTS) {
                        userDAO.lockAccount(conn, user.getAccountNumber());
                        auditService.logUserEvent(
                                conn,
                                user.getAccountNumber(),
                                "ACCOUNT_LOCKED",
                                "Account locked after multiple failed login attempts"
                        );
                        conn.commit();

                        throw new AccountLockedException(
                                "Account locked due to 3 failed attempts.");
                    }

                    throw new InvalidCredentialsException(
                            "Invalid PIN. Remaining attempts: " + remaining);
                }

                userDAO.updateFailedAttempts(conn, user.getAccountNumber(), 0);

                auditService.logUserEvent(
                        conn,
                        user.getAccountNumber(),
                        "USER_LOGIN_SUCCESS",
                        "User logged in successfully"
                );
                SessionManager.setCurrentUser(user);
                conn.commit();

                return user;

            } catch (AuthException e) {

                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }

                throw e;
            }

        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unable to connect to the database.", e);
        }
    }


    
    public Admin adminLogin(String username, String rawPassword) {

        // Check account existence
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); 

            try {
                Admin admin = adminDAO.findByUsername(conn, username);

                if (admin == null) {
                    throw new InvalidCredentialsException("Invalid username.");
                }

                if (!admin.isActive()) {
                    throw new DeletedAccountException("Admin account is inactive.");
                }

                // Verify password
                boolean valid = PasswordUtil.verify(rawPassword, admin.getPasswordHash());

                if (!valid) {
                    auditService.logAdmin(conn, admin.getUsername(), null,"ADMIN_LOGIN_FAILED",
                        "Admin login failed due to invalid password");

                    throw new InvalidCredentialsException("Invalid password. Try again.");
                }

                auditService.logAdmin(conn, admin.getUsername(), null,"ADMIN_LOGIN_SUCCESS",
                        "Admin logged in successfully");
                        
                SessionManager.setCurrentAdmin(admin);
                conn.commit();

                return admin;

            } catch (AuthException e) {
                try {
                    conn.rollback();
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
                throw e;
            }

        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Unable to connect to the database.", e);
        }
    }
}