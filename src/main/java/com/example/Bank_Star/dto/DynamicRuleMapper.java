package com.example.Bank_Star.dto;

import com.example.Bank_Star.domen.postgres.DynamicRule;
import com.example.Bank_Star.domen.postgres.Rule;
import com.example.Bank_Star.domen.postgres.RuleQuery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DynamicRuleMapper {

    // Добавляем метод для преобразования DTO в Entity
    @Mapping(target = "id", ignore = true)
    // ID генерируется БД
    DynamicRule toEntity(DynamicRuleDTO dto);

    @Mapping(target = "rules", ignore = true)
        // Игнорируем rules при маппинге DynamicRule -> DynamicRuleDTO
    DynamicRuleDTO toDto(DynamicRule entity);

    @Mapping(target = "dynamicRuleId", source = "dynamicRule.id")
    RuleDTO toRuleDto(Rule rule);

    @Mapping(target = "ruleId", source = "rule.id")
    RuleQueryDTO toRuleQueryDto(RuleQuery query);

    @Named("mapRules")
    default List<RuleDTO> mapRules(List<Rule> rules) {
        if (rules == null) {
            return List.of();
        }
        return rules.stream()
                .map(this::toRuleDto)
                .toList();
    }

    @Named("mapQueries")
    default List<RuleQueryDTO> mapQueries(List<Rule> rules) {
        if (rules == null) {
            return List.of();
        }
        return rules.stream()
                .flatMap(rule -> rule.getQueries() != null ? rule.getQueries().stream() : null)
                .map(this::toRuleQueryDto)
                .toList();
    }

    default DynamicRuleWithRulesDTO toFullResponse(DynamicRule entity) {
        if (entity == null) {
            return null;
        }

        DynamicRuleDTO dynamicRuleDTO = toDto(entity);
        List<RuleDTO> ruleDTOs = mapRules(entity.getRules());
        List<RuleQueryDTO> queryDTOs = mapQueries(entity.getRules());

        return DynamicRuleWithRulesDTO.builder()
                .dynamicRule(dynamicRuleDTO)
                .rules(ruleDTOs)
                .queries(queryDTOs)
                .build();
    }
}