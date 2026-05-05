package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.dto.request.UserPutRequest;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return findUserByIdOrThrowNotFound(id);
    }

    public void delete(Long id) {
        var userToDelete = findUserByIdOrThrowNotFound(id);

        repository.delete(userToDelete);
    }

    public void update(User authenticatedUser, UserPutRequest request) {
        authenticatedUser.setName(request.name());
        authenticatedUser.setEmail(request.email());
        authenticatedUser.setPassword(passwordEncoder.encode(request.password()));

        repository.save(authenticatedUser);
    }

    private User findUserByIdOrThrowNotFound(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
    }
}
