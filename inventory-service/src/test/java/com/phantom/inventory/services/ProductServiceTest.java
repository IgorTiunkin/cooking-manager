package com.phantom.inventory.services;

import com.phantom.inventory.models.Product;
import com.phantom.inventory.services.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest (webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ProductServiceTest {


    private final ProductService productService;

    @Autowired
    ProductServiceTest(ProductService productService) {
        this.productService = productService;
    }

    @Test
    public void whenGetAll_ThenSize2() {
        List<Product> allProducts = productService.getAllProducts();
        assertEquals(4, allProducts.size());
    }

    @Test
    public void whenGetById1_thenBread () {
        List<Product> allById = productService.getAllById(List.of(1));
        assertEquals("water", allById.get(0).getProductName());
    }

}