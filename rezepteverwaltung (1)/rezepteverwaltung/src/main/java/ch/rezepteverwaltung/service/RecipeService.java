package ch.rezepteverwaltung.service;

import ch.rezepteverwaltung.model.Recipe;
import ch.rezepteverwaltung.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    private final RecipeRepository repository;

    public RecipeService(RecipeRepository repository) {
        this.repository = repository;
    }

    public Recipe save(Recipe recipe) { return repository.save(recipe); }
    public List<Recipe> findAll() { return repository.findAll(); }
    public Optional<Recipe> findById(String id) { return repository.findById(Long.valueOf(id)); }
    public void deleteById(String id) { repository.deleteById(Long.valueOf(id)); }
}
