package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(@NotBlank(message = "The field 'refreshToken' is required") String refreshToken) {
}
