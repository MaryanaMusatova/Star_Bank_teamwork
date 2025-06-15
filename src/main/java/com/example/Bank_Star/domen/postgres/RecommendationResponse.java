package com.example.Bank_Star.domen.postgres;

import java.util.List;
import java.util.UUID;

public record RecommendationResponse(
        UUID userId,
        List<Recommendation> recommendations) {
}