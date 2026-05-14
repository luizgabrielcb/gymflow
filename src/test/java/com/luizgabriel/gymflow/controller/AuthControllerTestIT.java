package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.commons.FileUtils;
import com.luizgabriel.gymflow.config.AuthenticatedIntegrationConfig;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.RefreshTokenRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

@Sql(value = "/sql/auth/delete-refresh-tokens.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/sql/user/delete-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class AuthControllerTestIT extends AuthenticatedIntegrationConfig {

    private static final String URL = "/auth";

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

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
                .statusCode(HttpStatus.CREATED.value())
                .log().all();
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
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/auth/register returns 400 bad request when email already exists")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void register_ReturnsBadRequest_WhenEmailAlreadyExists() {
        var request = fileUtils.readResourceFile("user/post-request-register-user-email-already-exists.json");
        var response = fileUtils.readResourceFile("user/post-response-register-user-email-already-exists-400.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/register")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/auth/login returns 200 login response when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void login_ReturnsLoginResponse_WhenSuccessful() {
        var request = fileUtils.readResourceFile("user/post-request-login-user.json");

        loginAsUserToken();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/login")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("token", Matchers.notNullValue())
                .log().all();
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
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
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
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/auth/refresh returns 200 ok when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void refresh_ReturnsOk_WhenSuccessful() {
        var refreshToken = loginAsUserRefreshToken();

        var request = fileUtils.readResourceFile("user/post-request-refresh-token.json");

        request = request.replace("{refresh-token}", refreshToken);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/refresh")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", Matchers.notNullValue())
                .body("refreshToken", Matchers.notNullValue())
                .log().all();
    }

    @Test
    @DisplayName("POST v1/auth/refresh returns 400 bad request when fields are blank")
    void refresh_ReturnsBadRequest_WhenFieldsAreBlank() {
        var request = fileUtils.readResourceFile("user/post-request-refresh-token-blank-fields.json");
        var response = fileUtils.readResourceFile("user/post-response-refresh-token-blank-fields-400.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/refresh")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/auth/refresh returns 401 unauthorized when refresh token is invalid")
    void refresh_ReturnsUnauthorized_WhenRefreshTokenIsInvalid() {
        var request = fileUtils.readResourceFile("user/post-request-refresh-token.json");
        var response = fileUtils.readResourceFile("user/post-response-refresh-token-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/refresh")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/auth/logout returns 204 no content when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void logout_ReturnsNoContent_WhenSuccessful() {
        var refreshToken = loginAsUserRefreshToken();

        var request = fileUtils.readResourceFile("user/post-request-refresh-token.json");

        request = request.replace("{refresh-token}", refreshToken);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/logout")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        var refreshTokenRevoked = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new NotFoundException("Refresh token not found"));

        Assertions.assertThat(refreshTokenRevoked.isRevoked()).isTrue();
    }

    @Test
    @DisplayName("POST v1/auth/logout returns 401 unauthorized when refresh token is invalid")
    void logout_ReturnsUnauthorized_WhenRefreshTokenIsInvalid() {
        var request = fileUtils.readResourceFile("user/post-request-refresh-token.json");
        var response = fileUtils.readResourceFile("user/post-response-refresh-token-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL + "/logout")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }
}