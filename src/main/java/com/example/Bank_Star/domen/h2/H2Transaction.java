package com.example.Bank_Star.domen.h2;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "\"TRANSACTIONS\"") // Кавычки для точного соответствия
@Data
public class H2Transaction {
    @Id
    @Column(name = "\"ID\"")
    private UUID id;

    @Column(name = "\"USER_ID\"")
    private UUID userId;

    @Column(name = "\"PRODUCT_ID\"")
    private UUID productId;

    @Column(name = "\"TYPE\"")
    private String type;

    @Column(name = "\"AMOUNT\"")
    private BigDecimal amount;
}