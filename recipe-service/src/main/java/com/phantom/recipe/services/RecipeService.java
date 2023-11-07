package com.phantom.recipe.services;

import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.mappers.RecipeDTOMapper;
import com.phantom.recipe.repositories.RecipeRepository;
import com.phantom.recipe.models.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeDTOMapper recipeDTOMapper;


    public List<RecipeRestDTO> getAllRecipes() {
        List<Recipe> recipeList = recipeRepository.findAll();
        return recipeDTOMapper.mapToRecipeRestDTOList(recipeList);
    }

    @Transactional
    public boolean save(Recipe recipe) {
        recipeRepository.save(recipe);
        return true;
    }

    public boolean delete(Recipe recipe) {
        recipeRepository.delete(recipe);
        return true;
    }

    public Recipe getRecipeById(int id) {
        return recipeRepository.findById(id).orElseThrow(RuntimeException::new);//todo custom exception
    }
}
