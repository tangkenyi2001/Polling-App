package com.mentimeter.dto;

import java.time.Instant;

import com.mentimeter.entity.Poll;
import com.mentimeter.entity.PollType;

public record PollResponse(Long id, Long ownerId, String question, PollType type, Instant createdAt,
        Instant expiryDate) {
    public static PollResponse from(Poll poll) {
        return new PollResponse(poll.getId(), poll.getUser().getId(), poll.getQuestion(), poll.getType(),
                poll.getCreatedAt(), poll.getExpiryDate());
    }
}
