package com.example;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

/**
 * Поднимает тестконтейнеры и устанавливает их параметры в application.yml.
 * Заполняет БД и так далее.
 *
 * @see <a href="https://maciejwalkowiak.com/blog/testcontainers-spring-boot-setup/">The best way to use Testcontainers with Spring Boot</a>
 */
public class TestcontainersInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final String POSTGRES_IMAGE = "postgres:15-alpine";

    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE));

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
        Startables.deepStart(
                POSTGRES_CONTAINER
        ).join();

        Runtime.getRuntime().addShutdownHook(new Thread(POSTGRES_CONTAINER::close));

        initProperties(applicationContext);
    }

    private static void initProperties(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                "spring.datasource.url: " + POSTGRES_CONTAINER.getJdbcUrl(),
                "spring.datasource.username: " + POSTGRES_CONTAINER.getUsername(),
                "spring.datasource.password: " + POSTGRES_CONTAINER.getPassword()
        ).applyTo(applicationContext.getEnvironment());
    }
}
