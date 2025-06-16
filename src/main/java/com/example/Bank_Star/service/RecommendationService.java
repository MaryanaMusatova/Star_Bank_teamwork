package com.example.Bank_Star.service;

import com.example.Bank_Star.domen.postgres.*;
import com.example.Bank_Star.enums.ComparisonType;
import com.example.Bank_Star.enums.ProductType;
import com.example.Bank_Star.enums.TransactionType;
import com.example.Bank_Star.repository.RecommendationsRepository;
import com.example.Bank_Star.repository.postgres.DynamicRuleRepository;
import com.example.Bank_Star.repository.postgres.RuleStatsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final H2TransactionService transactionService;
    private final ObjectMapper objectMapper; // Добавлен для парсинга JSON

    public RecommendationResponse getRecommendations(UUID userId) {
        log.info("Получение рекомендаций для пользователя: {}", userId);
        if (!repository.isUserExists(userId)) {
            log.warn("Пользователь не найден: {}", userId);
            throw new UserNotFoundException("Пользователь с ID: " + userId + " не найден");
        }

        List<Recommendation> recommendations = staticRules.stream()
                .map(rule -> rule.check(userId, repository))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        // Вставка обновленного блока для динамических рекомендаций
        dynamicRuleRepository.findAll().forEach(dynamicRule -> {
            if (checkDynamicRule(userId, dynamicRule)) {
                recommendations.add(new Recommendation(
                        dynamicRule.getProductId(),
                        dynamicRule.getProductName(),
                        dynamicRule.getProductText()
                ));
                incrementRuleStats(dynamicRule.getId());  // Теперь передаем UUID
            }
        });

        return new RecommendationResponse(userId, recommendations);
    }

    private boolean checkDynamicRule(UUID userId, DynamicRule dynamicRule) {
        log.debug("Проверка динамического правила {} для пользователя {}", dynamicRule.getId(), userId);

        // Получаем все Rule для данного DynamicRule
        for (com.example.Bank_Star.domen.postgres.Rule rule : dynamicRule.getRules()) {
            // Для каждого Rule проверяем его RuleQuery
            for (RuleQuery query : rule.getQueries()) {
                boolean result = evaluateQuery(userId, query);
                if (query.getNegate() != null && query.getNegate()) {
                    result = !result;
                }
                if (!result) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean evaluateQuery(UUID userId, RuleQuery query) {
        try {
            String[] args = objectMapper.readValue(query.getArguments(), String[].class);

            switch (query.getQueryType()) {
                case "USER_OF":
                    return transactionService.userHasTransactionsOfType(userId, args[0]);
                case "ACTIVE_USER_OF":
                    return repository.isActiveUser(userId, ProductType.valueOf(args[0]));
                case "TRANSACTION_SUM_COMPARE":
                    return compareTransactionSum(userId, args);
                case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW":
                    return compareDepositWithdraw(userId, args);
                case "HAS_ANY_PRODUCT":
                    return transactionService.userHasAnyProduct(userId);
                default:
                    log.warn("Неизвестный тип запроса: {}", query.getQueryType());
                    return false;
            }
        } catch (Exception e) {
            log.error("Ошибка при выполнении запроса", e);
            return false;
        }
    }

    private boolean compareTransactionSum(UUID userId, String[] args) {
        if (args.length < 4) {
            log.error("Недостаточно аргументов для TRANSACTION_SUM_COMPARE");
            return false;
        }

        ProductType productType = ProductType.valueOf(args[0]);
        TransactionType transactionType = TransactionType.valueOf(args[1]);
        ComparisonType comparison = ComparisonType.valueOf(args[2]);
        BigDecimal value = new BigDecimal(args[3]);

        BigDecimal sum = repository.getTransactionSum(userId, productType, transactionType);
        return compareValues(sum, comparison, value);
    }

    private boolean compareDepositWithdraw(UUID userId, String[] args) {
        if (args.length < 2) {
            log.error("Недостаточно аргументов для TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW");
            return false;
        }

        ProductType productType = ProductType.valueOf(args[0]);
        ComparisonType comparison = ComparisonType.valueOf(args[1]);

        BigDecimal depositSum = repository.getTransactionSum(userId, productType, TransactionType.DEPOSIT);
        BigDecimal withdrawSum = repository.getTransactionSum(userId, productType, TransactionType.WITHDRAW);

        return compareValues(depositSum, comparison, withdrawSum);
    }

    private boolean compareValues(BigDecimal a, ComparisonType comparison, BigDecimal b) {
        if (a == null || b == null) return false;

        return switch (comparison) {
            case LESS -> a.compareTo(b) < 0;
            case EQUAL -> a.compareTo(b) == 0;
            case GREATER -> a.compareTo(b) > 0;
            case LESS_OR_EQUAL -> a.compareTo(b) <= 0;
            case GREATER_OR_EQUAL -> a.compareTo(b) >= 0;
        };
    }


    public String getRecommendations(String username) {
        UUID userId = repository.getUserIdByUsername(username);
        if (userId == null) {
            return "Пользователь не найден";
        }

        RecommendationResponse response = getRecommendations(userId);
        if (response.recommendations().isEmpty()) {
            return "Рекомендации отсутствуют";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Здравствуйте ").append(username).append("!\n");
        sb.append("Новые продукты для вас:\n");
        for (Recommendation recommendation : response.recommendations()) {
            sb.append(recommendation.name()).append(" - ").append(recommendation.text()).append("\n");
        }
        return sb.toString();
    }

    public void clearCaches() {
        repository.clearAllCaches();
        log.info("Все кэши рекомендаций очищены");
    }

    private void incrementRuleStats(UUID ruleId) {
        RuleStats stats = ruleStatsRepository.findById(ruleId)
                .orElse(new RuleStats(ruleId, 0));

        stats.setCount(stats.getCount() + 1);
        ruleStatsRepository.save(stats);
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}