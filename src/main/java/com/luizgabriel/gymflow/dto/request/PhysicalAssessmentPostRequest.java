package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PhysicalAssessmentPostRequest(@NotNull(message = "The field 'weight' cannot be null") BigDecimal weight,
                                            @NotNull(message = "The field 'height' cannot be null") BigDecimal height,
                                            @NotNull(message = "The field 'fatPercentage' cannot be null") BigDecimal fatPercentage,
                                            @NotNull(message = "The field 'userId' cannot be null") Long userId) {
}
