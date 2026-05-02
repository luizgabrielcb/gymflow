package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.domain.PhysicalAssessment;
import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.PhysicalAssessmentPostRequest;
import com.luizgabriel.gymflow.dto.request.PhysicalAssessmentPutRequest;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.PhysicalAssessmentRepository;
import com.luizgabriel.gymflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PhysicalAssessmentService {

    private final PhysicalAssessmentRepository assessmentRepository;
    private final UserRepository userRepository;

    public List<PhysicalAssessment> findByAuthenticatedUser(User user) {
        return assessmentRepository.findByUserId(user.getId());
    }

    public List<PhysicalAssessment> findUserAssessmentByUserId(Long id) {
        return assessmentRepository.findByUserId(id);
    }

    public PhysicalAssessment findByIdOrThrowNotFound(Long id) {
        return assessmentRepository.findById(id).orElseThrow(() -> new NotFoundException("Physical Assessment not Found"));
    }

    public PhysicalAssessment save(PhysicalAssessmentPostRequest request) {
        var user = userRepository.findById(request.userId()).orElseThrow(() -> new NotFoundException("User not Found"));

        var physicalAssessment = PhysicalAssessment.builder()
                .weight(request.weight())
                .height(request.height())
                .fatPercentage(request.fatPercentage())
                .user(user)
                .build();

        return assessmentRepository.save(physicalAssessment);
    }

    public void update(PhysicalAssessmentPutRequest request) {
        var physicalAssessment = assessmentRepository.findById(request.id()).orElseThrow(() -> new NotFoundException("Physical Assessment not Found"));

        physicalAssessment.setWeight(request.weight());
        physicalAssessment.setHeight(request.height());
        physicalAssessment.setFatPercentage(request.fatPercentage());

        assessmentRepository.save(physicalAssessment);
    }

    public void delete(Long id) {
        var physicalAssessmentToDelete = findByIdOrThrowNotFound(id);

        assessmentRepository.delete(physicalAssessmentToDelete);
    }
}
