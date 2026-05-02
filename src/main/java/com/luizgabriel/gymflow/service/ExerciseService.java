package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.domain.Exercise;
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
}
