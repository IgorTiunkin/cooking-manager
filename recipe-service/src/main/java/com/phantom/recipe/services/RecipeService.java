package com.phantom.recipe.services;

import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.exceptions.RecipeNotFoundException;
import com.phantom.recipe.mappers.RecipeDTOMapper;
import com.phantom.recipe.repositories.RecipeRepository;
import com.phantom.recipe.models.Recipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeDTOMapper recipeDTOMapper;


    public List<RecipeRestDTO> getAllRecipes() {
        List<Recipe> recipeList = recipeRepository.findAll();
        return recipeDTOMapper.mapToRecipeRestDTOList(recipeList);
    }

    @Transactional
    public Recipe save(Recipe recipe) {
        Recipe savedRecipe = recipeRepository.save(recipe);//todo - exception
        log.info("recipe saved in db {}", savedRecipe.getTitle());
        return savedRecipe;
    }

    @Transactional
    public RecipeRestDTO delete(Integer recipeId) {
        RecipeRestDTO recipeById = getRecipeById(recipeId);
        recipeRepository.deleteById(recipeId);
        return recipeById;
    }

    public RecipeRestDTO getRecipeById(Integer recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException("Recipe no found"));
        log.info("Found recipe # {}",  recipeId);
        return recipeDTOMapper.mapToRecipeRestDTOList(List.of(recipe)).get(0);//todo use base method
    }

    public Optional<Recipe> getRecipeByTitle(String title) {
        return recipeRepository.findRecipeByTitle(title);
    }
}
