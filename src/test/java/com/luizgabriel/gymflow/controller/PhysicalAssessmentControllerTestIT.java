package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.commons.FileUtils;
import com.luizgabriel.gymflow.config.AuthenticatedIntegrationConfig;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.PhysicalAssessmentRepository;
import com.luizgabriel.gymflow.repository.UserRepository;
import com.luizgabriel.gymflow.service.PhysicalAssessmentService;
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

import java.math.BigDecimal;

@Sql(value = "/sql/user/delete-users.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class PhysicalAssessmentControllerTestIT extends AuthenticatedIntegrationConfig {

    private static final String URL = "/physical-assessments";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhysicalAssessmentRepository assessmentRepository;

    @Autowired
    private PhysicalAssessmentService assessmentService;

    @Autowired
    private FileUtils fileUtils;

    @Test
    @DisplayName("POST v1/physical-assessments returns 201 created assessment id when successful")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void save_ReturnsCreatedAssessmentId_WhenSuccessful() {
        var token = loginAsAdmin();

        var request = fileUtils.readResourceFile("assessment/post-request-assessment.json");

        var user = userRepository.findByEmailIgnoreCase("admin.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        request = request.replace("{id}", user.getId().toString());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", Matchers.notNullValue())
                .log().all();
    }

    @Test
    @DisplayName("POST v1/physical-assessments returns 400 bad request when fields are null")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void save_ReturnsBadRequest_WhenFieldsAreNull() {
        var token = loginAsAdmin();

        var request = fileUtils.readResourceFile("assessment/post-request-assessment-null-fields.json");
        var response = fileUtils.readResourceFile("assessment/post-response-assessment-null-fields-400.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/physical-assessments returns 401 unauthorized when not authenticated")
    void save_ReturnsUnauthorized_WhenNotAuthenticated() {
        var request = fileUtils.readResourceFile("assessment/post-request-assessment.json");
        var response = fileUtils.readResourceFile("assessment/post-response-assessment-401.json");

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
    @DisplayName("POST v1/physical-assessments returns 403 forbidden when not authenticated")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void save_ReturnsForbidden_WhenNotAuthorized() {
        var token = loginAsUser();

        var request = fileUtils.readResourceFile("assessment/post-request-assessment-with-id-9999.json");
        var response = fileUtils.readResourceFile("assessment/post-response-assessment-403.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("POST v1/physical-assessments returns 404 not found when user not found")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void save_ReturnsNotFound_WhenUserNotFound() {
        var token = loginAsAdmin();

        var request = fileUtils.readResourceFile("assessment/post-request-assessment-user-not-found.json");
        var response = fileUtils.readResourceFile("assessment/post-response-assessment-404.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/physical-assessments returns a page with all assessments by authenticated user when successful status code 200")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByAuthenticatedUser_ReturnsPageWithAllAssessments_WhenSuccessful() {
        var token = loginAsAdmin();

        createAssessmentForUser(token, "admin.test@gmail.com");

        var response = fileUtils.readResourceFile("assessment/get-response-assessment-200.json");

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
    @DisplayName("GET v1/physical-assessments returns an empty page when assessments not found status code 200")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findByAuthenticatedUser_ReturnsEmptyPage_WhenAssessmentsNotFound() {
        var token = loginAsAdmin();

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
                .node("content")
                .isEqualTo("[]");
    }

    @Test
    @DisplayName("GET v1/physical-assessments returns 401 unauthorized when not authenticated")
    void findByAuthenticatedUser_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("assessment/get-response-assessment-401.json");

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
    @DisplayName("GET v1/physical-assessments/{id} returns an assessment by id when successful status code 200")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findById_ReturnsAssessmentById_WhenSuccessful() {
        var token = loginAsAdmin();

        var assessmentId = createAssessmentForUser(token, "admin.test@gmail.com");

        var response = fileUtils.readResourceFile("assessment/get-response-assessment-by-id-200.json");

        var body = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", assessmentId)
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
    @DisplayName("GET v1/physical-assessments/{id} returns 401 unauthorized when not authenticated")
    void findById_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("assessment/get-response-assessment-401.json");

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
    @DisplayName("GET v1/physical-assessments/{id} returns 403 forbidden when not authorized")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findById_ReturnsForbidden_WhenNotAuthorized() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("assessment/get-response-assessment-403.json");

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
    @DisplayName("GET v1/physical-assessments/{id} returns 404 not found when assessment not found")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findById_ReturnsNotFound_WhenAssessmentNotFound() {
        var token = loginAsAdmin();

        var response = fileUtils.readResourceFile("assessment/get-response-assessment-404.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", 9999L)
                .get(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/physical-assessments/user/{id} returns a page with all assessments by user id when successful status code 200")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findUserAssessmentByUserId_ReturnsPageWithAllAssessments_WhenSuccessful() {
        var token = loginAsAdmin();

        createAssessmentForUser(token, "admin.test@gmail.com");

        var user = userRepository.findByEmailIgnoreCase("admin.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var response = fileUtils.readResourceFile("assessment/get-response-assessment-200.json");

        var body = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", user.getId())
                .get(URL + "/user/{id}")
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
    @DisplayName("GET v1/physical-assessments/user/{id} returns an empty page when user does not have assessments status code 200")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findUserAssessmentByUserId_ReturnsEmptyPage_WhenUserDoesNotHaveAssessments() {
        var token = loginAsAdmin();

        var user = userRepository.findByEmailIgnoreCase("admin.test@gmail.com")
                .orElseThrow(() -> new NotFoundException("User not found"));

        var body = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", user.getId())
                .get(URL + "/user/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log().all()
                .extract().body().asString();

        JsonAssertions.assertThatJson(body)
                .node("content")
                .isEqualTo("[]");
    }

    @Test
    @DisplayName("GET v1/physical-assessments/user/{id} returns 401 unauthorized when not authenticated")
    void findUserAssessmentByUserId_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("assessment/get-response-assessment-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .pathParam("id", 9999L)
                .get(URL + "/user/{id}")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/physical-assessments/user/{id} returns 403 forbidden when not authorized")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findUserAssessmentByUserId_ReturnsForbidden_WhenNotAuthorized() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("assessment/get-response-assessment-403.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", 9999L)
                .get(URL + "/user/{id}")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("GET v1/physical-assessments/user/{id} returns 404 not found when user assessment not found")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void findUserAssessmentByUserId_ReturnsNotFound_WhenUserAssessmentNotFound() {
        var token = loginAsAdmin();

        var response = fileUtils.readResourceFile("assessment/get-response-user-assessment-404.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", 9999L)
                .get(URL + "/user/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("PUT v1/physical-assessments returns 204 no content when successful")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ReturnsNoContent_WhenSuccessful() {
        var token = loginAsAdmin();

        var assessmentId = createAssessmentForUser(token, "admin.test@gmail.com");

        var putRequest = fileUtils.readResourceFile("assessment/put-request-assessment.json");

        putRequest = putRequest.replace("{id}", assessmentId.toString());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(putRequest)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        var updatedAssessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new NotFoundException("Assessment not found"));

        Assertions.assertThat(updatedAssessment.getWeight()).isEqualTo(BigDecimal.valueOf(78.55));
    }

    @Test
    @DisplayName("PUT v1/physical-assessments returns 401 unauthorized when not authenticated")
    void update_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("assessment/put-response-assessment-401.json");

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
    @DisplayName("PUT v1/physical-assessments returns 403 forbidden when not authorized")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ReturnsForbidden_WhenNotAuthorized() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("assessment/put-response-assessment-403.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .put(URL)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("PUT v1/physical-assessments returns 404 not found when assessment not found")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void update_ReturnsNotFound_WhenAssessmentNotFound() {
        var token = loginAsAdmin();

        var request = fileUtils.readResourceFile("assessment/put-request-assessment-with-id-9999.json");
        var response = fileUtils.readResourceFile("assessment/put-response-assessment-404.json");

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
    @DisplayName("DELETE v1/physical-assessments/{id} returns 204 no content when successful")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ReturnsNoContent_WhenSuccessful() {
        var token = loginAsAdmin();

        var assessmentId = createAssessmentForUser(token, "admin.test@gmail.com");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", assessmentId)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .log().all();

        Assertions.assertThatThrownBy(() -> assessmentService.findById(assessmentId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("DELETE v1/physical-assessments/{id} returns 401 unauthorized when not authenticated")
    void delete_ReturnsUnauthorized_WhenNotAuthenticated() {
        var response = fileUtils.readResourceFile("assessment/delete-response-assessment-401.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .pathParam("id", 9999)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("DELETE v1/physical-assessments/{id} returns 403 forbidden when not authorized")
    @Sql(value = "/sql/user/insert-regular-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ReturnsForbidden_WhenNotAuthorized() {
        var token = loginAsUser();

        var response = fileUtils.readResourceFile("assessment/delete-response-assessment-403.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", 9999)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    @Test
    @DisplayName("DELETE v1/physical-assessments/{id} returns 404 not found when assessment not found")
    @Sql(value = "/sql/user/insert-admin-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void delete_ReturnsNotFound_WhenAssessmentNotFound() {
        var token = loginAsAdmin();

        var response = fileUtils.readResourceFile("assessment/delete-response-assessment-404.json");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .pathParam("id", 9999)
                .delete(URL + "/{id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body(Matchers.equalTo(response))
                .log().all();
    }

    private Long createAssessmentForUser(String token, String userEmail) {
        var request = fileUtils.readResourceFile("assessment/post-request-assessment.json");

        var user = userRepository.findByEmailIgnoreCase(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        request = request.replace("{id}", user.getId().toString());

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .jsonPath()
                .getLong("id");
    }
}