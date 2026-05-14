package com.luizgabriel.gymflow.service;

import com.luizgabriel.gymflow.commons.UserUtils;
import com.luizgabriel.gymflow.domain.User;
import com.luizgabriel.gymflow.exception.NotFoundException;
import com.luizgabriel.gymflow.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService service;

    @InjectMocks
    private UserUtils utils;

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("findAll returns a page with all users when successful")
    void findAll_ReturnsPageWithAllUsers_WhenSuccessful() {
        var user = utils.newUser();

        var userPage = new PageImpl<>(Collections.singletonList(user));

        var pageable = PageRequest.of(0, 1);

        BDDMockito.when(repository.findAll(pageable)).thenReturn(userPage);

        var userList = service.findAll(pageable);

        Assertions.assertThat(userList).isNotNull().isEqualTo(userPage);
    }

    @Test
    @DisplayName("findAll returns a empty page when user not found")
    void findAll_ReturnsEmptyPage_WhenUserNotFound() {
        var pageable = PageRequest.of(0, 1);

        Page<User> emptyPage = Page.empty();

        BDDMockito.when(repository.findAll(pageable)).thenReturn(emptyPage);

        var userList = service.findAll(pageable);

        Assertions.assertThat(userList).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("findById returns user when successful")
    void findById_ReturnsUser_WhenSuccessful() {
        var user = utils.newUser();

        BDDMockito.when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        var userById = service.findById(user.getId());

        Assertions.assertThat(userById).isNotNull().isEqualTo(user);
    }

    @Test
    @DisplayName("findById throws NotFoundException when user not found")
    void findById_ThrowsNotFoundException_WhenUserNotFound() {
        var user = utils.newUser();

        BDDMockito.when(repository.findById(user.getId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.findById(user.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("update saves updated user when successful")
    void update_SavesUpdatedUser_WhenSuccessful() {
        var user = utils.newUser();
        var userPutRequest = utils.newUserPutRequest();

        BDDMockito.when(passwordEncoder.encode(ArgumentMatchers.any())).thenReturn("encodedPassword");

        service.update(user, userPutRequest);

        BDDMockito.then(repository).should().save(ArgumentMatchers.any(User.class));
    }

    @Test
    @DisplayName("delete removes user when successful")
    void delete_RemovesUser_WhenSuccessful() {
        var user = utils.newUser();

        BDDMockito.when(repository.findById(user.getId())).thenReturn(Optional.of(user));

        service.delete(user.getId());

        BDDMockito.then(repository).should().delete(ArgumentMatchers.any(User.class));
    }

    @Test
    @DisplayName("delete throws NotFoundException when user not found")
    void delete_ThrowsNotFoundException_WhenUserNotFound() {
        var user = utils.newUser();

        BDDMockito.when(repository.findById(user.getId())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.delete(user.getId()))
                .isInstanceOf(NotFoundException.class);

        BDDMockito.then(repository).should(Mockito.never()).delete(ArgumentMatchers.any(User.class));
    }
}