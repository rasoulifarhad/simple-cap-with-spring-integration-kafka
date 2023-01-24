package com.farhad.example.cap.simple.http.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public class CapHandler {
    
    public Mono<ServerResponse> cap(ServerRequest serverRequest) {

        return ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("null",String.class);
    }
}
