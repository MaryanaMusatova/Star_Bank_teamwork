package com.example.Bank_Star.repository.postgres;

import com.example.Bank_Star.domen.postgres.RuleStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RuleStatsRepository extends JpaRepository<RuleStats, String> {
}