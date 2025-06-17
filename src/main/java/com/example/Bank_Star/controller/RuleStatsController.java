package com.example.Bank_Star.controller;

import com.example.Bank_Star.domen.postgres.RuleStats;
import com.example.Bank_Star.service.DynamicRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rule")
@RequiredArgsConstructor

public class RuleStatsController {
    private final DynamicRuleService dynamicRuleService;

    @GetMapping("/stats")
    public List<RuleStats> getRuleStats() {
        return dynamicRuleService.getAllRuleStats();
    }
}