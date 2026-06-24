package com.payflow.accountservice.exception;

import lombok.Getter;

@Getter
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}