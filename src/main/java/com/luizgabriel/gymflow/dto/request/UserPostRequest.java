package com.luizgabriel.gymflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserPostRequest {
    @NotBlank(message = "The field 'name' is required")
    private String name;
    @NotBlank(message = "The field 'email' is required")
    private String email;
    @NotBlank(message = "The field 'password' is required")
    private String password;
}
