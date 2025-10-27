package com.recipe.favourites_service.controller;

import com.recipe.favourites_service.dto.AddFavouriteRequest;
import com.recipe.favourites_service.model.Favourite;
import com.recipe.favourites_service.service.FavouritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/favourites")
public class FavouritesController {

    @Autowired
    private FavouritesService favouritesService;

    @PostMapping
    public Favourite addFavourite(@Valid @RequestBody AddFavouriteRequest request) {
        return favouritesService.addFavourite(request.getUserId(), request.getRecipeId());
    }

    // DELETE /favourites/{id} → remove favorite
    @DeleteMapping("/{id}")
    public void removeFavourite(@PathVariable Long id) {
        favouritesService.removeFavourite(id);
    }

    // GET /favourites/user/{userId} → list a user’s favorites
    @GetMapping("/user/{userId}")
    public List<Favourite> getFavouritesByUser(@PathVariable Long userId) {
        return favouritesService.getFavouritesByUserId(userId);
    }

    // GET /favourites/recipe/{recipeId} → how many favorites a recipe has
    @GetMapping("/recipe/{recipeId}")
    public long countFavouritesForRecipe(@PathVariable Long recipeId) {
        return favouritesService.countFavouritesByRecipeId(recipeId);
    }
}