package com.luizgabriel.gymflow.repository;

import com.luizgabriel.gymflow.domain.PhysicalAssessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhysicalAssessmentRepository extends JpaRepository<PhysicalAssessment, Long> {

    Page<PhysicalAssessment> findByUserId(Pageable pageable, Long id);
}
