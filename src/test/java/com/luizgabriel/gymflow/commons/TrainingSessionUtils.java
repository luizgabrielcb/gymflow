package com.luizgabriel.gymflow.commons;

import com.luizgabriel.gymflow.domain.*;
import com.luizgabriel.gymflow.dto.request.SessionSetPostRequest;
import com.luizgabriel.gymflow.dto.request.SessionSetPutRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class TrainingSessionUtils {

    public TrainingSession newTrainingSession() {
        var user = User.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .password("test")
                .build();

        var workout = Workout.builder()
                .id(1L)
                .name("Workout Test")
                .muscleGroups("Biceps")
                .user(user)
                .build();

        return TrainingSession.builder().id(1L).user(user).workout(workout).startedAt(Instant.now()).build();
    }

    public TrainingSession newTrainingSessionInProgress() {
        var trainingSession = newTrainingSession();

        trainingSession.setStatus(Status.IN_PROGRESS);

        return trainingSession;
    }

    public Exercise newExercise() {
        return Exercise.builder().id(1L).name("Squat").muscleGroup("Quadriceps").build();
    }

    public SessionSet newSessionSet() {
        var trainingSession = newTrainingSessionInProgress();
        var exercise = newExercise();

        return SessionSet.builder()
                .trainingSession(trainingSession)
                .exercise(exercise)
                .setNumber(4).
                repsNumber(12).
                weightKg(BigDecimal.valueOf(60)).
                restSeconds(90)
                .build();
    }

    public WorkoutExercise newWorkoutExercise() {
        var trainingSession = newTrainingSessionInProgress();
        var exercise = newExercise();

        return WorkoutExercise.builder().id(1L).workout(trainingSession.getWorkout()).exercise(exercise).build();
    }

    public SessionSetPostRequest newSessionSetPostRequest() {
        var exercise = newExercise();
        var sessionSet = newSessionSet();

        return SessionSetPostRequest.builder()
                .exerciseId(exercise.getId())
                .setNumber(sessionSet.getSetNumber())
                .repsNumber(sessionSet.getRepsNumber())
                .weightKg(sessionSet.getWeightKg())
                .restSeconds(sessionSet.getRestSeconds())
                .build();
    }

    public SessionSetPutRequest newSessionSetPutRequest() {
        return SessionSetPutRequest.builder()
                .repsNumber(15)
                .weightKg(BigDecimal.valueOf(70))
                .restSeconds(120)
                .build();
    }
}
