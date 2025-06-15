package com.example.Bank_Star;

import com.example.Bank_Star.domen.postgres.RecommendationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RecommendationControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate; // используем основной JdbcTemplate

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/recommendations/";

        // Создаем таблицы только если они не существуют
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id UUID PRIMARY KEY, " +
                    "username VARCHAR(255) NOT NULL)");

            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS products (" +
                    "id UUID PRIMARY KEY, " +
                    "type VARCHAR(50) NOT NULL)");

            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS user_products (" +
                    "user_id UUID NOT NULL, " +
                    "product_id UUID NOT NULL, " +
                    "PRIMARY KEY (user_id, product_id))");

            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "id UUID PRIMARY KEY, " +
                    "user_id UUID NOT NULL, " +
                    "product_id UUID NOT NULL, " +
                    "type VARCHAR(50) NOT NULL, " +
                    "amount DECIMAL(19,2) NOT NULL)");
        } catch (Exception e) {
            System.err.println("Ошибка создания таблиц: " + e.getMessage());
        }
    }

    @Test
    void getRecommendations_shouldReturnRecommendationsForValidUser() {

        UUID userId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        jdbcTemplate.update("INSERT INTO users (id, username) VALUES (?, ?)",
                userId.toString(), "testuser");
        jdbcTemplate.update("INSERT INTO products (id, type) VALUES (?, ?)",
                productId1.toString(), "DEBIT");
        jdbcTemplate.update("INSERT INTO products (id, type) VALUES (?, ?)",
                productId2.toString(), "SAVING");
        jdbcTemplate.update("INSERT INTO user_products (user_id, product_id) VALUES (?, ?)",
                userId.toString(), productId1.toString());
        jdbcTemplate.update("INSERT INTO user_products (user_id, product_id) VALUES (?, ?)",
                userId.toString(), productId2.toString());
        jdbcTemplate.update("""
                        INSERT INTO transactions (id, user_id, product_id, type, amount) 
                        VALUES (?, ?, ?, ?, ?)""",
                transactionId.toString(), userId.toString(), productId2.toString(),
                "DEPOSIT", 1500.00);


        ResponseEntity<RecommendationResponse> response = restTemplate.getForEntity(
                baseUrl + userId, RecommendationResponse.class);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().userId()).isEqualTo(userId);
        assertThat(response.getBody().recommendations()).isNotEmpty();
    }

    @Test
    void getRecommendations_shouldReturn404ForNonExistentUser() {
        UUID nonExistentUserId = UUID.randomUUID();

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + nonExistentUserId, String.class);


        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains("Пользователь не найден");
    }
}