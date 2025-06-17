package com.example.Bank_Star.exceptions;

import java.util.UUID;

public class ProductNotFoundException extends BankStarException {
    public ProductNotFoundException(UUID productId) {
        super("Product not found with ID: " + productId);
    }
}