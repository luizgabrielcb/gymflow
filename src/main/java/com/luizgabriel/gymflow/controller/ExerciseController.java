package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.dto.request.ExercisePostRequest;
import com.luizgabriel.gymflow.dto.request.ExercisePutRequest;
import com.luizgabriel.gymflow.dto.response.ExerciseGetResponse;
import com.luizgabriel.gymflow.dto.response.ExercisePostResponse;
import com.luizgabriel.gymflow.mapper.ExerciseMapper;
import com.luizgabriel.gymflow.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercises", description = "Exercise management")
public class ExerciseController {

    private final ExerciseService service;
    private final ExerciseMapper mapper;

    @Operation(summary = "Create a new exercise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Exercise created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    public ResponseEntity<ExercisePostResponse> save(@RequestBody @Valid ExercisePostRequest request) {
        var exercise = mapper.toExercise(request);

        var savedExercise = service.save(exercise);

        var exercisePostResponse = mapper.toExercisePostResponse(savedExercise);

        return ResponseEntity.status(HttpStatus.CREATED).body(exercisePostResponse);
    }

    @Operation(summary = "Get all exercises")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercises retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<Page<ExerciseGetResponse>> findAll(@PageableDefault(sort = "name", direction = Sort.Direction.ASC)
                                                             Pageable pageable) {
        var exercisesPage = service.findAll(pageable);

        var exerciseGetResponsePage = exercisesPage.map(mapper::toExerciseGetResponse);

        return ResponseEntity.ok(exerciseGetResponsePage);
    }

    @Operation(summary = "Get exercise by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exercise retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Exercise not found")
    })
    @GetMapping("{id}")
    public ResponseEntity<ExerciseGetResponse> findById(@PathVariable Long id) {
        var exercise = service.findById(id);

        var exerciseGetResponse = mapper.toExerciseGetResponse(exercise);

        return ResponseEntity.ok(exerciseGetResponse);
    }

    @Operation(summary = "Update an exercise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Exercise updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Exercise not found")
    })
    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid ExercisePutRequest request) {
        service.update(request);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete an exercise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Exercise deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Exercise not found")
    })
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}

