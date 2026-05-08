package com.luizgabriel.gymflow.commons;

import com.luizgabriel.gymflow.domain.PhysicalAssessment;
import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.PhysicalAssessmentPostRequest;
import com.luizgabriel.gymflow.dto.request.PhysicalAssessmentPutRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class PhysicalAssessmentUtils {

    public User newUser() {
        return User.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .password("test")
                .build();
    }

    public PhysicalAssessment newPhysicalAssessment() {
        var user = newUser();
        var request = newPhysicalAssessmentPostRequest();

        return PhysicalAssessment.builder()
                .id(1L)
                .weight(request.weight())
                .height(request.height())
                .fatPercentage(request.fatPercentage())
                .user(user)
                .createdAt(Instant.now())
                .build();
    }

    public PhysicalAssessmentPostRequest newPhysicalAssessmentPostRequest() {
        var user = newUser();

        return PhysicalAssessmentPostRequest.builder()
                .userId(user.getId())
                .weight(BigDecimal.valueOf(80.5))
                .height(BigDecimal.valueOf(1.79))
                .fatPercentage(BigDecimal.valueOf(12.3))
                .build();
    }

    public PhysicalAssessmentPutRequest newPhysicalAssessmentPutRequest() {
        var assessment = newPhysicalAssessment();

        return PhysicalAssessmentPutRequest.builder()
                .id(assessment.getId())
                .weight(assessment.getWeight())
                .height(assessment.getHeight())
                .fatPercentage(assessment.getFatPercentage())
                .build();
    }
}
