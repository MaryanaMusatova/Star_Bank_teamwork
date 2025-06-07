package com.example.Bank_Star.domen;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rule_stats")
public class RuleStats {
    @Id
    private String ruleId;
    private int count;

    public RuleStats() {
    }

    public RuleStats(String ruleId, int count) {
        this.ruleId = ruleId;
        this.count = count;
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
}