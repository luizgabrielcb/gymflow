package com.luizgabriel.gymflow.commons;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.LoginRequest;
import org.springframework.stereotype.Component;

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
}
