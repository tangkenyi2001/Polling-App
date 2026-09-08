package com.mentimeter.dto;

import java.time.Instant;

import com.mentimeter.entity.RatingResponse;

public record RatingResponseView(Long id, Long userId, Instant responseTime, Integer rating) {
    public static RatingResponseView from(RatingResponse response) {
        var user = response.getResponse().getUser();
        return new RatingResponseView(
                response.getId(),
                user == null ? null : user.getId(),
                response.getResponse().getResponseTime(),
                response.getRating());
    }
}
