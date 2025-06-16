package com.example.Bank_Star.domen.postgres;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "rule_stats", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleStats {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID ruleId;

    @Column(name = "count")
    private Integer count;
}