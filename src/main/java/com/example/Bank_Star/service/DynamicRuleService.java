package com.example.Bank_Star.service;


import com.example.Bank_Star.domen.DynamicRule;
import com.example.Bank_Star.repository.DynamicRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DynamicRuleService {
    private final DynamicRuleRepository repository;

    public DynamicRule addRule(DynamicRule rule) {
        return repository.save(rule);
    }

    public List<DynamicRule> getAllRules() {
        return repository.findAll();
    }

    public void deleteRule(UUID productId) {
        repository.deleteByProductId(productId);
    }
}