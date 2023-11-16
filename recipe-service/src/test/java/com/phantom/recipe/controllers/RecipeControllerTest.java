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




}