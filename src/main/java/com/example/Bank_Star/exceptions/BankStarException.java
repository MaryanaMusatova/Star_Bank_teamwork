package com.example.Bank_Star.exceptions;

// Базовое исключение для всех бизнес-ошибок приложения
public class BankStarException extends RuntimeException {
    public BankStarException(String message) {
        super(message);
    }

    public BankStarException(String message, Throwable cause) {
        super(message, cause);
    }
}