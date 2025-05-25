package com.example.Bank_Star;

import com.example.Bank_Star.domen.Recommendation;
import com.example.Bank_Star.domen.RecommendationResponse;
import com.example.Bank_Star.service.RecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecommendationControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RecommendationService recommendationService;

    @Test
    void getRecommendations_ShouldReturnValidResponse() {

        UUID userId = UUID.fromString("cd515076-5d8a-44be-930e-8d4fcb79f42d");
        Recommendation recommendation = new Recommendation(
                UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a"),
                "Invest 500",
                "Test description"
        );

        when(recommendationService.getRecommendations(userId))
                .thenReturn(new RecommendationResponse(userId, List.of(recommendation)));


        String url = "http://localhost:" + port + "/recommendations/" + userId;
        ResponseEntity<RecommendationResponse> response = restTemplate.getForEntity(
                url, RecommendationResponse.class);


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().recommendations().size());
        assertEquals("Invest 500", response.getBody().recommendations().get(0).name());
    }

    @Test
    void getRecommendations_WhenUserNotFound_ShouldReturn404() {

        UUID nonExistentUserId = UUID.randomUUID();

        when(recommendationService.getRecommendations(nonExistentUserId))
                .thenThrow(new RecommendationService.UserNotFoundException("User not found"));


        String url = "http://localhost:" + port + "/recommendations/" + nonExistentUserId;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);


        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("User not found"));
    }
}