package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.dto.request.ExercisePostRequest;
import com.luizgabriel.gymflow.dto.request.ExercisePutRequest;
import com.luizgabriel.gymflow.dto.response.ExerciseGetResponse;
import com.luizgabriel.gymflow.dto.response.ExercisePostResponse;
import com.luizgabriel.gymflow.mapper.ExerciseMapper;
import com.luizgabriel.gymflow.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService service;
    private final ExerciseMapper mapper;

    @PostMapping
    public ResponseEntity<ExercisePostResponse> save(@RequestBody @Valid ExercisePostRequest request) {
        var exercise = mapper.toExercise(request);

        var savedExercise = service.save(exercise);

        var exercisePostResponse = mapper.toExercisePostResponse(savedExercise);

        return ResponseEntity.status(HttpStatus.CREATED).body(exercisePostResponse);
    }

    @GetMapping
    public ResponseEntity<List<ExerciseGetResponse>> findAll() {
        var exerciseList = service.findAll();

        var exerciseGetResponseList = mapper.toExerciseGetResponseList(exerciseList);

        return ResponseEntity.ok(exerciseGetResponseList);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid ExercisePutRequest request) {
        service.update(request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}

