package com.example.Bank_Star.dto;

import java.util.List;
import java.util.UUID;

public record RecommendationResponseDTO(
        UUID userId,
        List<RecommendationDTO> recommendations
) {}

