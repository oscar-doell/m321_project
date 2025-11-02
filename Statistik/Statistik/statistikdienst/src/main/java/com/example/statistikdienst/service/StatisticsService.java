package com.example.statistikdienst.service;

import com.example.statistikdienst.model.Favorite;
import com.example.statistikdienst.model.Recipe;
import com.example.statistikdienst.model.dto.PopularRecipe;
import com.example.statistikdienst.model.dto.UserStat;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final RecipeClient recipeClient;
    private final FavoriteClient favoriteClient;

    public StatisticsService(RecipeClient recipeClient, FavoriteClient favoriteClient) {
        this.recipeClient = recipeClient;
        this.favoriteClient = favoriteClient;
    }

    @Cacheable("popular-recipes")
    public List<PopularRecipe> getMostPopularRecipes() {
        List<Favorite> favorites = favoriteClient.getAllFavorites();
        Map<Long, Long> favoriteCounts = favorites.stream()
                .collect(Collectors.groupingBy(Favorite::getRecipeId, Collectors.counting()));

        return favoriteCounts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    Recipe recipe = recipeClient.getRecipeById(entry.getKey()).block();
                    String title = recipe != null ? recipe.getTitle() : "Unknown";
                    return new PopularRecipe(entry.getKey(), title, entry.getValue());
                })
                .collect(Collectors.toList());
    }

    @Cacheable("recipes-per-category")
    public Map<String, Long> getRecipeCountPerCategory() {
        List<Recipe> recipes = recipeClient.getAllRecipes();
        return recipes.stream()
                .filter(recipe -> recipe.getCategory() != null && !recipe.getCategory().isEmpty())
                .collect(Collectors.groupingBy(Recipe::getCategory, Collectors.counting()));
    }

    @Cacheable(value = "user-stats", key = "#userId")
    public UserStat getUserStats(Long userId) {
        int recipesCreated = recipeClient.getRecipesByAuthorId(userId).size();
        int recipesFavorited = favoriteClient.getFavoritesByUserId(userId).size();
        return new UserStat(userId, recipesCreated, recipesFavorited);
    }
}
