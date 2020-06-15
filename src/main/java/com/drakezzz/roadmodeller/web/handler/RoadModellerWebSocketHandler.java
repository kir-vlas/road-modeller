package com.drakezzz.roadmodeller.web.handler;

import com.drakezzz.roadmodeller.executor.ContiniusActionExecutor;
import com.drakezzz.roadmodeller.web.dto.ModelDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Component
public class RoadModellerWebSocketHandler implements WebSocketHandler {

    private Flux<Long> messageFlux;

    private final ContiniusActionExecutor actionExecutor;

    private final ObjectMapper objectMapper;

    public RoadModellerWebSocketHandler(ContiniusActionExecutor actionExecutor, ObjectMapper objectMapper) {
        this.actionExecutor = actionExecutor;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() {
        messageFlux = Flux.interval(Duration.ofSeconds(1));
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(
                webSocketSession.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .flatMap(actionExecutor::executeAction)
                        .map(ModelDto::of)
                        .map(this::serialize)
                        .map(webSocketSession::textMessage));
    }

    private String serialize(Object message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
