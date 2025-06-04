package com.example.Bank_Star.repository;


import com.example.Bank_Star.domen.DynamicRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DynamicRuleRepository extends JpaRepository<DynamicRule, UUID> {
    void deleteByProductId(UUID productId);
}