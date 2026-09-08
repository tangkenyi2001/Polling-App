package com.mentimeter.dto;

import jakarta.validation.constraints.NotNull;

public record McqResponseRequest(Long userId, @NotNull Long mcqOptionId) {
}
