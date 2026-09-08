package com.mentimeter.dto;

import java.time.Instant;

import com.mentimeter.entity.WordCloudResponse;

public record WordCloudResponseView(Long id, Long userId, Instant responseTime, String text) {
    public static WordCloudResponseView from(WordCloudResponse response) {
        var user = response.getResponse().getUser();
        return new WordCloudResponseView(
                response.getId(),
                user == null ? null : user.getId(),
                response.getResponse().getResponseTime(),
                response.getText());
    }
}
