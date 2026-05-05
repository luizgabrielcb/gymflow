package com.luizgabriel.gymflow.dto.response;

import com.luizgabriel.gymflow.domain.Status;
import com.luizgabriel.gymflow.domain.Workout;

import java.time.Instant;

public record TrainingSessionGetResponse(Long id, Workout workout, Status status, Instant startedAt, Instant finishedAt, Integer duration) {
}
