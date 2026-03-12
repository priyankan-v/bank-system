package com.bank.service.exceptions;

public class DeletedAccountException extends AuthException {

    public DeletedAccountException(String message) {
        super(message);
    }
}
