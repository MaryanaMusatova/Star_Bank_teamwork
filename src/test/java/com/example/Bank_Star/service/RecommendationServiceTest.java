package com.example.Bank_Star.service;

import com.example.Bank_Star.domen.postgres.RecommendationResponse;
import com.example.Bank_Star.domen.postgres.RecommendationRule;
import com.example.Bank_Star.repository.RecommendationsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService service;

    @Mock
    private List<RecommendationRule> rules;

    @Mock
    private RecommendationsRepository repository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        List<RecommendationRule> mockedRules = new ArrayList<>();
        mockedRules.add(mock(RecommendationRule.class));

        rules = mockedRules;
    }

    @Test
    void testGetRecommendationsWhenUserExistsButNoRecommendationsAreAvailable() {
        UUID userId = UUID.randomUUID();
        when(repository.isUserExists(userId)).thenReturn(true);

        when(rules.get(0).check(eq(userId), eq(repository))).thenReturn(Optional.empty());

        RecommendationResponse response = service.getRecommendations(userId);

        assertEquals(response.userId(), userId);
        assertEquals(response.recommendations().size(), 0);
    }

    @Test
    void testGetRecommendationsWhenUserDoesntExist() {
        UUID nonExistingUserId = UUID.randomUUID();
        when(repository.isUserExists(nonExistingUserId)).thenReturn(false);

        Throwable thrown = assertThrows(RecommendationService.UserNotFoundException.class, () -> service.getRecommendations(nonExistingUserId));
        assertEquals(thrown.getMessage(), "Пользователь с идентификатором: " + nonExistingUserId + " не найден");
    }

    @Test
    void testGetRecommendationsNoRecommendationsGenerated() {
        UUID existingUserId = UUID.randomUUID();
        when(repository.isUserExists(existingUserId)).thenReturn(true);

        when(rules.get(0).check(eq(existingUserId), eq(repository)))
                .thenReturn(Optional.empty());

        RecommendationResponse response = service.getRecommendations(existingUserId);

        assertEquals(response.userId(), existingUserId);
        assertTrue(response.recommendations().isEmpty());
    }
}