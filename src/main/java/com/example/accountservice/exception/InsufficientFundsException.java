package com.example.accountservice.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(String accountId, BigDecimal amount) {
        super(String.format("Insufficient funds on account %s for amount %s", accountId, amount));
    }
}
