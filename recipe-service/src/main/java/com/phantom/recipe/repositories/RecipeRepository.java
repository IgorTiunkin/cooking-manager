package com.phantom.recipe.repositories;

import com.phantom.recipe.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository <Recipe, Integer> {

    Optional <Recipe> findRecipeByTitle(String title);

    @Query(value = "select recipe_id from recipe_productids_and_quantities r where productids_and_quantities_key = ?1"
            , nativeQuery = true)
    List<Integer> findAllRecipesIdWithProduct(Integer productId);

    List <Recipe> findAllByRecipeIdIn(List <Integer> recipeIds);
}
