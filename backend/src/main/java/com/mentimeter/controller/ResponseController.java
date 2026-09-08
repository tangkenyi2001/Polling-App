package com.mentimeter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mentimeter.dto.McqResponseRequest;
import com.mentimeter.dto.McqResponseView;
import com.mentimeter.dto.RatingResponseRequest;
import com.mentimeter.dto.RatingResponseView;
import com.mentimeter.dto.WordCloudResponseRequest;
import com.mentimeter.dto.WordCloudResponseView;
import com.mentimeter.service.ResponseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/polls/{pollId}/responses")
public class ResponseController {

    private final ResponseService responseService;

    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    @PostMapping("/mcq")
    public ResponseEntity<McqResponseView> submitMcq(@PathVariable Long pollId,
            @Valid @RequestBody McqResponseRequest request) {
        var response = responseService.submitMcqResponse(pollId, request.userId(), request.mcqOptionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(McqResponseView.from(response));
    }

    @PostMapping("/rating")
    public ResponseEntity<RatingResponseView> submitRating(@PathVariable Long pollId,
            @Valid @RequestBody RatingResponseRequest request) {
        var response = responseService.submitRatingResponse(pollId, request.userId(), request.rating());
        return ResponseEntity.status(HttpStatus.CREATED).body(RatingResponseView.from(response));
    }

    @PostMapping("/wordcloud")
    public ResponseEntity<WordCloudResponseView> submitWordCloud(@PathVariable Long pollId,
            @Valid @RequestBody WordCloudResponseRequest request) {
        var response = responseService.submitWordCloudResponse(pollId, request.userId(), request.text());
        return ResponseEntity.status(HttpStatus.CREATED).body(WordCloudResponseView.from(response));
    }

    @GetMapping("/mcq")
    public ResponseEntity<List<McqResponseView>> listMcq(@PathVariable Long pollId) {
        return ResponseEntity.ok(responseService.listMcqResponses(pollId).stream()
                .map(McqResponseView::from)
                .toList());
    }

    @GetMapping("/rating")
    public ResponseEntity<List<RatingResponseView>> listRating(@PathVariable Long pollId) {
        return ResponseEntity.ok(responseService.listRatingResponses(pollId).stream()
                .map(RatingResponseView::from)
                .toList());
    }

    @GetMapping("/wordcloud")
    public ResponseEntity<List<WordCloudResponseView>> listWordCloud(@PathVariable Long pollId) {
        return ResponseEntity.ok(responseService.listWordCloudResponses(pollId).stream()
                .map(WordCloudResponseView::from)
                .toList());
    }
}
