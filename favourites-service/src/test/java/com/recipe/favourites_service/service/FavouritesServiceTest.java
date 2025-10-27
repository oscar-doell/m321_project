package com.recipe.favourites_service.service;

import com.recipe.favourites_service.model.Favourite;
import com.recipe.favourites_service.repository.FavouritesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests the business logic layer (Service).
 * We @Mock the repository to isolate the service logic.
 * We want to test "if I call the service, does it call the repository correctly?"
 */
@ExtendWith(MockitoExtension.class)
public class FavouritesServiceTest {

    @Mock
    private FavouritesRepository favouritesRepository;

    @InjectMocks
    private FavouritesService favouritesService;

    @Test
    public void whenAddFavorite_shouldSaveAndReturnFavourite() {
        Favourite favouriteToSave = new Favourite();
        favouriteToSave.setUserId(1L);
        favouriteToSave.setRecipeId(100L);

        when(favouritesRepository.save(any(Favourite.class))).thenAnswer(invocation -> {
            Favourite f = invocation.getArgument(0);
            f.setId(1L);
            return f;
        });

        Favourite savedFavourite = favouritesService.addFavourite(1L, 100L);

        assertThat(savedFavourite).isNotNull();
        assertThat(savedFavourite.getId()).isEqualTo(1L);
        assertThat(savedFavourite.getUserId()).isEqualTo(1L);
        
        verify(favouritesRepository, times(1)).save(any(Favourite.class));
    }

    @Test
    public void whenRemoveFavourite_shouldCallDeleteById() {
        Long favouriteId = 1L;
        doNothing().when(favouritesRepository).deleteById(favouriteId);

        favouritesService.removeFavourite(favouriteId);

        verify(favouritesRepository, times(1)).deleteById(favouriteId);
    }

    @Test
    public void whenGetFavoritesByUserId_shouldReturnFavouriteList() {
        Favourite fav1 = new Favourite();
        fav1.setUserId(1L);
        fav1.setRecipeId(100L);
        List<Favourite> mockList = List.of(fav1);
        
        when(favouritesRepository.findByUserId(1L)).thenReturn(mockList);

        List<Favourite> result = favouritesService.getFavouritesByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRecipeId()).isEqualTo(100L);
        verify(favouritesRepository, times(1)).findByUserId(1L);
    }

    @Test
    public void whenCountFavouritesByRecipeId_shouldReturnCount() {
        when(favouritesRepository.countByRecipeId(100L)).thenReturn(5L);

        long count = favouritesService.countFavouritesByRecipeId(100L);

        assertThat(count).isEqualTo(5L);
        verify(favouritesRepository, times(1)).countByRecipeId(100L);
    }
}