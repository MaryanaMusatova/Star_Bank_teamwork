package com.example.Bank_Star.repository.postgres;

import com.example.Bank_Star.domen.postgres.DynamicRule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {

    @EntityGraph(attributePaths = {"rules", "rules.queries"})
    @Query("SELECT DISTINCT dr FROM DynamicRule dr " +
            "LEFT JOIN FETCH dr.rules r " +
            "LEFT JOIN FETCH r.queries " +
            "WHERE dr.id = :id")
    Optional<DynamicRule> findByIdWithFullData(@Param("id") UUID id);

    @Query("SELECT dr FROM DynamicRule dr WHERE dr.productId = :productId")
    Optional<DynamicRule> findByProductId(@Param("productId") UUID productId);
}