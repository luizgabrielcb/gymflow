package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ExercisePutRequest(@NotNull(message = "The field 'id' is required") Long id,
                                 @NotBlank(message = "The field 'name' is required") String name,
                                 @NotBlank(message = "The field 'muscleGroup' is required") String muscleGroup) {
}
