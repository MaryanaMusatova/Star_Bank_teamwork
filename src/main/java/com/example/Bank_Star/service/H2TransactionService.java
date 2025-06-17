package com.example.Bank_Star.service;

import com.example.Bank_Star.exceptions.TransactionValidationException;
import com.example.Bank_Star.repository.h2.H2TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class H2TransactionService {
    private final H2TransactionRepository transactionRepository;

    public boolean userHasTransactionsOfType(UUID userId, String transactionType) {
        return transactionRepository.userHasTransactionsOfType(userId, transactionType);
    }

    public boolean userHasAnyProduct(UUID userId) {
        return transactionRepository.userHasAnyProduct(userId);
    }

    public BigDecimal getTransactionSumByType(UUID userId, String transactionType) {
        if (userId == null || transactionType == null) {
            throw new TransactionValidationException("User ID and transaction type cannot be null");
        }
        return transactionRepository.getTransactionSumByType(userId, transactionType);
    }
}