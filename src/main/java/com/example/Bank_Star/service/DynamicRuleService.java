package com.example.Bank_Star.service;

import com.example.Bank_Star.domen.postgres.DynamicRule;
import com.example.Bank_Star.domen.postgres.RuleStats;
import com.example.Bank_Star.repository.postgres.DynamicRuleRepository;
import com.example.Bank_Star.repository.postgres.RuleStatsRepository;
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

    /**
     * Добавляет новое динамическое правило
     * @param rule Объект DynamicRule для сохранения
     * @return Сохраненный объект DynamicRule
     */
    @Transactional
    public DynamicRule addRule(DynamicRule rule) {
        DynamicRule savedRule = dynamicRuleRepository.save(rule);

        // Создаем запись статистики для нового правила
        RuleStats stats = new RuleStats(savedRule.getId(), 0);
        ruleStatsRepository.save(stats);

        log.info("Добавлено новое правило с ID: {}", savedRule.getId());
        return savedRule;
    }

    /**
     * Получает все динамические правила
     * @return Список всех DynamicRule
     */
    public List<DynamicRule> getAllRules() {
        return dynamicRuleRepository.findAll();
    }

    /**
     * Удаляет правило по productId
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
     * @return Список RuleStats
     */
    public List<RuleStats> getAllRuleStats() {
        return ruleStatsRepository.findAll();
    }

    /**
     * Находит правило по его ID
     * @param id UUID правила
     * @return Найденный DynamicRule
     */
    public DynamicRule getRuleById(UUID id) {
        return dynamicRuleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rule not found with id: " + id));
    }

    /**
     * Обновляет существующее правило
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
}