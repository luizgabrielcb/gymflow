package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PhysicalAssessmentPostRequest(@NotNull(message = "The field 'userId' is required") Long userId,
                                            @NotNull(message = "The field 'weight' is required") BigDecimal weight,
                                            @NotNull(message = "The field 'height' is required") BigDecimal height,
                                            @NotNull(message = "The field 'fatPercentage' is required") BigDecimal fatPercentage) {
}
