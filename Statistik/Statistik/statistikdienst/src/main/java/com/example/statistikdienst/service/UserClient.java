package com.example.statistikdienst.service;

import com.example.statistikdienst.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserClient {

    private static final Logger logger = LoggerFactory.getLogger(UserClient.class);
    private final WebClient webClient;

    @Value("${services.users}")
    private String userServiceUrl;

    public UserClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<User> getUserById(Long userId) {
        logger.info("Fetching user with id {} from {}", userId, userServiceUrl);
        return webClient.get()
                .uri(userServiceUrl + "/users/" + userId)
                .retrieve()
                .bodyToMono(User.class)
                .doOnError(error -> logger.error("Error fetching user with id {}", userId, error))
                .onErrorResume(error -> Mono.empty());
    }
}
