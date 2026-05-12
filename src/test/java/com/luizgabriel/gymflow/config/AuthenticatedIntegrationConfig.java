package com.luizgabriel.gymflow.config;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.http.HttpStatus;

public abstract class AuthenticatedIntegrationConfig extends IntegrationTestConfig {

    protected String loginAsAdmin() {
        return login("admin.test@gmail.com", "test");
    }

    protected String loginAsUser() {
        return login("user.test@gmail.com", "test");
    }

    private String login(String email, String password) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {"email": "%s", "password": "%s"}
                        """.formatted(email, password))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .path("token");
    }
}
