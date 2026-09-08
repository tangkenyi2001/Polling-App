package com.mentimeter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.mentimeter.dto.CreateMcqPollRequest;
import com.mentimeter.dto.CreateRatingPollRequest;
import com.mentimeter.dto.CreateWordCloudPollRequest;
import com.mentimeter.dto.McqOptionResponse;
import com.mentimeter.dto.PollResponse;
import com.mentimeter.dto.PollResultsResponse;
import com.mentimeter.service.PollEventService;
import com.mentimeter.service.PollResultsService;
import com.mentimeter.service.PollService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/polls")
public class PollController {

    private final PollService pollService;
    private final PollEventService pollEventService;
    private final PollResultsService pollResultsService;

    public PollController(PollService pollService, PollEventService pollEventService,
            PollResultsService pollResultsService) {
        this.pollService = pollService;
        this.pollEventService = pollEventService;
        this.pollResultsService = pollResultsService;
    }

    @PostMapping("/mcq")
    public ResponseEntity<PollResponse> createMcqPoll(@Valid @RequestBody CreateMcqPollRequest request) {
        var poll = pollService.createMcqPoll(request.ownerId(), request.question(), request.expiryDate(),
                request.allowMultipleAnswers(), request.options());
        return ResponseEntity.status(HttpStatus.CREATED).body(PollResponse.from(poll));
    }

    @PostMapping("/rating")
    public ResponseEntity<PollResponse> createRatingPoll(@Valid @RequestBody CreateRatingPollRequest request) {
        var poll = pollService.createRatingPoll(request.ownerId(), request.question(), request.expiryDate(),
                request.minRating(), request.maxRating());
        return ResponseEntity.status(HttpStatus.CREATED).body(PollResponse.from(poll));
    }

    @PostMapping("/wordcloud")
    public ResponseEntity<PollResponse> createWordCloudPoll(@Valid @RequestBody CreateWordCloudPollRequest request) {
        var poll = pollService.createWordCloudPoll(request.ownerId(), request.question(), request.expiryDate(),
                request.maxWords());
        return ResponseEntity.status(HttpStatus.CREATED).body(PollResponse.from(poll));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PollResponse> getPoll(@PathVariable Long id) {
        return ResponseEntity.ok(PollResponse.from(pollService.getPoll(id)));
    }

    @GetMapping("/{id}/options")
    public ResponseEntity<List<McqOptionResponse>> getOptions(@PathVariable Long id) {
        List<McqOptionResponse> options = pollService.getOptions(id).stream()
                .map(McqOptionResponse::from)
                .toList();
        return ResponseEntity.ok(options);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PollResponse>> listByOwner(@PathVariable Long ownerId) {
        List<PollResponse> polls = pollService.listPollsByUser(ownerId).stream()
                .map(PollResponse::from)
                .toList();
        return ResponseEntity.ok(polls);
    }

    @GetMapping(value = "/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable Long id) {
        return pollEventService.subscribe(id);
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<PollResultsResponse> getResults(@PathVariable Long id) {
        return ResponseEntity.ok(pollResultsService.computeResults(id));
    }
}
