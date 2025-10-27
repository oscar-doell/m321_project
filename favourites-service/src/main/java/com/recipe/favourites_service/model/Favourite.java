package com.recipe.favourites_service.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Entity
@Table(name = "favourites")
public class Favourite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // These IDs link to entities in other services (User and Recipe)
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long recipeId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;
}