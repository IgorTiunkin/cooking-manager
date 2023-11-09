package com.phantom.recipe.controllers;

import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.exceptions.SaveFailedException;
import com.phantom.recipe.mappers.RecipeMapper;
import com.phantom.recipe.models.Recipe;
import com.phantom.recipe.services.RecipeService;
import com.phantom.recipe.validators.RecipeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;
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
        boolean isFullDuplicateFound = recipeValidator.checkFullDuplicates(recipe);
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
    @ResponseStatus(HttpStatus.OK)
    public RecipeRestDTO deleteRecipe(@RequestBody RecipeRestDTO recipeRestDTO) {
        Recipe recipe = convertToRecipe(recipeRestDTO);
        boolean delete = recipeService.delete(recipe);
        if (delete) {
            return recipeRestDTO;
        } else {
            throw new RuntimeException("delete failure");//todo custom exception
        }

    }

    private Recipe convertToRecipe(RecipeRestDTO recipeRestDTO) {
        return modelMapper.map(recipeRestDTO, Recipe.class);
    }

    private RecipeRestDTO convertToRecipeRestDto(Recipe recipe) {
        return modelMapper.map(recipe, RecipeRestDTO.class);
    }


}
