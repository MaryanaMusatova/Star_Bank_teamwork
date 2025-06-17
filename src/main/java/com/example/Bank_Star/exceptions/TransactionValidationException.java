package com.example.Bank_Star.exceptions;

public class TransactionValidationException extends BankStarException {
    public TransactionValidationException(String message) {
        super(message);
    }
}
