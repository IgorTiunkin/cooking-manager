package com.phantom.recipe.controllers;

import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.exceptions.RecipeNotFoundException;
import com.phantom.recipe.mappers.RecipeMapper;
import com.phantom.recipe.models.Recipe;
import com.phantom.recipe.services.RecipeService;
import com.phantom.recipe.validators.RecipeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recipe")
@RequiredArgsConstructor
@Slf4j
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeMapper recipeMapper;
    private final RecipeValidator recipeValidator;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<RecipeRestDTO> getAllRecipes() {
        log.info("Request all recipes");
        return recipeService.getAllRecipes();
    }

    @GetMapping("/one")
    @ResponseStatus(HttpStatus.OK)
    public RecipeRestDTO getRecipeById(@RequestParam ("recipeId") Integer recipeId) {
        log.info("Request recipe # {}", recipeId);
        return recipeService.getRecipeById(recipeId);
    }

    @PostMapping("/save")
    public ResponseEntity <RecipeRestDTO> saveNewRecipe(@RequestBody RecipeRestDTO recipeRestDTO) {
        log.info("Request saving recipe: title - {}", recipeRestDTO.getTitle());
        Recipe recipe = recipeMapper.convertToRecipe(recipeRestDTO);
        boolean isFullDuplicateFound = recipeValidator.checkFullDuplicatesByTitle(recipe);
        boolean dbHasRecipeWithSameName = recipeValidator.dbHasRecipeWithSameName(recipe);
        if (!isFullDuplicateFound && dbHasRecipeWithSameName) {
            //countermeasure for retry - exception only for same name and different inside
            log.info("Exception. Same title is present");
            return new ResponseEntity<>(recipeRestDTO, HttpStatus.BAD_REQUEST);
        }
        if (!isFullDuplicateFound) {
            Recipe savedRecipe = recipeService.save(recipe);
            recipeRestDTO.setRecipeId(savedRecipe.getRecipeId());
            log.info("Successfully saved: title - {}", recipeRestDTO.getTitle());
        }
        return new ResponseEntity<>(recipeRestDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete")
    public ResponseEntity <RecipeRestDTO> deleteRecipe(@RequestParam Integer recipeId) {
        try {
            RecipeRestDTO recipeRestDTO = recipeService.delete(recipeId);
            log.info("Deleted recipe from db. Recipe id = {}", recipeRestDTO.getRecipeId());
            return new ResponseEntity<>(recipeRestDTO, HttpStatus.OK);
        } catch (RecipeNotFoundException recipeNotFoundException) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/update")
    public ResponseEntity <RecipeRestDTO> updateRecipe(@RequestBody RecipeRestDTO recipeRestDTO) {
        log.info("Request updating recipe: title - {}", recipeRestDTO.getTitle());
        Recipe recipe = recipeMapper.convertToRecipe(recipeRestDTO);
        recipe.setRecipeId(recipeRestDTO.getRecipeId());
        boolean dbHasRecipeWithSameName = recipeValidator.dbHasRecipeWithSameName(recipe);
        boolean isIdEqualsForSameTitle = recipeValidator.checkIdEqualityForSameTitle(recipe);
        if (!dbHasRecipeWithSameName || isIdEqualsForSameTitle) {
            //if there are no same title or recipe with same title has same id
            Recipe savedRecipe = recipeService.save(recipe);
            log.info("Successfully updated: title - {}", savedRecipe.getTitle());
            return new ResponseEntity<>(recipeRestDTO, HttpStatus.CREATED);
        }
        //different ids. same title - then exception
        log.info("Exception. Same title is present");
        return new ResponseEntity<>(recipeRestDTO, HttpStatus.BAD_REQUEST);
    }

}
