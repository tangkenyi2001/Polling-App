package com.mentimeter.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRatingPollRequest(
        @NotNull Long ownerId,
        @NotBlank String question,
        Instant expiryDate,
        @NotNull Integer minRating,
        @NotNull Integer maxRating) {
}
