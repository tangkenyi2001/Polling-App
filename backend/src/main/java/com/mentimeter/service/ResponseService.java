package com.mentimeter.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mentimeter.entity.MCQOption;
import com.mentimeter.entity.MCQResponse;
import com.mentimeter.entity.Poll;
import com.mentimeter.entity.PollType;
import com.mentimeter.entity.RatingPoll;
import com.mentimeter.entity.RatingResponse;
import com.mentimeter.entity.Response;
import com.mentimeter.entity.User;
import com.mentimeter.entity.WordCloudResponse;
import com.mentimeter.exception.InvalidResponseException;
import com.mentimeter.exception.ResourceNotFoundException;
import com.mentimeter.repository.MCQOptionRepository;
import com.mentimeter.repository.MCQResponseRepository;
import com.mentimeter.repository.PollRepository;
import com.mentimeter.repository.RatingPollRepository;
import com.mentimeter.repository.RatingResponseRepository;
import com.mentimeter.repository.ResponseRepository;
import com.mentimeter.repository.UserRepository;
import com.mentimeter.repository.WordCloudResponseRepository;

@Service
public class ResponseService {

    private final PollRepository pollRepository;
    private final UserRepository userRepository;
    private final ResponseRepository responseRepository;
    private final MCQOptionRepository mcqOptionRepository;
    private final MCQResponseRepository mcqResponseRepository;
    private final RatingPollRepository ratingPollRepository;
    private final RatingResponseRepository ratingResponseRepository;
    private final WordCloudResponseRepository wordCloudResponseRepository;
    private final PollEventService pollEventService;
    private final PollResultsService pollResultsService;

    public ResponseService(PollRepository pollRepository, UserRepository userRepository,
            ResponseRepository responseRepository, MCQOptionRepository mcqOptionRepository,
            MCQResponseRepository mcqResponseRepository, RatingPollRepository ratingPollRepository,
            RatingResponseRepository ratingResponseRepository,
            WordCloudResponseRepository wordCloudResponseRepository, PollEventService pollEventService,
            PollResultsService pollResultsService) {
        this.pollRepository = pollRepository;
        this.userRepository = userRepository;
        this.responseRepository = responseRepository;
        this.mcqOptionRepository = mcqOptionRepository;
        this.mcqResponseRepository = mcqResponseRepository;
        this.ratingPollRepository = ratingPollRepository;
        this.ratingResponseRepository = ratingResponseRepository;
        this.wordCloudResponseRepository = wordCloudResponseRepository;
        this.pollEventService = pollEventService;
        this.pollResultsService = pollResultsService;
    }

    @Transactional
    public MCQResponse submitMcqResponse(Long pollId, Long userId, Long mcqOptionId) {
        Poll poll = requirePollOfType(pollId, PollType.MCQ);

        MCQOption option = mcqOptionRepository.findById(mcqOptionId)
                .orElseThrow(() -> new ResourceNotFoundException("No option with id: " + mcqOptionId));
        if (!option.getPoll().getId().equals(pollId)) {
            throw new InvalidResponseException("Option " + mcqOptionId + " does not belong to poll " + pollId);
        }

        Response response = createBaseResponse(poll, userId);

        MCQResponse mcqResponse = new MCQResponse();
        mcqResponse.setResponse(response);
        mcqResponse.setMcqOption(option);
        MCQResponse saved = mcqResponseRepository.save(mcqResponse);
        pollEventService.notifyVote(pollId, pollResultsService.computeResults(pollId));
        return saved;
    }

    @Transactional
    public RatingResponse submitRatingResponse(Long pollId, Long userId, int rating) {
        Poll poll = requirePollOfType(pollId, PollType.RATING);

        RatingPoll ratingPoll = ratingPollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("No rating config for poll: " + pollId));
        if (rating < ratingPoll.getMinRating() || rating > ratingPoll.getMaxRating()) {
            throw new InvalidResponseException("Rating " + rating + " is outside allowed range ["
                    + ratingPoll.getMinRating() + ", " + ratingPoll.getMaxRating() + "]");
        }

        Response response = createBaseResponse(poll, userId);

        RatingResponse ratingResponse = new RatingResponse();
        ratingResponse.setResponse(response);
        ratingResponse.setRating(rating);
        RatingResponse saved = ratingResponseRepository.save(ratingResponse);
        pollEventService.notifyVote(pollId, pollResultsService.computeResults(pollId));
        return saved;
    }

    @Transactional
    public WordCloudResponse submitWordCloudResponse(Long pollId, Long userId, String text) {
        Poll poll = requirePollOfType(pollId, PollType.WORDCLOUD);

        Response response = createBaseResponse(poll, userId);

        WordCloudResponse wordCloudResponse = new WordCloudResponse();
        wordCloudResponse.setResponse(response);
        wordCloudResponse.setText(text);
        WordCloudResponse saved = wordCloudResponseRepository.save(wordCloudResponse);
        pollEventService.notifyVote(pollId, pollResultsService.computeResults(pollId));
        return saved;
    }

    private Poll requirePollOfType(Long pollId, PollType expectedType) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("No poll with id: " + pollId));
        if (poll.getType() != expectedType) {
            throw new InvalidResponseException(
                    "Poll " + pollId + " is of type " + poll.getType() + ", not " + expectedType);
        }
        return poll;
    }

    private Response createBaseResponse(Poll poll, Long userId) {
        Response response = new Response();
        response.setPoll(poll);
        response.setResponseTime(Instant.now());
        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("No user with id: " + userId));
            response.setUser(user);
        }
        return responseRepository.save(response);
    }

    public List<Response> listResponses(Long pollId) {
        return responseRepository.findByPollId(pollId);
    }

    public List<MCQResponse> listMcqResponses(Long pollId) {
        return mcqResponseRepository.findByResponsePollId(pollId);
    }

    public List<RatingResponse> listRatingResponses(Long pollId) {
        return ratingResponseRepository.findByResponsePollId(pollId);
    }

    public List<WordCloudResponse> listWordCloudResponses(Long pollId) {
        return wordCloudResponseRepository.findByResponsePollId(pollId);
    }
}
