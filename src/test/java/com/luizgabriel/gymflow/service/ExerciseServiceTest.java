package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.commons.ExerciseUtils;
import com.luizgabriel.gymflow.domain.Exercise;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.ExerciseRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ExerciseServiceTest {

    @InjectMocks
    private ExerciseService service;

    @InjectMocks
    private ExerciseUtils utils;

    @Mock
    private ExerciseRepository repository;

    @Test
    @DisplayName("save returns exercise when successful")
    void save_ReturnsExercise_WhenSuccessful() {
        var exercise = utils.newExercise();

        BDDMockito.when(repository.save(ArgumentMatchers.any(Exercise.class))).thenReturn(exercise);

        var savedExercise = service.save(exercise);

        Assertions.assertThat(savedExercise).isNotNull().isEqualTo(exercise);
    }

    @Test
    @DisplayName("findAll returns a list with all exercises when successful")
    void findAll_ReturnsListWithAllExercise_WhenSuccessful() {
        var exercise = utils.newExercise();

        var exerciseSingletonList = Collections.singletonList(exercise);

        BDDMockito.when(repository.findAll()).thenReturn(exerciseSingletonList);

        var exerciseList = service.findAll();

        Assertions.assertThat(exerciseList).isNotNull().isEqualTo(exerciseSingletonList);
    }

    @Test
    @DisplayName("save returns a empty list when exercise not found")
    void findAll_ReturnsEmptyList_WhenExerciseNotFound() {
        BDDMockito.when(repository.findAll()).thenReturn(Collections.emptyList());

        var exerciseList = service.findAll();

        Assertions.assertThat(exerciseList).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("findById returns exercise when successful")
    void findById_ReturnsExercise_WhenSuccessful() {
        var exercise = utils.newExercise();

        BDDMockito.when(repository.findById(exercise.getId())).thenReturn(Optional.of(exercise));

        var exerciseById = service.findById(exercise.getId());

        Assertions.assertThat(exerciseById).isNotNull().isEqualTo(exercise);
    }

    @Test
    @DisplayName("findById throws NotFoundException when exercise not found")
    void findById_ThrowsNotFoundException_WhenExerciseNotFound() {
        var exercise = utils.newExercise();

        BDDMockito.when(repository.findById(exercise.getId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findById(exercise.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("update saves updated exercise when successful")
    void update_SavesUpdatedExercise_WhenSuccessful() {
        var exercise = utils.newExercise();
        var exercisePutRequest = utils.newExercisePutRequest();

        BDDMockito.when(repository.findById(exercisePutRequest.id())).thenReturn(Optional.of(exercise));

        service.update(exercisePutRequest);

        BDDMockito.then(repository).should().save(ArgumentMatchers.any(Exercise.class));
    }

    @Test
    @DisplayName("update throws NotFoundException when exercise not found")
    void update_ThrowsNotFoundException_WhenExerciseNotFound() {
        var exercisePutRequest = utils.newExercisePutRequest();

        BDDMockito.when(repository.findById(exercisePutRequest.id())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.update(exercisePutRequest))
                .isInstanceOf(NotFoundException.class);

        BDDMockito.then(repository).should(Mockito.never()).save(ArgumentMatchers.any(Exercise.class));
    }

    @Test
    @DisplayName("delete removes exercise when successful")
    void delete_RemovesExercise_WhenSuccessful() {
        var exercise = utils.newExercise();

        BDDMockito.when(repository.findById(exercise.getId())).thenReturn(Optional.of(exercise));

        service.delete(exercise.getId());

        BDDMockito.then(repository).should().delete(ArgumentMatchers.any(Exercise.class));
    }

    @Test
    @DisplayName("delete throws NotFoundException when exercise not found")
    void delete_ThrowsNotFoundException_WhenExerciseNotFound() {
        var exercise = utils.newExercise();

        BDDMockito.when(repository.findById(exercise.getId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.delete(exercise.getId()))
                .isInstanceOf(NotFoundException.class);

        BDDMockito.then(repository).should(Mockito.never()).delete(ArgumentMatchers.any(Exercise.class));
    }
}