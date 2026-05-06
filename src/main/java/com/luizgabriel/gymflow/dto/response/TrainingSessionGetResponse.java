package com.luizgabriel.gymflow.dto.response;

import com.luizgabriel.gymflow.domain.Status;

import java.time.Instant;
import java.util.List;

public record TrainingSessionGetResponse(Long id,
                                         String workoutName,
                                         Status status,
                                         Instant startedAt,
                                         Instant finishedAt,
                                         Integer durationMinutes,
                                         List<SessionSetGetResponse> sets) {
}
