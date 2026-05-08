package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.commons.PhysicalAssessmentUtils;
import com.luizgabriel.gymflow.domain.PhysicalAssessment;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.PhysicalAssessmentRepository;
import com.luizgabriel.gymflow.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PhysicalAssessmentServiceTest {

    @InjectMocks
    private PhysicalAssessmentService service;

    @InjectMocks
    private PhysicalAssessmentUtils utils;

    @Mock
    private PhysicalAssessmentRepository assessmentRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("save returns a physical assessment when successful")
    void save_ReturnsPhysicalAssessment_WhenSuccessful() {
        var assessmentPostRequest = utils.newPhysicalAssessmentPostRequest();
        var physicalAssessment = utils.newPhysicalAssessment();
        var user = utils.newUser();

        BDDMockito.when(userRepository.findById(assessmentPostRequest.userId()))
                .thenReturn(Optional.of(user));

        BDDMockito.when(assessmentRepository.save(ArgumentMatchers.any(PhysicalAssessment.class)))
                .thenReturn(physicalAssessment);

        var savedAssessment = service.save(assessmentPostRequest);

        Assertions.assertThat(savedAssessment).isNotNull().isEqualTo(physicalAssessment);
    }

    @Test
    @DisplayName("save throws NotFoundException when user not found")
    void save_ThrowsNotFoundException_WhenUserNotFound() {
        var assessmentPostRequest = utils.newPhysicalAssessmentPostRequest();

        BDDMockito.when(userRepository.findById(assessmentPostRequest.userId()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.save(assessmentPostRequest))
                .isInstanceOf(NotFoundException.class);

        BDDMockito.then(assessmentRepository).should(Mockito.never()).save(ArgumentMatchers.any(PhysicalAssessment.class));
    }

    @Test
    @DisplayName("findByAuthenticatedUser returns a list with all physical assessments when successful")
    void findByAuthenticatedUser_ReturnsListWithAllPhysicalAssessment_WhenSuccessful() {
        var physicalAssessment = utils.newPhysicalAssessment();
        var user = utils.newUser();

        BDDMockito.when(assessmentRepository.findByUserId(user.getId()))
                .thenReturn(Collections.singletonList(physicalAssessment));

        var assessmentsByAuthenticatedUser = service.findByAuthenticatedUser(user);

        Assertions.assertThat(assessmentsByAuthenticatedUser).isNotNull().isEqualTo(Collections.singletonList(physicalAssessment));
    }

    @Test
    @DisplayName("findByAuthenticatedUser returns a empty list when physical assessment not found")
    void findByAuthenticatedUser_ReturnsEmptyList_WhenPhysicalAssessmentNotFound() {
        var user = utils.newUser();

        BDDMockito.when(assessmentRepository.findByUserId(user.getId()))
                .thenReturn(Collections.emptyList());

        var assessmentsByAuthenticatedUser = service.findByAuthenticatedUser(user);

        Assertions.assertThat(assessmentsByAuthenticatedUser).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("findUserAssessmentByUserId returns a list with all physical assessments when successful")
    void findUserAssessmentByUserId_ReturnsListWithAllPhysicalAssessment_WhenSuccessful() {
        var user = utils.newUser();
        var physicalAssessment = utils.newPhysicalAssessment();

        BDDMockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        BDDMockito.when(assessmentRepository.findByUserId(user.getId()))
                .thenReturn(Collections.singletonList(physicalAssessment));

        var assessmentsByUserId = service.findUserAssessmentByUserId(user.getId());

        Assertions.assertThat(assessmentsByUserId).isNotNull().isEqualTo(Collections.singletonList(physicalAssessment));
    }

    @Test
    @DisplayName("findUserAssessmentByUserId throws NotFoundException when user not found")
    void findUserAssessmentByUserId_ThrowsNotFoundException_WhenUserNotFound() {
        var user = utils.newUser();

        BDDMockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findUserAssessmentByUserId(user.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("findUserAssessmentByUserId returns empty list when physical assessment not found")
    void findUserAssessmentByUserId_ReturnsEmptyList_WhenPhysicalAssessmentNotFound() {
        var user = utils.newUser();

        BDDMockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        BDDMockito.when(assessmentRepository.findByUserId(user.getId()))
                .thenReturn(Collections.emptyList());

        var userAssessmentByUserId = service.findUserAssessmentByUserId(user.getId());

        Assertions.assertThat(userAssessmentByUserId).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("findById returns physical assessment when successful")
    void findById_ReturnsPhysicalAssessment_WhenSuccessful() {
        var physicalAssessment = utils.newPhysicalAssessment();

        BDDMockito.when(assessmentRepository.findById(physicalAssessment.getId()))
                .thenReturn(Optional.of(physicalAssessment));

        var assessmentById = service.findById(physicalAssessment.getId());

        Assertions.assertThat(assessmentById).isNotNull().isEqualTo(physicalAssessment);
    }

    @Test
    @DisplayName("findById throws NotFoundException when physical assessment not found")
    void findById_ThrowsNotFoundException_WhenPhysicalAssessmentNotFound() {
        var physicalAssessment = utils.newPhysicalAssessment();

        BDDMockito.when(assessmentRepository.findById(physicalAssessment.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findById(physicalAssessment.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("update saves updated physical assessment when successful")
    void update_SavesUpdatedPhysicalAssessment_WhenSuccessful() {
        var assessmentPutRequest = utils.newPhysicalAssessmentPutRequest();
        var physicalAssessment = utils.newPhysicalAssessment();

        BDDMockito.when(assessmentRepository.findById(assessmentPutRequest.id()))
                .thenReturn(Optional.of(physicalAssessment));

        service.update(assessmentPutRequest);

        BDDMockito.then(assessmentRepository).should().save(ArgumentMatchers.any(PhysicalAssessment.class));
    }

    @Test
    @DisplayName("update throws NotFoundException when physical assessment not found")
    void update_ThrowsNotFoundException_WhenPhysicalAssessmentNotFound() {
        var assessmentPutRequest = utils.newPhysicalAssessmentPutRequest();

        BDDMockito.when(assessmentRepository.findById(assessmentPutRequest.id()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.update(assessmentPutRequest))
                .isInstanceOf(NotFoundException.class);

        BDDMockito.then(assessmentRepository).should(Mockito.never()).save(ArgumentMatchers.any(PhysicalAssessment.class));
    }

    @Test
    @DisplayName("delete removes physical assessment when successful")
    void delete_RemovesPhysicalAssessment_WhenSuccessful() {
        var physicalAssessment = utils.newPhysicalAssessment();

        BDDMockito.when(assessmentRepository.findById(physicalAssessment.getId()))
                .thenReturn(Optional.of(physicalAssessment));

        service.delete(physicalAssessment.getId());

        BDDMockito.then(assessmentRepository).should().delete(ArgumentMatchers.any(PhysicalAssessment.class));
    }

    @Test
    @DisplayName("delete throws NotFoundException when physical assessment not found")
    void delete_ThrowsNotFoundException_WhenPhysicalAssessmentNotFound() {
        var physicalAssessment = utils.newPhysicalAssessment();

        BDDMockito.when(assessmentRepository.findById(physicalAssessment.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.delete(physicalAssessment.getId()))
                .isInstanceOf(NotFoundException.class);

        BDDMockito.then(assessmentRepository).should(Mockito.never()).delete(ArgumentMatchers.any(PhysicalAssessment.class));
    }
}