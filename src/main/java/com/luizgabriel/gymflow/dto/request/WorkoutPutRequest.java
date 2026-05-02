package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record WorkoutPutRequest(@NotNull(message = "The field 'id' cannot be null") Long id,
                                @NotBlank(message = "The field 'name' is required") String name,
                                @NotBlank(message = "The field 'muscleGroups' is required") String muscleGroups,
                                @NotEmpty(message = "The field 'exercises' cannot be empty") List<WorkoutExerciseRequest> exercises) {
}
