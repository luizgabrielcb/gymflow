package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ExercisePostRequest(@NotBlank(message = "The field 'name' is required") String name,
                                  @NotBlank(message = "The field 'muscleGroup' is required") String muscleGroup) {
}
