package com.bank.service;

import java.math.BigDecimal;
import java.sql.Connection;

import com.bank.dao.TransactionDAO;
import com.bank.dao.UserDAO;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.service.exceptions.InsufficientBalanceException;
import com.bank.util.DBConnection;

public class AccountService {

    private final UserDAO userDAO = new UserDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    //  BALANCE INQUIRY 
    public BigDecimal getBalance(User user) {
        return user.getBalance();
    }

    //  WITHDRAW 
    public void withdraw(User user, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid withdrawal amount.");
        }

        if (user.isLocked()) {
            throw new RuntimeException("Account is locked.");
        }

        if (user.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance.");
        }

        BigDecimal newBalance = user.getBalance().subtract(amount);

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                // Update balance
                userDAO.updateBalance(conn, user.getId(), newBalance);

                // Log transaction
                Transaction transaction = new Transaction();
                transaction.setAccountId(user.getId());
                transaction.setType("WITHDRAW");
                transaction.setAmount(amount);

                transactionDAO.save(conn, transaction);

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

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {
                // Update PIN
                userDAO.updatePin(user.getId(), newHashedPin);

                conn.commit();

            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("PIN change failed. Rolled back.", e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Database error during PIN change.", e);
        }
    }
}