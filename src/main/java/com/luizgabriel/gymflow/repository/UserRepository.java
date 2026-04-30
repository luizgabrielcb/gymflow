package com.luizgabriel.gymflow.repository;

import com.luizgabriel.gymflow.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
