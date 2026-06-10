package com.sda.project.controller;

import com.sda.project.dto.JwtResponse;
import com.sda.project.dto.LoginRequest;
import com.sda.project.dto.RegisterRequest;
import com.sda.project.entity.User;
import com.sda.project.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        User user = authService.getUserByEmail(request.getEmail());
        return ResponseEntity.ok(new JwtResponse(token, "Bearer", user.getUserId(), user.getEmail(), user.getRole()));
    }
}