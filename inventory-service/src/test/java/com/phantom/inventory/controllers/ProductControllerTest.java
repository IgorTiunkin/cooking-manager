package com.phantom.inventory.controllers;

import com.phantom.inventory.controllers.ProductController;
import com.phantom.inventory.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

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

    @Test
    public void whenGetIn_thenProductDto2() {
        List<ProductDTO> productsIn = productController.getProductsIn(Set.of(1, 2));
        assertEquals(2, productsIn.size());
        assertEquals(1,productsIn.get(0).getProductId());

    }
}