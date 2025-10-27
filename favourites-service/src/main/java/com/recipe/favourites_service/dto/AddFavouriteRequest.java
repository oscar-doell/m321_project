package com.recipe.favourites_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddFavouriteRequest {

    @NotNull(message = "userId cannot be null")
    private Long userId;

    @NotNull(message = "recipeId cannot be null")
    private Long recipeId;
}