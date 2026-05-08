package com.luizgabriel.gymflow.commons;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.UserPostRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceUtils {

    public User newUser() {
        return User.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .password("test")
                .build();
    }

    public UserPostRequest newUserPostRequest() {
        var user = newUser();

        return UserPostRequest.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
