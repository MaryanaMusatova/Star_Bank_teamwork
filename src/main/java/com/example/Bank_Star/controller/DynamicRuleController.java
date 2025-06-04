package com.example.Bank_Star.controller;

import com.example.Bank_Star.domen.DynamicRule;
import com.example.Bank_Star.service.DynamicRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rule")
public class DynamicRuleController {
    private final DynamicRuleService service;

    public DynamicRuleController(DynamicRuleService service) {
        this.service = service;
    }

    @PostMapping
    public DynamicRule addRule(@RequestBody DynamicRule rule) {
        return service.addRule(rule);
    }

    @GetMapping
    public List<DynamicRule> getAllRules() {
        return service.getAllRules();
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRule(@PathVariable UUID productId) {
        service.deleteRule(productId);
    }
}