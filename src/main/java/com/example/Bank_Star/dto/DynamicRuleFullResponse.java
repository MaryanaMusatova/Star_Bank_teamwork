// DynamicRuleFullResponse.java
package com.example.Bank_Star.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DynamicRuleFullResponse {
    private DynamicRuleDTO dynamicRule;
    private List<RuleDTO> rules;
    private List<RuleQueryDTO> queries;
}