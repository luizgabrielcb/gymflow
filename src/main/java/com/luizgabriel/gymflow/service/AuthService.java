package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.domain.UserRole;
import com.luizgabriel.gymflow.dto.request.UserPostRequest;
import com.luizgabriel.gymflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repository.findByEmail(email);
    }

    public void register(UserPostRequest request) {
        if (repository.findByEmail(request.getEmail()) != null) throw new RuntimeException("Email already exists");

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();

        repository.save(user);
    }
}
