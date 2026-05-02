package com.luizgabriel.gymflow.dto.response;

public record WorkoutExerciseResponse(Long id, String name, String muscleGroup, Integer sets, Integer reps) {
}
