package com.luizgabriel.gymflow.config;

import com.luizgabriel.gymflow.dto.response.LoginResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.http.HttpStatus;

public abstract class AuthenticatedIntegrationConfig extends IntegrationTestConfig {

    protected String loginAsAdminToken() {
        var loginResponse = login("admin.test@gmail.com", "test");

        return loginResponse.token();
    }

    protected String loginAsUserToken() {
        var loginResponse = login("user.test@gmail.com", "test");

        return loginResponse.token();
    }

    protected String loginAsUserRefreshToken() {
        var loginResponse = login("user.test@gmail.com", "test");

        return loginResponse.refreshToken();
    }

    protected String loginAdminRefreshToken() {
        var loginResponse = login("admin.test@gmail.com", "test");

        return loginResponse.refreshToken();
    }

    private LoginResponse login(String email, String password) {
        var response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body("""
                        {"email": "%s", "password": "%s"}
                        """.formatted(email, password))
                .when()
                .post("/auth/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();

        return new LoginResponse(
                response.path("token"),
                response.path("refreshToken")
        );
    }
}
