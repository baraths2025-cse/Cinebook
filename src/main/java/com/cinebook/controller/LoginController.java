
package com.cinebook.controller;

import com.cinebook.dto.LoginRequest;
import com.cinebook.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public LoginController(
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository) {

        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                        )
                    );

            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            securityContextRepository.saveContext(
                    context, httpRequest, httpResponse);

            User user = (User) authentication.getPrincipal();

            return ResponseEntity.ok(
                    "Login successful. Role: " + user.getRole());

        } catch (Exception e) {
            SecurityContextHolder.clearContext();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof User)) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please login");
        }

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                Map.of(
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "role", user.getRole().name()
                )
        );
    }
}
