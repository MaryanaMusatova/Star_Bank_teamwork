package com.example.Bank_Star.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionDTO {
    private UUID id;
    private UUID userId;
    private UUID productId;
    private String type;
    private BigDecimal amount;
}