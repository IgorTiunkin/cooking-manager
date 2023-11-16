package com.phantom.recipe.integration.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phantom.recipe.controllers.RecipeController;
import com.phantom.recipe.dto.ProductAndQuantityDTO;
import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.exceptions.RecipeNotFoundException;
import com.phantom.recipe.exceptions.RecipeSaveException;
import com.phantom.recipe.integration.BaseIT;
import com.phantom.recipe.models.Recipe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RecipeControllerIT extends BaseIT {

    private final RecipeController recipeController;
    private MockMvc mvc;
    private final ObjectMapper objectMapper;

    private final String BASE_URL = "/api/v1/recipe";
    private final String ALL_URL = BASE_URL + "/all";
    private final String ONE_ID_URL = BASE_URL + "/one";
    private final String SAVE_URL = BASE_URL + "/save";
    private final String DELETE_URL = BASE_URL + "/delete";
    private final String UPDATE_URL = BASE_URL + "/update";
    private final String ALL_BY_PRODUCT_URL = BASE_URL +"/all-by-product";

    private final String RECIPE_ID_PATH = "recipeId";
    private final String PRODUCT_ID_PATH = "productId";

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
    public RecipeControllerIT(RecipeController recipeController, ObjectMapper objectMapper) {
        this.recipeController = recipeController;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void initialiseRestAssuredMockMvcStandalone() {
        mvc = MockMvcBuilders.standaloneSetup(recipeController)
                .build();
    }

    @Test
    public void whenAll_then3() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(ALL_URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        RecipeRestDTO[] recipeRestDTOS = objectMapper.readValue(contentAsString, RecipeRestDTO[].class);

        assertEquals(3, recipeRestDTOS.length);

        RecipeRestDTO recipeRestDTO1 = recipeRestDTOS[0];
        assertAll(() -> assertEquals(RECIPE_REST_DTO_1, recipeRestDTO1),
                () -> assertEquals(RECIPE_REST_DTO_1.getRecipeId(), recipeRestDTO1.getRecipeId()));

        RecipeRestDTO recipeRestDTO2 = recipeRestDTOS[1];
        assertAll(() -> assertEquals(RECIPE_REST_DTO_2, recipeRestDTO2),
                () -> assertEquals(RECIPE_REST_DTO_2.getRecipeId(), recipeRestDTO2.getRecipeId()));

        RecipeRestDTO recipeRestDTO3 = recipeRestDTOS[2];
        assertAll(() -> assertEquals(RECIPE_REST_DTO_3, recipeRestDTO3),
                () -> assertEquals(RECIPE_REST_DTO_3.getRecipeId(), recipeRestDTO3.getRecipeId()));
    }

    @Test
    public void whenRecipeId1_thenRecipe1 () throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(ONE_ID_URL)
                .queryParam(RECIPE_ID_PATH, RECIPE_REST_DTO_1.getRecipeId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        RecipeRestDTO recipeRestDTO = objectMapper.readValue(contentAsString, RecipeRestDTO.class);

        assertAll(() -> assertEquals(RECIPE_REST_DTO_1, recipeRestDTO),
                () -> assertEquals(RECIPE_REST_DTO_1.getRecipeId(), recipeRestDTO.getRecipeId()));
    }

    @Test
    public void whenAbsentId_thenBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(ONE_ID_URL)
                .queryParam(RECIPE_ID_PATH, "6")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void whenAbsentTitle_andSave_thenSave() throws Exception {
        String restDTOAsString = objectMapper.writeValueAsString(RECIPE_REST_DTO_WITH_ABSENT_TITLE);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(SAVE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(restDTOAsString))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        RecipeRestDTO recipeRestDTO = objectMapper.readValue(contentAsString, RecipeRestDTO.class);

        assertEquals(RECIPE_REST_DTO_WITH_ABSENT_TITLE, recipeRestDTO);
    }

    @Test
    public void whenAbsoluteCopy_andSave_thenok() throws Exception {
        String restDTOAsString = objectMapper.writeValueAsString(RECIPE_REST_DTO_1_WITHOUT_ID);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(SAVE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(restDTOAsString))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        RecipeRestDTO recipeRestDTO = objectMapper.readValue(contentAsString, RecipeRestDTO.class);

        assertEquals(RECIPE_REST_DTO_1_WITHOUT_ID, recipeRestDTO);
    }

    @Test
    public void whenNotAbsoluteCopySameName_andSave_thenException() throws Exception {
        String restDTOAsString = objectMapper.writeValueAsString(RECIPE_REST_DTO_WITH_SAME_TITLE);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(SAVE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(restDTOAsString))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    public void whenDeleteAbsent_thenException() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(DELETE_URL)
                .queryParam(RECIPE_ID_PATH, "6")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    public void whenDeletePresent_thenOk() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(DELETE_URL)
                .queryParam(RECIPE_ID_PATH, RECIPE_REST_DTO_1.getRecipeId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        RecipeRestDTO recipeRestDTO = objectMapper.readValue(contentAsString, RecipeRestDTO.class);

        assertEquals(RECIPE_REST_DTO_1, recipeRestDTO);
    }

    @Test
    public void whenAbsentTitle_andUpdate_thenUpdate() throws Exception {
        String restDTOAsString = objectMapper.writeValueAsString(RECIPE_REST_DTO_WITH_ABSENT_TITLE);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(UPDATE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(restDTOAsString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        RecipeRestDTO recipeRestDTO = objectMapper.readValue(contentAsString, RecipeRestDTO.class);

        assertEquals(RECIPE_REST_DTO_WITH_ABSENT_TITLE, recipeRestDTO);
    }

    @Test
    public void whenAbsoluteCopy_andUpdate_thenok() throws Exception {
        String restDTOAsString = objectMapper.writeValueAsString(RECIPE_REST_DTO_1_WITHOUT_ID);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(UPDATE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(restDTOAsString))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        RecipeRestDTO recipeRestDTO = objectMapper.readValue(contentAsString, RecipeRestDTO.class);

        assertEquals(RECIPE_REST_DTO_1_WITHOUT_ID, recipeRestDTO);
    }

    @Test
    public void whenNotAbsoluteCopySameName_andUpdate_thenBadRequest() throws Exception {
        String restDTOAsString = objectMapper.writeValueAsString(RECIPE_REST_DTO_WITH_SAME_TITLE);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(SAVE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(restDTOAsString))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    public void whenProductId1_thenRecipeRestDTO1() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(ALL_BY_PRODUCT_URL)
                .queryParam(PRODUCT_ID_PATH, RECIPE1_PRODUCT_1_WATER.getProductId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        RecipeRestDTO [] recipeRestDTOs = objectMapper.readValue(contentAsString, RecipeRestDTO[].class);

        assertEquals(1, recipeRestDTOs.length);
        RecipeRestDTO recipeRestDTO = recipeRestDTOs[0];
        assertAll(() -> assertEquals(RECIPE_REST_DTO_1, recipeRestDTO),
                () -> assertEquals(RECIPE_REST_DTO_1.getRecipeId(), recipeRestDTO.getRecipeId()));
    }

    @Test
    public void whenProductId2_then2RecipeRestDTO() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(ALL_BY_PRODUCT_URL)
                .queryParam(PRODUCT_ID_PATH, RECIPE1_PRODUCT_2_BREAD.getProductId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        RecipeRestDTO [] recipeRestDTOs = objectMapper.readValue(contentAsString, RecipeRestDTO[].class);

        assertEquals(2, recipeRestDTOs.length);
        RecipeRestDTO recipeRestDTO = recipeRestDTOs[0];
        assertAll(() -> assertEquals(RECIPE_REST_DTO_1, recipeRestDTO),
                () -> assertEquals(RECIPE_REST_DTO_1.getRecipeId(), recipeRestDTO.getRecipeId()));
        RecipeRestDTO recipeRestDTO2 = recipeRestDTOs[1];
        assertAll(() -> assertEquals(RECIPE_REST_DTO_2, recipeRestDTO2),
                () -> assertEquals(RECIPE_REST_DTO_2.getRecipeId(), recipeRestDTO2.getRecipeId()));
    }


    @Test
    public void whenProductIdAbsent_then0RecipeRestDTO() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(ALL_BY_PRODUCT_URL)
                .queryParam(PRODUCT_ID_PATH, "6")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        RecipeRestDTO [] recipeRestDTOs = objectMapper.readValue(contentAsString, RecipeRestDTO[].class);

        assertEquals(0, recipeRestDTOs.length);
    }

}
