package com.recipe.favourites_service.repository;

import com.recipe.favourites_service.model.Favourite;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the data access layer in isolation.
 * @DataJpaTest loads only JPA components and uses an in-memory DB by default.
 * It's fast and perfect for testing custom queries.
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FavouritesRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FavouritesRepository favouritesRepository;

    @Test
    public void whenFindByUserId_shouldReturnCorrectFavourites() {
        Favourite fav1_user1 = new Favourite();
        fav1_user1.setUserId(1L);
        fav1_user1.setRecipeId(100L);
        entityManager.persist(fav1_user1);

        Favourite fav2_user1 = new Favourite();
        fav2_user1.setUserId(1L);
        fav2_user1.setRecipeId(101L);
        entityManager.persist(fav2_user1);

        Favourite fav1_user2 = new Favourite();
        fav1_user2.setUserId(2L);
        fav1_user2.setRecipeId(100L);
        entityManager.persist(fav1_user2);

        entityManager.flush();

        List<Favourite> foundFavourites = favouritesRepository.findByUserId(1L);

        assertThat(foundFavourites).hasSize(2);
        assertThat(foundFavourites).extracting(Favourite::getRecipeId).containsExactlyInAnyOrder(100L, 101L);
    }

    @Test
    public void whenFindByUserId_withNoFavourites_shouldReturnEmptyList() {
        List<Favourite> foundFavourites = favouritesRepository.findByUserId(99L);

        assertThat(foundFavourites).isEmpty();
    }

    @Test
    public void whenCountByRecipeId_shouldReturnCorrectCount() {
        Favourite fav1 = new Favourite();
        fav1.setUserId(1L);
        fav1.setRecipeId(100L);
        entityManager.persist(fav1);

        Favourite fav2 = new Favourite();
        fav2.setUserId(2L);
        fav2.setRecipeId(100L);
        entityManager.persist(fav2);

        entityManager.flush();

        long count = favouritesRepository.countByRecipeId(100L);

        assertThat(count).isEqualTo(2);
    }

    @Test
    public void whenCountByRecipeId_withNoFavourites_shouldReturnZero() {
        long count = favouritesRepository.countByRecipeId(99L);

        assertThat(count).isEqualTo(0);
    }
}
