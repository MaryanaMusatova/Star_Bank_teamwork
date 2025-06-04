package com.example.Bank_Star.service;

import com.example.Bank_Star.domen.Recommendation;
import com.example.Bank_Star.domen.RecommendationResponse;
import com.example.Bank_Star.domen.RecommendationRule;
import com.example.Bank_Star.repository.RecommendationsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//Центральный сервис рекомендательной системы,
// который координирует проверку правил и формирование рекомендаций (PS Артем Васяткин)
@Service
public class RecommendationService {
    private final List<RecommendationRule> rules;
    private final RecommendationsRepository repository;


    public RecommendationService(List<RecommendationRule> rules,
                                 RecommendationsRepository repository) {

        this.rules = rules;
        this.repository = repository;
    }

    public RecommendationResponse getRecommendations(UUID userId) {

        if (!repository.isUserExists(userId)) {

            throw new UserNotFoundException("User not found with ID: " + userId);
        }

        List<Recommendation> recommendations = rules.stream()
                .map(rule -> rule.check(userId, repository))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        return new RecommendationResponse(userId, recommendations);
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public String getRecommendations(String username) {
        UUID userId = repository.getUserIdByUsername(username);
        if (userId == null) {
            return "Пользователь не найден";
        }

        List<Recommendation> recommendations = repository.getRecommendations(userId);
        if (recommendations.isEmpty()) {
            return "Рекомендации отсутствуют";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Здравствуйте ").append(username).append("!\n");
        sb.append("Новые продукты для вас:\n");
        for (Recommendation recommendation : recommendations) {
            sb.append(recommendation.getName()).append(" - ").append(recommendation.getText()).append("\n");
        }
        return sb.toString();
    }

    public void clearCaches() {
    }
}