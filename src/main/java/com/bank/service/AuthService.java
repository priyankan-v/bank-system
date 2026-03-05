package com.bank.service;

import com.bank.dao.UserDAO;
import com.bank.model.User;
import com.bank.service.exceptions.AccountLockedException;
import com.bank.service.exceptions.InvalidCredentialsException;
import com.bank.util.PasswordUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private static final int MAX_FAILED_ATTEMPTS = 3;

    public User login(String accountNumber, String rawPin) {

        // Check account existence
        User user = userDAO.findByAccountNumber(accountNumber);

        if (user == null) {
            throw new InvalidCredentialsException("Invalid account number.");
        }

        // Check if account locked
        if (user.isLocked()) {
            throw new AccountLockedException("Account is locked. Contact bank.");
        }

        // Verify PIN
        boolean valid = PasswordUtil.verify(rawPin, user.getPinHash());

        if (!valid) {

            int attempts = user.getFailedAttempts() + 1;
            userDAO.updateFailedAttempts(user.getId(), attempts);

            int remaining = MAX_FAILED_ATTEMPTS - attempts;

            if (attempts >= MAX_FAILED_ATTEMPTS) {
                userDAO.lockAccount(user.getId());
                throw new AccountLockedException(
                        "Account locked due to 3 failed attempts.");
            }

            throw new InvalidCredentialsException(
                    "Invalid PIN. Remaining attempts: " + remaining);
        }

        userDAO.updateFailedAttempts(user.getId(), 0);

        return user;
    }
}