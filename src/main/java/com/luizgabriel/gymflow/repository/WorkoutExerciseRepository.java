package com.luizgabriel.gymflow.repository;

import com.luizgabriel.gymflow.domain.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, Long> {

    Optional<WorkoutExercise> findByExerciseIdAndWorkoutId(Long exerciseId, Long workoutId);
}
