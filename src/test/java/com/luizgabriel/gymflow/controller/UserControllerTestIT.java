package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.commons.FileUtils;
import com.luizgabriel.gymflow.config.AuthenticatedIntegrationConfig;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.UserRepository;
import com.luizgabriel.gymflow.service.UserService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.javacrumbs.jsonunit.assertj.JsonAssertions;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

@Sql(value = "/sql/user/delete-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class UserControllerTestIT extends AuthenticatedIntegrationConfig {

    private static final String URL = "/users";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("GET v1/users returns a page with all users when successful status code 200")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAll_ReturnsPageWithAllUsers_WhenSuccessful() {
        var token = loginAsAdmin();

        var response = fileUtils.readResourceFile("user/get-response-two-users-200.json");

        var body = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().body().asString();

        JsonAssertions.assertThatJson(body)
                .whenIgnoringPaths("content[*].id")
                .node("content")
                .isEqualTo(response);
    }

    @Test
    @DisplayName("GET v1/users returns 401 unauthorized when not authenticated")
    void findAll_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("user/get-response-user-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/users returns 403 forbidden when not authorized")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAll_ReturnsForbidden_WhenNotAuthorized() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("user/get-response-user-403.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/users/{id} returns an user by id when successful status code 200")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findById_ReturnsListWithAllUsers_WhenSuccessful() {
        var token = loginAsAdmin();

        var response = fileUtils.readResourceFile("user/get-response-one-user-200.json");

        var user = userRepository.findByNameIgnoreCase("Admin Test");

        var body = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", user.getId())
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().body().asString();

        JsonAssertions
                .assertThatJson(body)
                .whenIgnoringPaths("id")
                .isEqualTo(response);
    }

    @Test
    @DisplayName("GET v1/users/{id} returns 401 unauthorized when not authenticated")
    void findById_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("user/get-response-user-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .pathParam("id", 9999L)
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/users/{id} returns 403 forbidden when not authorized")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findById_ReturnsForbidden_WhenNotAuthorized() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("user/get-response-user-403.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", 9999L)
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/users/me returns authenticated user when successful status code 200")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findMe_ReturnsAuthenticatedUser_WhenSuccessful() {
        var token = loginAsAdmin();

        var response = fileUtils.readResourceFile("user/get-response-one-user-200.json");

        var body = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(URL + "/me")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().body().asString();

        JsonAssertions
                .assertThatJson(body)
                .whenIgnoringPaths("id")
                .isEqualTo(response);
    }

    @Test
    @DisplayName("GET v1/users/me returns 401 unauthorized when not authenticated")
    void findMe_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("user/get-response-user-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get(URL + "/me")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("PUT v1/users returns 204 no content when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ReturnsNoContent_WhenSuccessful() {
        var token = loginAsUser();

        var request = fileUtils.readResourceFile("user/put-request-user.json");

        var user = userRepository.findByNameIgnoreCase("User Test");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        var updatedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Assertions.assertThat(updatedUser.getName()).isEqualTo("updated-name");
    }

    @Test
    @DisplayName("PUT v1/users returns 400 bad request when email already exists")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ReturnsBadRequest_WhenEmailAlreadyExists() {
        var token = loginAsUser();

        var request = fileUtils.readResourceFile("user/put-request-user-email-already-exists.json");
        var response = fileUtils.readResourceFile("user/put-response-user-email-already-exists-400.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("PUT v1/users returns 400 bad request when fields are blank")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ReturnsBadRequest_WhenFieldsAreBlank() {
        var token = loginAsUser();

        var request = fileUtils.readResourceFile("user/put-request-user-email-blank-fields.json");
        var response = fileUtils.readResourceFile("user/put-response-user-blank-fields-400.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("PUT v1/users returns 401 unauthorized when not authenticated")
    void update_ReturnsUnauthorized_WhenNotAuthenticated() {
        var request = fileUtils.readResourceFile("user/put-request-user.json");
        var response = fileUtils.readResourceFile("user/put-response-user-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("DELETE v1/users returns 204 no content when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ReturnsNoContent_WhenSuccessful() {
        var token = loginAsAdmin();

        var user = userRepository.findByNameIgnoreCase("User Test");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", user.getId())
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        Assertions.assertThatThrownBy(() -> userService.findById(user.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("DELETE v1/users returns 401 unauthorized when not authenticated")
    void delete_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("user/delete-response-user-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .pathParam("id", 9999L)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("DELETE v1/users returns 403 forbidden when not authorized")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ReturnsForbidden_WhenNotAuthorized() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("user/delete-response-user-403.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", 9999L)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("DELETE v1/users returns 404 not found when user not found")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ReturnsNotFound_WhenUserNotFound() {
        var token = loginAsAdmin();

        var response = fileUtils.readResourceFile("user/delete-response-user-404.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", 9999L)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("DELETE v1/users/me returns 204 no content when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteMe_ReturnsNoContent_WhenSuccessful() {
        var token = loginAsUser();

        var user = userRepository.findByNameIgnoreCase("User Test");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete(URL + "/me")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        Assertions.assertThatThrownBy(() -> userService.findById(user.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("DELETE v1/users/me returns 401 unauthorized when not authenticated")
    void deleteMe_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("user/delete-response-user-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete(URL + "/me")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }
}