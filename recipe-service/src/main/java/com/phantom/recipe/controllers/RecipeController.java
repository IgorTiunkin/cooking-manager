package com.phantom.recipe.controllers;

import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.dto.RecipeRestElementOfListDTO;
import com.phantom.recipe.models.Recipe;
import com.phantom.recipe.services.RecipeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final ModelMapper modelMapper;

    @GetMapping("/all")
    public List<RecipeRestDTO> getAllRecipes() {
        List<RecipeRestDTO> allRecipes = recipeService.getAllRecipes();
        System.out.println(allRecipes);
        return allRecipes;
    }

    @GetMapping("/one")
    public RecipeRestDTO getRecipeById(@RequestParam ("recipeId") Integer recipeId) {
        Recipe recipeById = recipeService.getRecipeById(recipeId);
        return convertToRecipeRestDto(recipeById);
    }

    @PostMapping("/save")
    public RecipeRestDTO saveNewRecipe(@RequestBody RecipeRestDTO recipeDTO) {
        Recipe recipe = convertToRecipe(recipeDTO);
        boolean saved = recipeService.save(recipe);
        if (saved) {
            return recipeDTO;
        } else {
            throw new RuntimeException("saved failure");//todo custom exception
        }
    }

    @DeleteMapping("/delete")
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
