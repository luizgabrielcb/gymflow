package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record WorkoutPostRequest(@NotBlank(message = "The field 'name' is required") String name,
                                 @NotBlank(message = "The field 'muscleGroups' is required") String muscleGroups,
                                 @NotEmpty(message = "The field 'exercises' cannot be empty") List<WorkoutExerciseRequest> exercises) {
}
