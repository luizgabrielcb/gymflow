package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.domain.RefreshToken;
import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.LoginRequest;
import com.luizgabriel.gymflow.dto.request.RefreshTokenRequest;
import com.luizgabriel.gymflow.dto.response.LoginResponse;
import com.luizgabriel.gymflow.exception.UnauthorizedException;
import com.luizgabriel.gymflow.repository.RefreshTokenRepository;
import com.luizgabriel.gymflow.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponse login(LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var authentication = authenticationManager.authenticate(authToken);
        var user = (User) authentication.getPrincipal();
        var accessToken = tokenService.generateToken(user);

        var rawRefreshToken = tokenService.generateRefreshToken();
        refreshTokenRepository.save(new RefreshToken(rawRefreshToken, user, LocalDateTime.now().plusDays(7)));

        return new LoginResponse(accessToken, rawRefreshToken);
    }

    public LoginResponse refresh(RefreshTokenRequest request) {
        var refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (!refreshToken.isValid()) {
            throw new UnauthorizedException("Refresh token expired or revoked");
        }

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);

        var user = refreshToken.getUser();
        var newAccessToken = tokenService.generateToken(user);
        var newRawRefreshToken = tokenService.generateRefreshToken();

        refreshTokenRepository.save(new RefreshToken(newRawRefreshToken, user, LocalDateTime.now().plusDays(7)));

        return new LoginResponse(newAccessToken, newRawRefreshToken);
    }

    public void logout(RefreshTokenRequest request) {
        var refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        refreshToken.revoke();

        refreshTokenRepository.save(refreshToken);
    }
}
