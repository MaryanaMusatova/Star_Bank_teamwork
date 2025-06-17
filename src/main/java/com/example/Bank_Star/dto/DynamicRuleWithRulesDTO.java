package com.example.Bank_Star.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicRuleWithRulesDTO {
    private DynamicRuleDTO dynamicRule;
    private List<RuleDTO> rules;
    private List<RuleQueryDTO> queries;
}