package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserPutRequest(@NotBlank(message = "The field 'name' is required") String name,
                             @NotBlank(message = "The field 'email' is required") String email,
                             @NotBlank(message = "The field 'password' is required") String password) {
}
