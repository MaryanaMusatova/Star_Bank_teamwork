package com.example.Bank_Star.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;


@Data
public class DynamicRuleDTO {
    private UUID id;
    private String productName;
    private UUID productId;
    private String productText;
    @NotEmpty(message = "Rules list must contain at least one item")
    private List<RuleDTO> rules;
}