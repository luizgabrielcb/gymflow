package com.luizgabriel.gymflow.commons;

import com.luizgabriel.gymflow.domain.Exercise;
import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.domain.Workout;
import com.luizgabriel.gymflow.dto.request.WorkoutExerciseRequest;
import com.luizgabriel.gymflow.dto.request.WorkoutPostRequest;
import com.luizgabriel.gymflow.dto.request.WorkoutPutRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;

@Component
public class WorkoutUtils {

    public Workout newWorkout() {
        var user = User.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .password("test")
                .build();

        return Workout.builder()
                .id(1L)
                .name("Lower Body")
                .muscleGroups("Quadriceps")
                .user(user)
                .workoutExercises(new ArrayList<>())
                .build();
    }

    public Exercise newExercise() {
        return Exercise.builder()
                .id(1L)
                .name("Squat")
                .muscleGroup("Quadriceps")
                .build();
    }

    public WorkoutExerciseRequest newWorkoutExerciseRequest() {
        var exercise = newExercise();

        return WorkoutExerciseRequest.builder()
                .exerciseId(exercise.getId())
                .sets(3)
                .reps(10)
                .build();
    }

    public WorkoutPostRequest newWorkoutPostRequest() {
        var workout = newWorkout();
        var workoutExerciseRequest = newWorkoutExerciseRequest();

        var workoutExerciseRequestList = Collections.singletonList(workoutExerciseRequest);

        return WorkoutPostRequest.builder()
                .name(workout.getName())
                .muscleGroups(workout.getMuscleGroups())
                .exercises(workoutExerciseRequestList)
                .build();
    }

    public WorkoutPutRequest newWorkoutPutRequest() {
        var workout = newWorkout();
        var workoutExerciseRequest = newWorkoutExerciseRequest();

        var workoutExerciseRequestList = Collections.singletonList(workoutExerciseRequest);

        return WorkoutPutRequest.builder()
                .id(workout.getId())
                .name(workout.getName())
                .muscleGroups(workout.getMuscleGroups())
                .exercises(workoutExerciseRequestList)
                .build();
    }
}
