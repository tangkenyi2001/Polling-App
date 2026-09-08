package com.mentimeter.dto;

import java.time.Instant;

import com.mentimeter.entity.MCQResponse;

public record McqResponseView(Long id, Long userId, Instant responseTime, Long optionId, String optionValue) {
    public static McqResponseView from(MCQResponse response) {
        var user = response.getResponse().getUser();
        return new McqResponseView(
                response.getId(),
                user == null ? null : user.getId(),
                response.getResponse().getResponseTime(),
                response.getMcqOption().getId(),
                response.getMcqOption().getValue());
    }
}
