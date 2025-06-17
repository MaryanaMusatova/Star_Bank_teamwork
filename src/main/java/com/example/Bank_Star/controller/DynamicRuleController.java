package com.example.Bank_Star.controller;

import com.example.Bank_Star.domen.postgres.DynamicRule;
import com.example.Bank_Star.dto.DynamicRuleDTO;
import com.example.Bank_Star.dto.DynamicRuleMapper;
import com.example.Bank_Star.service.DynamicRuleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rule")
public class DynamicRuleController {
    private final DynamicRuleService service;
    private final DynamicRuleMapper mapper; // Добавляем маппер

    public DynamicRuleController(DynamicRuleService service, DynamicRuleMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DynamicRule addRule(@RequestBody @Valid DynamicRuleDTO dto) {
        // Преобразуем DTO в сущность (маппер должен быть реализован)
        DynamicRule rule = mapper.toEntity(dto);
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