package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.commons.FileUtils;
import com.luizgabriel.gymflow.config.AuthenticatedIntegrationConfig;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.ExerciseRepository;
import com.luizgabriel.gymflow.service.ExerciseService;
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
@Sql(value = "/sql/exercise/delete-exercises.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class ExerciseControllerTestIT extends AuthenticatedIntegrationConfig {
    private static final String URL = "/exercises";

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private ExerciseService exerciseService;

    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("POST v1/exercises returns created exercise id when successful")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void save_ReturnsCreatedExerciseId_WhenSuccessful() {
        var token = loginAsAdmin();

        var request = fileUtils.readResourceFile("exercise/post-request-exercise.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .body("id", Matchers.notNullValue());
    }

    @Test
    @DisplayName("POST v1/exercises returns 400 bad request when fields are blank")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void save_ReturnsBadRequest_WhenFieldsAreBlank() {
        var token = loginAsAdmin();

        var request = fileUtils.readResourceFile("exercise/post-request-exercise-blank-fields.json");
        var response = fileUtils.readResourceFile("exercise/post-response-exercise-blank-fields-400.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("POST v1/exercises returns 401 unauthorized when not authenticated")
    void save_ReturnsUnauthorized_WhenNotAuthenticated() {
        var request = fileUtils.readResourceFile("exercise/post-request-exercise.json");
        var response = fileUtils.readResourceFile("exercise/post-response-exercise-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all()
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("POST v1/exercises returns 403 forbidden when not authorized")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void save_ReturnsForbidden_WhenNotAuthorized() {
        var token = loginAsUser();

        var request = fileUtils.readResourceFile("exercise/post-request-exercise.json");
        var response = fileUtils.readResourceFile("exercise/post-response-exercise-403.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all()
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("GET v1/exercises returns a list with all exercises when successful")
    @Sql(value = "/sql/exercise/insert-exercises.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAll_ReturnsListWithAllExercises_WhenSuccessful() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("exercise/get-response-two-exercises-200.json");

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
                .whenIgnoringPaths("[*].id")
                .isEqualTo(response);
    }

    @Test
    @DisplayName("GET v1/exercises returns an empty list when exercises not found")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAll_ReturnsEmptyList_WhenExercisesNotFound() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("exercise/get-response-empty-list-200.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("GET v1/exercises returns 401 unauthorized when not authenticated")
    void findAll_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("exercise/get-response-exercise-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all()
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("GET v1/exercises/{id} returns exercise by id when successful")
    @Sql(value = "/sql/exercise/insert-exercises.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findById_ReturnsExercise_WhenSuccessful() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("exercise/get-response-one-exercise-200.json");

        var exercise = exerciseRepository.findByNameIgnoreCase("Squat");

        var body = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", exercise.getFirst().getId())
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().body().asString();

        JsonAssertions.assertThatJson(body)
                .whenIgnoringPaths("id")
                .isEqualTo(response);
    }

    @Test
    @DisplayName("GET v1/exercises/{id} returns 401 unauthorized when not authenticated")
    void findById_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("exercise/get-response-exercise-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .pathParam("id", 1L)
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all()
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("GET v1/exercises/{id} returns 404 not found when exercise not found")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findById_ReturnsNotFound_WhenExerciseNotFound() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("exercise/get-response-exercise-404.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", 9999L)
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("PUT v1/exercises returns no content when successful")
    @Sql(value = "/sql/exercise/insert-exercises.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ReturnsNoContent_WhenSuccessful() {
        var token = loginAsAdmin();

        var request = fileUtils.readResourceFile("exercise/put-request-exercise.json");

        var exercise = exerciseRepository.findByNameIgnoreCase("Bench Press");

        request = request.replace("{id}", exercise.getFirst().getId().toString());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        var updatedExercise = exerciseRepository.findById(exercise.getFirst().getId())
                .orElseThrow(() -> new NotFoundException("Exercise not found"));

        Assertions.assertThat(updatedExercise.getName()).isEqualTo("Push-up");
    }

    @Test
    @DisplayName("PUT v1/exercises returns 400 bad request when fields are blank and id null")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ReturnsBadRequest_WhenFieldsAreBlankAndIdNull() {
        var token = loginAsAdmin();

        var request = fileUtils.readResourceFile("exercise/put-request-exercise-blank-fields-and-id-null.json");
        var response = fileUtils.readResourceFile("exercise/put-response-exercise-blank-fields-and-id-null-400.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .log().all()
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("PUT v1/exercises returns 401 unauthorized when not authenticated")
    void update_ReturnsUnauthorized_WhenNotAuthenticated() {
        var request = fileUtils.readResourceFile("exercise/put-request-exercise.json");
        var response = fileUtils.readResourceFile("exercise/put-response-exercise-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all()
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("PUT v1/exercises returns 403 forbidden when not authorized")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ReturnsForbidden_WhenNotAuthorized() {
        var token = loginAsUser();

        var request = fileUtils.readResourceFile("exercise/put-request-exercise.json");
        var response = fileUtils.readResourceFile("exercise/put-response-exercise-403.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all()
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("PUT v1/exercises returns 404 not found when exercise not found")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ReturnsNotFound_WhenExerciseNotFound() {
        var token = loginAsAdmin();

        var request = fileUtils.readResourceFile("exercise/put-request-exercise-with-id-99.json");
        var response = fileUtils.readResourceFile("exercise/put-response-exercise-404.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("DELETE v1/exercises returns no content when successful")
    @Sql(value = "/sql/exercise/insert-exercises.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ReturnsNoContent_WhenSuccessful() {
        var token = loginAsAdmin();

        var exercise = exerciseRepository.findByNameIgnoreCase("Bench Press");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", exercise.getFirst().getId())
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        Assertions.assertThatThrownBy(() -> exerciseService.findById(exercise.getFirst().getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("DELETE v1/exercises returns 401 unauthorized when not authenticated")
    void delete_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("exercise/delete-response-exercise-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .pathParam("id", 1L)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .log().all()
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("DELETE v1/exercises returns 403 forbidden when not authorized")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ReturnsForbidden_WhenNotAuthorized() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("exercise/delete-response-exercise-403.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", 1L)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .log().all()
                .body(Matchers.equalTo(response));
    }

    @Test
    @DisplayName("DELETE v1/exercises returns 404 not found when exercise not found")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ReturnsNotFound_WhenExerciseNotFound() {
        var token = loginAsAdmin();

        var response = fileUtils.readResourceFile("exercise/delete-response-exercise-404.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", 9999L)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .log().all()
                .body(Matchers.equalTo(response));
    }
}