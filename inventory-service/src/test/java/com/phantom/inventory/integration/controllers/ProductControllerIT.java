package com.phantom.inventory.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phantom.inventory.controllers.ProductController;
import com.phantom.inventory.dto.ProductDTO;
import com.phantom.inventory.integration.BaseIT;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@AutoConfigureMockMvc
public class ProductControllerIT extends BaseIT {

    private final ProductController productController;
    private MockMvc mvc;
    private final ObjectMapper objectMapper;

    private final String BASE_URL = "/api/v1/product";
    private final String ALL_URL = BASE_URL + "/all";
    private final String IN_ID_URL = BASE_URL + "/in";
    private final String ONE_ID_URL = BASE_URL + "/one";
    private final String SAVE_URL = BASE_URL + "/save";
    private final String DELETE_URL = BASE_URL + "/delete";
    private final String UPDATE_URL = BASE_URL + "/update";

    private final String PRODUCT_ID_PATH = "productId";
    private final String PRODUCT_NAME_PATH = "productName";
    private final String CALORIES_PATH = "calories";


    private final ProductDTO PRODUCT_1_WATER = ProductDTO.builder()
            .productId(1)
            .productName("water")
            .calories(10)
            .build();
    private final ProductDTO PRODUCT_2_BREAD = ProductDTO.builder()
            .productId(2)
            .productName("bread")
            .calories(200)
            .build();
    private final ProductDTO PRODUCT_6_TEST = ProductDTO.builder()
            .productName("test")
            .calories(100)
            .build();

    @Autowired
    public ProductControllerIT(ProductController productController, ObjectMapper objectMapper) {
        this.productController = productController;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void initialiseRestAssuredMockMvcStandalone() {
        mvc = MockMvcBuilders.standaloneSetup(productController)
                .build();
    }

    @Test
    public void whenAll_then5() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(ALL_URL)
        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(5)));
    }

    @Test
    public void when1_thenWater() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(ONE_ID_URL)
        .accept(MediaType.APPLICATION_JSON)
        .param("productId", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$."+ PRODUCT_NAME_PATH,
                        Matchers.is(PRODUCT_1_WATER.getProductName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$." + CALORIES_PATH,
                        Matchers.is(PRODUCT_1_WATER.getCalories())));
    }

    @Test
    public void whenAbsentId_thenBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(ONE_ID_URL)
        .accept(MediaType.APPLICATION_JSON)
        .param("productId", "6"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void when12_thenWaterBread() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(IN_ID_URL)
        .accept(MediaType.APPLICATION_JSON)
        .param("productIds", "1", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$",
                        Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]." + PRODUCT_NAME_PATH,
                        Matchers.in(List.of(PRODUCT_1_WATER.getProductName(), PRODUCT_2_BREAD.getProductName()))));
    }

    @Test
    public void whenSaveAbsent_then6() throws Exception {
        String body = objectMapper.writeValueAsString(PRODUCT_6_TEST);
        mvc.perform(MockMvcRequestBuilders.post(SAVE_URL)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + PRODUCT_ID_PATH,
                        Matchers.is(6)))
                .andExpect(MockMvcResultMatchers.jsonPath("$." + CALORIES_PATH,
                        Matchers.is(PRODUCT_6_TEST.getCalories())))
                .andExpect(MockMvcResultMatchers.jsonPath("$." + PRODUCT_NAME_PATH,
                        Matchers.is(PRODUCT_6_TEST.getProductName())));
    }

    @Test
    public void whenSavePresentNotCopy_thenException() throws Exception{
        String body = objectMapper.writeValueAsString(ProductDTO.builder().productName(PRODUCT_1_WATER.getProductName())
        .build());
        mvc.perform(MockMvcRequestBuilders.post(SAVE_URL)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void whenSavePresentCopy_thenOriginal() throws Exception{
        String body = objectMapper.writeValueAsString(
                ProductDTO.builder().productName(PRODUCT_1_WATER.getProductName())
                .calories(PRODUCT_1_WATER.getCalories())
                .build());
        mvc.perform(MockMvcRequestBuilders.post(SAVE_URL)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + PRODUCT_ID_PATH,
                        Matchers.is(PRODUCT_1_WATER.getProductId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$." + PRODUCT_NAME_PATH,
                        Matchers.is(PRODUCT_1_WATER.getProductName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$." + CALORIES_PATH,
                        Matchers.is(PRODUCT_1_WATER.getCalories())));
    }

    @Test
    public void whenUpdateAbsentName_thenSave() throws Exception {
        String newProductName = "new water";
        String body = objectMapper.writeValueAsString(ProductDTO.builder()
                .productId(PRODUCT_1_WATER.getProductId())
                .productName(newProductName)
                .calories(PRODUCT_1_WATER.getCalories())
        .build());
        mvc.perform(MockMvcRequestBuilders.post(UPDATE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + PRODUCT_ID_PATH,
                        Matchers.is(PRODUCT_1_WATER.getProductId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$." + CALORIES_PATH,
                        Matchers.is(PRODUCT_1_WATER.getCalories())))
                .andExpect(MockMvcResultMatchers.jsonPath("$." + PRODUCT_NAME_PATH,
                        Matchers.is(newProductName)));
    }

    @Test
    public void whenUpdatePresentNotCopy_thenBadRequest() throws Exception {
        String body = objectMapper.writeValueAsString(ProductDTO.builder()
                .productId(PRODUCT_1_WATER.getProductId())
                .productName(PRODUCT_1_WATER.getProductName())
                .build());
        mvc.perform(MockMvcRequestBuilders.post(UPDATE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void whenUpdatePresentCopy_thenOriginal() throws Exception {
        String body = objectMapper.writeValueAsString(ProductDTO.builder()
                .productName(PRODUCT_1_WATER.getProductName())
                .calories(PRODUCT_1_WATER.getCalories())
                .build());
        mvc.perform(MockMvcRequestBuilders.post(UPDATE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + CALORIES_PATH,
                        Matchers.is(PRODUCT_1_WATER.getCalories())))
                .andExpect(MockMvcResultMatchers.jsonPath("$." + PRODUCT_NAME_PATH,
                        Matchers.is(PRODUCT_1_WATER.getProductName())));
    }

    @Test
    public void whenDeleteAbsent_thenException() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete(DELETE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .param(PRODUCT_ID_PATH, "6"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void whenDeletePresent_thenOriginal() throws Exception {
        mvc.perform(MockMvcRequestBuilders.delete(DELETE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .param(PRODUCT_ID_PATH, PRODUCT_1_WATER.getProductId().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$." + PRODUCT_ID_PATH,
                        Matchers.is(PRODUCT_1_WATER.getProductId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$." + CALORIES_PATH,
                        Matchers.is(PRODUCT_1_WATER.getCalories())))
                .andExpect(MockMvcResultMatchers.jsonPath("$." + PRODUCT_NAME_PATH,
                        Matchers.is(PRODUCT_1_WATER.getProductName())));
    }

}
