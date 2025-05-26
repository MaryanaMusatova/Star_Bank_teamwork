package com.example.Bank_Star.domen;

import java.util.List;
import java.util.UUID;

public record RecommendationResponse(
        UUID userId,
        List<Recommendation> recommendations) {
}