package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record WorkoutPutRequest(@NotNull(message = "The field 'id' is required") Long id,
                                @NotBlank(message = "The field 'name' is required") String name,
                                @NotBlank(message = "The field 'muscleGroups' is required") String muscleGroups,
                                @NotEmpty(message = "The field 'exercises' cannot be empty") List<WorkoutExerciseRequest> exercises) {
}
