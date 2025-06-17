package com.example.Bank_Star.controller;

import com.example.Bank_Star.dto.RecommendationDTO;
import com.example.Bank_Star.dto.RecommendationResponseDTO;
import com.example.Bank_Star.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @Test
    void getRecommendations_ShouldReturnValidResponse() throws Exception {
        UUID userId = UUID.randomUUID();
        RecommendationDTO recommendation1 = new RecommendationDTO(UUID.randomUUID(), "Кредит", "Описание кредита");
        RecommendationDTO recommendation2 = new RecommendationDTO(UUID.randomUUID(), "Инвестиции", "Описание инвестиций");

        RecommendationResponseDTO response = new RecommendationResponseDTO(userId, List.of(recommendation1, recommendation2));

        when(recommendationService.getRecommendations(userId)).thenReturn(response);

        mockMvc.perform(get("/recommendations/{userId}", userId)).andExpect(status().isOk()).andExpect(jsonPath("$.userId").value(userId.toString())).andExpect(jsonPath("$.recommendations.length()").value(2)).andExpect(jsonPath("$.recommendations[0].id").value(recommendation1.id().toString())) // используем id() вместо getId()
                .andExpect(jsonPath("$.recommendations[0].name").value(recommendation1.name())) // используем name() вместо getName()
                .andExpect(jsonPath("$.recommendations[1].text").value(recommendation2.text())); // используем text() вместо getText()
    }

    @Test
    void getRecommendations_ShouldReturnNotFound() throws Exception {
        UUID userId = UUID.randomUUID();

        when(recommendationService.getRecommendations(userId)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "User not found"));

        mockMvc.perform(get("/recommendations/{userId}", userId)).andExpect(status().isNotFound());
    }

    @Test
    void getRecommendations_ShouldReturnEmptyList() throws Exception {
        UUID userId = UUID.randomUUID();
        RecommendationResponseDTO response = new RecommendationResponseDTO(userId, List.of());

        when(recommendationService.getRecommendations(userId)).thenReturn(response);

        mockMvc.perform(get("/recommendations/{userId}", userId)).andExpect(status().isOk()).andExpect(jsonPath("$.recommendations.length()").value(0));
    }
}