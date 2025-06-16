package com.example.Bank_Star.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DynamicRuleWithRulesDTO {
    private DynamicRuleDTO dynamicRule;
    private List<RuleDTO> rules;
    private List<RuleQueryDTO> queries;

    public DynamicRuleWithRulesDTO(DynamicRuleDTO dynamicRule,
                                   List<RuleDTO> rules,
                                   List<RuleQueryDTO> queries) {
        this.dynamicRule = dynamicRule;
        this.rules = rules;
        this.queries = queries;
    }
}
