package com.example.Bank_Star.repository.postgres;

import com.example.Bank_Star.domen.postgres.DynamicRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional("postgresTransactionManager") // Явно указываем, что транзакции идут в PostgreSQL
public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {
    Optional<DynamicRule> findByProductId(UUID productId);
}