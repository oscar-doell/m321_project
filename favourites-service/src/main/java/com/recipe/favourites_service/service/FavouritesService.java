package com.recipe.favourites_service.service;

import com.recipe.favourites_service.model.Favourite;
import com.recipe.favourites_service.repository.FavouritesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavouritesService {

    @Autowired
    private FavouritesRepository favouritesRepository;

    public Favourite addFavourite(Long userId, Long recipeId) {
        Favourite newFavourite = new Favourite();
        newFavourite.setUserId(userId);
        newFavourite.setRecipeId(recipeId);
        return favouritesRepository.save(newFavourite);
    }

    public void removeFavourite(Long favouriteId) {
        favouritesRepository.deleteById(favouriteId);
    }

    public List<Favourite> getFavouritesByUserId(Long userId) {
        return favouritesRepository.findByUserId(userId);
    }

    public long countFavouritesByRecipeId(Long recipeId) {
        return favouritesRepository.countByRecipeId(recipeId);
    }
}
