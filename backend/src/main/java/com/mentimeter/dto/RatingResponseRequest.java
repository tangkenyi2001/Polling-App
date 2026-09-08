package com.mentimeter.dto;

import jakarta.validation.constraints.NotNull;

public record RatingResponseRequest(Long userId, @NotNull Integer rating) {
}
