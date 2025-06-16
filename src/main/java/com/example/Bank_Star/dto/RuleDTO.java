package com.example.Bank_Star.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class RuleDTO {
    private UUID id;
    private String productName;
    private UUID productId;
    private String productText;
    private UUID dynamicRuleId;

    public RuleDTO(UUID id, String productName, UUID productId,
                   String productText, UUID dynamicRuleId) {
        this.id = id;
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.dynamicRuleId = dynamicRuleId;
    }

    public static RuleDTO fromEntity(com.example.Bank_Star.domen.postgres.Rule entity) {
        return new RuleDTO(
                entity.getId(),
                entity.getProductName(),
                entity.getProductId(),
                entity.getProductText(),
                entity.getDynamicRule() != null ? entity.getDynamicRule().getId() : null
        );
    }
}