package com.onenotebe.service;

import com.onenotebe.dto.auth.RegisterRequest;
import com.onenotebe.dto.auth.RegisterResponse;
import com.onenotebe.exception.DuplicateEmailException;
import com.onenotebe.exception.DuplicateUsernameException;
import com.onenotebe.model.Role;
import com.onenotebe.model.User;
import com.onenotebe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Authentication-related business logic. Handles secure user registration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user with ROLE_USER.
     * Uses input sanitization and enforces username/email uniqueness.
     */
    public RegisterResponse register(RegisterRequest request) {
        var username = sanitizeUsername(request.username());
        var email = sanitizeEmail(request.email());
        var passwordRaw = request.password();

        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUsernameException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email already exists");
        }

        var hashed = passwordEncoder.encode(passwordRaw);
        var user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(hashed);
        user.setRole(Role.ROLE_USER);

        var saved = userRepository.save(user);
        log.info("User registered [username={}]", saved.getUsername());
        return new RegisterResponse(saved.getId(), saved.getUsername(), saved.getEmail());
    }

    private String sanitizeUsername(String input) {
        if (input == null) {
            return "";
        }
        return input.trim();
    }

    private String sanitizeEmail(String input) {
        if (input == null) {
            return "";
        }
        return input.trim().toLowerCase();
    }
}