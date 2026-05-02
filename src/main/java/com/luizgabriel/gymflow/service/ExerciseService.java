package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.domain.Exercise;
import com.luizgabriel.gymflow.dto.request.ExercisePutRequest;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository repository;

    public Exercise save(Exercise exercise) {
        return repository.save(exercise);
    }

    public List<Exercise> findAll() {
        return repository.findAll();
    }

    public void update(ExercisePutRequest request) {
        var exercise = repository.findById(request.id()).orElseThrow(() ->
                new NotFoundException("Exercise not found with id: " + request.id()));

        exercise.setName(request.name());
        exercise.setMuscleGroup(request.muscleGroup());

        repository.save(exercise);
    }

    public void delete(Long id) {
        var exercise = repository.findById(id).orElseThrow(() ->
                new NotFoundException("Exercise not found with id: " + id));

        repository.deleteById(exercise.getId());
    }
}
