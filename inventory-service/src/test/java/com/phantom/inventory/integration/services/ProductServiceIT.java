package com.phantom.inventory.integration.services;


import com.phantom.inventory.exceptions.ProductDeleteException;
import com.phantom.inventory.exceptions.ProductSaveException;
import com.phantom.inventory.exceptions.ProductUpdateException;
import com.phantom.inventory.integration.BaseIT;
import com.phantom.inventory.models.Product;
import com.phantom.inventory.models.ProductInStock;
import com.phantom.inventory.repositories.ProductInStockRepository;
import com.phantom.inventory.services.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceIT extends BaseIT {

    private final ProductService productService;
    private final ProductInStockRepository productInStockRepository;

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
    private final Product PRODUCT_6_TEST = Product.builder()
            .productName("test")
            .calories(100)
            .build();

    @Autowired
    public ProductServiceIT(ProductService productService, ProductInStockRepository productInStockRepository) {
        this.productService = productService;
        this.productInStockRepository = productInStockRepository;
    }

    @Test
    public void when1and2then_waterAndBread() {
        List<Product> allById = productService.getAllById
                (List.of(PRODUCT_1_WATER.getProductId(), PRODUCT_2_BREAD.getProductId()));
        Product product1 = allById.get(0);
        assertAll(  () -> assertEquals(product1.getProductId(), PRODUCT_1_WATER.getProductId()),
                () -> assertEquals(product1.getProductName(), PRODUCT_1_WATER.getProductName()),
                () -> assertEquals(product1.getCalories(), PRODUCT_1_WATER.getCalories()));
        Product product2 = allById.get(1);
        assertAll(  () -> assertEquals(product2.getProductId(), PRODUCT_2_BREAD.getProductId()),
                () -> assertEquals(product2.getProductName(), PRODUCT_2_BREAD.getProductName()),
                () -> assertEquals(product2.getCalories(), PRODUCT_2_BREAD.getCalories()));
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

    @Test
    public void whenId1_thenWater() {
        Product product1 = productService.getById(1).get();
        assertAll(  () -> assertEquals(product1.getProductId(), PRODUCT_1_WATER.getProductId()),
                () -> assertEquals(product1.getProductName(), PRODUCT_1_WATER.getProductName()),
                () -> assertEquals(product1.getCalories(), PRODUCT_1_WATER.getCalories()));
    }

    @Test
    public void whenWater_thenWater() {
        Product productByName = productService.getByName(PRODUCT_1_WATER.getProductName()).get();
        assertAll(  () -> assertEquals(productByName.getProductId(), PRODUCT_1_WATER.getProductId()),
                () -> assertEquals(productByName.getProductName(), PRODUCT_1_WATER.getProductName()),
                () -> assertEquals(productByName.getCalories(), PRODUCT_1_WATER.getCalories()));
    }

    @Test
    public void whenSaveAbsent_then6() {
        Product savedProduct = productService.save(PRODUCT_6_TEST);
        assertAll(  ()->assertEquals(6, savedProduct.getProductId()),
                ()->assertEquals(savedProduct.getProductName(), PRODUCT_6_TEST.getProductName()),
                ()->assertEquals(savedProduct.getCalories(), PRODUCT_6_TEST.getCalories())
        );
    }

    @Test
    public void whenSavePresentNotCopy_thenException() {
        Product water = Product.builder().productName("water").build();
        assertThrows(ProductSaveException.class, () -> productService.save(water));
    }

    @Test
    public void whenSavePresentCopy_thenOriginal() {
        Product water = Product.builder().productName("water").calories(10).build();
        Product savedProduct = productService.save(water);
        assertAll(  () -> assertEquals(savedProduct.getProductId(), PRODUCT_1_WATER.getProductId()),
                () -> assertEquals(savedProduct.getProductName(), PRODUCT_1_WATER.getProductName()),
                () -> assertEquals(savedProduct.getCalories(), PRODUCT_1_WATER.getCalories())
        );
    }

    @Test
    public void whenUpdateAbsentName_thenSave() {
        String newName = "newWater";
        Product newWater = Product.builder()
                .productId(PRODUCT_1_WATER.getProductId())
                .calories(PRODUCT_1_WATER.getCalories())
                .productName(newName)
                .build();
        newWater.setProductName(newName);
        Product savedProduct = productService.update(newWater);
        assertAll(  ()->assertEquals(PRODUCT_1_WATER.getProductId(), savedProduct.getProductId()),
                ()->assertEquals(newName, savedProduct.getProductName()),
                ()->assertEquals(PRODUCT_1_WATER.getCalories(), savedProduct.getCalories())
        );
        List<Product> allProducts = productService.getAllProducts();
        Optional<Product> oldProduct = allProducts.stream()
                .filter(product -> product.getProductName().equals(PRODUCT_1_WATER.getProductName())).findAny();
        assertTrue(oldProduct.isEmpty());
    }

    @Test
    public void whenUpdatePresentNotCopy_thenException() {
        Product water = Product.builder().productName("water").build();
        assertThrows(ProductUpdateException.class, () -> productService.update(water));
    }

    @Test
    public void whenUpdatePresentCopy_thenOriginal() {
        Product savedProduct = productService.update(PRODUCT_1_WATER);
        assertAll(  () -> assertEquals(PRODUCT_1_WATER.getProductId() ,savedProduct.getProductId()),
                () -> assertEquals(PRODUCT_1_WATER.getProductName(), savedProduct.getProductName()),
                () -> assertEquals(PRODUCT_1_WATER.getCalories(), savedProduct.getCalories())
        );
    }

    @Test
    public void whenDeleteAbsent_thenException() {
        assertThrows(ProductDeleteException.class,
                () -> productService.delete(6));
    }

    @Test
    public void whenDeletePresent_thenOriginal() {
        Product deletedProduct = productService.delete(PRODUCT_1_WATER.getProductId());
        assertAll(() -> assertEquals(PRODUCT_1_WATER.getProductId(), deletedProduct.getProductId()),
                () -> assertEquals(PRODUCT_1_WATER.getProductName(), deletedProduct.getProductName()),
                () -> assertEquals(PRODUCT_1_WATER.getCalories(), deletedProduct.getCalories()));
        List<Product> allProducts = productService.getAllProducts();
        assertEquals(4, allProducts.size());
        Optional<Product> optionalProduct = allProducts.stream()
                .filter(product -> product.getProductId().equals(PRODUCT_1_WATER.getProductId())).findAny();
        assertTrue(optionalProduct.isEmpty());
        List<ProductInStock> productInStockRepositoryAll = productInStockRepository.findAll();
        Optional<ProductInStock> productInStock1 = productInStockRepositoryAll.stream()
                .filter(productInStock -> productInStock.getProduct().getProductId().equals(PRODUCT_1_WATER.getProductId()))
                .findAny();
        assertTrue(productInStock1.isEmpty());

    }

}
