package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.domain.PhysicalAssessment;
import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.PhysicalAssessmentPostRequest;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.PhysicalAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PhysicalAssessmentService {

    private final PhysicalAssessmentRepository repository;

    public List<PhysicalAssessment> findByAuthenticatedUser(User user) {
        return repository.findByUserId(user.getId());
    }

    public PhysicalAssessment findByIdOrThrowNotFound(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Physical Assessment not Found"));
    }

    public PhysicalAssessment save(PhysicalAssessmentPostRequest request, User user) {

        var physicalAssessment = PhysicalAssessment.builder()
                .weight(request.weight())
                .height(request.height())
                .fatPercentage(request.fatPercentage())
                .user(user)
                .build();

         return repository.save(physicalAssessment);
    }
}
