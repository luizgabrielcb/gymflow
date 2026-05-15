package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SessionSetPutRequest(
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