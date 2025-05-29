package com.example.Bank_Star.domen;

import com.example.Bank_Star.repository.RecommendationsRepository;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRule {
    Optional<Recommendation> check(UUID userId, RecommendationsRepository repository);
}