package com.example.Bank_Star.repository;

import com.example.Bank_Star.domen.Recommendation;
import com.example.Bank_Star.domen.RuleStats;
import com.example.Bank_Star.enums.ComparisonType;
import com.example.Bank_Star.enums.ProductType;
import com.example.Bank_Star.enums.TransactionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public class RecommendationsRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private RuleStatsRepository ruleStatsRepository;

    public RecommendationsRepository(
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean isUserExists(UUID userId) {
        String sql = "SELECT COUNT(*) > 0 FROM users WHERE id = ?";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
    }

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

    public boolean hasNoProductType(UUID userId, ProductType productType) {
    }

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

    public boolean compareTransactionSum(UUID userId, ProductType productType, TransactionType transactionType,
                                         ComparisonType comparison, BigDecimal value) {
        BigDecimal sum = getTransactionSum(userId, productType, transactionType);
        return compareValues(sum, comparison, value);
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

    public UUID getUserIdByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, UUID.class, username);
    }

    public List<Recommendation> getRecommendations(UUID userId) {
        String sql = "SELECT * FROM recommendations WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            UUID id = UUID.fromString(rs.getString("id"));
            String name = rs.getString("name");
            String description = rs.getString("description");
            return new Recommendation(id, name, description);
        }, userId.toString());
    }

    public void incrementRuleCount(String ruleId) {
        RuleStats ruleStats = ruleStatsRepository.findById(ruleId).orElse(new RuleStats(ruleId, 0));
        ruleStats.setCount(ruleStats.getCount() + 1);
        ruleStatsRepository.save(ruleStats);
    }

    public void deleteRuleCount(String ruleId) {
        ruleStatsRepository.deleteById(ruleId);
    }

}