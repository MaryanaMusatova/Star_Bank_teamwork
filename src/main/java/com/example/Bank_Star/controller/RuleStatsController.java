package com.example.Bank_Star.controller;

import com.example.Bank_Star.domen.RuleStats;
import com.example.Bank_Star.repository.RuleStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rule")
public class RuleStatsController {

    @Autowired
    private RuleStatsRepository ruleStatsRepository;

    @GetMapping("/stats")
    public List<RuleStats> getRuleStats() {
        return ruleStatsRepository.findAll();
    }
}