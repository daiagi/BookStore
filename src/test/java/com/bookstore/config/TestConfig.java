package com.bookstore.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import javax.annotation.PreDestroy;

@TestConfiguration
@Testcontainers
public class TestConfig {

    @Container
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.3");

    @Bean
    public PostgreSQLContainer<?> postgresContainer() {
        return postgres;
    }



    @PreDestroy
    public void cleanup() {
        postgres.stop();
        postgres.close();
    }
}
