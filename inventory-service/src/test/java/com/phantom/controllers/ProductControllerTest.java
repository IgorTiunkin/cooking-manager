package com.phantom.controllers;

import com.phantom.inventory.controllers.ProductController;
import com.phantom.inventory.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ProductControllerTest {

    private final ProductController productController;

    @Autowired
    ProductControllerTest(ProductController productController) {
        this.productController = productController;
    }

    @Test
    public void whenGetAll_thenProductDto() {
        List<ProductDTO> allProducts = productController.getAllProducts();
        assertEquals(ProductDTO.class, allProducts.get(0).getClass());
    }
}