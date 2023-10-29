package com.phantom.services;

import com.phantom.models.ProductInStock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ProductInStockServiceTest {

    private final ProductInStockService productInStockService;

    @Autowired
    ProductInStockServiceTest(ProductInStockService productInStockService) {
        this.productInStockService = productInStockService;
    }

    @Test
    public void when2Id_then2Dto () {
        List<ProductInStock> allStockQuantityById = productInStockService.getAllStockQuantityById(List.of(1, 2));
        assertEquals(2, allStockQuantityById.size());
    }

    @Test
    public void when1Id_then1Dto () {
        List<ProductInStock> allStockQuantityById = productInStockService.getAllStockQuantityById(List.of(2));
        assertEquals(1, allStockQuantityById.size());
    }

}