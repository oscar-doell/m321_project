package com.example.statistikdienst.service;

import com.example.statistikdienst.model.Favorite;
import com.example.statistikdienst.model.Recipe;
import com.example.statistikdienst.model.dto.PopularRecipe;
import com.example.statistikdienst.model.dto.UserStat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private RecipeClient recipeClient;

    @Mock
    private FavoriteClient favoriteClient;

    @InjectMocks
    private StatisticsService statisticsService;

    private Recipe recipe1;
    private Recipe recipe2;
    private Recipe recipe3;
    private Recipe recipe4;
    private Recipe recipe5;
    private Recipe recipe6;

    @BeforeEach
    void setUp() {
        recipe1 = new Recipe();
        recipe1.setId(1L);
        recipe1.setTitle("Spaghetti Bolognese");
        recipe1.setCategory("Italian");
        recipe1.setAuthorId(1L);

        recipe2 = new Recipe();
        recipe2.setId(2L);
        recipe2.setTitle("Brownies");
        recipe2.setCategory("Dessert");
        recipe2.setAuthorId(2L);

        recipe3 = new Recipe();
        recipe3.setId(3L);
        recipe3.setTitle("Pizza Margherita");
        recipe3.setCategory("Italian");
        recipe3.setAuthorId(1L);

        recipe4 = new Recipe();
        recipe4.setId(4L);
        recipe4.setTitle("Tiramisu");
        recipe4.setCategory("Dessert");
        recipe4.setAuthorId(3L);

        recipe5 = new Recipe();
        recipe5.setId(5L);
        recipe5.setTitle("Lasagna");
        recipe5.setCategory("Italian");
        recipe5.setAuthorId(1L);

        recipe6 = new Recipe();
        recipe6.setId(6L);
        recipe6.setTitle("Cheesecake");
        recipe6.setCategory("Dessert");
        recipe6.setAuthorId(2L);
    }

    @Test
    void getMostPopularRecipes_shouldReturnSortedLimitedList() {
        Favorite fav1 = new Favorite(); fav1.setRecipeId(1L);
        Favorite fav2 = new Favorite(); fav2.setRecipeId(1L);
        Favorite fav3 = new Favorite(); fav3.setRecipeId(2L);
        Favorite fav4 = new Favorite(); fav4.setRecipeId(3L);
        Favorite fav5 = new Favorite(); fav5.setRecipeId(3L);
        Favorite fav6 = new Favorite(); fav6.setRecipeId(3L);
        Favorite fav7 = new Favorite(); fav7.setRecipeId(4L);
        Favorite fav8 = new Favorite(); fav8.setRecipeId(4L);
        Favorite fav9 = new Favorite(); fav9.setRecipeId(5L);
        Favorite fav10 = new Favorite(); fav10.setRecipeId(6L);

        when(favoriteClient.getAllFavorites()).thenReturn(List.of(fav1, fav2, fav3, fav4, fav5, fav6, fav7, fav8, fav9, fav10));
        when(recipeClient.getRecipeById(1L)).thenReturn(Mono.just(recipe1));
        when(recipeClient.getRecipeById(2L)).thenReturn(Mono.just(recipe2));
        when(recipeClient.getRecipeById(3L)).thenReturn(Mono.just(recipe3));
        when(recipeClient.getRecipeById(4L)).thenReturn(Mono.just(recipe4));
        when(recipeClient.getRecipeById(5L)).thenReturn(Mono.just(recipe5));
        when(recipeClient.getRecipeById(6L)).thenReturn(Mono.just(recipe6));


        List<PopularRecipe> popularRecipes = statisticsService.getMostPopularRecipes();

        assertEquals(5, popularRecipes.size());
        assertEquals("Pizza Margherita", popularRecipes.get(0).title());
        assertEquals(3, popularRecipes.get(0).favorites());
        assertEquals("Tiramisu", popularRecipes.get(1).title());
        assertEquals(2, popularRecipes.get(1).favorites());
        assertEquals("Spaghetti Bolognese", popularRecipes.get(2).title());
        assertEquals(2, popularRecipes.get(2).favorites());
        assertEquals("Brownies", popularRecipes.get(3).title());
        assertEquals(1, popularRecipes.get(3).favorites());
        assertEquals("Lasagna", popularRecipes.get(4).title());
        assertEquals(1, popularRecipes.get(4).favorites());
    }

    @Test
    void getMostPopularRecipes_shouldReturnEmptyListWhenNoFavorites() {
        when(favoriteClient.getAllFavorites()).thenReturn(Collections.emptyList());

        List<PopularRecipe> popularRecipes = statisticsService.getMostPopularRecipes();

        assertTrue(popularRecipes.isEmpty());
    }

    @Test
    void getMostPopularRecipes_shouldHandleRecipeNotFound() {
        Favorite fav1 = new Favorite(); fav1.setRecipeId(99L); // Non-existent recipe

        when(favoriteClient.getAllFavorites()).thenReturn(List.of(fav1));
        when(recipeClient.getRecipeById(99L)).thenReturn(Mono.empty());

        List<PopularRecipe> popularRecipes = statisticsService.getMostPopularRecipes();

        assertEquals(1, popularRecipes.size());
        assertEquals("Unknown", popularRecipes.get(0).title());
        assertEquals(1, popularRecipes.get(0).favorites());
    }

    @Test
    void getRecipeCountPerCategory_shouldReturnCorrectCounts() {
        Recipe recipeWithNullCategory = new Recipe();
        recipeWithNullCategory.setId(7L);
        recipeWithNullCategory.setTitle("Soup");
        recipeWithNullCategory.setCategory(null);

        Recipe recipeWithEmptyCategory = new Recipe();
        recipeWithEmptyCategory.setId(8L);
        recipeWithEmptyCategory.setTitle("Salad");
        recipeWithEmptyCategory.setCategory("");

        when(recipeClient.getAllRecipes()).thenReturn(List.of(recipe1, recipe2, recipe3, recipeWithNullCategory, recipeWithEmptyCategory));

        Map<String, Long> categoryCounts = statisticsService.getRecipeCountPerCategory();

        assertEquals(2, categoryCounts.size());
        assertEquals(2, categoryCounts.get("Italian"));
        assertEquals(1, categoryCounts.get("Dessert"));
    }

    @Test
    void getRecipeCountPerCategory_shouldReturnEmptyMapWhenNoRecipes() {
        when(recipeClient.getAllRecipes()).thenReturn(Collections.emptyList());

        Map<String, Long> categoryCounts = statisticsService.getRecipeCountPerCategory();

        assertTrue(categoryCounts.isEmpty());
    }

    @Test
    void getUserStats_shouldReturnCorrectStats() {
        when(recipeClient.getRecipesByAuthorId(1L)).thenReturn(List.of(recipe1, recipe3, recipe5));
        Favorite fav1 = new Favorite(); fav1.setRecipeId(2L);
        Favorite fav2 = new Favorite(); fav2.setRecipeId(4L);
        when(favoriteClient.getFavoritesByUserId(1L)).thenReturn(List.of(fav1, fav2));

        UserStat userStat = statisticsService.getUserStats(1L);

        assertEquals(1L, userStat.userId());
        assertEquals(3, userStat.recipesCreated());
        assertEquals(2, userStat.recipesFavorited());
    }

    @Test
    void getUserStats_shouldReturnZeroStatsWhenNoRecipesOrFavorites() {
        when(recipeClient.getRecipesByAuthorId(anyLong())).thenReturn(Collections.emptyList());
        when(favoriteClient.getFavoritesByUserId(anyLong())).thenReturn(Collections.emptyList());

        UserStat userStat = statisticsService.getUserStats(99L);

        assertEquals(99L, userStat.userId());
        assertEquals(0, userStat.recipesCreated());
        assertEquals(0, userStat.recipesFavorited());
    }
}
