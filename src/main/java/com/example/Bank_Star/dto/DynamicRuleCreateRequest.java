package com.example.Bank_Star.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DynamicRuleCreateRequest {
    private String productName;
    private UUID productId;
    private String productText;
    private List<RuleCreateRequest> rules;
}


