package com.phantom.inventory.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phantom.inventory.controllers.ProductInStockController;
import com.phantom.inventory.dto.*;
import com.phantom.inventory.exceptions.ProductNotEnoughQuantityException;
import com.phantom.inventory.exceptions.ProductStockAlreadyChanged;
import com.phantom.inventory.integration.BaseIT;
import com.phantom.inventory.models.Product;
import com.phantom.inventory.models.ProductInStock;
import com.phantom.inventory.models.StockChange;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductInStockControllerIT extends BaseIT {

    private MockMvc mvc;
    private final ObjectMapper objectMapper;
    private final ProductInStockController productInStockController;

    private final String BASE_URL = "/api/v1/product-in-stock";
    private final String STOCK_BY_ID_URL = BASE_URL + "/get-by-id";
    private final String CHANGE_STOCK_URL = BASE_URL + "/change";
    private final String GET_STOCK_FOR_IDS_URL = BASE_URL + "/stocks";
    private final String BOOK_ORDER_URL = BASE_URL + "/book-order";
    private final String CHECK_REPLENISHMENT_URL = BASE_URL + "/check-replenishment";

    private final Product PRODUCT_1_WATER = Product.builder()
            .productId(1)
            .productName("water")
            .calories(10)
            .build();
    private final Product PRODUCT_2_BREAD = Product.builder()
            .productId(2)
            .productName("bread")
            .calories(200)
            .build();
    private final ProductInStock PRODUCT_IN_STOCK_1_WATER = ProductInStock.builder()
            .stockId(1)
            .quantity(3)
            .recommendedQuantity(15)
            .build();
    private final ProductInStock PRODUCT_IN_STOCK_2_BREAD = ProductInStock.builder()
            .stockId(2)
            .quantity(30)
            .recommendedQuantity(10)
            .build();
    private final ProductInStock PRODUCT_IN_STOCK_3_TOMATO = ProductInStock.builder()
            .stockId(3)
            .quantity(10)
            .recommendedQuantity(10)
            .build();
    private final ProductInStock PRODUCT_IN_STOCK_4_POTATO = ProductInStock.builder()
            .stockId(4)
            .quantity(11)
            .recommendedQuantity(40)
            .build();
    private final ProductInStock PRODUCT_IN_STOCK_5_COFFEE = ProductInStock.builder()
            .stockId(5)
            .quantity(0)
            .recommendedQuantity(0)
            .build();
    private final LocalDateTime EXISTED_TIMESTAMP = LocalDateTime.parse("2023-11-10 23:45:17.215199",
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

    @Autowired
    public ProductInStockControllerIT(ObjectMapper objectMapper, ProductInStockController productInStockController) {
        this.objectMapper = objectMapper;
        this.productInStockController = productInStockController;
    }

    @BeforeEach
    public void initialiseRestAssuredMockMvcStandalone() {
        mvc = MockMvcBuilders.standaloneSetup(productInStockController)
                .build();
    }

    @Test
    public void whenPresentId_thenQuantity () throws Exception {
        Integer id = PRODUCT_1_WATER.getProductId();
        mvc.perform(MockMvcRequestBuilders.get(STOCK_BY_ID_URL)
        .accept(MediaType.APPLICATION_JSON)
        .param("productId", id.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$",
                        Matchers.is(PRODUCT_IN_STOCK_1_WATER.getQuantity())));
    }

    @Test
    public void whenAbsentId_thenZero () throws Exception {
        Integer id = 6;
        mvc.perform(MockMvcRequestBuilders.get(STOCK_BY_ID_URL)
                .accept(MediaType.APPLICATION_JSON)
                .param("productId", id.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$",
                        Matchers.is(0)));
    }

    @Test
    public void whenDuplicateTimestampChangeStock_thenOk() throws Exception {
        String body = objectMapper.writeValueAsString(StockUpdateDTO.builder()
                .productId(1)
                .change(100)
                .timestamp(EXISTED_TIMESTAMP)
                .build());
        mvc.perform(MockMvcRequestBuilders.post(CHANGE_STOCK_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void whenAbsentId_thenBadRequest() throws Exception {
        String body = objectMapper.writeValueAsString(StockUpdateDTO.builder()
                .productId(6)
                .change(100)
                .build());
        mvc.perform(MockMvcRequestBuilders.post(CHANGE_STOCK_URL)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void whenQuantityNotEnough_thenBadRequest() throws Exception {
        String body = objectMapper.writeValueAsString(StockUpdateDTO.builder()
                .productId(PRODUCT_1_WATER.getProductId())
                .change(-PRODUCT_IN_STOCK_1_WATER.getQuantity()-1)
                .build());
        mvc.perform(MockMvcRequestBuilders.post(CHANGE_STOCK_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void whenQuantityUpdate_thenOk() throws Exception {
        Integer change = 10;
        LocalDateTime timestamp = LocalDateTime.now();
        String body = objectMapper.writeValueAsString(StockUpdateDTO.builder()
                .productId(PRODUCT_1_WATER.getProductId())
                .change(change)
                .timestamp(timestamp)
                .build());

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(CHANGE_STOCK_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ProductInStock productInStock = objectMapper.readValue(contentAsString, ProductInStock.class);
        assertEquals(PRODUCT_IN_STOCK_1_WATER.getQuantity()+change, productInStock.getQuantity());
    }

    @Test
    public void whenCheckReplenishment_then2() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(CHECK_REPLENISHMENT_URL)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ProductInStock[] productInStocks = objectMapper.readValue(contentAsString, ProductInStock[].class);
        ProductInStock productInStock1 = productInStocks[0];
        assertAll(() -> assertEquals(PRODUCT_IN_STOCK_1_WATER.getQuantity(), productInStock1.getQuantity()),
                () -> assertEquals(PRODUCT_IN_STOCK_1_WATER.getRecommendedQuantity(), productInStock1.getRecommendedQuantity()));

        ProductInStock productInStock2 = productInStocks[1];
        assertAll(() -> assertEquals(PRODUCT_IN_STOCK_4_POTATO.getQuantity(), productInStock2.getQuantity()),
                () -> assertEquals(PRODUCT_IN_STOCK_4_POTATO.getRecommendedQuantity(), productInStock2.getRecommendedQuantity()));
    }

    @Test
    public void whenDuplicateTimestampBook_thenOk() throws Exception {
        String body = objectMapper.writeValueAsString(RecipeCookingOrderDTO.builder()
                .recipeId(1)
                .timestamp(EXISTED_TIMESTAMP)
                .build());
        mvc.perform(MockMvcRequestBuilders.post(BOOK_ORDER_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void whenQuantityNotEnoughBook_thenBadRequest() throws Exception {
        List<ProductAndQuantityDTO> productAndQuantityDTOList = new ArrayList<>();
        productAndQuantityDTOList.add(ProductAndQuantityDTO.builder().productId(1).quantity(100).build());
        String body = objectMapper.writeValueAsString(RecipeCookingOrderDTO.builder()
                .recipeId(1)
                .productAndQuantityDTOList(productAndQuantityDTOList)
                .build());
        mvc.perform(MockMvcRequestBuilders.post(BOOK_ORDER_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void whenChange_thenOk() throws Exception {
        LocalDateTime timestamp = LocalDateTime.now();
        ProductAndQuantityDTO change1 = ProductAndQuantityDTO.builder().productId(1).quantity(2).build();
        ProductAndQuantityDTO change2 = ProductAndQuantityDTO.builder().productId(2).quantity(10).build();
        List<ProductAndQuantityDTO> productAndQuantityDTOList = List.of(change1, change2);
        String body = objectMapper.writeValueAsString(RecipeCookingOrderDTO.builder()
                .recipeId(1).productAndQuantityDTOList(productAndQuantityDTOList).timestamp(timestamp).build());

        mvc.perform(MockMvcRequestBuilders.post(BOOK_ORDER_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void whenRequest2_then2() throws Exception {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(GET_STOCK_FOR_IDS_URL)
                .accept(MediaType.APPLICATION_JSON)
                .param("productIds", "1", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        ProductsForPrepareDTO[] productsForPrepareDTOS = objectMapper.readValue(contentAsString, ProductsForPrepareDTO[].class);

        assertEquals(2, productsForPrepareDTOS.length);
        ProductsForPrepareDTO productsForPrepareDTO1 = productsForPrepareDTOS[0];
        ProductsForPrepareDTO productsForPrepareDTO2 = productsForPrepareDTOS[1];
        assertAll(() -> assertEquals(PRODUCT_IN_STOCK_1_WATER.getQuantity(), productsForPrepareDTO1.getCurrentQuantity()),
                () -> assertEquals(PRODUCT_IN_STOCK_2_BREAD.getQuantity(), productsForPrepareDTO2.getCurrentQuantity()));
    }
}
