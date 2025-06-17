package com.example.Bank_Star.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class RuleQueryDTO {
    private UUID id;
    private String queryType;
    private Boolean negate;
    private String arguments;
    private UUID ruleId;
}