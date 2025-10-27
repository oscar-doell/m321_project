package com.recipe.favourites_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for the Favorites Spring Boot service.
 * The @SpringBootApplication annotation enables component scanning,
 * which will find your controller, service, and repository.
 */
@SpringBootApplication
public class FavouritesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FavouritesServiceApplication.class, args);
	}

}
