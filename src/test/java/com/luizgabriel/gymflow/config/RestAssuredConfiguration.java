package com.luizgabriel.gymflow.config;

import com.luizgabriel.gymflow.dto.request.LoginRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import static com.luizgabriel.gymflow.commons.Constants.*;

@TestConfiguration
@Lazy
public class RestAssuredConfiguration {

    @LocalServerPort
    private int port;

    @Bean(name = "requestSpecificationRegularUser")
    public RequestSpecification requestSpecificationRegularUser() {
        return RestAssured.given()
                .baseUri(BASE_URI + port)
                .header("Authorization", "Bearer " + getToken(REGULAR_USERNAME));
    }

    @Bean(name = "requestSpecificationAdminUser")
    public RequestSpecification requestSpecificationAdminUser() {
        return RestAssured.given()
                .baseUri(BASE_URI + port)
                .header("Authorization", "Bearer " + getToken(ADMIN_USERNAME));
    }

    private String getToken(String username) {
        return RestAssured.given()
                .baseUri(BASE_URI + port)
                .contentType(ContentType.JSON)
                .body(new LoginRequest(username, PASSWORD))
                .when()
                .post("/v1/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }
}
