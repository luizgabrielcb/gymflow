package com.luizgabriel.gymflow.controller;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.UserPutRequest;
import com.luizgabriel.gymflow.dto.response.UserGetResponse;
import com.luizgabriel.gymflow.mapper.UserMapper;
import com.luizgabriel.gymflow.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("me")
    public ResponseEntity<UserGetResponse> findMe(@AuthenticationPrincipal User user) {
        var myUser = service.findById(user.getId());

        var userGetResponse = mapper.toUserGetResponse(myUser);

        return ResponseEntity.ok(userGetResponse);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody @Valid UserPutRequest request, @AuthenticationPrincipal User user) {
        service.update(user, request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal User user) {
        service.delete(user.getId());

        return ResponseEntity.noContent().build();
    }

}
