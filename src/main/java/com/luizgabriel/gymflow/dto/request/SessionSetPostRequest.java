package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record SessionSetPostRequest(
        @NotNull(message = "The field 'trainingSessionId' is required")
        Long trainingSessionId,

        @NotNull(message = "The field 'exerciseId' is required")
        Long exerciseId,

        @Positive(message = "The field 'setNumber' must be greater than 0")
        @NotNull(message = "The field 'setNumber' is required")
        Integer setNumber,

        @Positive(message = "The field 'repsNumber' must be greater than 0")
        @NotNull(message = "The field 'repsNumber' is required")
        Integer repsNumber,

        @PositiveOrZero(message = "The field 'weightKg' must be greater than or equal to 0")
        @NotNull(message = "The field 'weightKg' is required")
        BigDecimal weightKg,

        @PositiveOrZero(message = "The field 'restSeconds' must be greater than or equal to 0")
        @NotNull(message = "The field 'restSeconds' is required")
        Integer restSeconds) {
}
