package com.example.Bank_Star.dto;

import com.example.Bank_Star.domen.postgres.DynamicRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DynamicRuleMapper {

    DynamicRuleDTO toDto(DynamicRule entity);

    default DynamicRuleWithRulesDTO toDtoWithRules(DynamicRule entity, List<RuleDTO> rules) {
        return DynamicRuleWithRulesDTO.builder()
                .dynamicRule(toDto(entity))
                .rules(rules)
                .build();
    }

    default DynamicRuleWithRulesDTO toFullDto(DynamicRule entity, List<RuleDTO> rules, List<RuleQueryDTO> queries) {
        return DynamicRuleWithRulesDTO.builder()
                .dynamicRule(toDto(entity))
                .rules(rules)
                .queries(queries)
                .build();
    }

    @Named("mapRules")
    default List<RuleDTO> mapRules(List<com.example.Bank_Star.domen.postgres.Rule> rules) {
        return rules.stream()
                .map(this::toRuleDto)
                .toList();
    }

    @Mapping(source = "dynamicRule.id", target = "dynamicRuleId")
    RuleDTO toRuleDto(com.example.Bank_Star.domen.postgres.Rule rule);

    @Mapping(source = "rule.id", target = "ruleId")
    RuleQueryDTO toRuleQueryDto(com.example.Bank_Star.domen.postgres.RuleQuery query);
}