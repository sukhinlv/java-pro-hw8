package com.example;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import static com.example.utils.FileUtils.loadFileFromClasspath;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

class AppCommonsTest extends AbstractIntegrationTest {

    @Value("${springdoc.swagger-ui.url}")
    String swaggerSpec;

    @Test
    void should_load_swagger_specification() {
        // Given
        String expectedSpec = loadFileFromClasspath("api" + swaggerSpec);

        // When
        String actualSpec = given()
                .contentType(ContentType.JSON)
                .basePath(swaggerSpec)
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().asString();

        // Then
        assertThat(actualSpec).isEqualToNormalizingWhitespace(expectedSpec);
    }
}
