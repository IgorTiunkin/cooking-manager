package com.phantom.recipe.integration.services;

import com.phantom.recipe.dto.ProductAndQuantityDTO;
import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.exceptions.RecipeNotFoundException;
import com.phantom.recipe.exceptions.RecipeSaveException;
import com.phantom.recipe.integration.BaseIT;
import com.phantom.recipe.models.Recipe;
import com.phantom.recipe.services.RecipeService;
import org.junit.jupiter.api.Assertions;
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
    private final Recipe RECIPE_1_WITHOUT_ID = Recipe.builder()
            .title("Salad")
            .actions("Mix.")
            .productIDsAndQuantities(PRODUCT_IDS_AND_QUANTITIES_FOR_RECIPE_1)
            .build();
    private final Recipe RECIPE_WITH_ABSENT_TITLE = Recipe.builder().title("Test").actions("Test")
            .productIDsAndQuantities(new HashMap<>()).build();
    private final Recipe RECIPE_WITH_SAME_TITLE = Recipe.builder().title(RECIPE_1.getTitle()).actions("Test2")
            .productIDsAndQuantities(new HashMap<>()).build();

    @Autowired
    public RecipeServiceIT(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @Test
    public void whenAll_then3() {
        List<RecipeRestDTO> allRecipes = recipeService.getAllRecipes();
        assertEquals(3, allRecipes.size());

        RecipeRestDTO recipeRestDTO1 = allRecipes.get(0);
        List<ProductAndQuantityDTO> productAndQuantityDTOList1 = recipeRestDTO1.getProductAndQuantityDTOList();
        ProductAndQuantityDTO recipe1product1 = productAndQuantityDTOList1.get(0);
        ProductAndQuantityDTO recipe1product2 = productAndQuantityDTOList1.get(1);
        assertAll(() -> assertEquals(RECIPE_REST_DTO_1.getRecipeId(), recipeRestDTO1.getRecipeId()),
                () -> assertEquals(RECIPE_REST_DTO_1.getTitle(), recipeRestDTO1.getTitle()),
                () -> assertEquals(RECIPE_REST_DTO_1.getActions(), recipeRestDTO1.getActions()),
                () -> assertEquals(RECIPE1_PRODUCT_1_WATER.getProductId(), recipe1product1.getProductId()),
                () -> assertEquals(RECIPE1_PRODUCT_1_WATER.getProductName(), recipe1product1.getProductName()),
                () -> assertEquals(RECIPE1_PRODUCT_1_WATER.getCalories(), recipe1product1.getCalories()),
                () -> assertEquals(RECIPE1_PRODUCT_1_WATER.getQuantity(), recipe1product1.getQuantity()),
                () -> assertEquals(RECIPE1_PRODUCT_2_BREAD.getProductId(), recipe1product2.getProductId()),
                () -> assertEquals(RECIPE1_PRODUCT_2_BREAD.getProductName(), recipe1product2.getProductName()),
                () -> assertEquals(RECIPE1_PRODUCT_2_BREAD.getCalories(), recipe1product2.getCalories()),
                () -> assertEquals(RECIPE1_PRODUCT_2_BREAD.getQuantity(), recipe1product2.getQuantity()));


        RecipeRestDTO recipeRestDTO2 = allRecipes.get(1);
        List<ProductAndQuantityDTO> productAndQuantityDTOList2 = recipeRestDTO2.getProductAndQuantityDTOList();
        ProductAndQuantityDTO recipe2product1 = productAndQuantityDTOList2.get(0);
        ProductAndQuantityDTO recipe2product2 = productAndQuantityDTOList2.get(1);
        assertAll(() -> assertEquals(RECIPE_REST_DTO_2.getRecipeId(), recipeRestDTO2.getRecipeId()),
                () -> assertEquals(RECIPE_REST_DTO_2.getTitle(), recipeRestDTO2.getTitle()),
                () -> assertEquals(RECIPE_REST_DTO_2.getActions(), recipeRestDTO2.getActions()),
                () -> assertEquals(RECIPE2_PRODUCT_2_BREAD.getProductId(), recipe2product1.getProductId()),
                () -> assertEquals(RECIPE2_PRODUCT_2_BREAD.getProductName(), recipe2product1.getProductName()),
                () -> assertEquals(RECIPE2_PRODUCT_2_BREAD.getCalories(), recipe2product1.getCalories()),
                () -> assertEquals(RECIPE2_PRODUCT_2_BREAD.getQuantity(), recipe2product1.getQuantity()),
                () -> assertEquals(RECIPE2_PRODUCT_3_TOMATO.getProductId(), recipe2product2.getProductId()),
                () -> assertEquals(RECIPE2_PRODUCT_3_TOMATO.getProductName(), recipe2product2.getProductName()),
                () -> assertEquals(RECIPE2_PRODUCT_3_TOMATO.getCalories(), recipe2product2.getCalories()),
                () -> assertEquals(RECIPE2_PRODUCT_3_TOMATO.getQuantity(), recipe2product2.getQuantity()));


        RecipeRestDTO recipeRestDTO3 = allRecipes.get(2);
        assertAll(() -> assertEquals(RECIPE_REST_DTO_3.getRecipeId(), recipeRestDTO3.getRecipeId()),
                () -> assertEquals(RECIPE_REST_DTO_3.getTitle(), recipeRestDTO3.getTitle()),
                () -> assertEquals(RECIPE_REST_DTO_3.getActions(), recipeRestDTO3.getActions()));
    }

    @Test
    public void whenRecipeId1_thenRecipe1 () {
        RecipeRestDTO recipeById1 = recipeService.getRecipeById(RECIPE_REST_DTO_1.getRecipeId());
        List<ProductAndQuantityDTO> productAndQuantityDTOList1 = recipeById1.getProductAndQuantityDTOList();
        ProductAndQuantityDTO recipe1product1 = productAndQuantityDTOList1.get(0);
        ProductAndQuantityDTO recipe1product2 = productAndQuantityDTOList1.get(1);
        assertAll(() -> assertEquals(RECIPE_REST_DTO_1.getRecipeId(), recipeById1.getRecipeId()),
                () -> assertEquals(RECIPE_REST_DTO_1.getTitle(), recipeById1.getTitle()),
                () -> assertEquals(RECIPE_REST_DTO_1.getActions(), recipeById1.getActions()),
                () -> assertEquals(RECIPE1_PRODUCT_1_WATER.getProductId(), recipe1product1.getProductId()),
                () -> assertEquals(RECIPE1_PRODUCT_1_WATER.getProductName(), recipe1product1.getProductName()),
                () -> assertEquals(RECIPE1_PRODUCT_1_WATER.getCalories(), recipe1product1.getCalories()),
                () -> assertEquals(RECIPE1_PRODUCT_1_WATER.getQuantity(), recipe1product1.getQuantity()),
                () -> assertEquals(RECIPE1_PRODUCT_2_BREAD.getProductId(), recipe1product2.getProductId()),
                () -> assertEquals(RECIPE1_PRODUCT_2_BREAD.getProductName(), recipe1product2.getProductName()),
                () -> assertEquals(RECIPE1_PRODUCT_2_BREAD.getCalories(), recipe1product2.getCalories()),
                () -> assertEquals(RECIPE1_PRODUCT_2_BREAD.getQuantity(), recipe1product2.getQuantity()));
    }

    @Test
    public void whenAbsentId_thenRecipeNotFoundException() {
        assertThrows(RecipeNotFoundException.class,
                () -> recipeService.getRecipeById(4));
    }

    @Test
    public void whenFullDuplicates_andCheckFullDuplicates_thenTrue() {
        assertTrue(recipeService.checkFullDuplicatesByTitle(RECIPE_1_WITHOUT_ID));
    }

    @Test
    public void whenAbsentTitle_andCheckFullDuplicates_thenFalse() {
        assertFalse(recipeService.checkFullDuplicatesByTitle(RECIPE_WITH_ABSENT_TITLE));
    }

    @Test
    public void whenPresentTitleWrongActions_andCheckFullDuplicates_thenFalse() {
        Recipe recipe = Recipe.builder().title(RECIPE_1.getTitle()).actions("Test").build();
        assertFalse(recipeService.checkFullDuplicatesByTitle(recipe));
    }

    @Test
    public void whenSameTitle_andCheckSameTitle_thenTrue() {
        assertTrue(recipeService.dbHasRecipeWithSameName(RECIPE_1_WITHOUT_ID));
    }

    @Test
    public void whenAbsentTitle_andCheckSameTitle_thenTrue() {
        Recipe recipe = Recipe.builder().recipeId(1).title("Test").build();
        assertFalse(recipeService.dbHasRecipeWithSameName(recipe));
    }

    @Test
    public void whenSameIdAndTitle_andCheckIDSame_thenTrue() {
        assertTrue(recipeService.checkIdEqualityForSameTitle(RECIPE_1));
    }

    @Test
    public void whenDifferentIdAndSameTitle_andCheckIDSame_thenFalse() {
        assertFalse(recipeService.checkIdEqualityForSameTitle(RECIPE_1_WITHOUT_ID));
    }

    @Test
    public void whenDifferentTitle_andCheckIDSame_thenFalse() {
        assertFalse(recipeService.checkIdEqualityForSameTitle(RECIPE_WITH_ABSENT_TITLE));
    }

    @Test
    public void whenAbsentTitle_andGetByTitle_thenEmpty() {
        assertTrue(recipeService.getRecipeByTitle(RECIPE_WITH_ABSENT_TITLE.getTitle()).isEmpty());
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
        Recipe savedRecipe = recipeService.save(RECIPE_WITH_ABSENT_TITLE);
        assertEquals(RECIPE_WITH_ABSENT_TITLE.getTitle(), savedRecipe.getTitle());
        Optional<Recipe> recipeByTitle = recipeService.getRecipeByTitle(RECIPE_WITH_ABSENT_TITLE.getTitle());
        assertTrue(recipeByTitle.isPresent());
    }

    @Test
    public void whenAbsoluteCopy_andSave_thenok() {
        Recipe savedRecipe = recipeService.save(RECIPE_1_WITHOUT_ID);
        assertEquals(RECIPE_1.getRecipeId(), savedRecipe.getRecipeId());
    }

    @Test
    public void whenNotAbsoluteCopySameName_andSave_thenException() {
        assertThrows(RecipeSaveException.class,
                () -> recipeService.save(RECIPE_WITH_SAME_TITLE));
    }

}
