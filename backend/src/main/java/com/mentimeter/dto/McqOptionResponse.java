package com.mentimeter.dto;

import com.mentimeter.entity.MCQOption;

public record McqOptionResponse(Long id, String value) {
    public static McqOptionResponse from(MCQOption option) {
        return new McqOptionResponse(option.getId(), option.getValue());
    }
}
