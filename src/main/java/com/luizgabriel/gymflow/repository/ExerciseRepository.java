package com.luizgabriel.gymflow.repository;

import com.luizgabriel.gymflow.domain.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
}
