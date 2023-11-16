package com.phantom.recipe.integration.services;

import com.phantom.recipe.dto.ProductAndQuantityDTO;
import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.exceptions.RecipeNotFoundException;
import com.phantom.recipe.exceptions.RecipeSaveException;
import com.phantom.recipe.exceptions.RecipeUpdateException;
import com.phantom.recipe.integration.BaseIT;
import com.phantom.recipe.models.Recipe;
import com.phantom.recipe.services.RecipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeServiceIT extends BaseIT {

    private final RecipeService recipeService;

    private final ProductAndQuantityDTO RECIPE1_PRODUCT_1_WATER = ProductAndQuantityDTO.builder()
            .productId(1)
            .productName("water")
            .calories(10)
            .quantity(3)
            .build();
    private final ProductAndQuantityDTO RECIPE1_PRODUCT_2_BREAD = ProductAndQuantityDTO.builder()
            .productId(2)
            .productName("bread")
            .calories(200)
            .quantity(5)
            .build();
    private final ProductAndQuantityDTO RECIPE2_PRODUCT_3_TOMATO = ProductAndQuantityDTO.builder()
            .productId(3)
            .productName("tomato")
            .calories(50)
            .quantity(5)
            .build();
    private final ProductAndQuantityDTO RECIPE2_PRODUCT_2_BREAD = ProductAndQuantityDTO.builder()
            .productId(2)
            .productName("bread")
            .calories(200)
            .quantity(3)
            .build();
    private final RecipeRestDTO RECIPE_REST_DTO_1 = RecipeRestDTO.builder()
            .recipeId(1)
            .title("Salad")
            .actions("Mix.")
            .productAndQuantityDTOList(List.of(RECIPE1_PRODUCT_1_WATER, RECIPE1_PRODUCT_2_BREAD))
            .build();
    private final RecipeRestDTO RECIPE_REST_DTO_2 = RecipeRestDTO.builder()
            .recipeId(2)
            .title("Tomato and bread")
            .actions("Just eat")
            .productAndQuantityDTOList(List.of(RECIPE2_PRODUCT_2_BREAD, RECIPE2_PRODUCT_3_TOMATO))
            .build();
    private final RecipeRestDTO RECIPE_REST_DTO_3 = RecipeRestDTO.builder()
            .recipeId(3)
            .title("Simple bread")
            .actions("Eat")
            .productAndQuantityDTOList(new ArrayList<>())
            .build();
    private final Map<Integer, Integer> PRODUCT_IDS_AND_QUANTITIES_FOR_RECIPE_1 =
            Map.of(RECIPE1_PRODUCT_1_WATER.getProductId(), RECIPE1_PRODUCT_1_WATER.getQuantity(),
                    RECIPE1_PRODUCT_2_BREAD.getProductId(), RECIPE1_PRODUCT_2_BREAD.getQuantity());
    private final Recipe RECIPE_1 = Recipe.builder()
            .recipeId(1)
            .title("Salad")
            .actions("Mix.")
            .productIDsAndQuantities(PRODUCT_IDS_AND_QUANTITIES_FOR_RECIPE_1)
            .build();
    private final RecipeRestDTO RECIPE_REST_DTO_1_WITHOUT_ID = RecipeRestDTO.builder()
            .title("Salad")
            .actions("Mix.")
            .productAndQuantityDTOList(List.of(RECIPE1_PRODUCT_1_WATER, RECIPE1_PRODUCT_2_BREAD))
            .build();
    private final RecipeRestDTO RECIPE_REST_DTO_WITH_ABSENT_TITLE = RecipeRestDTO.builder()
            .title("Test")
            .actions("Test")
            .productAndQuantityDTOList(new ArrayList<>()).build();
    private final RecipeRestDTO RECIPE_REST_DTO_WITH_SAME_TITLE = RecipeRestDTO.builder()
            .title(RECIPE_1.getTitle())
            .actions("Test2")
            .productAndQuantityDTOList(new ArrayList<>()).build();

    @Autowired
    public RecipeServiceIT(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @Test
    public void whenAll_then3() {
        List<RecipeRestDTO> allRecipes = recipeService.getAllRecipes();
        assertEquals(3, allRecipes.size());

        RecipeRestDTO recipeRestDTO1 = allRecipes.get(0);
        assertAll(() -> assertEquals(RECIPE_REST_DTO_1, recipeRestDTO1),
                () -> assertEquals(RECIPE_REST_DTO_1.getRecipeId(), recipeRestDTO1.getRecipeId()));

        RecipeRestDTO recipeRestDTO2 = allRecipes.get(1);
        assertAll(() -> assertEquals(RECIPE_REST_DTO_2, recipeRestDTO2),
                () -> assertEquals(RECIPE_REST_DTO_2.getRecipeId(), recipeRestDTO2.getRecipeId()));


        RecipeRestDTO recipeRestDTO3 = allRecipes.get(2);
        assertAll(() -> assertEquals(RECIPE_REST_DTO_3, recipeRestDTO3),
                () -> assertEquals(RECIPE_REST_DTO_3.getRecipeId(), recipeRestDTO3.getRecipeId()));
    }

    @Test
    public void whenRecipeId1_thenRecipe1 () {
        RecipeRestDTO recipeById1 = recipeService.getRecipeById(RECIPE_REST_DTO_1.getRecipeId());
        assertAll(() -> assertEquals(RECIPE_REST_DTO_1, recipeById1),
                () -> assertEquals(RECIPE_REST_DTO_1.getRecipeId(), recipeById1.getRecipeId()));
    }

    @Test
    public void whenAbsentId_thenRecipeNotFoundException() {
        assertThrows(RecipeNotFoundException.class,
                () -> recipeService.getRecipeById(4));
    }

    @Test
    public void whenAbsentTitle_andGetByTitle_thenEmpty() {
        assertTrue(recipeService.getRecipeByTitle(RECIPE_REST_DTO_WITH_ABSENT_TITLE.getTitle()).isEmpty());
    }

    @Test
    public void whenPresentTitle_andGetByTitle_thenOk() {
        Optional<Recipe> recipeByTitle = recipeService.getRecipeByTitle(RECIPE_1.getTitle());
        assertTrue(recipeByTitle.isPresent());

        Recipe recipe = recipeByTitle.get();
        assertAll(() -> assertEquals(RECIPE_1.getTitle(), recipe.getTitle()),
                () -> assertEquals(RECIPE_1.getRecipeId(), recipe.getRecipeId()),
                () -> assertEquals(RECIPE_1.getActions(), recipe.getActions()));
    }

    @Test
    public void whenAbsentTitle_andSave_thenSave() {
        RecipeRestDTO savedRecipeRestDTO = recipeService.save(RECIPE_REST_DTO_WITH_ABSENT_TITLE);
        assertEquals(RECIPE_REST_DTO_WITH_ABSENT_TITLE.getTitle(), savedRecipeRestDTO.getTitle());
        Optional<Recipe> recipeByTitle = recipeService.getRecipeByTitle(RECIPE_REST_DTO_WITH_ABSENT_TITLE.getTitle());
        assertTrue(recipeByTitle.isPresent());
    }

    @Test
    public void whenAbsoluteCopy_andSave_thenok() {
        RecipeRestDTO savedRecipe = recipeService.save(RECIPE_REST_DTO_1_WITHOUT_ID);
        assertEquals(RECIPE_1.getRecipeId(), savedRecipe.getRecipeId());
    }

    @Test
    public void whenNotAbsoluteCopySameName_andSave_thenException() {
        assertThrows(RecipeSaveException.class,
                () -> recipeService.save(RECIPE_REST_DTO_WITH_SAME_TITLE));
    }

    @Test
    public void whenDeleteAbsent_thenException() {
        assertThrows(RecipeNotFoundException.class,
                () -> recipeService.delete(6));
    }

    @Test
    public void whenDeletePresent_thenOk() {
        RecipeRestDTO recipeRestDTO = recipeService.delete(RECIPE_REST_DTO_1.getRecipeId());
        assertAll( () -> assertEquals(RECIPE_REST_DTO_1.getRecipeId(), recipeRestDTO.getRecipeId()),
                () -> assertEquals(RECIPE_REST_DTO_1.getTitle(), recipeRestDTO.getTitle()),
                () -> assertEquals(RECIPE_REST_DTO_1.getActions(), recipeRestDTO.getActions()));
    }

    @Test
    public void whenAbsentTitle_andUpdate_thenUpdate() {
        RecipeRestDTO savedRecipeRestDTO = recipeService.update(RECIPE_REST_DTO_WITH_ABSENT_TITLE);
        assertEquals(RECIPE_REST_DTO_WITH_ABSENT_TITLE.getTitle(), savedRecipeRestDTO.getTitle());
        Optional<Recipe> recipeByTitle = recipeService.getRecipeByTitle(RECIPE_REST_DTO_WITH_ABSENT_TITLE.getTitle());
        assertTrue(recipeByTitle.isPresent());
    }

    @Test
    public void whenAbsoluteCopy_andUpdate_thenok() {
        RecipeRestDTO savedRecipe = recipeService.update(RECIPE_REST_DTO_1_WITHOUT_ID);
        assertEquals(RECIPE_1.getRecipeId(), savedRecipe.getRecipeId());
    }

    @Test
    public void whenNotAbsoluteCopySameName_andUpdate_thenException() {
        assertThrows(RecipeUpdateException.class,
                () -> recipeService.update(RECIPE_REST_DTO_WITH_SAME_TITLE));
    }

    @Test
    public void whenProductId1_thenRecipeRestDTO1() {
        List<RecipeRestDTO> recipeByProductId = recipeService.getRecipeByProductId(RECIPE1_PRODUCT_1_WATER.getProductId());
        assertEquals(1, recipeByProductId.size());
        RecipeRestDTO recipeRestDTO = recipeByProductId.get(0);
        assertEquals(RECIPE_REST_DTO_1, recipeRestDTO);
    }

    @Test
    public void whenProductId2_then2RecipeRestDTO() {
        List<RecipeRestDTO> recipeByProductId = recipeService.getRecipeByProductId(RECIPE2_PRODUCT_2_BREAD.getProductId());
        assertEquals(2, recipeByProductId.size());
        RecipeRestDTO recipeRestDTO1 = recipeByProductId.get(0);
        assertEquals(RECIPE_REST_DTO_1, recipeRestDTO1);
        RecipeRestDTO recipeRestDTO2 = recipeByProductId.get(1);
        assertEquals(RECIPE_REST_DTO_2, recipeRestDTO2);
    }

    @Test
    public void whenProductIdAbsent_then0RecipeRestDTO() {
        List<RecipeRestDTO> recipeByProductId = recipeService.getRecipeByProductId(6);
        assertEquals(0, recipeByProductId.size());
    }

}
