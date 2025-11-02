package com.example.statistikdienst.service;

import com.example.statistikdienst.model.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
public class RecipeClient {

    private static final Logger logger = LoggerFactory.getLogger(RecipeClient.class);
    private final WebClient webClient;

    @Value("${services.recipes}")
    private String recipeServiceUrl;

    public RecipeClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public List<Recipe> getAllRecipes() {
        logger.info("Fetching all recipes from {}", recipeServiceUrl);
        return webClient.get()
                .uri(recipeServiceUrl + "/recipes")
                .retrieve()
                .bodyToFlux(Recipe.class)
                .collectList()
                .doOnError(error -> logger.error("Error fetching all recipes", error))
                .onErrorReturn(Collections.emptyList())
                .block();
    }

    public Mono<Recipe> getRecipeById(Long recipeId) {
        logger.info("Fetching recipe with id {} from {}", recipeId, recipeServiceUrl);
        return webClient.get()
                .uri(recipeServiceUrl + "/recipes/" + recipeId)
                .retrieve()
                .bodyToMono(Recipe.class)
                .doOnError(error -> logger.error("Error fetching recipe with id {}", recipeId, error))
                .onErrorResume(error -> Mono.empty());
    }

    public List<Recipe> getRecipesByAuthorId(Long userId) {
        logger.info("Fetching recipes for author {} from {}", userId, recipeServiceUrl);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(recipeServiceUrl + "/recipes").queryParam("authorId", userId).build())
                .retrieve()
                .bodyToFlux(Recipe.class)
                .collectList()
                .doOnError(error -> logger.error("Error fetching recipes for author {}", userId, error))
                .onErrorReturn(Collections.emptyList())
                .block();
    }
}
