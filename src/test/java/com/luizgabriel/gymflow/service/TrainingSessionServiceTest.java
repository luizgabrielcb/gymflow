package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.commons.TrainingSessionUtils;
import com.luizgabriel.gymflow.domain.SessionSet;
import com.luizgabriel.gymflow.domain.Status;
import com.luizgabriel.gymflow.domain.TrainingSession;
import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.exception.BadRequestException;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.SessionSetRepository;
import com.luizgabriel.gymflow.repository.TrainingSessionRepository;
import com.luizgabriel.gymflow.repository.WorkoutExerciseRepository;
import com.luizgabriel.gymflow.repository.WorkoutRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TrainingSessionServiceTest {

    @InjectMocks
    private TrainingSessionService service;

    @InjectMocks
    private TrainingSessionUtils utils;

    @Mock
    private TrainingSessionRepository trainingSessionRepository;

    @Mock
    private WorkoutRepository workoutRepository;

    @Mock
    private WorkoutExerciseRepository workoutExerciseRepository;

    @Mock
    private SessionSetRepository sessionSetRepository;

    @Test
    @DisplayName("startSession returns a training session when successful")
    void startSession_ReturnsTrainingSession_WhenSuccessful() {
        var trainingSession = utils.newTrainingSession();
        var trainingSessionInProgress = utils.newTrainingSessionInProgress();

        BDDMockito.when(trainingSessionRepository.existsByUserIdAndStatus(
                        trainingSession.getUser().getId(), Status.IN_PROGRESS))
                .thenReturn(false);

        BDDMockito.when(workoutRepository.findById(trainingSession.getWorkout().getId()))
                .thenReturn(Optional.of(trainingSession.getWorkout()));

        BDDMockito.when(trainingSessionRepository.save(ArgumentMatchers.any(TrainingSession.class)))
                .thenReturn(trainingSessionInProgress);

        var trainingSessionStarted = service.startSession(trainingSession.getWorkout().getId(), trainingSession.getUser());

        Assertions.assertThat(trainingSessionStarted).isNotNull();
        Assertions.assertThat(trainingSessionStarted.getStatus()).isEqualTo(Status.IN_PROGRESS);
        Assertions.assertThat(trainingSessionStarted.getUser()).isEqualTo(trainingSession.getUser());
        Assertions.assertThat(trainingSessionStarted.getWorkout()).isEqualTo(trainingSession.getWorkout());
    }

    @Test
    @DisplayName("startSession throws BadRequestException when already have an active training session")
    void startSession_ThrowsBadRequestException_WhenAlreadyHaveActiveTrainingSession() {
        var trainingSession = utils.newTrainingSession();

        BDDMockito.when(trainingSessionRepository.existsByUserIdAndStatus(
                        trainingSession.getUser().getId(), Status.IN_PROGRESS))
                .thenReturn(true);

        Assertions.assertThatThrownBy(() -> service.startSession(trainingSession.getWorkout().getId(), trainingSession.getUser()))
                .isInstanceOf(BadRequestException.class);

        BDDMockito.then(trainingSessionRepository).should(Mockito.never()).save(ArgumentMatchers.any(TrainingSession.class));
    }

    @Test
    @DisplayName("startSession throws NotFoundException when workout is not found")
    void startSession_ThrowsNotFoundException_WhenWorkoutIsNotFound() {
        var trainingSession = utils.newTrainingSession();

        BDDMockito.when(trainingSessionRepository.existsByUserIdAndStatus(
                        trainingSession.getUser().getId(), Status.IN_PROGRESS))
                .thenReturn(false);

        BDDMockito.when(workoutRepository.findById(trainingSession.getWorkout().getId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.startSession(trainingSession.getWorkout().getId(), trainingSession.getUser()))
                .isInstanceOf(NotFoundException.class);

        BDDMockito.then(trainingSessionRepository).should(Mockito.never()).save(ArgumentMatchers.any(TrainingSession.class));
    }

    @Test
    @DisplayName("startSession throws BadRequestException when workout does not belong to user")
    void startSession_ThrowsBadRequestException_WhenWorkoutDoesNotBelongToUser() {
        var trainingSession = utils.newTrainingSession();

        var anotherUser = User.builder().id(99L).build();

        BDDMockito.when(trainingSessionRepository.existsByUserIdAndStatus(
                        anotherUser.getId(), Status.IN_PROGRESS))
                .thenReturn(false);

        BDDMockito.when(workoutRepository.findById(trainingSession.getWorkout().getId())).thenReturn(Optional.of(trainingSession.getWorkout()));

        Assertions.assertThatThrownBy(() -> service.startSession(trainingSession.getWorkout().getId(), anotherUser))
                .isInstanceOf(BadRequestException.class);

        BDDMockito.then(trainingSessionRepository).should(Mockito.never()).save(ArgumentMatchers.any(TrainingSession.class));
    }

    @Test
    @DisplayName("addSet returns a session set when successful")
    void addSet_ReturnsSessionSet_WhenSuccessful() {
        var trainingSessionInProgress = utils.newTrainingSessionInProgress();
        var sessionSet = utils.newSessionSet();
        var workoutExercise = utils.newWorkoutExercise();
        var sessionSetPostRequest = utils.newSessionSetPostRequest();

        BDDMockito.when(trainingSessionRepository.findById(trainingSessionInProgress.getId()))
                .thenReturn(Optional.of(trainingSessionInProgress));

        BDDMockito.when(workoutExerciseRepository.findByExerciseIdAndWorkoutId(
                        sessionSetPostRequest.exerciseId(), trainingSessionInProgress.getWorkout().getId()))
                .thenReturn(Optional.of(workoutExercise));

        BDDMockito.when(sessionSetRepository.existsByTrainingSessionIdAndExerciseIdAndSetNumber(
                        trainingSessionInProgress.getId(), sessionSetPostRequest.exerciseId(), sessionSetPostRequest.setNumber()))
                .thenReturn(false);

        BDDMockito.when(sessionSetRepository.save(ArgumentMatchers.any(SessionSet.class))).thenReturn(sessionSet);

        var addedSessionSet = service.addSet(trainingSessionInProgress.getId(), sessionSetPostRequest, trainingSessionInProgress.getUser());

        Assertions.assertThat(addedSessionSet).isNotNull();
        Assertions.assertThat(addedSessionSet.getTrainingSession()).isEqualTo(trainingSessionInProgress);
        Assertions.assertThat(addedSessionSet.getExercise()).isEqualTo(workoutExercise.getExercise());
        Assertions.assertThat(addedSessionSet.getSetNumber()).isEqualTo(sessionSet.getSetNumber());
        Assertions.assertThat(addedSessionSet.getRepsNumber()).isEqualTo(sessionSet.getRepsNumber());
        Assertions.assertThat(addedSessionSet.getWeightKg()).isEqualTo(sessionSet.getWeightKg());
        Assertions.assertThat(addedSessionSet.getRestSeconds()).isEqualTo(sessionSet.getRestSeconds());
    }

    @Test
    @DisplayName("addSet throws NotFoundException when training session is not found")
    void addSet_ThrowsNotFoundException_WhenTrainingSessionIsNotFound() {
        var trainingSessionInProgress = utils.newTrainingSessionInProgress();
        var sessionSetPostRequest = utils.newSessionSetPostRequest();

        BDDMockito.when(trainingSessionRepository.findById(trainingSessionInProgress.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.addSet
                        (trainingSessionInProgress.getId(), sessionSetPostRequest, trainingSessionInProgress.getUser()))
                .isInstanceOf(NotFoundException.class);

        BDDMockito.then(sessionSetRepository).should(Mockito.never()).save(ArgumentMatchers.any(SessionSet.class));
    }

    @Test
    @DisplayName("addSet throws BadRequestException when training session does not belong to user")
    void addSet_ThrowsBadRequestException_WhenTrainingSessionDoesNotBelongToUser() {
        var trainingSessionInProgress = utils.newTrainingSessionInProgress();
        var sessionSetPostRequest = utils.newSessionSetPostRequest();

        var anotherUser = User.builder().id(99L).build();

        BDDMockito.when(trainingSessionRepository.findById(trainingSessionInProgress.getId()))
                .thenReturn(Optional.of(trainingSessionInProgress));

        Assertions.assertThatThrownBy(() -> service.addSet
                        (trainingSessionInProgress.getId(), sessionSetPostRequest, anotherUser))
                .isInstanceOf(BadRequestException.class);

        BDDMockito.then(sessionSetRepository).should(Mockito.never()).save(ArgumentMatchers.any(SessionSet.class));
    }

    @Test
    @DisplayName("addSet throws BadRequestException when training session is not in progress")
    void addSet_ThrowsBadRequestException_WhenTrainingSessionIsNotInProgress() {
        var trainingSession = utils.newTrainingSession();
        var sessionSetPostRequest = utils.newSessionSetPostRequest();

        BDDMockito.when(trainingSessionRepository.findById(trainingSession.getId()))
                .thenReturn(Optional.of(trainingSession));

        Assertions.assertThatThrownBy(() -> service.addSet
                        (trainingSession.getId(), sessionSetPostRequest, trainingSession.getUser()))
                .isInstanceOf(BadRequestException.class);

        BDDMockito.then(sessionSetRepository).should(Mockito.never()).save(ArgumentMatchers.any(SessionSet.class));
    }

    @Test
    @DisplayName("addSet throws BadRequestException when exercise is not part of workout")
    void addSet_ThrowsBadRequestException_WhenExerciseIsNotPartOfWorkout() {
        var trainingSessionInProgress = utils.newTrainingSessionInProgress();
        var sessionSetPostRequest = utils.newSessionSetPostRequest();

        BDDMockito.when(trainingSessionRepository.findById(trainingSessionInProgress.getId()))
                .thenReturn(Optional.of(trainingSessionInProgress));

        BDDMockito.when(workoutExerciseRepository.findByExerciseIdAndWorkoutId(
                        sessionSetPostRequest.exerciseId(), trainingSessionInProgress.getWorkout().getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.addSet
                        (trainingSessionInProgress.getId(), sessionSetPostRequest, trainingSessionInProgress.getUser()))
                .isInstanceOf(BadRequestException.class);

        BDDMockito.then(sessionSetRepository).should(Mockito.never()).save(ArgumentMatchers.any(SessionSet.class));
    }

    @Test
    @DisplayName("addSet throws BadRequestException when set number already exists for exercise in training session")
    void addSet_ThrowsBadRequestException_WhenSetNumberAlreadyExistsForExerciseInTrainingSession() {
        var trainingSessionInProgress = utils.newTrainingSessionInProgress();
        var sessionSetPostRequest = utils.newSessionSetPostRequest();
        var workoutExercise = utils.newWorkoutExercise();

        BDDMockito.when(trainingSessionRepository.findById(trainingSessionInProgress.getId()))
                .thenReturn(Optional.of(trainingSessionInProgress));

        BDDMockito.when(workoutExerciseRepository.findByExerciseIdAndWorkoutId(
                        sessionSetPostRequest.exerciseId(), trainingSessionInProgress.getWorkout().getId()))
                .thenReturn(Optional.of(workoutExercise));

        BDDMockito.when(sessionSetRepository.existsByTrainingSessionIdAndExerciseIdAndSetNumber(
                        trainingSessionInProgress.getId(), sessionSetPostRequest.exerciseId(), sessionSetPostRequest.setNumber()))
                .thenReturn(true);

        Assertions.assertThatThrownBy(() -> service.addSet
                        (trainingSessionInProgress.getId(), sessionSetPostRequest, trainingSessionInProgress.getUser()))
                .isInstanceOf(BadRequestException.class);

        BDDMockito.then(sessionSetRepository).should(Mockito.never()).save(ArgumentMatchers.any(SessionSet.class));
    }

    @Test
    @DisplayName("finishTrainingSession finishes training session when successful")
    void finishTrainingSession_FinishesTrainingSession_WhenSuccessful() {
        var trainingSession = utils.newTrainingSessionInProgress();

        BDDMockito.when(trainingSessionRepository.findById(trainingSession.getId()))
                .thenReturn(Optional.of(trainingSession));

        service.finishTrainingSession(trainingSession.getId(), trainingSession.getUser());

        Assertions.assertThat(trainingSession.getStatus()).isEqualTo(Status.COMPLETED);
        Assertions.assertThat(trainingSession.getFinishedAt()).isNotNull();
        Assertions.assertThat(trainingSession.getDurationMinutes()).isNotNull();

        BDDMockito.then(trainingSessionRepository).should().save(trainingSession);
    }

    @Test
    @DisplayName("finishTrainingSession throws NotFoundException when training session is not found")
    void finishTrainingSession_ThrowsNotFoundException_WhenTrainingSessionIsNotFound() {
        var trainingSession = utils.newTrainingSessionInProgress();

        BDDMockito.when(trainingSessionRepository.findById(trainingSession.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.finishTrainingSession(trainingSession.getId(), trainingSession.getUser()))
                .isInstanceOf(NotFoundException.class);

        BDDMockito.then(trainingSessionRepository).should(Mockito.never()).save(ArgumentMatchers.any(TrainingSession.class));
    }

    @Test
    @DisplayName("finishTrainingSession throws BadRequestException when training session does not belong to user")
    void finishTrainingSession_ThrowsBadRequestException_WhenTrainingSessionDoesNotBelongToUser() {
        var trainingSession = utils.newTrainingSessionInProgress();
        var anotherUser = User.builder().id(99L).build();

        BDDMockito.when(trainingSessionRepository.findById(trainingSession.getId()))
                .thenReturn(Optional.of(trainingSession));

        Assertions.assertThatThrownBy(() -> service.finishTrainingSession(trainingSession.getId(), anotherUser))
                .isInstanceOf(BadRequestException.class);

        BDDMockito.then(trainingSessionRepository).should(Mockito.never()).save(ArgumentMatchers.any(TrainingSession.class));
    }

    @Test
    @DisplayName("finishTrainingSession throws BadRequestException when training session is not in progress")
    void finishTrainingSession_ThrowsBadRequestException_WhenTrainingSessionIsNotInProgress() {
        var trainingSession = utils.newTrainingSession();

        BDDMockito.when(trainingSessionRepository.findById(trainingSession.getId()))
                .thenReturn(Optional.of(trainingSession));

        Assertions.assertThatThrownBy(() -> service.finishTrainingSession(trainingSession.getId(), trainingSession.getUser()))
                .isInstanceOf(BadRequestException.class);

        BDDMockito.then(trainingSessionRepository).should(Mockito.never()).save(ArgumentMatchers.any(TrainingSession.class));
    }

    @Test
    @DisplayName("cancelTrainingSession cancels training session when successful")
    void cancelTrainingSession_CancelsTrainingSession_WhenSuccessful() {
        var trainingSession = utils.newTrainingSessionInProgress();

        BDDMockito.when(trainingSessionRepository.findById(trainingSession.getId()))
                .thenReturn(Optional.of(trainingSession));

        service.cancelTrainingSession(trainingSession.getId(), trainingSession.getUser());

        Assertions.assertThat(trainingSession.getStatus()).isEqualTo(Status.CANCELLED);
        Assertions.assertThat(trainingSession.getFinishedAt()).isNull();
        Assertions.assertThat(trainingSession.getDurationMinutes()).isNull();

        BDDMockito.then(trainingSessionRepository).should().save(trainingSession);
    }

    @Test
    @DisplayName("cancelTrainingSession throws NotFoundException when training session is not found")
    void cancelTrainingSession_ThrowsNotFoundException_WhenTrainingSessionIsNotFound() {
        var trainingSession = utils.newTrainingSessionInProgress();

        BDDMockito.when(trainingSessionRepository.findById(trainingSession.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.cancelTrainingSession(trainingSession.getId(), trainingSession.getUser()))
                .isInstanceOf(NotFoundException.class);

        BDDMockito.then(trainingSessionRepository).should(Mockito.never()).save(ArgumentMatchers.any(TrainingSession.class));
    }

    @Test
    @DisplayName("cancelTrainingSession throws BadRequestException when training session does not belong to user")
    void cancelTrainingSession_ThrowsBadRequestException_WhenTrainingSessionDoesNotBelongToUser() {
        var trainingSession = utils.newTrainingSessionInProgress();
        var anotherUser = User.builder().id(99L).build();

        BDDMockito.when(trainingSessionRepository.findById(trainingSession.getId()))
                .thenReturn(Optional.of(trainingSession));

        Assertions.assertThatThrownBy(() -> service.cancelTrainingSession(trainingSession.getId(), anotherUser))
                .isInstanceOf(BadRequestException.class);

        BDDMockito.then(trainingSessionRepository).should(Mockito.never()).save(ArgumentMatchers.any(TrainingSession.class));
    }

    @Test
    @DisplayName("cancelTrainingSession throws BadRequestException when training session is not in progress")
    void cancelTrainingSession_ThrowsBadRequestException_WhenTrainingSessionIsNotInProgress() {
        var trainingSession = utils.newTrainingSession();

        BDDMockito.when(trainingSessionRepository.findById(trainingSession.getId()))
                .thenReturn(Optional.of(trainingSession));

        Assertions.assertThatThrownBy(() -> service.cancelTrainingSession(trainingSession.getId(), trainingSession.getUser()))
                .isInstanceOf(BadRequestException.class);

        BDDMockito.then(trainingSessionRepository).should(Mockito.never()).save(ArgumentMatchers.any(TrainingSession.class));
    }

    @Test
    @DisplayName("findAll returns a list with all training sessions by user id when successful")
    void findAll_ReturnsListWithAllTrainingSessionsByUserId_WhenSuccessful() {
        var trainingSession = utils.newTrainingSession();
        var trainingSessionListExpected = Collections.singletonList(trainingSession);

        BDDMockito.when(trainingSessionRepository.findAllByUserId(trainingSession.getUser().getId()))
                .thenReturn(trainingSessionListExpected);

        var trainingSessionList = service.findAll(trainingSession.getUser());

        Assertions.assertThat(trainingSessionList).isNotNull().isEqualTo(trainingSessionListExpected).hasSize(1);
    }

    @Test
    @DisplayName("findAll returns a empty list when user does not have training session")
    void findAll_ReturnsEmptyList_WhenUserDoesNotHaveTrainingSession() {
        var user = User.builder().id(99L).build();

        BDDMockito.when(trainingSessionRepository.findAllByUserId(user.getId()))
                .thenReturn(Collections.emptyList());

        var trainingSessionList = service.findAll(user);

        Assertions.assertThat(trainingSessionList).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("findCurrentTrainingSession returns current training session when successful")
    void findCurrentTrainingSession_ReturnsCurrentTrainingSession_WhenSuccessful() {
        var trainingSession = utils.newTrainingSessionInProgress();

        BDDMockito.when(trainingSessionRepository.findByUserIdAndStatus(trainingSession.getUser().getId(), Status.IN_PROGRESS))
                .thenReturn(Optional.of(trainingSession));

        var currentTrainingSession = service.findCurrentTrainingSession(trainingSession.getUser());

        Assertions.assertThat(currentTrainingSession).isEqualTo(trainingSession);
    }

    @Test
    @DisplayName("findCurrentTrainingSession throws NotFoundException when no active training session is found")
    void findCurrentTrainingSession_ThrowsNotFoundException_WhenNoActiveTrainingSessionFound() {
        var trainingSession = utils.newTrainingSession();

        BDDMockito.when(trainingSessionRepository.findByUserIdAndStatus(trainingSession.getUser().getId(), Status.IN_PROGRESS))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findCurrentTrainingSession(trainingSession.getUser()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("findById returns a training session by id and user when successful")
    void findById_ReturnsTrainingSessionByIdAndUser_WhenSuccessful() {
        var trainingSession = utils.newTrainingSessionInProgress();

        BDDMockito.when(trainingSessionRepository.findById(trainingSession.getId()))
                .thenReturn(Optional.of(trainingSession));

        var trainingSessionById = service.findById(trainingSession.getId(), trainingSession.getUser());

        Assertions.assertThat(trainingSessionById).isEqualTo(trainingSession);
    }

    @Test
    @DisplayName("findById throws NotFoundException when training session is not found")
    void findById_ThrowsNotFoundException_WhenTrainingSessionIsNotFound() {
        var trainingSession = utils.newTrainingSessionInProgress();

        BDDMockito.when(trainingSessionRepository.findById(trainingSession.getId()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findById(trainingSession.getId(), trainingSession.getUser()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("findById throws BadRequestException when training session does not belong to user")
    void findById_ThrowsBadRequestException_WhenTrainingSessionDoesNotBelongToUser() {
        var trainingSession = utils.newTrainingSessionInProgress();
        var anotherUser = User.builder().id(99L).build();

        BDDMockito.when(trainingSessionRepository.findById(trainingSession.getId()))
                .thenReturn(Optional.of(trainingSession));

        Assertions.assertThatThrownBy(() -> service.findById(trainingSession.getId(), anotherUser))
                .isInstanceOf(BadRequestException.class);
    }
}