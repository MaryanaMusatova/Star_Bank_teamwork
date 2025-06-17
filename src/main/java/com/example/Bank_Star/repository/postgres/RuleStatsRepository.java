package com.example.Bank_Star.repository.postgres;

import com.example.Bank_Star.domen.postgres.RuleStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RuleStatsRepository extends JpaRepository<RuleStats, UUID> {
}