package com.recipe.favourites_service.repository;

import com.recipe.favourites_service.model.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavouritesRepository extends JpaRepository<Favourite, Long> {

    // Automatically generates a query for: GET /favorites/user/{userId}
    List<Favourite> findByUserId(Long userId);

    // Automatically generates a query for: GET /favorites/recipe/{recipeId}
    long countByRecipeId(Long recipeId);
}