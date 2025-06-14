package com.example.Bank_Star.offers;

import com.example.Bank_Star.domen.Recommendation;
import com.example.Bank_Star.domen.RecommendationRule;
import com.example.Bank_Star.enums.ProductType;
import com.example.Bank_Star.enums.TransactionType;
import com.example.Bank_Star.repository.RecommendationsRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRule implements RecommendationRule {
    private static final Recommendation SIMPLE_CREDIT = new Recommendation(
            UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f"),
            "Простой кредит",
            "Откройте мир выгодных кредитов с нами!..."
    );

    @Override
    public Optional<Recommendation> check(UUID userId, RecommendationsRepository repository) {
        boolean rule1 = repository.hasNoProductType(userId, ProductType.CREDIT);
        BigDecimal depositSum = repository.getTransactionSum(userId, ProductType.DEBIT, TransactionType.DEPOSIT);
        BigDecimal withdrawSum = repository.getTransactionSum(userId, ProductType.DEBIT, TransactionType.WITHDRAW);
        boolean rule2 = depositSum.compareTo(withdrawSum) > 0;
        boolean rule3 = withdrawSum.compareTo(new BigDecimal("100000")) > 0;
        return rule1 && rule2 && rule3 ? Optional.of(SIMPLE_CREDIT) : Optional.empty();
    }
}
