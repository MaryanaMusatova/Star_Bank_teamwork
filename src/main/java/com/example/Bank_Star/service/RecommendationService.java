package com.example.Bank_Star.service;

import com.example.Bank_Star.domen.*;
import com.example.Bank_Star.enums.ComparisonType;
import com.example.Bank_Star.enums.ProductType;
import com.example.Bank_Star.enums.TransactionType;
import com.example.Bank_Star.repository.DynamicRuleRepository;
import com.example.Bank_Star.repository.RecommendationsRepository;
import com.example.Bank_Star.repository.RuleStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final List<RecommendationRule> staticRules;
    private final RecommendationsRepository repository;
    private final DynamicRuleRepository dynamicRuleRepository;
    private final RuleStatsRepository ruleStatsRepository;

    public RecommendationResponse getRecommendations(UUID userId) {
        log.info("Getting recommendations for user: {}", userId);
        if (!repository.isUserExists(userId)) {
            log.warn("User not found: {}", userId);
            throw new UserNotFoundException("User not found with ID: " + userId);
        }

        List<Recommendation> recommendations = staticRules.stream()
                .map(rule -> rule.check(userId, repository))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        dynamicRuleRepository.findAll().forEach(rule -> {
            if (checkDynamicRule(userId, rule)) {
                recommendations.add(new Recommendation(
                        rule.getProductId(),
                        rule.getProductName(),
                        rule.getProductText()
                ));
                incrementRuleStats(rule.getId().toString());
            }
        });

        return new RecommendationResponse(userId, recommendations);
    }

    private boolean checkDynamicRule(UUID userId, DynamicRule rule) {
        log.debug("Checking dynamic rule {} for user {}", rule.getId(), userId);
        for (RuleQuery query : rule.getRule()) {
            boolean result = evaluateQuery(userId, query);
            if (query.getNegate() != null && query.getNegate()) {
                result = !result;
            }
            if (!result) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluateQuery(UUID userId, RuleQuery query) {
        String[] args = query.getArguments().split(",");

        switch (query.getQuery()) {
            case "USER_OF":
                return repository.usesProductType(userId,
                        ProductType.valueOf(args[0]));
            case "ACTIVE_USER_OF":
                return isActiveUser(userId,
                        ProductType.valueOf(args[0]));
            case "TRANSACTION_SUM_COMPARE":
                return compareTransactionSum(userId, args);
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
                return compareDepositWithdraw(userId, args);
            default:
                return false;
        }
    }

    private boolean isActiveUser(UUID userId, ProductType productType) {
        String sql = """
                SELECT COUNT(*) >= 5 FROM transactions t
                JOIN products p ON t.product_id = p.id
                WHERE t.user_id = ? AND p.type = ?
                """;
        return Boolean.TRUE.equals(repository.getJdbcTemplate().queryForObject(
                sql, Boolean.class, userId.toString(), productType.name()));
    }

    private boolean compareTransactionSum(UUID userId, String[] args) {
        ProductType productType = ProductType.valueOf(args[0]);
        TransactionType transactionType = TransactionType.valueOf(args[1]);
        ComparisonType comparison = ComparisonType.valueOf(args[2]);
        BigDecimal value = new BigDecimal(args[3]);

        BigDecimal sum = repository.getTransactionSum(userId, productType, transactionType);
        return compareValues(sum, comparison, value);
    }

    private boolean compareDepositWithdraw(UUID userId, String[] args) {
        ProductType productType = ProductType.valueOf(args[0]);
        ComparisonType comparison = ComparisonType.valueOf(args[1]);

        BigDecimal depositSum = repository.getTransactionSum(userId, productType, TransactionType.DEPOSIT);
        BigDecimal withdrawSum = repository.getTransactionSum(userId, productType, TransactionType.WITHDRAW);
        return compareValues(depositSum, comparison, withdrawSum);
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

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public String getRecommendations(String username) {
        UUID userId = repository.getUserIdByUsername(username);
        if (userId == null) {
            return "Пользователь не найден";
        }

        List<Recommendation> recommendations = repository.getRecommendations(userId);
        if (recommendations.isEmpty()) {
            return "Рекомендации отсутствуют";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Здравствуйте ").append(username).append("!\n");
        sb.append("Новые продукты для вас:\n");
        for (Recommendation recommendation : recommendations) {
            sb.append(recommendation.getName()).append(" - ").append(recommendation.getText()).append("\n");
        }
        return sb.toString();
    }

    public void clearCaches() {
        repository.clearAllCaches();
        log.info("All recommendation caches cleared");
    }

    private void incrementRuleStats(String ruleId) {
        RuleStats stats = ruleStatsRepository.findById(ruleId)
                .orElse(new RuleStats(ruleId, 0));
        stats.setCount(stats.getCount() + 1);
        ruleStatsRepository.save(stats);
        log.debug("Incremented stats for rule {} to {}", ruleId, stats.getCount());
    }
}
