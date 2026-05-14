package com.luizgabriel.gymflow.repository;

import com.luizgabriel.gymflow.domain.Workout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    @EntityGraph(attributePaths = "workoutExercises")
    Page<Workout> findAllByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = "workoutExercises")
    Optional<Workout> findById(Long id);

    Optional<Workout> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndUserIdAndIdNot(String name, Long userId, Long id);

    boolean existsByNameIgnoreCaseAndUserId(String name, Long userId);
}
