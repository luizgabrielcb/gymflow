package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.commons.WorkoutUtils;
import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.domain.Workout;
import com.luizgabriel.gymflow.dto.request.WorkoutExerciseRequest;
import com.luizgabriel.gymflow.exception.BadRequestException;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.ExerciseRepository;
import com.luizgabriel.gymflow.repository.WorkoutRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @InjectMocks
    private WorkoutService service;

    @InjectMocks
    private WorkoutUtils utils;

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Test
    @DisplayName("save returns a workout when successful")
    void save_ReturnsWorkout_WhenSuccessful() {
        var workout = utils.newWorkout();
        var workoutPostRequest = utils.newWorkoutPostRequest();
        var exercise = utils.newExercise();

        BDDMockito.when(exerciseRepository.findAllById(workoutPostRequest.exercises()
                        .stream()
                        .map(WorkoutExerciseRequest::exerciseId).toList()))
                .thenReturn(List.of(exercise));

        BDDMockito.when(workoutRepository.save(ArgumentMatchers.any())).thenReturn(workout);

        var savedWorkout = service.save(workoutPostRequest, workout.getUser());

        Assertions.assertThat(savedWorkout).isNotNull().isEqualTo(workout);
    }

    @Test
    @DisplayName("save throws NotFoundException when exercise not found")
    void save_ThrowsNotFoundException_WhenExerciseNotFound() {
        var workout = utils.newWorkout();
        var workoutPostRequest = utils.newWorkoutPostRequest();

        BDDMockito.when(exerciseRepository.findAllById(workoutPostRequest.exercises()
                        .stream()
                        .map(WorkoutExerciseRequest::exerciseId).toList()))
                .thenReturn(Collections.emptyList());

        Assertions.assertThatThrownBy(() -> service.save(workoutPostRequest, workout.getUser()))
                .isInstanceOf(NotFoundException.class);

        BDDMockito.then(workoutRepository).should(Mockito.never()).save(ArgumentMatchers.any(Workout.class));
    }

    @Test
    @DisplayName("findAll returns a list with all workouts by user id when successful")
    void findAll_ReturnsListWithAllWorkoutsByUserId_WhenSuccessful() {
        var workout = utils.newWorkout();

        var workoutList = Collections.singletonList(workout);

        BDDMockito.when(workoutRepository.findAllByUserId(workout.getUser().getId()))
                .thenReturn(workoutList);

        var workouts = service.findAll(workout.getUser());

        Assertions.assertThat(workouts).isNotNull().isEqualTo(workoutList);
    }

    @Test
    @DisplayName("findAll returns a empty list when user does not have workouts")
    void findAll_ReturnsEmptyList_WhenUserDoesNotHaveWorkouts() {
        var user = User.builder().id(99L).build();

        BDDMockito.when(workoutRepository.findAllByUserId(user.getId()))
                .thenReturn(Collections.emptyList());

        var workouts = service.findAll(user);

        Assertions.assertThat(workouts).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("update saves updated workout when successful")
    void update_SavesUpdatedWorkout_WhenSuccessful() {
        var workout = utils.newWorkout();
        var workoutPutRequest = utils.newWorkoutPutRequest();
        var exercise = utils.newExercise();

        BDDMockito.when(workoutRepository.findById(workoutPutRequest.id()))
                .thenReturn(Optional.of(workout));

        BDDMockito.when(exerciseRepository.findAllById(workoutPutRequest.exercises()
                        .stream()
                        .map(WorkoutExerciseRequest::exerciseId).toList()))
                .thenReturn(List.of(exercise));

        BDDMockito.when(workoutRepository.save(ArgumentMatchers.any())).thenReturn(workout);

        service.update(workoutPutRequest, workout.getUser());

        BDDMockito.then(workoutRepository).should().save(ArgumentMatchers.any(Workout.class));
    }

    @Test
    @DisplayName("update throws NotFoundException when workout is not found")
    void update_ThrowsNotFoundException_WhenWorkoutNotFound() {
        var workoutPutRequest = utils.newWorkoutPutRequest();
        var workout = utils.newWorkout();

        BDDMockito.when(workoutRepository.findById(workoutPutRequest.id()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.update(workoutPutRequest, workout.getUser()))
                .isInstanceOf(NotFoundException.class);

        BDDMockito.then(workoutRepository).should(Mockito.never()).save(ArgumentMatchers.any(Workout.class));
    }

    @Test
    @DisplayName("update throws BadRequestException when workout does not belong to user")
    void update_ThrowsBadRequestException_WhenWorkoutDoesNotBelongToUser() {
        var workoutPutRequest = utils.newWorkoutPutRequest();
        var workout = utils.newWorkout();
        var anotherUser = User.builder().id(99L).build();

        BDDMockito.when(workoutRepository.findById(workoutPutRequest.id()))
                .thenReturn(Optional.of(workout));

        Assertions.assertThatThrownBy(() -> service.update(workoutPutRequest, anotherUser))
                .isInstanceOf(BadRequestException.class);

        BDDMockito.then(workoutRepository).should(Mockito.never()).save(ArgumentMatchers.any(Workout.class));
    }


    @Test
    @DisplayName("update throws NotFoundException when exercise not found")
    void update_ThrowsNotFoundException_WhenExerciseNotFound() {
        var workoutPutRequest = utils.newWorkoutPutRequest();
        var workout = utils.newWorkout();

        BDDMockito.when(workoutRepository.findById(workoutPutRequest.id()))
                .thenReturn(Optional.of(workout));

        BDDMockito.when(exerciseRepository.findAllById(workoutPutRequest.exercises()
                        .stream()
                        .map(WorkoutExerciseRequest::exerciseId).toList()))
                .thenReturn(Collections.emptyList());

        Assertions.assertThatThrownBy(() -> service.update(workoutPutRequest, workout.getUser()))
                .isInstanceOf(NotFoundException.class);

        BDDMockito.then(workoutRepository).should(Mockito.never()).save(ArgumentMatchers.any(Workout.class));
    }

    @Test
    @DisplayName("delete removes workout when successful")
    void delete_RemovesWorkout_WhenSuccessful() {
        var workout = utils.newWorkout();

        BDDMockito.when(workoutRepository.findById(workout.getId()))
                .thenReturn(Optional.of(workout));

        service.delete(workout.getId(), workout.getUser());

        BDDMockito.then(workoutRepository).should().delete(ArgumentMatchers.any(Workout.class));
    }

    @Test
    @DisplayName("delete throws NotFoundException when workout not found")
    void delete_ThrowsNotFoundException_WhenWorkoutNotFound() {
        var workout = utils.newWorkout();

        BDDMockito.when(workoutRepository.findById(workout.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.delete(workout.getId(), workout.getUser()))
                .isInstanceOf(NotFoundException.class);

        BDDMockito.then(workoutRepository).should(Mockito.never()).delete(ArgumentMatchers.any(Workout.class));
    }

    @Test
    @DisplayName("delete throws BadRequestException when workout does not belong to user")
    void delete_ThrowsBadRequestException_WhenWorkoutDoesNotBelongToUser() {
        var workout = utils.newWorkout();
        var anotherUser = User.builder().id(99L).build();

        BDDMockito.when(workoutRepository.findById(workout.getId()))
                .thenReturn(Optional.of(workout));

        Assertions.assertThatThrownBy(() -> service.delete(workout.getId(), anotherUser))
                .isInstanceOf(BadRequestException.class);

        BDDMockito.then(workoutRepository).should(Mockito.never()).delete(ArgumentMatchers.any(Workout.class));
    }
}