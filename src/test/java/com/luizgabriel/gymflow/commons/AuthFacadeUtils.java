package com.luizgabriel.gymflow.commons;

import com.luizgabriel.gymflow.domain.RefreshToken;
import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.LoginRequest;
import com.luizgabriel.gymflow.dto.request.RefreshTokenRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuthFacadeUtils {

    public User newUser() {
        return User.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .password("test")
                .build();
    }

    public LoginRequest newLoginRequest() {
        var user = newUser();

        return LoginRequest.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    public RefreshToken newRefreshToken(User user) {
        return new RefreshToken("valid-refresh-token", user, LocalDateTime.now().plusDays(7));
    }

    public RefreshToken newExpiredRefreshToken(User user) {
        return new RefreshToken("expired-refresh-token", user, LocalDateTime.now().minusDays(1));
    }

    public RefreshToken newRevokedRefreshToken(User user) {
        var refreshToken = new RefreshToken("revoked-refresh-token", user, LocalDateTime.now().plusDays(7));
        refreshToken.revoke();
        return refreshToken;
    }

    public RefreshTokenRequest newRefreshTokenRequest() {
        return new RefreshTokenRequest("valid-refresh-token");
    }
}
