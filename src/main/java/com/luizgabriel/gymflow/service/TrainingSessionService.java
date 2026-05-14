package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.domain.*;
import com.luizgabriel.gymflow.dto.request.SessionSetPostRequest;
import com.luizgabriel.gymflow.exception.BadRequestException;
import com.luizgabriel.gymflow.exception.ForbiddenException;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.SessionSetRepository;
import com.luizgabriel.gymflow.repository.TrainingSessionRepository;
import com.luizgabriel.gymflow.repository.WorkoutExerciseRepository;
import com.luizgabriel.gymflow.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TrainingSessionService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final WorkoutRepository workoutRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final SessionSetRepository sessionSetRepository;


    public TrainingSession startSession(Long workoutId, User user) {
        validateNoActiveSession(user);

        var workout = findWorkoutOrThrowNotFound(workoutId);

        validateWorkoutOwnership(workout, user);

        var trainingSession = TrainingSession.builder()
                .user(user)
                .workout(workout)
                .status(Status.IN_PROGRESS)
                .build();

        return trainingSessionRepository.save(trainingSession);
    }

    public SessionSet addSet(Long trainingSessionId, SessionSetPostRequest request, User user) {
        var trainingSession = findTrainingSessionOrThrowNotFound(trainingSessionId);

        validateTrainingSessionOwnership(trainingSession, user);

        validateTrainingSessionInProgress(trainingSession);

        var workoutExercise = findWorkoutExerciseOrThrow(request.exerciseId(), trainingSession.getWorkout().getId());

        validateSetUniqueness(trainingSession, workoutExercise.getExercise(), request.setNumber());

        var sessionSet = SessionSet.builder()
                .trainingSession(trainingSession)
                .exercise(workoutExercise.getExercise())
                .setNumber(request.setNumber())
                .repsNumber(request.repsNumber())
                .weightKg(request.weightKg())
                .restSeconds(request.restSeconds())
                .build();

        return sessionSetRepository.save(sessionSet);
    }

    public void finishTrainingSession(Long trainingSessionId, User user) {
        var trainingSession = findTrainingSessionOrThrowNotFound(trainingSessionId);

        validateTrainingSessionOwnership(trainingSession, user);

        validateTrainingSessionInProgress(trainingSession);

        trainingSession.setStatus(Status.COMPLETED);

        var finishedAt = Instant.now();

        trainingSession.setFinishedAt(finishedAt);

        trainingSession.setDurationMinutes((int) Duration.between(trainingSession.getStartedAt(), finishedAt).toMinutes());

        trainingSessionRepository.save(trainingSession);
    }

    public void cancelTrainingSession(Long trainingSessionId, User user) {
        var trainingSession = findTrainingSessionOrThrowNotFound(trainingSessionId);

        validateTrainingSessionOwnership(trainingSession, user);

        validateTrainingSessionInProgress(trainingSession);

        trainingSession.setStatus(Status.CANCELLED);

        trainingSessionRepository.save(trainingSession);
    }

    public Page<TrainingSession> findAll(Pageable pageable, User user) {
        return trainingSessionRepository.findAllByUserId(pageable, user.getId());
    }

    public TrainingSession findCurrentTrainingSession(User user) {
        return trainingSessionRepository.findByUserIdAndStatus(user.getId(), Status.IN_PROGRESS).orElseThrow(() ->
                new NotFoundException("No active training session found"));
    }

    public TrainingSession findById(Long trainingSessionId, User user) {
        var trainingSession = findTrainingSessionOrThrowNotFound(trainingSessionId);

        validateTrainingSessionOwnership(trainingSession, user);

        return trainingSession;
    }

    private Workout findWorkoutOrThrowNotFound(Long workoutId) {
        return workoutRepository.findById(workoutId)
                .orElseThrow(() -> new NotFoundException("Workout with id " + workoutId + " not found"));
    }

    private TrainingSession findTrainingSessionOrThrowNotFound(Long id) {
        return trainingSessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Training session with id " + id + " not found"));
    }

    private WorkoutExercise findWorkoutExerciseOrThrow(Long exerciseId, Long workoutId) {
        return workoutExerciseRepository.findByExerciseIdAndWorkoutId(exerciseId, workoutId)
                .orElseThrow(() -> new BadRequestException("Exercise with id " + exerciseId + " is not part of this workout"));
    }

    private void validateNoActiveSession(User user) {
        if (trainingSessionRepository.existsByUserIdAndStatus(user.getId(), Status.IN_PROGRESS)) {
            throw new BadRequestException("You already have an active training session");
        }
    }

    private void validateWorkoutOwnership(Workout workout, User user) {
        if (!workout.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have permission to access this workout");
        }
    }

    private void validateTrainingSessionOwnership(TrainingSession trainingSession, User user) {
        if (!trainingSession.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You do not have permission to access this training session");
        }
    }

    private void validateTrainingSessionInProgress(TrainingSession trainingSession) {
        if (trainingSession.getStatus() != Status.IN_PROGRESS) {
            throw new BadRequestException("Cannot modify a training session that is not in progress");
        }
    }

    private void validateSetUniqueness(TrainingSession trainingSession, Exercise exercise, Integer setNumber) {
        if (sessionSetRepository.existsByTrainingSessionIdAndExerciseIdAndSetNumber(
                trainingSession.getId(),
                exercise.getId(),
                setNumber
        )) {
            throw new BadRequestException("Set number already exists for this exercise in this training session");
        }
    }
}
