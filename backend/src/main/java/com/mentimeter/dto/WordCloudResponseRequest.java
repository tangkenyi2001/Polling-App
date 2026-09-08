package com.mentimeter.dto;

import jakarta.validation.constraints.NotBlank;

public record WordCloudResponseRequest(Long userId, @NotBlank String text) {
}
