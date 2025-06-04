package com.example.Bank_Star.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AppConfigTest {

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @BeforeEach
    void setUp() {}

    @Test
    void testDataSourceConfiguration() {
        assertThat(dataSource).isInstanceOf(HikariDataSource.class);
        HikariDataSource hikariDS = (HikariDataSource) dataSource;
        assertThat(hikariDS.getJdbcUrl()).isEqualTo("jdbc:h2:file:./transaction;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        assertThat(hikariDS.getDriverClassName()).isEqualTo("org.h2.Driver");
        assertThat(hikariDS.getUsername()).isEmpty();
        assertThat(hikariDS.getPassword()).isEmpty();
    }
}