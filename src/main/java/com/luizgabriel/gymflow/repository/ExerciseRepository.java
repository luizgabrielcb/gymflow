package com.luizgabriel.gymflow.repository;

import com.luizgabriel.gymflow.domain.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByNameIgnoreCase(String name);

    Page<Exercise> findAll(Pageable pageable);
}
