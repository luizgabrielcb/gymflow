package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.commons.FileUtils;
import com.luizgabriel.gymflow.config.IntegrationTestConfiguration;
import com.luizgabriel.gymflow.repository.TrainingSessionRepository;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = RestAssuredConfig.class)
class TrainingSessionControllerTest extends IntegrationTestConfiguration {

    private static final String URL = "/v1/training-sessions";

    @Autowired
    @Qualifier(value = "requestSpecificationRegularUser")
    private RequestSpecification requestSpecificationRegularUser;

    @Autowired
    @Qualifier(value = "requestSpecificationAdminUser")
    private RequestSpecification requestSpecificationAdminUser;

    @BeforeEach
    public void setup() {
        RestAssured.requestSpecification = requestSpecificationRegularUser;
    }

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private TrainingSessionRepository repository;


}