package com.phantom.recipe.controllers;

import com.phantom.recipe.dto.RecipeRestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RecipeControllerTest {

    private final RecipeController recipeController;

    @Autowired
    RecipeControllerTest(RecipeController recipeController) {
        this.recipeController = recipeController;
    }

   @Test
    public void whenAll_then1() {
        List<RecipeRestDTO> allReceipts = recipeController.getAllRecipes();
        assertEquals(1, allReceipts.size());
    }

    @Test
    public void when1_thenSalad() {
        RecipeRestDTO receiptById = recipeController.getRecipeById(1);
        assertEquals("Salad", receiptById.getTitle());
    }


}