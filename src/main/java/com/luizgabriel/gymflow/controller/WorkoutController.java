package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.WorkoutPostRequest;
import com.luizgabriel.gymflow.dto.request.WorkoutPutRequest;
import com.luizgabriel.gymflow.dto.response.WorkoutGetResponse;
import com.luizgabriel.gymflow.dto.response.WorkoutPostResponse;
import com.luizgabriel.gymflow.mapper.WorkoutMapper;
import com.luizgabriel.gymflow.service.WorkoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService service;
    private final WorkoutMapper mapper;

    @PostMapping
    public ResponseEntity<WorkoutPostResponse> save(@RequestBody @Valid WorkoutPostRequest request,
                                                    @AuthenticationPrincipal User user) {
        var savedWorkout = service.save(request, user);

        var workoutPostResponse = mapper.toWorkoutPostResponse(savedWorkout);

        return ResponseEntity.status(HttpStatus.CREATED).body(workoutPostResponse);
    }

    @GetMapping
    public ResponseEntity<List<WorkoutGetResponse>> findAll(@AuthenticationPrincipal User user) {
        var workoutList = service.findAll(user);

        var workoutGetResponseList = mapper.toWorkoutGetResponseList(workoutList);

        return ResponseEntity.status(HttpStatus.OK).body(workoutGetResponseList);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid WorkoutPutRequest request, @AuthenticationPrincipal User user) {
        service.update(request, user);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.delete(id, user);

        return ResponseEntity.noContent().build();
    }
}
