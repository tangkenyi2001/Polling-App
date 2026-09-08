package com.mentimeter.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class PollEventService {

    private final Map<Long, CopyOnWriteArrayList<SseEmitter>> subscribers = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long pollId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        CopyOnWriteArrayList<SseEmitter> pollSubscribers = subscribers.computeIfAbsent(pollId,
                id -> new CopyOnWriteArrayList<>());
        pollSubscribers.add(emitter);

        Runnable cleanup = () -> pollSubscribers.remove(emitter);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(e -> cleanup.run());

        return emitter;
    }

    public void notifyVote(Long pollId, Object resultsPayload) {
        List<SseEmitter> pollSubscribers = subscribers.get(pollId);
        if (pollSubscribers == null) {
            return;
        }
        for (SseEmitter emitter : pollSubscribers) {
            try {
                emitter.send(SseEmitter.event().name("vote").data(resultsPayload, MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                emitter.complete();
                pollSubscribers.remove(emitter);
            }
        }
    }
}
