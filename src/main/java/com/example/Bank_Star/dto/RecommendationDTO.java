package com.example.Bank_Star.dto;

import java.util.UUID;

public record RecommendationDTO(
        UUID id,
        String name,
        String text
) {}