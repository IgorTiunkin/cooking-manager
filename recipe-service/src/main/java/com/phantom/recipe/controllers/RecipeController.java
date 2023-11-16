package com.phantom.recipe.controllers;

import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.exceptions.RecipeNotFoundException;
import com.phantom.recipe.exceptions.RecipeSaveException;
import com.phantom.recipe.exceptions.RecipeUpdateException;
import com.phantom.recipe.services.RecipeService;
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

    @GetMapping("/all")
    public ResponseEntity <List <RecipeRestDTO>> getAllRecipes() {
        log.info("Request all recipes");
        List<RecipeRestDTO> recipeRestDTOList = recipeService.getAllRecipes();
        return new ResponseEntity<>(recipeRestDTOList, HttpStatus.OK);
    }

    @GetMapping("/one")
    public ResponseEntity <RecipeRestDTO> getRecipeById(@RequestParam ("recipeId") Integer recipeId) {
        log.info("Request recipe # {}", recipeId);
        RecipeRestDTO recipeRestDTO = recipeService.getRecipeById(recipeId);
        return new ResponseEntity<>(recipeRestDTO, HttpStatus.OK);
    }

    @ExceptionHandler (RecipeNotFoundException.class)
    public ResponseEntity <RecipeRestDTO> failedFoundRecipe(RecipeNotFoundException recipeNotFoundException) {
        log.info("Recipe not found {}", recipeNotFoundException.getMessage());
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/save")
    public ResponseEntity <RecipeRestDTO> saveNewRecipe(@RequestBody RecipeRestDTO recipeRestDTO) {
        log.info("Request saving recipe: title - {}", recipeRestDTO.getTitle());
        RecipeRestDTO savedRecipeRestDTO = recipeService.save(recipeRestDTO);
        return new ResponseEntity<>(savedRecipeRestDTO, HttpStatus.CREATED);
    }

    @ExceptionHandler (RecipeSaveException.class)
    public ResponseEntity <RecipeRestDTO> failedSaveRecipe(RecipeSaveException recipeSaveException) {
        log.info("Exception. Same title is present");
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    @DeleteMapping("/delete")
    public ResponseEntity <RecipeRestDTO> deleteRecipe(@RequestParam ("recipeId") Integer recipeId) {
        RecipeRestDTO recipeRestDTO = recipeService.delete(recipeId);
        log.info("Deleted recipe from db. Recipe id = {}", recipeRestDTO.getRecipeId());
        return new ResponseEntity<>(recipeRestDTO, HttpStatus.OK);
    }


    @PostMapping("/update")
    public ResponseEntity <RecipeRestDTO> updateRecipe(@RequestBody RecipeRestDTO recipeRestDTO) {
        log.info("Request updating recipe: title - {}", recipeRestDTO.getTitle());
        RecipeRestDTO updatedRecipeRestDTO = recipeService.update(recipeRestDTO);
        return new ResponseEntity<>(updatedRecipeRestDTO, HttpStatus.OK);
    }

    @ExceptionHandler (RecipeUpdateException.class)
    public ResponseEntity <RecipeRestDTO> failedSaveRecipe(RecipeUpdateException recipeUpdateException) {
        log.info("Exception. Same title is present");
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/all-by-product")
    public ResponseEntity <List <RecipeRestDTO>> getAllRecipesByProduct(
            @RequestParam ("productId") Integer productId) {
        log.info("Request all recipes by product. Id {}", productId);
        List<RecipeRestDTO> recipeByProductId = recipeService.getRecipeByProductId(productId);
        return new ResponseEntity<>(recipeByProductId, HttpStatus.OK);
    }



}
