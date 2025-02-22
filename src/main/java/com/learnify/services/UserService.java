package com.learnify.services;

import com.learnify.models.User;
import com.learnify.models.Role;
import com.learnify.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Collections;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash password
        // In registerUser method:
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        return userRepository.save(user);
    }

    public User updateProfile(String userEmail, String bio, String userProfileImage, String name, String password) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Update bio if provided
        if (bio != null) {
            user.setBio(bio);
        }

        // Update profile image if provided
        if (userProfileImage != null) {
            user.setUserProfileImage(userProfileImage);
        }

        // Update name if provided
        if (name != null) {
            user.setName(name);
        }

        // Update password if provided
        if (password != null) {
            user.setPassword(passwordEncoder.encode(password));
        }

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
