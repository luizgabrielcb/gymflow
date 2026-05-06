package com.luizgabriel.gymflow.repository;

import com.luizgabriel.gymflow.domain.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    List<Workout> findAllByUserId(Long userId);
}
