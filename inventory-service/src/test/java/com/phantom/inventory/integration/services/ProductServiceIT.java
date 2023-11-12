package com.phantom.inventory.integration.services;


import com.phantom.inventory.integration.BaseIT;
import com.phantom.inventory.models.Product;
import com.phantom.inventory.services.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceIT extends BaseIT {

    private final ProductService productService;

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
    private final Product PRODUCT_3_TOMATO = Product.builder()
            .productId(3)
            .productName("tomato")
            .calories(50)
            .build();
    private final Product PRODUCT_4_POTATO = Product.builder()
            .productId(4)
            .productName("potato")
            .calories(70)
            .build();
    private final Product PRODUCT_5_COFFEE = Product.builder()
            .productId(5)
            .productName("coffee")
            .calories(10)
            .build();

    @Autowired
    public ProductServiceIT(ProductService productService) {
        this.productService = productService;
    }

    @Test
    public void whenAll_thenSize5() {
        List<Product> allProducts = productService.getAllProducts();
        assertEquals(5, allProducts.size());
        Product product1 = allProducts.get(0);
        assertAll(  () -> assertEquals(product1.getProductId(), PRODUCT_1_WATER.getProductId()),
                    () -> assertEquals(product1.getProductName(), PRODUCT_1_WATER.getProductName()),
                    () -> assertEquals(product1.getCalories(), PRODUCT_1_WATER.getCalories()));
        Product product2 = allProducts.get(1);
        assertAll(  () -> assertEquals(product2.getProductId(), PRODUCT_2_BREAD.getProductId()),
                () -> assertEquals(product2.getProductName(), PRODUCT_2_BREAD.getProductName()),
                () -> assertEquals(product2.getCalories(), PRODUCT_2_BREAD.getCalories()));
        Product product3 = allProducts.get(2);
        assertAll(  () -> assertEquals(product3.getProductId(), PRODUCT_3_TOMATO.getProductId()),
                () -> assertEquals(product3.getProductName(), PRODUCT_3_TOMATO.getProductName()),
                () -> assertEquals(product3.getCalories(), PRODUCT_3_TOMATO.getCalories()));
        Product product4 = allProducts.get(3);
        assertAll(  () -> assertEquals(product4.getProductId(), PRODUCT_4_POTATO.getProductId()),
                () -> assertEquals(product4.getProductName(), PRODUCT_4_POTATO.getProductName()),
                () -> assertEquals(product4.getCalories(), PRODUCT_4_POTATO.getCalories()));
        Product product5 = allProducts.get(4);
        assertAll(  () -> assertEquals(product5.getProductId(), PRODUCT_5_COFFEE.getProductId()),
                () -> assertEquals(product5.getProductName(), PRODUCT_5_COFFEE.getProductName()),
                () -> assertEquals(product5.getCalories(), PRODUCT_5_COFFEE.getCalories()));
    }
}
