package com.learnify.controllers;

import com.learnify.models.LoginRequest;
import com.learnify.models.User;
import com.learnify.services.AuthService;
import com.learnify.services.UserService;
import com.learnify.utils.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService, UserService userService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

        if (user == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtils.generateToken(user);
        return ResponseEntity.ok(token);
    }
}