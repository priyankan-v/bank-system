package com.bank.service.exceptions;

public class InvalidCredentialsException extends AuthException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}