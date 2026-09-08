package com.mentimeter.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mentimeter.entity.MCQOption;
import com.mentimeter.entity.MCQPoll;
import com.mentimeter.entity.Poll;
import com.mentimeter.entity.PollType;
import com.mentimeter.entity.RatingPoll;
import com.mentimeter.entity.User;
import com.mentimeter.entity.WordCloudPoll;
import com.mentimeter.exception.ResourceNotFoundException;
import com.mentimeter.repository.MCQOptionRepository;
import com.mentimeter.repository.MCQPollRepository;
import com.mentimeter.repository.PollRepository;
import com.mentimeter.repository.RatingPollRepository;
import com.mentimeter.repository.UserRepository;
import com.mentimeter.repository.WordCloudPollRepository;

@Service
public class PollService {

    private final PollRepository pollRepository;
    private final UserRepository userRepository;
    private final MCQPollRepository mcqPollRepository;
    private final MCQOptionRepository mcqOptionRepository;
    private final RatingPollRepository ratingPollRepository;
    private final WordCloudPollRepository wordCloudPollRepository;

    public PollService(PollRepository pollRepository, UserRepository userRepository,
            MCQPollRepository mcqPollRepository, MCQOptionRepository mcqOptionRepository,
            RatingPollRepository ratingPollRepository, WordCloudPollRepository wordCloudPollRepository) {
        this.pollRepository = pollRepository;
        this.userRepository = userRepository;
        this.mcqPollRepository = mcqPollRepository;
        this.mcqOptionRepository = mcqOptionRepository;
        this.ratingPollRepository = ratingPollRepository;
        this.wordCloudPollRepository = wordCloudPollRepository;
    }

    @Transactional
    public Poll createMcqPoll(Long ownerId, String question, Instant expiryDate,
            boolean allowMultipleAnswers, List<String> optionValues) {
        Poll poll = createBasePoll(ownerId, question, expiryDate, PollType.MCQ);

        MCQPoll mcqPoll = new MCQPoll();
        mcqPoll.setPoll(poll);
        mcqPoll.setAllowMultipleAnswers(allowMultipleAnswers);
        mcqPollRepository.save(mcqPoll);

        for (String value : optionValues) {
            MCQOption option = new MCQOption();
            option.setPoll(poll);
            option.setValue(value);
            mcqOptionRepository.save(option);
        }

        return poll;
    }

    @Transactional
    public Poll createRatingPoll(Long ownerId, String question, Instant expiryDate,
            int minRating, int maxRating) {
        if (minRating >= maxRating) {
            throw new IllegalArgumentException("minRating must be less than maxRating");
        }
        Poll poll = createBasePoll(ownerId, question, expiryDate, PollType.RATING);

        RatingPoll ratingPoll = new RatingPoll();
        ratingPoll.setPoll(poll);
        ratingPoll.setMinRating(minRating);
        ratingPoll.setMaxRating(maxRating);
        ratingPollRepository.save(ratingPoll);

        return poll;
    }

    @Transactional
    public Poll createWordCloudPoll(Long ownerId, String question, Instant expiryDate, int maxWords) {
        Poll poll = createBasePoll(ownerId, question, expiryDate, PollType.WORDCLOUD);

        WordCloudPoll wordCloudPoll = new WordCloudPoll();
        wordCloudPoll.setPoll(poll);
        wordCloudPoll.setMaxWords(maxWords);
        wordCloudPollRepository.save(wordCloudPoll);

        return poll;
    }

    private Poll createBasePoll(Long ownerId, String question, Instant expiryDate, PollType type) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("No user with id: " + ownerId));

        Poll poll = new Poll();
        poll.setUser(owner);
        poll.setQuestion(question);
        poll.setType(type);
        poll.setCreatedAt(Instant.now());
        poll.setExpiryDate(expiryDate);
        return pollRepository.save(poll);
    }

    public Poll getPoll(Long pollId) {
        return pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("No poll with id: " + pollId));
    }

    public List<Poll> listPollsByUser(Long ownerId) {
        return pollRepository.findByUserId(ownerId);
    }

    public List<MCQOption> getOptions(Long pollId) {
        return mcqOptionRepository.findByPollId(pollId);
    }
}
