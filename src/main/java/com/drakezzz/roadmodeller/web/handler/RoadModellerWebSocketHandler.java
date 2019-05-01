package com.drakezzz.roadmodeller.web.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class RoadModellerWebSocketHandler implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(webSocketSession.receive()
                .map(msg -> "RECEIVED ON SERVER :: " + LocalDateTime.now())
                .map(webSocketSession::textMessage) );
    }


}
