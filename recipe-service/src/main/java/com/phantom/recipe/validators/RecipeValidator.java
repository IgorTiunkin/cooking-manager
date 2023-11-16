package com.phantom.recipe.validators;

import com.phantom.recipe.models.Recipe;
import com.phantom.recipe.services.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecipeValidator {

    private final RecipeService recipeService;


    public boolean checkFullDuplicatesByTitle(Recipe recipe) {
        Optional<Recipe> recipeByTitle = recipeService.getRecipeByTitle(recipe.getTitle());
        if (recipeByTitle.isEmpty()) return false;
        Recipe recipeFromDb = recipeByTitle.get();
        return recipeFromDb.equals(recipe);
    }

    public boolean dbHasRecipeWithSameName(Recipe recipe) {
        Optional<Recipe> recipeByTitle = recipeService.getRecipeByTitle(recipe.getTitle());
        return recipeByTitle.isPresent();
    }

    public boolean checkIdEqualityForSameTitle(Recipe recipe) {
        Optional<Recipe> recipeByTitle = recipeService.getRecipeByTitle(recipe.getTitle());
        if (recipeByTitle.isEmpty()) return false;
        Recipe recipeFromDb = recipeByTitle.get();
        return recipeFromDb.getRecipeId() == recipe.getRecipeId();
    }
}
