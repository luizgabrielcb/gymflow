package com.luizgabriel.gymflow.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
@ActiveProfiles("itest")
public class TestcontainersConfiguration {
    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER;

    static {
        POSTGRES_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.9"))
                .withDatabaseName("gymflow");

        POSTGRES_CONTAINER.start();
    }

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return POSTGRES_CONTAINER;
    }
}
