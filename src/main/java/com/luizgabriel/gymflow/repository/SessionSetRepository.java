package com.luizgabriel.gymflow.repository;

import com.luizgabriel.gymflow.domain.SessionSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionSetRepository extends JpaRepository<SessionSet, Long> {
    boolean existsByTrainingSessionIdAndExerciseIdAndSetNumber(Long trainingSessionId, Long exerciseId, Integer setNumber);
}
