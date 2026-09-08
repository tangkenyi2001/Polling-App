package com.mentimeter.dto;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateMcqPollRequest(
        @NotNull Long ownerId,
        @NotBlank String question,
        Instant expiryDate,
        boolean allowMultipleAnswers,
        @NotEmpty List<@NotBlank String> options) {
}
