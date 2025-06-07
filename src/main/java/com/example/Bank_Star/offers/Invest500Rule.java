package com.example.Bank_Star.offers;

import com.example.Bank_Star.domen.Recommendation;
import com.example.Bank_Star.domen.RecommendationRule;
import com.example.Bank_Star.enums.ComparisonType;
import com.example.Bank_Star.enums.ProductType;
import com.example.Bank_Star.enums.TransactionType;
import com.example.Bank_Star.repository.RecommendationsRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class Invest500Rule implements RecommendationRule {
    static final Recommendation INVEST_500 = new Recommendation(
            UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
            "Invest 500",
            "Откройте свой путь к успеху с индивидуальным " +
                    "инвестиционным счетом (ИИС) от нашего банка! " +
                    "Воспользуйтесь налоговыми льготами и начните инвестировать с умом. " +
                    "Пополните счет до конца года и получите выгоду в виде вычета " +
                    "на взнос в следующем налоговом периоде. " +
                    "Не упустите возможность разнообразить свой портфель, " +
                    "снизить риски и следить за актуальными рыночными тенденциями. " +
                    "Откройте ИИС сегодня и станьте ближе к финансовой независимости!");

    @Override
    public Optional<Recommendation> check(UUID userId, RecommendationsRepository repository) {
        boolean rule1 = repository.usesProductType(userId, ProductType.DEBIT);
        boolean rule2 = repository.hasNoProductType(userId, ProductType.INVEST);
        boolean rule3 = repository.compareTransactionSum(userId,
                ProductType.SAVING,
                TransactionType.DEPOSIT,
                ComparisonType.GREATER,
                new BigDecimal("1000"));
        return rule1 && rule2 && rule3 ? Optional.of(INVEST_500) : Optional.empty();
    }
}