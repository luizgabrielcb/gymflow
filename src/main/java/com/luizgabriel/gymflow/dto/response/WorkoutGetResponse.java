package com.luizgabriel.gymflow.dto.response;

import java.util.List;

public record WorkoutGetResponse(Long id, String name, String muscleGroups, List<WorkoutExerciseResponse> exercises) {
}
