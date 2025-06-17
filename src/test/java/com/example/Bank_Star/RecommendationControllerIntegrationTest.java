package com.example.Bank_Star;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class RecommendationControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withUsername("student")
            .withPassword("chocolatefrog")
            .withDatabaseName("recommendations");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.postgresql.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.postgresql.datasource.username", postgres::getUsername);
        registry.add("spring.postgresql.datasource.password", postgres::getPassword);
    }

    @Test
    void getRecommendations_ShouldReturnNotFoundForUnknownUser() {
        // 1. Генерируем случайный UUID, которого точно нет в базе
        UUID unknownUserId = UUID.randomUUID();

        // 2. Убедимся, что пользователя действительно нет
        // (если у вас есть доступ к репозиторию в тесте)
        // when(repository.isUserExists(unknownUserId)).thenReturn(false);

        // 3. Отправляем запрос
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/recommendations/" + unknownUserId,
                String.class);

        // 4. Проверяем
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Дополнительная проверка тела ответа
        assertTrue(response.getBody().contains("User not found"));
    }
}