package com.example.Bank_Star.controller;

import com.example.Bank_Star.domen.Recommendation;
import com.example.Bank_Star.domen.RecommendationResponse;
import com.example.Bank_Star.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers = RecommendationController.class)
class RecommendationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecommendationService recommendationService;

    @Test
    void testGetRecommendationsSuccess() throws Exception {
        UUID validUserId = UUID.randomUUID();

        List<Recommendation> recommendations = List.of(
                new Recommendation(UUID.randomUUID(), "First Rec", "Text of first recommendation"),
                new Recommendation(UUID.randomUUID(), "Second Rec", "Text of second recommendation")
        );

        RecommendationResponse expectedResponse = new RecommendationResponse(validUserId, recommendations);

        when(recommendationService.getRecommendations(validUserId)).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/recommendations/" + validUserId.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testGetRecommendationsNotFound() throws Exception {
        UUID invalidUserId = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");

        when(recommendationService.getRecommendations(invalidUserId)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/recommendations/" + invalidUserId.toString()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}