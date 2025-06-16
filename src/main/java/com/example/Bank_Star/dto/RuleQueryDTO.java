package com.example.Bank_Star.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class RuleQueryDTO {
    private UUID id;
    private UUID ruleId;
    private String queryType;
    private Boolean negate;
    private String arguments;

    public RuleQueryDTO(UUID id, UUID ruleId, String queryType,
                        Boolean negate, String arguments) {
        this.id = id;
        this.ruleId = ruleId;
        this.queryType = queryType;
        this.negate = negate;
        this.arguments = arguments;
    }

    public static RuleQueryDTO fromEntity(com.example.Bank_Star.domen.postgres.RuleQuery entity) {
        return new RuleQueryDTO(
                entity.getId(),
                entity.getRule() != null ? entity.getRule().getId() : null,
                entity.getQueryType(),
                entity.getNegate(),
                entity.getArguments()
        );
    }
}