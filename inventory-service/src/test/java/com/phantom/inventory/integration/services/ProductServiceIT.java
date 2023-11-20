package com.phantom.inventory.integration.services;


import com.phantom.inventory.dto.ProductDTO;
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

    private final ProductDTO PRODUCTDTO_1_WATER = ProductDTO.builder()
            .productId(1)
            .productName("water")
            .calories(10)
            .build();
    private final ProductDTO PRODUCTDTO_2_BREAD = ProductDTO.builder()
            .productId(2)
            .productName("bread")
            .calories(200)
            .build();
    private final ProductDTO PRODUCTDTO_3_TOMATO = ProductDTO.builder()
            .productId(3)
            .productName("tomato")
            .calories(50)
            .build();
    private final ProductDTO PRODUCTDTO_4_POTATO = ProductDTO.builder()
            .productId(4)
            .productName("potato")
            .calories(70)
            .build();
    private final ProductDTO PRODUCTDTO_5_COFFEE = ProductDTO.builder()
            .productId(5)
            .productName("coffee")
            .calories(10)
            .build();
    private final ProductDTO PRODUCTDTO_6_TEST = ProductDTO.builder()
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
        List<ProductDTO> allById = productService.getAllById
                (List.of(PRODUCTDTO_1_WATER.getProductId(), PRODUCTDTO_2_BREAD.getProductId()));
        ProductDTO product1 = allById.get(0);
        assertAll(  () -> assertEquals(product1.getProductId(), PRODUCTDTO_1_WATER.getProductId()),
                () -> assertEquals(product1.getProductName(), PRODUCTDTO_1_WATER.getProductName()),
                () -> assertEquals(product1.getCalories(), PRODUCTDTO_1_WATER.getCalories()));
        ProductDTO product2 = allById.get(1);
        assertAll(  () -> assertEquals(product2.getProductId(), PRODUCTDTO_2_BREAD.getProductId()),
                () -> assertEquals(product2.getProductName(), PRODUCTDTO_2_BREAD.getProductName()),
                () -> assertEquals(product2.getCalories(), PRODUCTDTO_2_BREAD.getCalories()));
    }

    @Test
    public void whenAll_thenSize5() {
        List<ProductDTO> allProducts = productService.getAllProducts();
        assertEquals(5, allProducts.size());
        ProductDTO product1 = allProducts.get(0);
        assertAll(  () -> assertEquals(product1.getProductId(), PRODUCTDTO_1_WATER.getProductId()),
                    () -> assertEquals(product1.getProductName(), PRODUCTDTO_1_WATER.getProductName()),
                    () -> assertEquals(product1.getCalories(), PRODUCTDTO_1_WATER.getCalories()));
        ProductDTO product2 = allProducts.get(1);
        assertAll(  () -> assertEquals(product2.getProductId(), PRODUCTDTO_2_BREAD.getProductId()),
                () -> assertEquals(product2.getProductName(), PRODUCTDTO_2_BREAD.getProductName()),
                () -> assertEquals(product2.getCalories(), PRODUCTDTO_2_BREAD.getCalories()));
        ProductDTO product3 = allProducts.get(2);
        assertAll(  () -> assertEquals(product3.getProductId(), PRODUCTDTO_3_TOMATO.getProductId()),
                () -> assertEquals(product3.getProductName(), PRODUCTDTO_3_TOMATO.getProductName()),
                () -> assertEquals(product3.getCalories(), PRODUCTDTO_3_TOMATO.getCalories()));
        ProductDTO product4 = allProducts.get(3);
        assertAll(  () -> assertEquals(product4.getProductId(), PRODUCTDTO_4_POTATO.getProductId()),
                () -> assertEquals(product4.getProductName(), PRODUCTDTO_4_POTATO.getProductName()),
                () -> assertEquals(product4.getCalories(), PRODUCTDTO_4_POTATO.getCalories()));
        ProductDTO product5 = allProducts.get(4);
        assertAll(  () -> assertEquals(product5.getProductId(), PRODUCTDTO_5_COFFEE.getProductId()),
                () -> assertEquals(product5.getProductName(), PRODUCTDTO_5_COFFEE.getProductName()),
                () -> assertEquals(product5.getCalories(), PRODUCTDTO_5_COFFEE.getCalories()));
    }

    @Test
    public void whenId1_thenWater() {
        ProductDTO product1 = productService.getById(1);
        assertAll(  () -> assertEquals(product1.getProductId(), PRODUCTDTO_1_WATER.getProductId()),
                () -> assertEquals(product1.getProductName(), PRODUCTDTO_1_WATER.getProductName()),
                () -> assertEquals(product1.getCalories(), PRODUCTDTO_1_WATER.getCalories()));
    }

    @Test
    public void whenWater_thenWater() {
        Product productByName = productService.getByName(PRODUCTDTO_1_WATER.getProductName()).get();
        assertAll(  () -> assertEquals(productByName.getProductId(), PRODUCTDTO_1_WATER.getProductId()),
                () -> assertEquals(productByName.getProductName(), PRODUCTDTO_1_WATER.getProductName()),
                () -> assertEquals(productByName.getCalories(), PRODUCTDTO_1_WATER.getCalories()));
    }

    @Test
    public void whenSaveAbsent_then6() {
        ProductDTO savedProduct = productService.save(PRODUCTDTO_6_TEST);
        assertAll(  ()->assertEquals(6, savedProduct.getProductId()),
                ()->assertEquals(savedProduct.getProductName(), PRODUCTDTO_6_TEST.getProductName()),
                ()->assertEquals(savedProduct.getCalories(), PRODUCTDTO_6_TEST.getCalories())
        );
    }

    @Test
    public void whenSavePresentNotCopy_thenException() {
        ProductDTO water = ProductDTO.builder().productName("water").build();
        assertThrows(ProductSaveException.class, () -> productService.save(water));
    }

    @Test
    public void whenSavePresentCopy_thenOriginal() {
        ProductDTO water = ProductDTO.builder().productName("water").calories(10).build();
        ProductDTO savedProduct = productService.save(water);
        assertAll(  () -> assertEquals(savedProduct.getProductId(), PRODUCTDTO_1_WATER.getProductId()),
                () -> assertEquals(savedProduct.getProductName(), PRODUCTDTO_1_WATER.getProductName()),
                () -> assertEquals(savedProduct.getCalories(), PRODUCTDTO_1_WATER.getCalories())
        );
    }

    @Test
    public void whenUpdateAbsentName_thenSave() {
        String newName = "newWater";
        ProductDTO newWater = ProductDTO.builder()
                .productId(PRODUCTDTO_1_WATER.getProductId())
                .calories(PRODUCTDTO_1_WATER.getCalories())
                .productName(newName)
                .build();
        newWater.setProductName(newName);
        ProductDTO savedProduct = productService.update(newWater);
        assertAll(  ()->assertEquals(PRODUCTDTO_1_WATER.getProductId(), savedProduct.getProductId()),
                ()->assertEquals(newName, savedProduct.getProductName()),
                ()->assertEquals(PRODUCTDTO_1_WATER.getCalories(), savedProduct.getCalories())
        );
        List<ProductDTO> allProducts = productService.getAllProducts();
        Optional<ProductDTO> oldProduct = allProducts.stream()
                .filter(product -> product.getProductName().equals(PRODUCTDTO_1_WATER.getProductName())).findAny();
        assertTrue(oldProduct.isEmpty());
    }

    @Test
    public void whenUpdatePresentNotCopy_thenException() {
        ProductDTO water = ProductDTO.builder().productName("water").build();
        assertThrows(ProductUpdateException.class, () -> productService.update(water));
    }

    @Test
    public void whenUpdatePresentCopy_thenOriginal() {
        ProductDTO savedProduct = productService.update(PRODUCTDTO_1_WATER);
        assertAll(  () -> assertEquals(PRODUCTDTO_1_WATER.getProductId() ,savedProduct.getProductId()),
                () -> assertEquals(PRODUCTDTO_1_WATER.getProductName(), savedProduct.getProductName()),
                () -> assertEquals(PRODUCTDTO_1_WATER.getCalories(), savedProduct.getCalories())
        );
    }

    @Test
    public void whenDeleteAbsent_thenException() {
        assertThrows(ProductDeleteException.class,
                () -> productService.delete(6));
    }

    @Test
    public void whenDeletePresent_thenOriginal() {
        ProductDTO deletedProduct = productService.delete(PRODUCTDTO_1_WATER.getProductId());
        assertAll(() -> assertEquals(PRODUCTDTO_1_WATER.getProductId(), deletedProduct.getProductId()),
                () -> assertEquals(PRODUCTDTO_1_WATER.getProductName(), deletedProduct.getProductName()),
                () -> assertEquals(PRODUCTDTO_1_WATER.getCalories(), deletedProduct.getCalories()));
        List<ProductDTO> allProducts = productService.getAllProducts();
        assertEquals(4, allProducts.size());
        Optional<ProductDTO> optionalProduct = allProducts.stream()
                .filter(product -> product.getProductId().equals(PRODUCTDTO_1_WATER.getProductId())).findAny();
        assertTrue(optionalProduct.isEmpty());
        List<ProductInStock> productInStockRepositoryAll = productInStockRepository.findAll();
        Optional<ProductInStock> productInStock1 = productInStockRepositoryAll.stream()
                .filter(productInStock -> productInStock.getProduct().getProductId().equals(PRODUCTDTO_1_WATER.getProductId()))
                .findAny();
        assertTrue(productInStock1.isEmpty());

    }

}
