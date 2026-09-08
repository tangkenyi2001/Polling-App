package com.mentimeter.dto;

import java.util.List;

public record PollResultsResponse(long responseCount, List<OptionResult> options, List<WordResult> words,
        List<String> text) {

    public record OptionResult(Long optionId, String label, long voteCount) {
    }

    public record WordResult(String word, long count) {
    }
}
