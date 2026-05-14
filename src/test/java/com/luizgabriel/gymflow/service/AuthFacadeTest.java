package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.commons.AuthFacadeUtils;
import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.exception.UnauthorizedException;
import com.luizgabriel.gymflow.repository.RefreshTokenRepository;
import com.luizgabriel.gymflow.security.TokenService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthFacadeTest {

    @InjectMocks
    private AuthFacade service;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private AuthFacadeUtils utils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Test
    @DisplayName("login returns login response when successful")
    void login_ReturnsLoginResponse_WhenSuccessful() {
        var loginRequest = utils.newLoginRequest();
        var user = utils.newUser();
        var token = "generated-token";

        BDDMockito.when(authenticationManager.authenticate(
                        ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));

        BDDMockito.when(tokenService.generateToken(user)).thenReturn(token);

        var loginResponse = service.login(loginRequest);

        Assertions.assertThat(loginResponse).isNotNull();
        Assertions.assertThat(loginResponse.token()).isEqualTo(token);
    }

    @Test
    @DisplayName("login throws BadCredentialsException when credentials are invalid")
    void login_ThrowsBadCredentialsException_WhenCredentialsAreInvalid() {
        var loginRequest = utils.newLoginRequest();

        BDDMockito.when(authenticationManager.authenticate(
                        ArgumentMatchers.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        Assertions.assertThatThrownBy(() -> service.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        BDDMockito.then(tokenService).should(Mockito.never()).generateToken(ArgumentMatchers.any(User.class));
    }

    @Test
    @DisplayName("refresh returns login response when successful")
    void refresh_ReturnsLoginResponse_WhenSuccessful() {
        var user = utils.newUser();
        var refreshToken = utils.newRefreshToken(user);
        var request = utils.newRefreshTokenRequest();
        var newAccessToken = "new-access-token";
        var newRefreshToken = "new-refresh-token";

        BDDMockito.when(refreshTokenRepository.findByToken(request.refreshToken()))
                .thenReturn(Optional.of(refreshToken));

        BDDMockito.when(tokenService.generateToken(user)).thenReturn(newAccessToken);
        BDDMockito.when(tokenService.generateRefreshToken()).thenReturn(newRefreshToken);

        var response = service.refresh(request);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.token()).isEqualTo(newAccessToken);
        Assertions.assertThat(response.refreshToken()).isEqualTo(newRefreshToken);
        Assertions.assertThat(refreshToken.isRevoked()).isTrue();
    }

    @Test
    @DisplayName("refresh throws UnauthorizedException when refresh token is invalid")
    void refresh_ThrowsUnauthorizedException_WhenRefreshTokenNotFound() {
        var request = utils.newRefreshTokenRequest();

        BDDMockito.when(refreshTokenRepository.findByToken(request.refreshToken()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.refresh(request))
                .isInstanceOf(UnauthorizedException.class);

        BDDMockito.then(tokenService).should(Mockito.never()).generateToken(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("refresh throws UnauthorizedException when refresh token is expired")
    void refresh_ThrowsUnauthorizedException_WhenRefreshTokenIsExpired() {
        var user = utils.newUser();
        var expiredRefreshToken = utils.newExpiredRefreshToken(user);
        var request = utils.newRefreshTokenRequest();

        BDDMockito.when(refreshTokenRepository.findByToken(request.refreshToken()))
                .thenReturn(Optional.of(expiredRefreshToken));

        Assertions.assertThatThrownBy(() -> service.refresh(request))
                .isInstanceOf(UnauthorizedException.class);

        BDDMockito.then(tokenService).should(Mockito.never()).generateToken(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("refresh throws UnauthorizedException when refresh token is revoked")
    void refresh_ThrowsUnauthorizedException_WhenRefreshTokenIsRevoked() {
        var user = utils.newUser();
        var revokedRefreshToken = utils.newRevokedRefreshToken(user);
        var request = utils.newRefreshTokenRequest();

        BDDMockito.when(refreshTokenRepository.findByToken(request.refreshToken()))
                .thenReturn(Optional.of(revokedRefreshToken));

        Assertions.assertThatThrownBy(() -> service.refresh(request))
                .isInstanceOf(UnauthorizedException.class);

        BDDMockito.then(tokenService).should(Mockito.never()).generateToken(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("logout revokes refresh token when successful")
    void logout_RevokesRefreshToken_WhenSuccessful() {
        var user = utils.newUser();
        var refreshToken = utils.newRefreshToken(user);
        var request = utils.newRefreshTokenRequest();

        BDDMockito.when(refreshTokenRepository.findByToken(request.refreshToken()))
                .thenReturn(Optional.of(refreshToken));

        service.logout(request);

        Assertions.assertThat(refreshToken.isRevoked()).isTrue();
        BDDMockito.then(refreshTokenRepository).should().save(refreshToken);
    }

    @Test
    @DisplayName("logout throws UnauthorizedException when refresh token is invalid")
    void logout_ThrowsUnauthorizedException_WhenRefreshTokenNotFound() {
        var request = utils.newRefreshTokenRequest();

        BDDMockito.when(refreshTokenRepository.findByToken(request.refreshToken()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.logout(request))
                .isInstanceOf(UnauthorizedException.class);

        BDDMockito.then(refreshTokenRepository).should(Mockito.never()).save(ArgumentMatchers.any());
    }
}