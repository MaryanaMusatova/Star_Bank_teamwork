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
}