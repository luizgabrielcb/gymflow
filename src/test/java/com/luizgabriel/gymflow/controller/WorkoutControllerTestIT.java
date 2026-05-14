package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.commons.FileUtils;
import com.luizgabriel.gymflow.config.AuthenticatedIntegrationConfig;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.ExerciseRepository;
import com.luizgabriel.gymflow.repository.UserRepository;
import com.luizgabriel.gymflow.repository.WorkoutRepository;
import com.luizgabriel.gymflow.service.WorkoutService;
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
class WorkoutControllerTestIT extends AuthenticatedIntegrationConfig {
    private static final String URL = "/workouts";

    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkoutService workoutService;

    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("POST v1/workouts returns 201 created workout when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/exercise/insert-exercise-bench-press.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void save_ReturnsCreatedWorkout_WhenSuccessful() {
        var token = loginAsUser();

        var request = fileUtils.readResourceFile("workout/post-request-workout.json");
        var response = fileUtils.readResourceFile("workout/post-response-workout-201.json");

        var exercise = exerciseRepository.findByNameIgnoreCase("Bench Press");

        request = request.replace("{exerciseId}", exercise.getFirst().getId().toString());

        var body = RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log().all()
                .extract().body().asString();

        JsonAssertions.assertThatJson(body)
                .whenIgnoringPaths("id", "exercises[*].id")
                .isEqualTo(response);
    }

    @Test
    @DisplayName("POST v1/workouts returns 400 bad request when fields are blank")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/exercise/insert-exercise-bench-press.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void save_ReturnsBadRequest_WhenFieldsAreBlank() {
        var token = loginAsUser();

        var request = fileUtils.readResourceFile("workout/post-request-workout-blank-fields.json");
        var response = fileUtils.readResourceFile("workout/post-response-workout-blank-fields-400.json");

        var exercise = exerciseRepository.findByNameIgnoreCase("Bench Press");

        request = request.replace("{exerciseId}", exercise.getFirst().getId().toString());

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/workouts returns 400 bad request when workout name already exists")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/exercise/insert-exercise-bench-press.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void save_ReturnsBadRequest_WhenWorkoutNameAlreadyExists() {
        var token = loginAsUser();

        var request = fileUtils.readResourceFile("workout/post-request-workout.json");
        var response = fileUtils.readResourceFile("workout/post-response-workout-name-already-exists-400.json");

        var exercise = exerciseRepository.findByNameIgnoreCase("Bench Press");

        request = request.replace("{exerciseId}", exercise.getFirst().getId().toString());

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/workouts returns 401 unauthorized when not authenticated")
    void save_ReturnsUnauthorized_WhenNotAuthenticated() {
        var request = fileUtils.readResourceFile("workout/post-request-workout.json");
        var response = fileUtils.readResourceFile("workout/post-response-workout-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/workouts returns a list with all workouts from authenticated user when successful status code 200")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAll_ReturnsListWithAllWorkouts_WhenSuccessful() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("workout/get-response-workout-200.json");

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
                .whenIgnoringPaths("[*].id")
                .isEqualTo(response);
    }

    @Test
    @DisplayName("GET v1/workouts returns an empty list when workout not found status code 200")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findAll_ReturnsEmptyList_WhenWorkoutNotFound() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("workout/get-response-workout-empty-list-200.json");

        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .when()
                .get(URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/workouts returns 401 unauthorized when not authenticated")
    void findAll_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("workout/get-response-workout-401.json");

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
    @DisplayName("PUT v1/workouts returns 204 no content when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/exercise/insert-exercise-bench-press.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ReturnsNoContent_WhenSuccessful() {
        var token = loginAsUser();

        var request = fileUtils.readResourceFile("workout/put-request-workout.json");

        var workout = workoutRepository.findByNameIgnoreCase("Upper Body")
                .orElseThrow(() -> new NotFoundException("Workout not found"));

        request = request.replace("{id}", workout.getId().toString());

        var exercise = exerciseRepository.findByNameIgnoreCase("Bench Press");

        request = request.replace("{exerciseId}", exercise.getFirst().getId().toString());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        var updatedWorkout = workoutRepository.findById(workout.getId())
                .orElseThrow(() -> new NotFoundException("Workout not found"));

        Assertions.assertThat(updatedWorkout.getMuscleGroups()).isEqualTo("CHEST and TRICEPS");
    }

    @Test
    @DisplayName("PUT v1/workouts returns 400 bad request when invalid data")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ReturnsBadRequest_WhenInvalidData() {
        var token = loginAsUser();

        var request = fileUtils.readResourceFile("workout/put-request-workout-invalid-data.json");
        var response = fileUtils.readResourceFile("workout/put-response-workout-invalid-data-400.json");

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
    @DisplayName("PUT v1/workouts returns 400 bad request when workout name already exists for user")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-two-workouts.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/exercise/insert-exercise-squat-legs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ReturnsBadRequest_WhenWorkoutNameAlreadyExistsForUser() {
        var token = loginAsUser();

        var request = fileUtils.readResourceFile("workout/put-request-workout-name-already-exists.json");
        var response = fileUtils.readResourceFile("workout/put-response-workout-name-already-exists-400.json");

        var workout = workoutRepository.findByNameIgnoreCase("Upper Body")
                .orElseThrow(() -> new NotFoundException("Workout not found"));

        request = request.replace("{id}", workout.getId().toString());

        var exercise = exerciseRepository.findByNameIgnoreCase("Squat");

        request = request.replace("{exerciseId}", exercise.getFirst().getId().toString());

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
    @DisplayName("PUT v1/workouts returns 401 unauthorized when not authenticated")
    void update_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("workout/put-response-workout-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("PUT v1/workouts returns 404 not found when workout not found")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/exercise/insert-exercise-squat-legs.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ReturnsNotFound_WhenWorkoutNotFound() {
        var token = loginAsUser();

        var request = fileUtils.readResourceFile("workout/put-request-workout-with-id-9999.json");
        var response = fileUtils.readResourceFile("workout/put-response-workout-not-found-404.json");

        var exercise = exerciseRepository.findByNameIgnoreCase("Squat");

        request = request.replace("{exerciseId}", exercise.getFirst().getId().toString());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("DELETE v1/workouts/{id} returns 204 no content when successful")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ReturnsNoContent_WhenSuccessful() {
        var token = loginAsUser();

        var workout = workoutRepository.findByNameIgnoreCase("Upper Body")
                .orElseThrow(() -> new NotFoundException("Workout not found"));

        var user = userRepository.findByEmailIgnoreCase("user.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", workout.getId())
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        Assertions.assertThatThrownBy(() -> workoutService.findById(workout.getId(), user))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("DELETE v1/workouts/{id} returns 401 unauthorized when not authenticated")
    void delete_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("workout/delete-response-workout-401.json");

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
    @DisplayName("DELETE v1/workouts/{id} returns 403 forbidden when workout does not belong to authenticated user")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/user/insert-another-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/workout/insert-one-workout-for-another-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ReturnsForbidden_WhenNotOwner() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("workout/delete-response-workout-403.json");

        var workout = workoutRepository.findByNameIgnoreCase("Upper Body")
                .orElseThrow(() -> new NotFoundException("Workout not found"));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", workout.getId())
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("DELETE v1/workouts/{id} returns 404 not found when workout not found")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ReturnsNotFound_WhenWorkoutNotFound() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("workout/delete-response-workout-not-found-404.json");

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
}