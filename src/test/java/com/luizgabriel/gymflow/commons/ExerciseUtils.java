package com.luizgabriel.gymflow.commons;

import com.luizgabriel.gymflow.domain.Exercise;
import com.luizgabriel.gymflow.dto.request.ExercisePutRequest;
import org.springframework.stereotype.Component;

@Component
public class ExerciseUtils {

    public Exercise newExercise() {
        return Exercise.builder()
                .id(1L)
                .name("Squat")
                .muscleGroup("Quadriceps")
                .build();
    }

    public ExercisePutRequest newExercisePutRequest() {
        var exercise = newExercise();

        return ExercisePutRequest.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .muscleGroup(exercise.getMuscleGroup())
                .build();
    }
}
