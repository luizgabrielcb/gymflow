package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExercisePutRequest(@NotNull(message = "The field 'id' cannot be null") Long id,
                                 @NotBlank(message = "The field 'name' is required") String name,
                                 @NotBlank(message = "The field 'muscleGroup' is required") String muscleGroup) {
}
