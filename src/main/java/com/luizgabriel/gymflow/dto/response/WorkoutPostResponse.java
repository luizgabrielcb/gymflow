package com.luizgabriel.gymflow.dto.response;

import java.util.List;

public record WorkoutPostResponse(Long id, String name, String muscleGroups, List<WorkoutExerciseResponse> exercises) {
}
