package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.commons.AuthFacadeUtils;
import com.luizgabriel.gymflow.domain.User;
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

@ExtendWith(MockitoExtension.class)
class AuthFacadeTest {

    @InjectMocks
    private AuthFacade service;

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
}