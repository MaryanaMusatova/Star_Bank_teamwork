package com.example.Bank_Star.service;

import com.example.Bank_Star.domen.postgres.DynamicRule;
import com.example.Bank_Star.domen.postgres.RuleStats;
import com.example.Bank_Star.repository.postgres.DynamicRuleRepository;
import com.example.Bank_Star.repository.postgres.RuleStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicRuleService {
    private final DynamicRuleRepository repository;
    private final RuleStatsRepository ruleStatsRepository;


    public DynamicRule addRule(DynamicRule rule) {
        return repository.save(rule);
    }

    public List<DynamicRule> getAllRules() {
        return repository.findAll();
    }

    public void deleteRule(UUID productId) {
        repository.deleteByProductId(productId);
        // Удаляем статистику для этого правила
        ruleStatsRepository.deleteById(productId.toString());
        log.info("Удалено правило с productId {} и его статистика", productId);
    }

    public List<RuleStats> getAllRuleStats() {
        return ruleStatsRepository.findAll();
    }
}