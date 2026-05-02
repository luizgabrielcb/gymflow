package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.domain.Workout;
import com.luizgabriel.gymflow.domain.WorkoutExercise;
import com.luizgabriel.gymflow.dto.request.WorkoutExerciseRequest;
import com.luizgabriel.gymflow.dto.request.WorkoutPostRequest;
import com.luizgabriel.gymflow.dto.request.WorkoutPutRequest;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.ExerciseRepository;
import com.luizgabriel.gymflow.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;

    public Workout save(WorkoutPostRequest request, User user) {

        var workout = Workout.builder()
                .name(request.name())
                .muscleGroups(request.muscleGroups())
                .user(user)
                .workoutExercises(new ArrayList<>())
                .build();

        insertExercisesToWorkout(workout, request.exercises());

        return workoutRepository.save(workout);
    }

    public List<Workout> findAll(User user) {
        return workoutRepository.findAllByUserId(user.getId());
    }

    public void update(WorkoutPutRequest request, User user) {
        var workout = workoutRepository.findByIdAndUserId(request.id(), user.getId()).orElseThrow(() ->
                new NotFoundException("Workout with id " + request.id() + " not found"));

        workout.setName(request.name());
        workout.setMuscleGroups(request.muscleGroups());

        workout.getWorkoutExercises().clear();

        insertExercisesToWorkout(workout, request.exercises());

        workoutRepository.save(workout);
    }

    public void delete(Long id, User user) {
        var workout = workoutRepository.findByIdAndUserId(id, user.getId()).orElseThrow(() ->
                new NotFoundException("Workout with id " + id + " not found"));

        workoutRepository.delete(workout);
    }

    private void insertExercisesToWorkout(Workout workout, List<WorkoutExerciseRequest> exercises) {
        for (var workoutExerciseRequest : exercises) {

            var exercise = exerciseRepository.findById(workoutExerciseRequest.exerciseId())
                    .orElseThrow(() ->
                            new NotFoundException("Exercise with id " + workoutExerciseRequest.exerciseId() + " not found"));

            var workoutExercise = WorkoutExercise.builder()
                    .workout(workout)
                    .exercise(exercise)
                    .sets(workoutExerciseRequest.sets())
                    .reps(workoutExerciseRequest.reps())
                    .build();

            workout.getWorkoutExercises().add(workoutExercise);
        }
    }
}
