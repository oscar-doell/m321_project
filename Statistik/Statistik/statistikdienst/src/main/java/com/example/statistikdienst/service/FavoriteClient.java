package com.example.statistikdienst.service;

import com.example.statistikdienst.model.Favorite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Service
public class FavoriteClient {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteClient.class);
    private final WebClient webClient;

    @Value("${services.favorites}")
    private String favoriteServiceUrl;

    public FavoriteClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public List<Favorite> getAllFavorites() {
        logger.info("Fetching all favorites from {}", favoriteServiceUrl);
        return webClient.get()
                .uri(favoriteServiceUrl + "/favorites/all")
                .retrieve()
                .bodyToFlux(Favorite.class)
                .collectList()
                .doOnError(error -> logger.error("Error fetching all favorites", error))
                .onErrorReturn(Collections.emptyList())
                .block();
    }

    public List<Favorite> getFavoritesByUserId(Long userId) {
        logger.info("Fetching favorites for user {} from {}", userId, favoriteServiceUrl);
        return webClient.get()
                .uri(favoriteServiceUrl + "/favorites/user/" + userId)
                .retrieve()
                .bodyToFlux(Favorite.class)
                .collectList()
                .doOnError(error -> logger.error("Error fetching favorites for user {}", userId, error))
                .onErrorReturn(Collections.emptyList())
                .block();
    }
}
