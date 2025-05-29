package com.example.Bank_Star.repository;

import com.example.Bank_Star.enums.ComparisonType;
import com.example.Bank_Star.enums.ProductType;
import com.example.Bank_Star.enums.TransactionType;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class RecommendationsRepository {
    @Getter
    private final JdbcTemplate jdbcTemplate;

    // Кеш для проверки существования пользователя
    private final Cache<String, Boolean> userExistsCache;

    // Кеш для проверки использования продукта
    private final Cache<String, Boolean> usesProductCache;

    // Кеш для сумм транзакций
    private final Cache<String, BigDecimal> transactionSumCache;

    // Кеш для проверки активного пользователя
    private final Cache<String, Boolean> activeUserCache;

    public RecommendationsRepository(
            @Qualifier("postgresJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        // Настройка кешей
        this.userExistsCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(10_000)
                .build();

        this.usesProductCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(10_000)
                .build();

        this.transactionSumCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(10_000)
                .build();

        this.activeUserCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(10_000)
                .build();
    }

    public boolean isUserExists(UUID userId) {
        String key = userId.toString();
        return userExistsCache.get(key, k -> {
            String sql = "SELECT COUNT(*) > 0 FROM users WHERE id = ?";
            return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                    sql, Boolean.class, k));
        });
    }

    public boolean usesProductType(UUID userId, ProductType productType) {
        String key = userId + "_uses_" + productType.name();
        return usesProductCache.get(key, k -> {
            String sql = """
                    SELECT COUNT(*) > 0 FROM user_products up
                    JOIN products p ON up.product_id = p.id
                    WHERE up.user_id = ? AND p.type = ?
                    """;
            return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql,
                    Boolean.class,
                    userId.toString(),
                    productType.name()));
        });
    }

    public boolean hasNoProductType(UUID userId, ProductType productType) {
        String key = userId + "_nouse_" + productType.name();
        return usesProductCache.get(key, k -> !usesProductType(userId, productType));
    }

    public BigDecimal getTransactionSum(UUID userId, ProductType productType, TransactionType transactionType) {
        String key = userId + "_sum_" + productType.name() + "_" + transactionType.name();
        return transactionSumCache.get(key, k -> {
            String sql = """
                    SELECT COALESCE(SUM(t.amount), 0) FROM transactions t
                    JOIN products p ON t.product_id = p.id
                    WHERE t.user_id = ? AND p.type = ? AND t.type = ?
                    """;
            return jdbcTemplate.queryForObject(sql,
                    BigDecimal.class,
                    userId.toString(),
                    productType.name(),
                    transactionType.name());
        });
    }

    public boolean compareTransactionSum(UUID userId, ProductType productType, TransactionType transactionType,
                                         ComparisonType comparison, BigDecimal value) {
        BigDecimal sum = getTransactionSum(userId, productType, transactionType);
        return compareValues(sum, comparison, value);
    }

    public boolean isActiveUser(UUID userId, ProductType productType) {
        String key = userId + "_active_" + productType.name();
        return activeUserCache.get(key, k -> {
            String sql = """
                    SELECT COUNT(*) >= 5 FROM transactions t
                    JOIN products p ON t.product_id = p.id
                    WHERE t.user_id = ? AND p.type = ?
                    """;
            return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                    sql, Boolean.class, userId.toString(), productType.name()));
        });
    }

    private boolean compareValues(BigDecimal a, ComparisonType comparison, BigDecimal b) {
        return switch (comparison) {
            case LESS -> a.compareTo(b) < 0;
            case EQUAL -> a.compareTo(b) == 0;
            case GREATER -> a.compareTo(b) > 0;
            case LESS_OR_EQUAL -> a.compareTo(b) <= 0;
            case GREATER_OR_EQUAL -> a.compareTo(b) >= 0;
        };
    }
}