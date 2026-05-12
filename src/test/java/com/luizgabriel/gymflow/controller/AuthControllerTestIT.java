package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.commons.FileUtils;
import com.luizgabriel.gymflow.config.AuthenticatedIntegrationConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

@Sql(value = "/sql/user/delete-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class AuthControllerTestIT extends AuthenticatedIntegrationConfig {
    private static final String URL = "/auth";

    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("POST v1/auth/register returns 201 created when successful")
    void register_ReturnsCreated_WhenSuccessful() {
        var request = fileUtils.readResourceFile("user/post-request-register-user.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/register")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("POST v1/auth/register returns 400 bad request when fields are blank")
    void register_ReturnsBadRequest_WhenFieldsAreBlank() {
        var request = fileUtils.readResourceFile("user/post-request-register-user-blank-fields.json");
        var response = fileUtils.readResourceFile("user/post-response-register-user-blank-fields-400.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/register")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("POST v1/auth/register returns 400 bad request when email already exists")
    @Sql(value = "/sql/user/insert-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void register_ReturnsBadRequest_WhenEmailAlreadyExists() {
        var request = fileUtils.readResourceFile("user/post-request-register-user-email-already-exists.json");
        var response = fileUtils.readResourceFile("user/post-response-register-user-email-already-exists-400.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/register")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("POST v1/auth/login returns 200 login response when successful")
    @Sql(value = "/sql/user/insert-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void login_ReturnsLoginResponse_WhenSuccessful() {
        var request = fileUtils.readResourceFile("user/post-request-login-user.json");
        var response = fileUtils.readResourceFile("user/post-response-login-user-200.json");

        var token = loginAsUser();

        response = response.replace("{token}", token);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/login")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("POST v1/auth/login returns 400 bad request when fields are blank")
    void login_ReturnsBadRequest_WhenFieldsAreBlank() {
        var request = fileUtils.readResourceFile("user/post-request-login-user-blank-fields.json");
        var response = fileUtils.readResourceFile("user/post-response-login-user-blank-fields-400.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/login")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("POST v1/auth/login returns 401 unauthorized when invalid credentials")
    void login_ReturnsUnauthorized_WhenInvalidCredentials() {
        var request = fileUtils.readResourceFile("user/post-request-login-user.json");
        var response = fileUtils.readResourceFile("user/post-response-login-user-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/login")
                .then()
                .log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response));
    }
}