package com.luizgabriel.gymflow.repository;

import com.luizgabriel.gymflow.domain.PhysicalAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhysicalAssessmentRepository extends JpaRepository<PhysicalAssessment, Long> {

    List<PhysicalAssessment> findByUserId(Long id);
}
