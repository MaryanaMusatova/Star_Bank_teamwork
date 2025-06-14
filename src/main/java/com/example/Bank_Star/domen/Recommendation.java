package com.example.Bank_Star.domen;

import java.util.UUID;

public record Recommendation(
        UUID id,
        String name,
        String text
) {
    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }
}


