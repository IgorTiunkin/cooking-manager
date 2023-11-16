package com.phantom.recipe.services;

import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.exceptions.RecipeNotFoundException;
import com.phantom.recipe.exceptions.RecipeSaveException;
import com.phantom.recipe.exceptions.RecipeUpdateException;
import com.phantom.recipe.mappers.RecipeDTOMapper;
import com.phantom.recipe.mappers.RecipeMapper;
import com.phantom.recipe.repositories.RecipeRepository;
import com.phantom.recipe.models.Recipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final RecipeMapper recipeMapper;


    public List<RecipeRestDTO> getAllRecipes() {
        List<Recipe> recipeList = recipeRepository.findAll();
        return recipeDTOMapper.mapToRecipeRestDTOList(recipeList);
    }

    public RecipeRestDTO getRecipeById(Integer recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe no found"));
        log.info("Found recipe # {}",  recipeId);
        return recipeDTOMapper.mapToRecipeRestDTOList(List.of(recipe)).get(0);
    }

    public Optional<Recipe> getRecipeByTitle(String title) {
        return recipeRepository.findRecipeByTitle(title);
    }

    @Transactional
    public RecipeRestDTO save(RecipeRestDTO recipeRestDTO) {
        Recipe recipe = recipeMapper.convertToRecipe(recipeRestDTO);

        //If recipe with such name absent - save
        Optional<Recipe> recipeByTitle = getRecipeByTitle(recipe.getTitle());
        if (recipeByTitle.isEmpty()) {
            Recipe savedRecipe = recipeRepository.save(recipe);
            return recipeDTOMapper.mapToRecipeRestDTOList(List.of(savedRecipe)).get(0);
        }

        //if present - if absolute copy - ignore, else - block save if exception
        Recipe recipeFromDBByTitle = recipeByTitle.get();
        if (recipe.equals(recipeFromDBByTitle)) {
            return recipeDTOMapper.mapToRecipeRestDTOList(List.of(recipeFromDBByTitle)).get(0);
        }
        throw new RecipeSaveException("Such product name already present");
    }

    @Transactional
    public RecipeRestDTO delete(Integer recipeId) {
        RecipeRestDTO recipeById = getRecipeById(recipeId);
        recipeRepository.deleteById(recipeId);
        return recipeById;
    }

    @Transactional
    public RecipeRestDTO update(RecipeRestDTO recipeRestDTO) {
        Recipe recipe = recipeMapper.convertToRecipe(recipeRestDTO);

        //If recipe with such name absent - update
        Optional<Recipe> recipeByTitle = getRecipeByTitle(recipe.getTitle());
        if (recipeByTitle.isEmpty()) {
            Recipe savedRecipe = recipeRepository.save(recipe);
            return recipeDTOMapper.mapToRecipeRestDTOList(List.of(savedRecipe)).get(0);
        }

        //if present - if absolute copy - ignore, else - block update if exception
        Recipe recipeFromDBByTitle = recipeByTitle.get();
        if (recipe.equals(recipeFromDBByTitle)) {
            return recipeDTOMapper.mapToRecipeRestDTOList(List.of(recipeFromDBByTitle)).get(0);
        }
        throw new RecipeUpdateException("Such product name already present");
    }

    public List<RecipeRestDTO> getRecipeByProductId(Integer productId) {
        List<Integer> allRecipesIdWithProduct = recipeRepository.findAllRecipesIdWithProduct(productId);
        List<Recipe> allRecipesWithProduct = recipeRepository.findAllByRecipeIdIn(allRecipesIdWithProduct);
        return recipeDTOMapper.mapToRecipeRestDTOList(allRecipesWithProduct);
    }
}
