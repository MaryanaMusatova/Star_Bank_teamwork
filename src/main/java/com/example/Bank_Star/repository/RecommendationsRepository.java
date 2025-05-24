package com.example.Bank_Star.repository;

import com.example.Bank_Star.enums.ComparisonType;
import com.example.Bank_Star.enums.ProductType;
import com.example.Bank_Star.enums.TransactionType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.UUID;

@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;

    public RecommendationsRepository(@Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    //Метод проверяет, существует ли пользователь
//с указанным идентификатором в БД (PS Артем Васяткин)
    public boolean isUserExixts(UUID userId) {
        String sql = "SELECT COUNT(*) > 0 FROM users WHERE id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql,
                Boolean.class,
                userId.toString()));
    }
    //Метод проверяет, использует ли указанный пользователь
//хотя бы один продукт определенного типа (PS Артем Васяткин)
    public boolean usesProductType(UUID userId, ProductType productType) {
        String sql = """
                SELECT COUNT(*) > 0 FROM user_products up
                JOIN products p ON up.product_id = p.id
                WHERE up.user_id = ? AND p.type = ?
                """;
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql,
                Boolean.class,
                userId.toString(),
                productType.name()));
    }
    //Вспомогательный метод, проверяет обратное условие
//по сравнению с методом usesProductType (PS Артем Васяткин)
    public boolean hasNoProductType(UUID userId, ProductType productType) {
        return !usesProductType(userId, productType);
    }
    //Метод вычисляет сумму транзакций определенного типа
//для указанного пользователя и типа продукта (PS Артем Васяткин)
    public BigDecimal getTransactionSum(UUID userId, ProductType productType, TransactionType transactionType) {
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
    }
    //Метод выполняет сравнение суммы транзакций пользователя
//с заданным значением  (PS Артем Васяткин)
    public boolean compareTransactionSum(UUID userId, ProductType productType, TransactionType transactionType,
                                         ComparisonType comparison, BigDecimal value) {
        BigDecimal sum = getTransactionSum(userId, productType, transactionType);
        return compareValues(sum, comparison, value);
    }
    //Приватный метод выполняет сравнение двух чисел с учетом
//указанного оператора сравнения (PS Артем Васяткин)
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