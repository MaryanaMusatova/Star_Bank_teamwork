package com.example.Bank_Star.service;

import com.example.Bank_Star.domen.postgres.DynamicRule;
import com.example.Bank_Star.domen.postgres.Rule;
import com.example.Bank_Star.domen.postgres.RuleStats;
import com.example.Bank_Star.dto.DynamicRuleMapper;
import com.example.Bank_Star.dto.DynamicRuleWithRulesDTO;
import com.example.Bank_Star.repository.postgres.DynamicRuleRepository;
import com.example.Bank_Star.repository.postgres.RuleStatsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicRuleService {
    private final DynamicRuleRepository dynamicRuleRepository;
    private final RuleStatsRepository ruleStatsRepository;
    private final DynamicRuleMapper dynamicRuleMapper;

    /**
     * Добавляет новое динамическое правило
     *
     * @param rule Объект DynamicRule для сохранения
     * @return Сохраненный объект DynamicRule
     */
    @Transactional
    public DynamicRule addRule(DynamicRule rule) {
        // 1. Валидация - правило должно содержать хотя бы одно подправило
        if (rule.getRules() == null || rule.getRules().isEmpty()) {
            throw new IllegalArgumentException("DynamicRule должен содержать хотя бы одно Rule");
        }

        // 2. Сохраняем DynamicRule (каскад сохранит связанные Rules)
        DynamicRule savedRule = dynamicRuleRepository.save(rule);

        // 3. Получаем первое подправило
        Rule firstRule = savedRule.getRules().get(0);

        // 4. Создаем статистику для подправила (Rule), а не для основного правила (DynamicRule)
        RuleStats stats = new RuleStats(firstRule.getId(), 0);
        ruleStatsRepository.save(stats);

        log.info("Добавлено новое правило с ID: {} и статистикой для подправила {}",
                savedRule.getId(), firstRule.getId());
        return savedRule;
    }
    /**
     * Получает все динамические правила
     *
     * @return Список всех DynamicRule
     */
    public List<DynamicRule> getAllRules() {
        return dynamicRuleRepository.findAll();
    }

    /**
     * Удаляет правило по productId
     *
     * @param productId UUID productId правила для удаления
     */
    @Transactional
    public void deleteRule(UUID productId) {
        // Находим правило по productId
        DynamicRule rule = dynamicRuleRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Rule not found with productId: " + productId));

        // Удаляем статистику правила
        ruleStatsRepository.deleteById(rule.getId());

        // Удаляем само правило
        dynamicRuleRepository.delete(rule);

        log.info("Удалено правило с productId {} и его статистика", productId);
    }

    /**
     * Получает всю статистику правил
     *
     * @return Список RuleStats
     */
    public List<RuleStats> getAllRuleStats() {
        return ruleStatsRepository.findAll();
    }

    /**
     * Находит правило по его ID
     *
     * @param id UUID правила
     * @return Найденный DynamicRule
     */
    public DynamicRule getRuleById(UUID id) {
        return dynamicRuleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rule not found with id: " + id));
    }

    /**
     * Обновляет существующее правило
     *
     * @param rule Обновленные данные правила
     * @return Обновленный DynamicRule
     */
    @Transactional
    public DynamicRule updateRule(DynamicRule rule) {
        if (!dynamicRuleRepository.existsById(rule.getId())) {
            throw new IllegalArgumentException("Rule not found with id: " + rule.getId());
        }
        return dynamicRuleRepository.save(rule);
    }

    @Transactional(readOnly = true)
    public DynamicRuleWithRulesDTO getFullRule(UUID id) {
        DynamicRule rule = dynamicRuleRepository.findByIdWithFullData(id)
                .orElseThrow(() -> new EntityNotFoundException("Rule not found"));
        return dynamicRuleMapper.toFullResponse(rule);
    }
}

