package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@Slf4j
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestcontainersInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DisplayNameGeneration(value = DisplayNameGenerator.ReplaceUnderscores.class)
public abstract class AbstractIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Value("${server.port}")
    int serverPort;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost:" + serverPort;
    }
}
