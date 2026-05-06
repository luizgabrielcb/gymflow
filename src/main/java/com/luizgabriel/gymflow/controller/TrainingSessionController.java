package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.SessionSetPostRequest;
import com.luizgabriel.gymflow.dto.response.SessionSetIdPostResponse;
import com.luizgabriel.gymflow.dto.response.TrainingSessionGetResponse;
import com.luizgabriel.gymflow.dto.response.TrainingSessionIdPostResponse;
import com.luizgabriel.gymflow.mapper.TrainingSessionMapper;
import com.luizgabriel.gymflow.service.TrainingSessionService;
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
public class TrainingSessionController {

    private final TrainingSessionService service;
    private final TrainingSessionMapper mapper;

    @PostMapping
    public ResponseEntity<TrainingSessionIdPostResponse> startSession(@RequestParam Long workoutId, @AuthenticationPrincipal User user) {
        var trainingSessionStarted = service.startSession(workoutId, user);

        var trainingSessionIdPostResponse = mapper.toTrainingSessionIdPostResponse(trainingSessionStarted);

        return ResponseEntity.status(HttpStatus.CREATED).body(trainingSessionIdPostResponse);
    }

    @PostMapping("{id}/sets")
    public ResponseEntity<SessionSetIdPostResponse> addSet(@PathVariable Long id,
                                                           @Valid @RequestBody SessionSetPostRequest request,
                                                           @AuthenticationPrincipal User user) {
        var sessionSetSaved = service.addSet(id, request, user);

        var sessionSetIdPostResponse = mapper.toSessionSetIdPostResponse(sessionSetSaved);

        return ResponseEntity.status(HttpStatus.CREATED).body(sessionSetIdPostResponse);
    }

    @PatchMapping({"{id}/finish"})
    public ResponseEntity<Void> finishTrainingSession(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.finishTrainingSession(id, user);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}/cancel")
    public ResponseEntity<Void> cancelTrainingSession(@PathVariable Long id, @AuthenticationPrincipal User user) {
        service.cancelTrainingSession(id, user);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TrainingSessionGetResponse>> findAll(@AuthenticationPrincipal User user) {
        var trainingSessionList = service.findAll(user);

        var trainingSessionGetResponseList = mapper.toTrainingSessionGetResponseList(trainingSessionList);

        return ResponseEntity.ok(trainingSessionGetResponseList);
    }

    @GetMapping("current")
    public ResponseEntity<TrainingSessionGetResponse> findCurrentTrainingSession(@AuthenticationPrincipal User user) {
        var currentTrainingSession = service.findCurrentTrainingSession(user);

        var trainingSessionGetResponse = mapper.toTrainingSessionGetResponse(currentTrainingSession);

        return ResponseEntity.ok(trainingSessionGetResponse);
    }

    @GetMapping("{id}")
    public ResponseEntity<TrainingSessionGetResponse> findById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        var trainingSession = service.findById(id, user);

        var trainingSessionGetResponse = mapper.toTrainingSessionGetResponse(trainingSession);

        return ResponseEntity.ok(trainingSessionGetResponse);
    }
}
