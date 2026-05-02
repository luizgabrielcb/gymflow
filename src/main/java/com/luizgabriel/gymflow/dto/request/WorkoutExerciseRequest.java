package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotNull;

public record WorkoutExerciseRequest(@NotNull(message = "The field 'exerciseId' cannot be null") Long exerciseId,
                                     @NotNull(message = "The field 'sets' cannot be null") Integer sets,
                                     @NotNull(message = "The field 'reps' cannot be null") Integer reps) {
}
