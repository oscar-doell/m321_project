package com.example.statistikdienst.controller;

import com.example.statistikdienst.model.dto.PopularRecipe;
import com.example.statistikdienst.model.dto.UserStat;
import com.example.statistikdienst.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/popular")
    public List<PopularRecipe> getPopularRecipes() {
        return statisticsService.getMostPopularRecipes();
    }

    @GetMapping("/categories")
    public Map<String, Long> getCategories() {
        return statisticsService.getRecipeCountPerCategory();
    }

    @GetMapping("/users/{userId}")
    public UserStat getUserStats(@PathVariable Long userId) {
        return statisticsService.getUserStats(userId);
    }
}
