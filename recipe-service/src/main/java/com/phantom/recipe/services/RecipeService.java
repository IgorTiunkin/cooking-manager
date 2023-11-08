package com.phantom.recipe.services;

import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.mappers.RecipeDTOMapper;
import com.phantom.recipe.repositories.RecipeRepository;
import com.phantom.recipe.models.Recipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public boolean save(Recipe recipe) {
        recipeRepository.save(recipe);//todo - exception
        log.info("recipe saved in db {}", recipe.getTitle());
        return true;
    }

    public boolean delete(Recipe recipe) {
        recipeRepository.delete(recipe);
        return true;
    }

    public RecipeRestDTO getRecipeById(Integer recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(RuntimeException::new);//todo custom exception not found
        log.info("Found recipe # {}",  recipeId);
        return recipeDTOMapper.mapToRecipeRestDTOList(List.of(recipe)).get(0);
    }
}
