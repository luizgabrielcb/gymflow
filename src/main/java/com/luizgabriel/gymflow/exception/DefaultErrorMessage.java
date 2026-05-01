package com.luizgabriel.gymflow.exception;

import lombok.Builder;

@Builder
public record DefaultErrorMessage(String message, int status) {
}
