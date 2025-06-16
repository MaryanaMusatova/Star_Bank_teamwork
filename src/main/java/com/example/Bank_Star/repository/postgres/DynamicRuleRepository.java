package com.example.Bank_Star.repository.postgres;

import com.example.Bank_Star.domen.postgres.DynamicRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional("postgresTransactionManager")
public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {
    Optional<DynamicRule> findByProductId(UUID productId);

    @Query("SELECT dr FROM DynamicRule dr LEFT JOIN FETCH dr.rules WHERE dr.id = :id")
    Optional<DynamicRule> findByIdWithRules(@Param("id") UUID id);

    @Query("SELECT dr FROM DynamicRule dr " +
            "LEFT JOIN FETCH dr.rules r " +
            "LEFT JOIN FETCH r.queries " +
            "WHERE dr.id = :id")
    Optional<DynamicRule> findByIdWithRulesAndQueries(@Param("id") UUID id); // Исправленное имя метода

    // Добавьте этот новый метод
    @Query("SELECT DISTINCT dr FROM DynamicRule dr " +
            "LEFT JOIN FETCH dr.rules r " +
            "LEFT JOIN FETCH r.queries q " +
            "WHERE dr.id = :id")
    Optional<DynamicRule> findByIdWithFullData(@Param("id") UUID id);
}