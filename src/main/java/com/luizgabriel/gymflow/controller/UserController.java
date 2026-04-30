package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public ResponseEntity<List<User>> findAllUsers() {
        var userList = service.findAll();

        return ResponseEntity.ok(userList);
    }

    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        var userSaved = service.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userSaved);
    }
}
