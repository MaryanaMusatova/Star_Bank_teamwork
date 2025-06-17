package com.example.Bank_Star.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RuleStatsDTO {
    private UUID ruleId;
    private Integer count;
}