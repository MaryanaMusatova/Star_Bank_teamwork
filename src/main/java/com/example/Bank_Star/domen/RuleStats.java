package com.example.Bank_Star.domen;

import jakarta.persistence.Entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Entity
@Table(name = "rule_stats")
public class RuleStats {

    @Id
    private String ruleId;
    private int count;
    @jakarta.persistence.Id
    private Long id;

    public RuleStats(String ruleId, int i) {
        this.ruleId = ruleId;
        this.count = i;
    }

    public RuleStats() {
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}