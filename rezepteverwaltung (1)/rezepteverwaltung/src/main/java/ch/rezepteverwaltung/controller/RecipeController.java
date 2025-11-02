package ch.rezepteverwaltung.controller;

import ch.rezepteverwaltung.model.Recipe;
import ch.rezepteverwaltung.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Recipe> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getById(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Recipe create(@RequestBody Recipe recipe) {
        return service.save(recipe);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> update(@PathVariable String id, @RequestBody Recipe recipe) {
        return service.findById(id)
                .map(r -> {
                    recipe.setId(id);
                    return ResponseEntity.ok(service.save(recipe));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
