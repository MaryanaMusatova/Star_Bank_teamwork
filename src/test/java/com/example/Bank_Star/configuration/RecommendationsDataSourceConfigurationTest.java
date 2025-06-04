package com.example.Bank_Star.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RecommendationsDataSourceConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("recommendationsDataSource")
    private DataSource recommendationsDataSource;

    @Autowired
    @Qualifier("recommendationsJdbcTemplate")
    private JdbcTemplate recommendationsJdbcTemplate;

    @BeforeAll
    static void beforeAll() {
        System.out.println("\n---- Тестируем RecommendationsDataSourceConfiguration ----\n");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("\n---- Завершение теста RecommendationsDataSourceConfiguration ----\n");
    }

    @BeforeEach
    void setUp() {
        boolean containsDataSource = applicationContext.containsBean("recommendationsDataSource");
        System.out.println("Bean 'recommendationsDataSource' существует: " + containsDataSource);
    }

    @Test
    void testRecommendationsDataSource() {
        assertThat(recommendationsDataSource).isInstanceOf(HikariDataSource.class);
        HikariDataSource hikariDS = (HikariDataSource) recommendationsDataSource;

        assertThat(hikariDS.getJdbcUrl())
                .withFailMessage("URL-адрес JDBC не соответствует конфигурации.")
                .isEqualTo("jdbc:h2:file:./transaction;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");

        assertThat(hikariDS.getDriverClassName())
                .withFailMessage("Имя класса драйвера не соответствует конфигурации.")
                .isEqualTo("org.h2.Driver");

        assertThat(hikariDS.getUsername())
                .withFailMessage("Имя пользователя не соответствует конфигурации.")
                .isEqualTo("sa");

        assertThat(hikariDS.getPassword())
                .withFailMessage("Пароль не соответствует конфигурации.")
                .isEmpty();
    }

    @Test
    void testRecommendationsJdbcTemplate() {
        assertThat(recommendationsJdbcTemplate.getDataSource())
                .withFailMessage("DataSource JdbcTemplate не указывает на правильный источник.")
                .isSameAs(recommendationsDataSource);
    }
}