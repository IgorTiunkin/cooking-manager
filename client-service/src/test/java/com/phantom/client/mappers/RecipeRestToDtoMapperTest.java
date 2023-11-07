package com.phantom.client.mappers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RecipeRestToDtoMapperTest {

    private final RecipeRestToDtoMapper recipeRestToDtoMapper;

    @Autowired
    RecipeRestToDtoMapperTest(RecipeRestToDtoMapper recipeRestToDtoMapper) {
        this.recipeRestToDtoMapper = recipeRestToDtoMapper;
    }

}