package com.bank.service;

import java.math.BigDecimal;
import java.sql.Connection;

import com.bank.dao.TransactionDAO;
import com.bank.dao.UserDAO;
import com.bank.model.Admin;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.service.exceptions.InsufficientBalanceException;
import com.bank.util.DBConnection;

public class AccountService {

    private final UserDAO userDAO = new UserDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final AuditService auditService = new AuditService();

    // VALIDATE ACTIVE USER
    private void validateActiveUser(User user) {
    if (!user.isActive()) {
        throw new RuntimeException("Account is inactive.");
    }
}
    //  BALANCE INQUIRY 
    public BigDecimal getBalance(User user) {
        validateActiveUser(user);
        return user.getBalance();
    }

    //  WITHDRAW 
    public void withdraw(User user, BigDecimal amount) {

        validateActiveUser(user);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid withdrawal amount.");
        }

        if (user.isLocked()) {
            throw new RuntimeException("Account is locked.");
        }

        if (user.getBalance().compareTo(amount) < 0 ) {
            throw new InsufficientBalanceException("Insufficient balance.");
        }

        BigDecimal newBalance = user.getBalance().subtract(amount);

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                // Update balance
                userDAO.updateBalance(conn, user.getAccountNumber(), newBalance);

                // Log transaction
                Transaction transaction = new Transaction();
                transaction.setAccountNumber(user.getAccountNumber());
                transaction.setType("WITHDRAW");
                transaction.setAmount(amount);

                transactionDAO.save(conn, transaction);

                // Audit log
                auditService.logUserEvent(
                        conn,
                        user.getAccountNumber(),
                        "WITHDRAW",
                        "User withdrew " + amount
                );

                conn.commit();

                user.setBalance(newBalance);

            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Transaction failed. Rolled back.", e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Database error during withdrawal.", e);
        }
    }

    //  CHANGE PIN 
    public void changePin(User user, String newHashedPin) {

        validateActiveUser(user);

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {
                // Update PIN
                userDAO.updatePin(conn, user.getAccountNumber(), newHashedPin);

                // Audit log
                auditService.logUserEvent(
                        conn,
                        user.getAccountNumber(),
                        "PIN_CHANGE",
                        "User changed PIN"
                );

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("PIN change failed. Rolled back.", e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Database error during PIN change.", e);
        }
    }

    // ADMIN DEPOSIT
    public void depositByAdmin(User user, Admin admin, BigDecimal amount) {

        validateActiveUser(user);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid deposit amount.");
        }

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                BigDecimal newBalance = user.getBalance().add(amount);

                // update balance
                userDAO.updateBalance(conn, user.getAccountNumber(), newBalance);

                // financial transaction
                Transaction transaction = new Transaction();
                transaction.setAccountNumber(user.getAccountNumber());
                transaction.setType("ADMIN_DEPOSIT");
                transaction.setAmount(amount);

                transactionDAO.save(conn, transaction);

                // audit log
                auditService.logAdminEvent(
                        conn,
                        user.getAccountNumber(),
                        admin.getUsername(),
                        "ADMIN_DEPOSIT",
                        "Bank deposited " + amount
                );

                conn.commit();

                user.setBalance(newBalance);

            } catch (Exception e) {

                conn.rollback();
                throw new RuntimeException("Deposit failed", e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ADMIN WITHDRAW
    public void withdrawByAdmin(User user, Admin admin, BigDecimal amount) {

        validateActiveUser(user);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid withdrawal amount.");
        }

        if (user.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance.");
        }

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                BigDecimal newBalance = user.getBalance().subtract(amount);

                userDAO.updateBalance(conn, user.getAccountNumber(), newBalance);

                Transaction transaction = new Transaction();
                transaction.setAccountNumber(user.getAccountNumber());
                transaction.setType("ADMIN_WITHDRAW");
                transaction.setAmount(amount);

                transactionDAO.save(conn, transaction);

                auditService.logAdminEvent(
                        conn,
                        user.getAccountNumber(),
                        admin.getUsername(),
                        "ADMIN_WITHDRAW",
                        "Admin withdrew " + amount
                );

                conn.commit();

                user.setBalance(newBalance);

            } catch (Exception e) {

                conn.rollback();
                throw new RuntimeException("Admin withdrawal failed", e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}