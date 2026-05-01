package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.LoginRequest;
import com.luizgabriel.gymflow.dto.response.LoginResponse;
import com.luizgabriel.gymflow.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public LoginResponse login(LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var authentication = authenticationManager.authenticate(authToken);
        var user = (User) authentication.getPrincipal();
        var token = tokenService.generateToken(user);
        return new LoginResponse(token);
    }
}
