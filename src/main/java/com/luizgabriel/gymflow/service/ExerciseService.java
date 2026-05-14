package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.domain.Exercise;
import com.luizgabriel.gymflow.dto.request.ExercisePutRequest;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository repository;

    public Exercise save(Exercise exercise) {
        return repository.save(exercise);
    }

    public Page<Exercise> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Exercise findById(Long id) {
        return findExerciseOrThrowNotFound(id);
    }

    public void update(ExercisePutRequest request) {
        var exercise = findExerciseOrThrowNotFound(request.id());

        exercise.setName(request.name());
        exercise.setMuscleGroup(request.muscleGroup());

        repository.save(exercise);
    }

    public void delete(Long id) {
        var exercise = findExerciseOrThrowNotFound(id);

        repository.delete(exercise);
    }

    private Exercise findExerciseOrThrowNotFound(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new NotFoundException("Exercise with id " + id + " not found"));
    }
}
