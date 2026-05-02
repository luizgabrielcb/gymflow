package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PhysicalAssessmentPutRequest(@NotNull(message = "The field 'id' cannot be null") Long id,
                                           @NotNull(message = "The field 'weight' cannot be null") BigDecimal weight,
                                           @NotNull(message = "The field 'height' cannot be null") BigDecimal height,
                                           @NotNull(message = "The field 'fatPercentage' cannot be null") BigDecimal fatPercentage) {
}
