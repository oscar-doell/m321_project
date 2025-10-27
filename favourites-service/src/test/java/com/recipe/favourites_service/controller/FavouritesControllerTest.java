package com.recipe.favourites_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe.favourites_service.dto.AddFavouriteRequest;
import com.recipe.favourites_service.model.Favourite;
import com.recipe.favourites_service.service.FavouritesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests the API (Controller) layer.
 * @WebMvcTest loads only the web layer (not the full app context).
 * We must @MockBean the service to isolate the controller.
 * This checks: "Is the API endpoint wired correctly? Does it accept/return correct JSON?
 * Does it call the service?"
 */
@WebMvcTest(FavouritesController.class)
public class FavouritesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FavouritesService favouritesService;

    @Test
    public void whenPostFavourites_shouldReturn200AndFavourite() throws Exception {
        AddFavouriteRequest request = new AddFavouriteRequest();
        request.setUserId(1L);
        request.setRecipeId(100L);

        Favourite responseFavourite = new Favourite();
        responseFavourite.setId(1L);
        responseFavourite.setUserId(1L);
        responseFavourite.setRecipeId(100L);

        when(favouritesService.addFavourite(anyLong(), anyLong())).thenReturn(responseFavourite);

        mockMvc.perform(post("/favourites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.recipeId", is(100)));
    }

    @Test
    public void whenPostFavourites_withInvalidInput_shouldReturn400() throws Exception {
        AddFavouriteRequest request = new AddFavouriteRequest();
        request.setRecipeId(100L);

        mockMvc.perform(post("/favourites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenDeleteFavourites_shouldReturn200() throws Exception {
        Long favouriteId = 1L;
        doNothing().when(favouritesService).removeFavourite(favouriteId);

        mockMvc.perform(delete("/favourites/{id}", favouriteId))
                .andExpect(status().isOk());
    }

    @Test
    public void whenGetFavouritesByUser_shouldReturn200AndList() throws Exception {
        Favourite fav1 = new Favourite();
        fav1.setId(1L);
        fav1.setUserId(1L);
        fav1.setRecipeId(100L);
        List<Favourite> favourites = List.of(fav1);

        when(favouritesService.getFavouritesByUserId(1L)).thenReturn(favourites);

        mockMvc.perform(get("/favourites/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    public void whenGetFavouritesForRecipe_shouldReturn200AndCount() throws Exception {
        when(favouritesService.countFavouritesByRecipeId(100L)).thenReturn(5L);

        mockMvc.perform(get("/favourites/recipe/{recipeId}", 100L))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
}
