package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.dto.request.LoginRequest;
import com.luizgabriel.gymflow.dto.request.UserPostRequest;
import com.luizgabriel.gymflow.dto.response.LoginResponse;
import com.luizgabriel.gymflow.service.AuthFacade;
import com.luizgabriel.gymflow.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication and registration")
public class AuthController {

    private final AuthService authService;
    private final AuthFacade authFacade;

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Email already exists or invalid data")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserPostRequest request) {
        authService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        var token = authFacade.login(request);

        return ResponseEntity.ok(token);
    }
}


