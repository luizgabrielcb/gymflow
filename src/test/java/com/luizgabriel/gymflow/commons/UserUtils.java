package com.luizgabriel.gymflow.commons;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.UserPutRequest;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {

    public User newUser() {
        return User.builder()
                .id(1L)
                .name("test")
                .email("test@gmail.com")
                .password("test")
                .build();
    }

    public UserPutRequest newUserPutRequest() {
        var user = newUser();

        return UserPutRequest.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }
}
