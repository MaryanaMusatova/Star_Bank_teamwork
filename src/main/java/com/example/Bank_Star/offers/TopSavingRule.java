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
public class TopSavingRule implements RecommendationRule {
    private static final Recommendation TOP_SAVING = new Recommendation(
            UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925"),
            "Top Saving",
            "Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, " +
                    "который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков " +
                    "и потерянных квитанций — всё под контролем!" +
                    "Преимущества «Копилки»:" +
                    "Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет " +
                    "автоматически переводить определенную сумму на ваш счет." +
                    "Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и " +
                    "корректируйте стратегию при необходимости." +
                    "Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен " +
                    "только через мобильное приложение или интернет-банкинг." +
                    "Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!"
    );

    @Override
    public Optional<Recommendation> check(UUID userId, RecommendationsRepository repository) {
        boolean rule1 = repository.usesProductType(userId, ProductType.DEBIT);
        boolean rule2a = repository.compareTransactionSum(
                userId, ProductType.DEBIT, TransactionType.DEPOSIT,
                ComparisonType.GREATER_OR_EQUAL, new BigDecimal("50000"));
        boolean rule2b = repository.compareTransactionSum(
                userId, ProductType.SAVING, TransactionType.DEPOSIT,
                ComparisonType.GREATER_OR_EQUAL, new BigDecimal("50000"));
        boolean rule2 = rule2a || rule2b;
        boolean rule3 = repository.compareTransactionSum(
                userId, ProductType.DEBIT, TransactionType.DEPOSIT,
                ComparisonType.GREATER,
                repository.getTransactionSum(userId, ProductType.DEBIT, TransactionType.WITHDRAW));
        return rule1 && rule2 && rule3 ? Optional.of(TOP_SAVING) : Optional.empty();
    }
}