package com.bank.service.exceptions;

public class AccountLockedException extends AuthException {
    public AccountLockedException(String message) {
        super(message);
    }
}