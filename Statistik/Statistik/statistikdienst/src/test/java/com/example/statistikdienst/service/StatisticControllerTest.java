package com.example.statistikdienst.controller;

import com.example.statistikdienst.model.dto.PopularRecipe;
import com.example.statistikdienst.model.dto.UserStat;
import com.example.statistikdienst.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    void getPopularRecipes_shouldReturnPopularRecipes() throws Exception {
        PopularRecipe pr1 = new PopularRecipe(1L, "Recipe A", 10L);
        PopularRecipe pr2 = new PopularRecipe(2L, "Recipe B", 5L);
        List<PopularRecipe> popularRecipes = Arrays.asList(pr1, pr2);

        when(statisticsService.getMostPopularRecipes()).thenReturn(popularRecipes);

        mockMvc.perform(get("/api/stats/popular"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].recipeId", is(1)))
                .andExpect(jsonPath("$[0].title", is("Recipe A")))
                .andExpect(jsonPath("$[0].favorites", is(10)))
                .andExpect(jsonPath("$[1].recipeId", is(2)))
                .andExpect(jsonPath("$[1].title", is("Recipe B")))
                .andExpect(jsonPath("$[1].favorites", is(5)));
    }

    @Test
    void getPopularRecipes_shouldReturnEmptyListWhenNoPopularRecipes() throws Exception {
        when(statisticsService.getMostPopularRecipes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/stats/popular"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getCategories_shouldReturnRecipeCountPerCategory() throws Exception {
        Map<String, Long> categoryCounts = Map.of("Italian", 10L, "Dessert", 5L);

        when(statisticsService.getRecipeCountPerCategory()).thenReturn(categoryCounts);

        mockMvc.perform(get("/api/stats/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Italian", is(10)))
                .andExpect(jsonPath("$.Dessert", is(5)));
    }

    @Test
    void getCategories_shouldReturnEmptyMapWhenNoCategories() throws Exception {
        when(statisticsService.getRecipeCountPerCategory()).thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/api/stats/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getUserStats_shouldReturnUserStats() throws Exception {
        UserStat userStat = new UserStat(1L, 5, 10);

        when(statisticsService.getUserStats(1L)).thenReturn(userStat);

        mockMvc.perform(get("/api/stats/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.recipesCreated", is(5)))
                .andExpect(jsonPath("$.recipesFavorited", is(10)));
    }

    @Test
    void getUserStats_shouldReturnZeroStatsForNonExistentUser() throws Exception {
        UserStat userStat = new UserStat(99L, 0, 0);

        when(statisticsService.getUserStats(99L)).thenReturn(userStat);

        mockMvc.perform(get("/api/stats/users/99"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId", is(99)))
                .andExpect(jsonPath("$.recipesCreated", is(0)))
                .andExpect(jsonPath("$.recipesFavorited", is(0)));
    }
}
