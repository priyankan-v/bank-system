package com.bank.service.exceptions;

public class InsufficientBalanceException extends AuthException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}