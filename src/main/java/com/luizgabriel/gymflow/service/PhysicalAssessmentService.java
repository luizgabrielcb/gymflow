package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.domain.PhysicalAssessment;
import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.PhysicalAssessmentPostRequest;
import com.luizgabriel.gymflow.dto.request.PhysicalAssessmentPutRequest;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.PhysicalAssessmentRepository;
import com.luizgabriel.gymflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhysicalAssessmentService {

    private final PhysicalAssessmentRepository assessmentRepository;
    private final UserRepository userRepository;

    public PhysicalAssessment save(PhysicalAssessmentPostRequest request) {
        var user = findUserOrThrowNotFound(request.userId());

        var physicalAssessment = PhysicalAssessment.builder()
                .weight(request.weight())
                .height(request.height())
                .fatPercentage(request.fatPercentage())
                .user(user)
                .build();

        return assessmentRepository.save(physicalAssessment);
    }

    public Page<PhysicalAssessment> findByAuthenticatedUser(Pageable pageable, User user) {
        return assessmentRepository.findByUserId(pageable, user.getId());
    }

    public Page<PhysicalAssessment> findUserAssessmentByUserId(Pageable pageable, Long id) {
        findUserOrThrowNotFound(id);

        return assessmentRepository.findByUserId(pageable, id);
    }

    public PhysicalAssessment findById(Long id) {
        return findAssessmentByIdOrThrowNotFound(id);
    }

    public void update(PhysicalAssessmentPutRequest request) {
        var physicalAssessment = findAssessmentByIdOrThrowNotFound(request.id());

        physicalAssessment.setWeight(request.weight());
        physicalAssessment.setHeight(request.height());
        physicalAssessment.setFatPercentage(request.fatPercentage());

        assessmentRepository.save(physicalAssessment);
    }

    public void delete(Long id) {
        var physicalAssessmentToDelete = findAssessmentByIdOrThrowNotFound(id);

        assessmentRepository.delete(physicalAssessmentToDelete);
    }

    private PhysicalAssessment findAssessmentByIdOrThrowNotFound(Long id) {
        return assessmentRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Physical Assessment with id " + id + " not found"));
    }

    private User findUserOrThrowNotFound(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " not found"));
    }
}
