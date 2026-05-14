package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.SessionSetPostRequest;
import com.luizgabriel.gymflow.dto.response.SessionSetIdPostResponse;
import com.luizgabriel.gymflow.dto.response.TrainingSessionGetResponse;
import com.luizgabriel.gymflow.dto.response.TrainingSessionIdPostResponse;
import com.luizgabriel.gymflow.mapper.TrainingSessionMapper;
import com.luizgabriel.gymflow.service.TrainingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/training-sessions")
@RequiredArgsConstructor
@Tag(name = "Training Sessions", description = "Training session management")
public class TrainingSessionController {

    private final TrainingSessionService service;
    private final TrainingSessionMapper mapper;

    @Operation(summary = "Start a new training session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Training session started successfully"),
            @ApiResponse(responseCode = "400", description = "Active session already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Workout does not belong to authenticated user"),
            @ApiResponse(responseCode = "404", description = "Workout not found")
    })
    @PostMapping
    public ResponseEntity<TrainingSessionIdPostResponse> startSession(@RequestParam Long workoutId, @AuthenticationPrincipal User user) {
        var trainingSessionStarted = service.startSession(workoutId, user);

        var trainingSessionIdPostResponse = mapper.toTrainingSessionIdPostResponse(trainingSessionStarted);

        return ResponseEntity.status(HttpStatus.CREATED).body(trainingSessionIdPostResponse);
    }

    @Operation(summary = "Add a set to a training session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Set added successfully"),
            @ApiResponse(responseCode = "400", description = "Session not in progress, duplicate set, or exercise not part of workout"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Session does not belong to authenticated user"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @PostMapping("{id}/sets")
    public ResponseEntity<SessionSetIdPostResponse> addSet(@PathVariable Long id,
                                                           @Valid @RequestBody SessionSetPostRequest request,
                                                           @AuthenticationPrincipal User user) {
        var sessionSetSaved = service.addSet(id, request, user);

        var sessionSetIdPostResponse = mapper.toSessionSetIdPostResponse(sessionSetSaved);

        return ResponseEntity.status(HttpStatus.CREATED).body(sessionSetIdPostResponse);
    }

    @Operation(summary = "Finish a training session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Training session finished successfully"),
            @ApiResponse(responseCode = "400", description = "Session not in progress"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Session does not belong to authenticated user"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @PatchMapping({"{id}/finish"})
    public ResponseEntity<Void> finishTrainingSession(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.finishTrainingSession(id, user);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cancel a training session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Training session cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Session not in progress"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Session does not belong to authenticated user"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @PatchMapping("{id}/cancel")
    public ResponseEntity<Void> cancelTrainingSession(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.cancelTrainingSession(id, user);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all training sessions from authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training sessions retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<TrainingSessionGetResponse>> findAll(@AuthenticationPrincipal User user) {
        var trainingSessionList = service.findAll(user);

        var trainingSessionGetResponseList = mapper.toTrainingSessionGetResponseList(trainingSessionList);

        return ResponseEntity.ok(trainingSessionGetResponseList);
    }

    @Operation(summary = "Get current active training session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current session retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "No active session found")
    })
    @GetMapping("current")
    public ResponseEntity<TrainingSessionGetResponse> findCurrentTrainingSession(@AuthenticationPrincipal User user) {
        var currentTrainingSession = service.findCurrentTrainingSession(user);

        var trainingSessionGetResponse = mapper.toTrainingSessionGetResponse(currentTrainingSession);

        return ResponseEntity.ok(trainingSessionGetResponse);
    }

    @Operation(summary = "Get training session by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training session retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Session does not belong to authenticated user"),
            @ApiResponse(responseCode = "404", description = "Session not found")
    })
    @GetMapping("{id}")
    public ResponseEntity<TrainingSessionGetResponse> findById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        var trainingSession = service.findById(id, user);

        var trainingSessionGetResponse = mapper.toTrainingSessionGetResponse(trainingSession);

        return ResponseEntity.ok(trainingSessionGetResponse);
    }
}
