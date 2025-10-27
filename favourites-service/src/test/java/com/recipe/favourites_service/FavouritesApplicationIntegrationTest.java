package com.recipe.favourites_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe.favourites_service.dto.AddFavouriteRequest;
import com.recipe.favourites_service.model.Favourite;
import com.recipe.favourites_service.repository.FavouritesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @SpringBootTest loads the entire application.
 * @Testcontainers starts a real PostgreSQL DB in a Docker container.
 * This test verifies the entire application stack, from API to the real DB.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class FavouritesApplicationIntegrationTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FavouritesRepository favouritesRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @AfterEach
    public void cleanup() {
        favouritesRepository.deleteAll();
    }

    @Test
    public void testFavouriteLifecycle() throws Exception {
        AddFavouriteRequest request = new AddFavouriteRequest();
        request.setUserId(1L);
        request.setRecipeId(100L);

        // 1. POST: Create a new favorite
        MvcResult postResult = mockMvc.perform(post("/favourites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        // Extract the created favorite's ID
        Favourite createdFavourite = objectMapper.readValue(postResult.getResponse().getContentAsString(), Favourite.class);
        Long createdId = createdFavourite.getId();

        // 2. GET (by User): Verify the favourite was saved for the user
        mockMvc.perform(get("/favourites/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(createdId.intValue())));

        // 3. GET (by Recipe): Verify the count for the recipe is 1
        mockMvc.perform(get("/favourites/recipe/{recipeId}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(1)));
        
        // 4. GET (Actuator Health): Verify the service is health
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));

        // 5. DELETE: Remove the favourite
        mockMvc.perform(delete("/favourites/{id}", createdId))
                .andExpect(status().isOk());

        // 6. GET (by Recipe): Verify the count is now 0
        mockMvc.perform(get("/favourites/recipe/{recipeId}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(0)));

        // 7. GET (by User): Verify the list is now empty
        mockMvc.perform(get("/favourites/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
