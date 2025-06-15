package com.example.Bank_Star.repository.h2;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class H2TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    public H2TransactionRepository(@Qualifier("h2JdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean userHasTransactionsOfType(UUID userId, String transactionType) {
        String sql = """
                SELECT COUNT(*) > 0 
                FROM TRANSACTIONS 
                WHERE USER_ID = ? AND TYPE = ?
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, transactionType));
    }

    public boolean userHasAnyProduct(UUID userId) {
        String sql = """
                SELECT COUNT(*) > 0 
                FROM TRANSACTIONS 
                WHERE USER_ID = ? AND PRODUCT_ID IS NOT NULL
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId));
    }

    public BigDecimal getTransactionSumByType(UUID userId, String transactionType) {
        String sql = """
                SELECT COALESCE(SUM(AMOUNT), 0) 
                FROM TRANSACTIONS 
                WHERE USER_ID = ? AND TYPE = ?
                """;
        return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId, transactionType);
    }
}