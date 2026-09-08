package com.mentimeter.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mentimeter.dto.PollResultsResponse;
import com.mentimeter.entity.MCQOption;
import com.mentimeter.entity.Poll;
import com.mentimeter.exception.ResourceNotFoundException;
import com.mentimeter.repository.MCQOptionRepository;
import com.mentimeter.repository.MCQResponseRepository;
import com.mentimeter.repository.PollRepository;
import com.mentimeter.repository.RatingResponseRepository;
import com.mentimeter.repository.WordCloudResponseRepository;

@Service
public class PollResultsService {

    private final PollRepository pollRepository;
    private final MCQOptionRepository mcqOptionRepository;
    private final MCQResponseRepository mcqResponseRepository;
    private final RatingResponseRepository ratingResponseRepository;
    private final WordCloudResponseRepository wordCloudResponseRepository;

    public PollResultsService(PollRepository pollRepository, MCQOptionRepository mcqOptionRepository,
            MCQResponseRepository mcqResponseRepository, RatingResponseRepository ratingResponseRepository,
            WordCloudResponseRepository wordCloudResponseRepository) {
        this.pollRepository = pollRepository;
        this.mcqOptionRepository = mcqOptionRepository;
        this.mcqResponseRepository = mcqResponseRepository;
        this.ratingResponseRepository = ratingResponseRepository;
        this.wordCloudResponseRepository = wordCloudResponseRepository;
    }

    public PollResultsResponse computeResults(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("No poll with id: " + pollId));
        return computeResults(poll);
    }

    public PollResultsResponse computeResults(Poll poll) {
        return switch (poll.getType()) {
            case MCQ -> computeMcqResults(poll.getId());
            case WORDCLOUD -> computeWordCloudResults(poll.getId());
            case RATING -> computeRatingResults(poll.getId());
        };
    }

    private PollResultsResponse computeMcqResults(Long pollId) {
        List<MCQOption> options = mcqOptionRepository.findByPollId(pollId);
        long totalResponses = mcqResponseRepository.countByResponsePollId(pollId);

        Map<Long, Long> counts = new HashMap<>();
        for (MCQResponseRepository.OptionVoteCount c : mcqResponseRepository.countByOption(pollId)) {
            counts.put(c.getOptionId(), c.getVoteCount());
        }

        List<PollResultsResponse.OptionResult> optionResults = options.stream()
                .map(o -> new PollResultsResponse.OptionResult(o.getId(), o.getValue(),
                        counts.getOrDefault(o.getId(), 0L)))
                .toList();

        return new PollResultsResponse(totalResponses, optionResults, null, null);
    }

    private PollResultsResponse computeWordCloudResults(Long pollId) {
        long totalResponses = wordCloudResponseRepository.countByResponsePollId(pollId);

        List<PollResultsResponse.WordResult> words = wordCloudResponseRepository.countByWord(pollId).stream()
                .map(w -> new PollResultsResponse.WordResult(w.getWord(), w.getWordCount()))
                .toList();

        return new PollResultsResponse(totalResponses, null, words, null);
    }

    private PollResultsResponse computeRatingResults(Long pollId) {
        long totalResponses = ratingResponseRepository.countByResponsePollId(pollId);

        List<PollResultsResponse.OptionResult> ratingCounts = ratingResponseRepository.countByRating(pollId).stream()
                .map(r -> new PollResultsResponse.OptionResult((long) r.getRating(), String.valueOf(r.getRating()),
                        r.getRatingCount()))
                .toList();

        return new PollResultsResponse(totalResponses, ratingCounts, null, null);
    }
}
