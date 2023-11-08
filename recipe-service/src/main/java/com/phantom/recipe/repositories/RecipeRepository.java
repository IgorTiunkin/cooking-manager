package com.phantom.recipe.repositories;

import com.phantom.recipe.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository <Recipe, Integer> {

    Optional <Recipe> findRecipeByTitle(String title);
}
