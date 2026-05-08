package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.commons.AuthServiceUtils;
import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.exception.BadRequestException;
import com.luizgabriel.gymflow.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService service;

    @InjectMocks
    private AuthServiceUtils utils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("loadUserByUsername returns user details when successful")
    void loadUserByUsername_ReturnsUserDetails_WhenSuccessful() {
        var user = utils.newUser();
        var email = "test@gmail.com";

        BDDMockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        var userDetails = service.loadUserByUsername(email);

        Assertions.assertThat(userDetails).isNotNull().isEqualTo(user);
    }

    @Test
    @DisplayName("loadUserByUsername throws UsernameNotFoundException when email not found")
    void loadUserByUsername_ThrowsUsernameNotFoundException_WhenEmailNotFound() {
        var email = "test@gmail.com";

        BDDMockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.loadUserByUsername(email))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    @DisplayName("register saves user when successful")
    void register_SavesUser_WhenSuccessful() {
        var userPostRequest = utils.newUserPostRequest();

        BDDMockito.when(userRepository.findByEmail(userPostRequest.email()))
                .thenReturn(Optional.empty());

        BDDMockito.when(passwordEncoder.encode(ArgumentMatchers.any())).thenReturn("encodedPassword");

        service.register(userPostRequest);

        BDDMockito.then(userRepository).should().save(ArgumentMatchers.any(User.class));
    }

    @Test
    @DisplayName("register throws BadRequestException when email already exists")
    void register_ThrowsBadRequestException_WhenEmailAlreadyExists() {
        var user = utils.newUser();
        var userPostRequest = utils.newUserPostRequest();

        BDDMockito.when(userRepository.findByEmail(userPostRequest.email()))
                .thenReturn(Optional.of(user));

        Assertions.assertThatThrownBy(() -> service.register(userPostRequest))
                .isInstanceOf(BadRequestException.class);

        BDDMockito.then(userRepository).should(Mockito.never()).save(ArgumentMatchers.any(User.class));
    }
}