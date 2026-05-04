package com.luizgabriel.gymflow.repository;

import com.luizgabriel.gymflow.domain.Status;
import com.luizgabriel.gymflow.domain.TrainingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {

    boolean existsByUserIdAndStatus(Long userId, Status status);

    List<TrainingSession> findAllByUserId(Long userId);

    Optional<TrainingSession> findByUserIdAndStatus(Long userId, Status status);
}
