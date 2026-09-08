package com.mentimeter.dto;

import java.time.LocalDateTime;

import com.mentimeter.entity.User;

public record UserResponse(Long id, String email, LocalDateTime createdAt, LocalDateTime lastLogin) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getCreatedAt(), user.getLastLogin());
    }
}
