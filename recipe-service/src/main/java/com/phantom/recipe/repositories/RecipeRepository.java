package com.phantom.recipe.repositories;

import com.phantom.recipe.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository <Recipe, Integer> {
}
