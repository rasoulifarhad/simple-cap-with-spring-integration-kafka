package com.farhad.example.cap.simple.http.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import com.farhad.example.cap.simple.http.handler.CapHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class CapRouteConfig {
    
    private String CAP_END_POINT = "/api/fn/reactive/cap/";

    @Bean
    public RouterFunction<ServerResponse> capRoute(CapHandler handler) {

        return RouterFunctions
                        .route(GET(CAP_END_POINT)
                                    .and(accept(MediaType.APPLICATION_JSON)),
                                handler::cap);
    }
 }
