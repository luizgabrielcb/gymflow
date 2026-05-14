package com.luizgabriel.gymflow.repository;

import com.luizgabriel.gymflow.domain.Status;
import com.luizgabriel.gymflow.domain.TrainingSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {

    boolean existsByUserIdAndStatus(Long userId, Status status);

    @EntityGraph(attributePaths = {"sets", "sets.exercise", "workout"})
    Page<TrainingSession> findAllByUserId(Pageable pageable, Long userId);

    @EntityGraph(attributePaths = {"sets", "sets.exercise", "workout"})
    Optional<TrainingSession> findByUserIdAndStatus(Long userId, Status status);

    @EntityGraph(attributePaths = {"sets", "sets.exercise", "workout"})
    Optional<TrainingSession> findById(Long id);
}
