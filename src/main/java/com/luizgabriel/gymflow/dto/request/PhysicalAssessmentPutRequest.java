package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PhysicalAssessmentPutRequest(@NotNull(message = "The field 'id' is required") Long id,
                                           @NotNull(message = "The field 'weight' is required") BigDecimal weight,
                                           @NotNull(message = "The field 'height' is required") BigDecimal height,
                                           @NotNull(message = "The field 'fatPercentage' is required") BigDecimal fatPercentage) {
}
