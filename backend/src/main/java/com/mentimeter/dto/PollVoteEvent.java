package com.mentimeter.dto;

public record PollVoteEvent(Long pollId, PollResultsResponse results) {
}
