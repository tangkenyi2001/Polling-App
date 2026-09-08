package com.mentimeter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.mentimeter.dto.PollResultsResponse;
import com.mentimeter.dto.PollVoteEvent;

@Component
public class PollVoteProducer {

    private final KafkaTemplate<String, PollVoteEvent> kafkaTemplate;
    private final String topic;

    public PollVoteProducer(KafkaTemplate<String, PollVoteEvent> kafkaTemplate,
            @Value("${mentimeter.kafka.poll-vote-events-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(Long pollId, PollResultsResponse results) {
        kafkaTemplate.send(topic, pollId.toString(), new PollVoteEvent(pollId, results));
    }
}
