package com.example.Bank_Star.service;

import com.example.Bank_Star.repository.RecommendationsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecommendationService {
    private final RecommendationsRepository repository;

    public RecommendationResponse getRecommendations(UUID userId) {
        if (!repository.isUserExists(userId)) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }

                .map(rule -> rule.check(userId, repository))
                .filter(Optional::isPresent)
                .map(Optional::get)

        return new RecommendationResponse(userId, recommendations);
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}