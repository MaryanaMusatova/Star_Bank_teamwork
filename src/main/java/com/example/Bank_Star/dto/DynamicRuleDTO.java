package com.example.Bank_Star.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class DynamicRuleDTO {
    private UUID id;
    private String productName;
    private UUID productId;
    private String productText;

    // Конструктор для ручного маппинга
    public DynamicRuleDTO(UUID id, String productName, UUID productId, String productText) {
        this.id = id;
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
    }

    // Статический метод конвертации
    public static DynamicRuleDTO fromEntity(com.example.Bank_Star.domen.postgres.DynamicRule entity) {
        return new DynamicRuleDTO(
                entity.getId(),
                entity.getProductName(),
                entity.getProductId(),
                entity.getProductText()
        );
    }
}