package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.dto.response.UserGetResponse;
import com.luizgabriel.gymflow.mapper.UserMapper;
import com.luizgabriel.gymflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final UserMapper mapper;

    @GetMapping
    public ResponseEntity<List<UserGetResponse>> findAllUsers() {
        var userList = service.findAll();

        var userGetResponseList = mapper.toUserGetResponseList(userList);

        return ResponseEntity.ok(userGetResponseList);
    }
}
