package com.example.Bank_Star.repository;

import com.example.Bank_Star.domen.postgres.Recommendation;
import com.example.Bank_Star.enums.ComparisonType;
import com.example.Bank_Star.enums.ProductType;
import com.example.Bank_Star.enums.TransactionType;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class RecommendationsRepository {
    private final JdbcTemplate h2JdbcTemplate;
    private final JdbcTemplate postgresJdbcTemplate;

    // Добавьте явные аннотации @Qualifier в конструктор
    @Autowired
    public RecommendationsRepository(
            @Qualifier("h2JdbcTemplate") JdbcTemplate h2JdbcTemplate,
            @Qualifier("postgresJdbcTemplate") JdbcTemplate postgresJdbcTemplate) {
        this.h2JdbcTemplate = h2JdbcTemplate;
        this.postgresJdbcTemplate = postgresJdbcTemplate;
    }

    // Кеш для проверки существования пользователя
    private final Cache<String, Boolean> userExistsCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(10_000)
            .build();

    // Кеш для проверки использования продукта
    private final Cache<String, Boolean> usesProductCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(10_000)
            .build();

    // Кеш для сумм транзакций
    private final Cache<String, BigDecimal> transactionSumCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(10_000)
            .build();

    // Кеш для проверки активного пользователя
    private final Cache<String, Boolean> activeUserCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(10_000)
            .build();

    /**
     * Проверяет существование пользователя в H2
     */
    public boolean isUserExists(UUID userId) {
        String key = userId.toString();
        return userExistsCache.get(key, k -> {
            String sql = "SELECT CAST(COUNT(*) > 0 AS BOOLEAN) FROM USERS WHERE ID = ?";
            try {
                Boolean exists = h2JdbcTemplate.queryForObject(sql, Boolean.class, k);
                return exists != null && exists;
            } catch (Exception e) {
                log.error("Error checking user existence", e);
                return false;
            }
        });
    }


    /**
     * Проверяет, использует ли пользователь продукт определенного типа (H2)
     */
    public boolean usesProductType(UUID userId, ProductType productType) {
        String key = userId + "_uses_" + productType.name();
        return usesProductCache.get(key, k -> {
            String sql = """
                    SELECT COUNT(*) > 0 FROM \"TRANSACTIONS\" t
                    JOIN \"PRODUCTS\" p ON t.\"PRODUCT_ID\" = p.\"ID\"
                    WHERE t.\"USER_ID\" = ? AND p.\"TYPE\" = ?
                    """;
            return Boolean.TRUE.equals(h2JdbcTemplate.queryForObject(sql,
                    Boolean.class,
                    userId.toString(),
                    productType.name()));
        });
    }

    /**
     * Проверяет, что у пользователя нет продукта определенного типа (H2)
     */
    public boolean hasNoProductType(UUID userId, ProductType productType) {
        String key = userId + "_nouse_" + productType.name();
        return usesProductCache.get(key, k -> !usesProductType(userId, productType));
    }

    /**
     * Получает сумму транзакций по типу продукта и типу транзакции (H2)
     */
    public BigDecimal getTransactionSum(UUID userId, ProductType productType, TransactionType transactionType) {
        String key = userId + "_sum_" + productType.name() + "_" + transactionType.name();
        return transactionSumCache.get(key, k -> {
            String sql = """
                    SELECT COALESCE(SUM(t.\"AMOUNT\"), 0) FROM \"TRANSACTIONS\" t
                    JOIN \"PRODUCTS\" p ON t.\"PRODUCT_ID\" = p.\"ID\"
                    WHERE t.\"USER_ID\" = ? AND p.\"TYPE\" = ? AND t.\"TYPE\" = ?
                    """;
            return h2JdbcTemplate.queryForObject(sql,
                    BigDecimal.class,
                    userId.toString(),
                    productType.name(),
                    transactionType.name());
        });
    }

    /**
     * Сравнивает сумму транзакций с заданным значением (H2)
     */
    public boolean compareTransactionSum(UUID userId, ProductType productType, TransactionType transactionType,
                                         ComparisonType comparison, BigDecimal value) {
        BigDecimal sum = getTransactionSum(userId, productType, transactionType);
        return compareValues(sum, comparison, value);
    }

    /**
     * Проверяет, является ли пользователь активным по продукту (H2)
     */
    public boolean isActiveUser(UUID userId, ProductType productType) {
        String key = userId + "_active_" + productType.name();
        return activeUserCache.get(key, k -> {
            String sql = """
                    SELECT COUNT(*) >= 5 FROM \"TRANSACTIONS\" t
                    JOIN \"PRODUCTS\" p ON t.\"PRODUCT_ID\" = p.\"ID\"
                    WHERE t.\"USER_ID\" = ? AND p.\"TYPE\" = ?
                    """;
            return Boolean.TRUE.equals(h2JdbcTemplate.queryForObject(
                    sql, Boolean.class, userId.toString(), productType.name()));
        });
    }

    /**
     * Получает ID пользователя по имени (H2)
     */
    public UUID getUserIdByUsername(String username) {
        String sql = "SELECT \"ID\" FROM \"USERS\" WHERE \"USERNAME\" = ?";
        List<UUID> userIds = h2JdbcTemplate.queryForList(sql, UUID.class, username);
        if (userIds.size() != 1) {
            log.warn("Найдено {} пользователей с именем пользователя {}", userIds.size(), username);
            return null;
        }
        return userIds.get(0);
    }

    /**
     * Получает динамические рекомендации из PostgreSQL
     */
    public List<Recommendation> getDynamicRecommendations(UUID userId) {
        String sql = "SELECT * FROM dynamic_rules WHERE user_id = ?";
        return postgresJdbcTemplate.query(sql, (rs, rowNum) -> {
            UUID id = UUID.fromString(rs.getString("id"));
            String name = rs.getString("name");
            String description = rs.getString("description");
            return new Recommendation(id, name, description);
        }, userId.toString());
    }

    /**
     * Вспомогательный метод для сравнения значений
     */
    private boolean compareValues(BigDecimal a, ComparisonType comparison, BigDecimal b) {
        return switch (comparison) {
            case LESS -> a.compareTo(b) < 0;
            case EQUAL -> a.compareTo(b) == 0;
            case GREATER -> a.compareTo(b) > 0;
            case LESS_OR_EQUAL -> a.compareTo(b) <= 0;
            case GREATER_OR_EQUAL -> a.compareTo(b) >= 0;
        };
    }

    /**
     * Очищает все кэши
     */
    public void clearAllCaches() {
        userExistsCache.invalidateAll();
        usesProductCache.invalidateAll();
        transactionSumCache.invalidateAll();
        activeUserCache.invalidateAll();
        log.debug("Все кэши очищены");
    }
}