package com.luizgabriel.gymflow.repository;

import com.luizgabriel.gymflow.domain.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findAllByUserId(Long userId);

    Optional<Workout> findByIdAndUserId(Long id, Long userId);
}
