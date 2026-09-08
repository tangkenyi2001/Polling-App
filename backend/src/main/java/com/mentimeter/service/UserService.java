package com.mentimeter.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mentimeter.entity.User;
import com.mentimeter.exception.DuplicateEmailException;
import com.mentimeter.exception.ResourceNotFoundException;
import com.mentimeter.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email already registered: " + email);
        }
        User user = new User(email, passwordEncoder.encode(rawPassword));
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No user with email: " + email));
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new ResourceNotFoundException("No user with email: " + email);
        }
        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No user with id: " + userId));
    }
}
