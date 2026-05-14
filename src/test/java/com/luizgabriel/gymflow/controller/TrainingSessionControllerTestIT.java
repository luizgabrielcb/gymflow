package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.commons.FileUtils;
import com.luizgabriel.gymflow.config.AuthenticatedIntegrationConfig;
import com.luizgabriel.gymflow.domain.Status;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.ExerciseRepository;
import com.luizgabriel.gymflow.repository.TrainingSessionRepository;
import com.luizgabriel.gymflow.repository.UserRepository;
import com.luizgabriel.gymflow.repository.WorkoutRepository;
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
class TrainingSessionControllerTestIT extends AuthenticatedIntegrationConfig {

    private static final String URL = "/training-sessions";

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private TrainingSessionRepository trainingSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("POST v1/training-sessions returns 201 created when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void startSession_ReturnsCreated_WhenSuccessful() {
        var token = loginAsUserToken();

        var workout = workoutRepository.findByNameIgnoreCase("Upper Body")
                .orElseThrow(() -> new NotFoundException("Workout not found"));

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .queryParam("workoutId", workout.getId())
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.notNullValue())
                .log().all();
    }

    @Test
    @DisplayName("POST v1/training-sessions returns 400 bad request when active session already exists")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-in-progress-training-session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void startSession_ReturnsBadRequest_WhenActiveSessionAlreadyExists() {
        var token = loginAsUserToken();

        var response = fileUtils.readResourceFile("training-session/post-response-training-session-active-session-400.json");

        var workout = workoutRepository.findByNameIgnoreCase("Upper Body")
                .orElseThrow(() -> new NotFoundException("Workout not found"));

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .queryParam("workoutId", workout.getId())
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/training-sessions returns 401 unauthorized when not authenticated")
    void startSession_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("training-session/post-response-training-session-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .queryParam("workoutId", 9999L)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/training-sessions returns 403 forbidden when workout does not belong to authenticated user")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/insert-another-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout-for-another-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void startSession_ReturnsForbidden_WhenWorkoutNotOwner() {
        var token = loginAsUserToken();

        var response = fileUtils.readResourceFile("training-session/post-response-training-session-403.json");

        var workout = workoutRepository.findByNameIgnoreCase("Upper Body")
                .orElseThrow(() -> new NotFoundException("Workout not found"));

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .queryParam("workoutId", workout.getId())
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/training-sessions returns 404 not found when workout not found")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void startSession_ReturnsNotFound_WhenWorkoutNotFound() {
        var token = loginAsUserToken();
        var response = fileUtils.readResourceFile("training-session/post-response-training-session-workout-not-found-404.json");

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .queryParam("workoutId", 9999L)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/training-sessions/{id}/sets returns 201 created when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/exercise/insert-exercise-bench-press.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-workout-exercise-bench-press.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-in-progress-training-session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void addSet_ReturnsCreated_WhenSuccessful() {
        var token = loginAsUserToken();

        var request = fileUtils.readResourceFile("training-session/post-request-session-set.json");

        var user = userRepository.findByEmailIgnoreCase("user.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var trainingSession = trainingSessionRepository.findByUserIdAndStatus(user.getId(), Status.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        var exercise = exerciseRepository.findByNameIgnoreCase("Bench Press");
        request = request.replace("{exerciseId}", exercise.getFirst().getId().toString());

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", trainingSession.getId())
                .body(request)
                .when()
                .post(URL + "/{id}/sets")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.notNullValue())
                .log().all();
    }

    @Test
    @DisplayName("POST v1/training-sessions/{id}/sets returns 400 bad request when fields are invalid")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-in-progress-training-session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void addSet_ReturnsBadRequest_WhenFieldsAreInvalid() {
        var token = loginAsUserToken();

        var request = fileUtils.readResourceFile("training-session/post-request-session-set-invalid-fields.json");
        var response = fileUtils.readResourceFile("training-session/post-response-session-set-invalid-fields-400.json");

        var user = userRepository.findByEmailIgnoreCase("user.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var trainingSession = trainingSessionRepository.findByUserIdAndStatus(user.getId(), Status.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", trainingSession.getId())
                .body(request)
                .when()
                .post(URL + "/{id}/sets")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/training-sessions/{id}/sets returns 400 bad request when session is not in progress")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/exercise/insert-exercise-bench-press.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-completed-training-session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void addSet_ReturnsBadRequest_WhenSessionIsNotInProgress() {
        var token = loginAsUserToken();

        var request = fileUtils.readResourceFile("training-session/post-request-session-set.json");
        var response = fileUtils.readResourceFile("training-session/post-response-session-set-not-in-progress-400.json");

        var user = userRepository.findByEmailIgnoreCase("user.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var trainingSession = trainingSessionRepository.findByUserIdAndStatus(user.getId(), Status.COMPLETED)
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        var exercise = exerciseRepository.findByNameIgnoreCase("Bench Press");
        request = request.replace("{exerciseId}", exercise.getFirst().getId().toString());

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", trainingSession.getId())
                .body(request)
                .when()
                .post(URL + "/{id}/sets")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/training-sessions/{id}/sets returns 400 bad request when duplicate set")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/exercise/insert-exercise-bench-press.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-workout-exercise-bench-press.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-in-progress-training-session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-session-set.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void addSet_ReturnsBadRequest_WhenDuplicateSet() {
        var token = loginAsUserToken();

        var request = fileUtils.readResourceFile("training-session/post-request-session-set.json");
        var response = fileUtils.readResourceFile("training-session/post-response-session-set-duplicate-400.json");

        var user = userRepository.findByEmailIgnoreCase("user.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var trainingSession = trainingSessionRepository.findByUserIdAndStatus(user.getId(), Status.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        var exercise = exerciseRepository.findByNameIgnoreCase("Bench Press");
        request = request.replace("{exerciseId}", exercise.getFirst().getId().toString());

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", trainingSession.getId())
                .body(request)
                .when()
                .post(URL + "/{id}/sets")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/training-sessions/{id}/sets returns 400 bad request when exercise is not part of workout")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/exercise/insert-exercise-bench-press.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/exercise/insert-exercise-squat-legs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-in-progress-training-session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void addSet_ReturnsBadRequest_WhenExerciseNotPartOfWorkout() {
        var token = loginAsUserToken();

        var request = fileUtils.readResourceFile("training-session/post-request-session-set.json");

        var user = userRepository.findByEmailIgnoreCase("user.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var trainingSession = trainingSessionRepository.findByUserIdAndStatus(user.getId(), Status.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        var exercise = exerciseRepository.findByNameIgnoreCase("Squat");
        request = request.replace("{exerciseId}", exercise.getFirst().getId().toString());

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", trainingSession.getId())
                .body(request)
                .when()
                .post(URL + "/{id}/sets")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("status", Matchers.equalTo(400))
                .body("message", Matchers.containsString("is not part of this workout"))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/training-sessions/{id}/sets returns 401 unauthorized when not authenticated")
    void addSet_ReturnsUnauthorized_WhenNotAuthenticated() {
        var request = fileUtils.readResourceFile("training-session/post-request-session-set.json");
        var response = fileUtils.readResourceFile("training-session/post-response-session-set-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("id", 9999L)
                .body(request)
                .when()
                .post(URL + "/{id}/sets")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/training-sessions/{id}/sets returns 403 forbidden when training session does not belong to authenticated user")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/insert-another-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/exercise/insert-exercise-bench-press.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout-for-another-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-in-progress-training-session-for-another-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void addSet_ReturnsForbidden_WhenNotOwner() {
        var token = loginAsUserToken();

        var request = fileUtils.readResourceFile("training-session/post-request-session-set.json");
        var response = fileUtils.readResourceFile("training-session/post-response-session-set-403.json");

        var anotherUser = userRepository.findByEmailIgnoreCase("test.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var trainingSession = trainingSessionRepository.findByUserIdAndStatus(anotherUser.getId(), Status.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        var exercise = exerciseRepository.findByNameIgnoreCase("Bench Press");
        request = request.replace("{exerciseId}", exercise.getFirst().getId().toString());

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", trainingSession.getId())
                .body(request)
                .when()
                .post(URL + "/{id}/sets")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/training-sessions/{id}/sets returns 404 not found when session not found")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/exercise/insert-exercise-bench-press.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void addSet_ReturnsNotFound_WhenSessionNotFound() {
        var token = loginAsUserToken();

        var request = fileUtils.readResourceFile("training-session/post-request-session-set.json");
        var response = fileUtils.readResourceFile("training-session/post-response-session-set-not-found-404.json");

        var exercise = exerciseRepository.findByNameIgnoreCase("Bench Press");
        request = request.replace("{exerciseId}", exercise.getFirst().getId().toString());

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", 9999L)
                .body(request)
                .when()
                .post(URL + "/{id}/sets")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("PATCH v1/training-sessions/{id}/finish returns 204 no content when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-in-progress-training-session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void finishTrainingSession_ReturnsNoContent_WhenSuccessful() {
        var token = loginAsUserToken();

        var user = userRepository.findByEmailIgnoreCase("user.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var trainingSession = trainingSessionRepository.findByUserIdAndStatus(user.getId(), Status.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", trainingSession.getId())
                .when()
                .patch(URL + "/{id}/finish")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        var finishedSession = trainingSessionRepository.findById(trainingSession.getId())
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        Assertions.assertThat(finishedSession.getStatus()).isEqualTo(Status.COMPLETED);
    }

    @Test
    @DisplayName("PATCH v1/training-sessions/{id}/finish returns 400 bad request when session is not in progress")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-completed-training-session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void finishTrainingSession_ReturnsBadRequest_WhenSessionIsNotInProgress() {
        var token = loginAsUserToken();

        var response = fileUtils.readResourceFile("training-session/patch-response-finish-not-in-progress-400.json");

        var user = userRepository.findByEmailIgnoreCase("user.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var trainingSession = trainingSessionRepository.findByUserIdAndStatus(user.getId(), Status.COMPLETED)
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", trainingSession.getId())
                .when()
                .patch(URL + "/{id}/finish")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("PATCH v1/training-sessions/{id}/finish returns 401 unauthorized when not authenticated")
    void finishTrainingSession_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("training-session/patch-response-finish-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("id", 9999L)
                .when()
                .patch(URL + "/{id}/finish")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("PATCH v1/training-sessions/{id}/finish returns 403 forbidden when training session does not belong to authenticated user")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/insert-another-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout-for-another-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-in-progress-training-session-for-another-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void finishTrainingSession_ReturnsForbidden_WhenNotOwner() {
        var token = loginAsUserToken();

        var response = fileUtils.readResourceFile("training-session/patch-response-finish-403.json");

        var anotherUser = userRepository.findByEmailIgnoreCase("test.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var trainingSession = trainingSessionRepository.findByUserIdAndStatus(anotherUser.getId(), Status.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", trainingSession.getId())
                .when()
                .patch(URL + "/{id}/finish")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("PATCH v1/training-sessions/{id}/finish returns 404 not found when session not found")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void finishTrainingSession_ReturnsNotFound_WhenSessionNotFound() {
        var token = loginAsUserToken();

        var response = fileUtils.readResourceFile("training-session/patch-response-finish-not-found-404.json");

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", 9999L)
                .when()
                .patch(URL + "/{id}/finish")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("PATCH v1/training-sessions/{id}/cancel returns 204 no content when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-in-progress-training-session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void cancelTrainingSession_ReturnsNoContent_WhenSuccessful() {
        var token = loginAsUserToken();

        var user = userRepository.findByEmailIgnoreCase("user.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var trainingSession = trainingSessionRepository.findByUserIdAndStatus(user.getId(), Status.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", trainingSession.getId())
                .when()
                .patch(URL + "/{id}/cancel")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        var cancelledSession = trainingSessionRepository.findById(trainingSession.getId())
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        Assertions.assertThat(cancelledSession.getStatus()).isEqualTo(Status.CANCELLED);
    }

    @Test
    @DisplayName("PATCH v1/training-sessions/{id}/cancel returns 400 bad request when session is not in progress")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-completed-training-session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void cancelTrainingSession_ReturnsBadRequest_WhenSessionIsNotInProgress() {
        var token = loginAsUserToken();

        var response = fileUtils.readResourceFile("training-session/patch-response-cancel-not-in-progress-400.json");

        var user = userRepository.findByEmailIgnoreCase("user.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var trainingSession = trainingSessionRepository.findByUserIdAndStatus(user.getId(), Status.COMPLETED)
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", trainingSession.getId())
                .when()
                .patch(URL + "/{id}/cancel")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("PATCH v1/training-sessions/{id}/cancel returns 401 unauthorized when not authenticated")
    void cancelTrainingSession_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("training-session/patch-response-cancel-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("id", 9999L)
                .when()
                .patch(URL + "/{id}/cancel")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("PATCH v1/training-sessions/{id}/cancel returns 403 forbidden when not owner")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/insert-another-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout-for-another-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-in-progress-training-session-for-another-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void cancelTrainingSession_ReturnsForbidden_WhenNotOwner() {
        var token = loginAsUserToken();

        var response = fileUtils.readResourceFile("training-session/patch-response-cancel-403.json");

        var user = userRepository.findByEmailIgnoreCase("test.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var trainingSession = trainingSessionRepository.findByUserIdAndStatus(user.getId(), Status.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", trainingSession.getId())
                .when()
                .patch(URL + "/{id}/cancel")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("PATCH v1/training-sessions/{id}/cancel returns 404 not found when session not found")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void cancelTrainingSession_ReturnsNotFound_WhenSessionNotFound() {
        var token = loginAsUserToken();

        var response = fileUtils.readResourceFile("training-session/patch-response-cancel-not-found-404.json");

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", 9999L)
                .when()
                .patch(URL + "/{id}/cancel")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/training-sessions returns a page with all training sessions when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-in-progress-training-session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAll_ReturnsPageWithAllTrainingSessions_WhenSuccessful() {
        var token = loginAsUserToken();

        var response = fileUtils.readResourceFile("training-session/get-response-training-session-list-200.json");

        var body = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().body().asString();

        JsonAssertions.assertThatJson(body)
                .whenIgnoringPaths("content[*].id", "content[0].startedAt")
                .node("content")
                .isEqualTo(response);
    }

    @Test
    @DisplayName("GET v1/training-sessions returns empty page when no sessions found")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAll_ReturnsEmptyPage_WhenNoSessionsFound() {
        var token = loginAsUserToken();

        var body = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().body().asString();

        JsonAssertions.assertThatJson(body)
                .node("content")
                .isEqualTo("[]");
    }

    @Test
    @DisplayName("GET v1/training-sessions returns 401 unauthorized when not authenticated")
    void findAll_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("training-session/get-response-training-session-401.json");

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
    @DisplayName("GET v1/training-sessions/current returns current session when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-in-progress-training-session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findCurrentTrainingSession_ReturnsCurrentSession_WhenSuccessful() {
        var token = loginAsUserToken();

        var response = fileUtils.readResourceFile("training-session/get-response-current-training-session-200.json");

        var body = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get(URL + "/current")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().body().asString();

        JsonAssertions.assertThatJson(body)
                .whenIgnoringPaths("id", "startedAt", "finishedAt", "sets[*].id")
                .isEqualTo(response);
    }

    @Test
    @DisplayName("GET v1/training-sessions/current returns 401 unauthorized when not authenticated")
    void findCurrentTrainingSession_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("training-session/get-response-current-training-session-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get(URL + "/current")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/training-sessions/current returns 404 not found when no active session")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findCurrentTrainingSession_ReturnsNotFound_WhenNoActiveSession() {
        var token = loginAsUserToken();

        var response = fileUtils.readResourceFile("training-session/get-response-current-training-session-not-found-404.json");

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get(URL + "/current")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/training-sessions/{id} returns training session when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-in-progress-training-session.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findById_ReturnsTrainingSession_WhenSuccessful() {
        var token = loginAsUserToken();

        var response = fileUtils.readResourceFile("training-session/get-response-training-session-by-id-200.json");

        var user = userRepository.findByEmailIgnoreCase("user.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var trainingSession = trainingSessionRepository.findByUserIdAndStatus(user.getId(), Status.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        var body = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", trainingSession.getId())
                .when()
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().body().asString();

        JsonAssertions.assertThatJson(body)
                .whenIgnoringPaths("id", "startedAt", "finishedAt", "sets[*].id")
                .isEqualTo(response);
    }

    @Test
    @DisplayName("GET v1/training-sessions/{id} returns 401 unauthorized when not authenticated")
    void findById_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("training-session/get-response-training-session-by-id-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("id", 9999L)
                .when()
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/training-sessions/{id} returns 403 forbidden when training session does not belong to authenticated user")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/insert-another-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout-for-another-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/training-session/insert-one-in-progress-training-session-for-another-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findById_ReturnsForbidden_WhenNotOwner() {
        var token = loginAsUserToken();

        var response = fileUtils.readResourceFile("training-session/get-response-training-session-by-id-403.json");

        var user = userRepository.findByEmailIgnoreCase("test.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var trainingSession = trainingSessionRepository.findByUserIdAndStatus(user.getId(), Status.IN_PROGRESS)
                .orElseThrow(() -> new NotFoundException("Training session not found"));

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", trainingSession.getId())
                .when()
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/training-sessions/{id} returns 404 not found when session not found")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findById_ReturnsNotFound_WhenSessionNotFound() {
        var token = loginAsUserToken();

        var response = fileUtils.readResourceFile("training-session/get-response-training-session-by-id-not-found-404.json");

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .pathParam("id", 9999L)
                .when()
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }
}