package com.phantom.recipe.controllers;

import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.mappers.RecipeMapper;
import com.phantom.recipe.models.Recipe;
import com.phantom.recipe.services.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<RecipeRestDTO> getAllRecipes() {
        log.info("Request all recipes");
        List<RecipeRestDTO> allRecipes = recipeService.getAllRecipes();
        return allRecipes;
    }

    @GetMapping("/one")
    @ResponseStatus(HttpStatus.OK)
    public RecipeRestDTO getRecipeById(@RequestParam ("recipeId") Integer recipeId) {
        log.info("Request recipe # {}", recipeId);
        return recipeService.getRecipeById(recipeId);
    }

    @PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeRestDTO saveNewRecipe(@RequestBody RecipeRestDTO recipeDTO) {
        log.info("Request saving recipe: title - {}", recipeDTO.getTitle()); //todo - check duplicates
        Recipe recipe = recipeMapper.convertToRecipe(recipeDTO);
        boolean saved = recipeService.save(recipe);
        if (saved) {
            return recipeDTO;
        } else {
            throw new RuntimeException("saved failure");//todo custom exception
        }
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
