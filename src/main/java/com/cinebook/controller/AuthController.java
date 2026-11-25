package com.cinebook.controller;

import com.cinebook.model.Role;
import com.cinebook.model.User;
import com.cinebook.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public String register(@RequestBody User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already registered";
        }

        User newUser = new User();

        newUser.setName(user.getName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRole(Role.CUSTOMER);

        userRepository.save(newUser);

        return "Registration successful";
    }
}