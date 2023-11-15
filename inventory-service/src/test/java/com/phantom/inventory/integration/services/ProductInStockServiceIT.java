package com.phantom.inventory.integration.services;


import com.phantom.inventory.dto.ProductAndQuantityDTO;
import com.phantom.inventory.dto.RecipeCookingOrderDTO;
import com.phantom.inventory.dto.StockUpdateDTO;
import com.phantom.inventory.exceptions.ProductNotEnoughQuantityException;
import com.phantom.inventory.exceptions.ProductNotFoundException;
import com.phantom.inventory.exceptions.ProductStockAlreadyChanged;
import com.phantom.inventory.integration.BaseIT;
import com.phantom.inventory.models.Product;
import com.phantom.inventory.models.ProductInStock;
import com.phantom.inventory.models.StockChange;
import com.phantom.inventory.repositories.StockChangeRepository;
import com.phantom.inventory.services.ProductInStockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProductInStockServiceIT extends BaseIT {

    private final ProductInStockService productInStockService;
    private final StockChangeRepository stockChangeRepository;

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
    public ProductInStockServiceIT(ProductInStockService productInStockService, StockChangeRepository stockChangeRepository) {
        this.productInStockService = productInStockService;
        this.stockChangeRepository = stockChangeRepository;
    }

    @Test
    public void whenRequest2_then2() {
        List<ProductInStock> productInStockByIds = productInStockService.getProductInStockByIds(List.of(1, 2));
        assertEquals(2, productInStockByIds.size());
        ProductInStock productInStock1 = productInStockByIds.get(0);
        assertAll(() -> assertEquals(PRODUCT_IN_STOCK_1_WATER.getStockId(), productInStock1.getStockId()),
                () -> assertEquals(PRODUCT_IN_STOCK_1_WATER.getQuantity(), productInStock1.getQuantity()),
                () -> assertEquals(PRODUCT_IN_STOCK_1_WATER.getRecommendedQuantity(), productInStock1.getRecommendedQuantity()),
                () -> assertEquals(PRODUCT_1_WATER.getProductId(), productInStock1.getProduct().getProductId()),
                () -> assertEquals(PRODUCT_1_WATER.getProductName(),productInStock1.getProduct().getProductName()),
                () -> assertEquals(PRODUCT_1_WATER.getCalories(), productInStock1.getProduct().getCalories()));
        ProductInStock productInStock2 = productInStockByIds.get(1);
        assertAll(() -> assertEquals(PRODUCT_IN_STOCK_2_BREAD.getStockId(), productInStock2.getStockId()),
                () -> assertEquals(PRODUCT_IN_STOCK_2_BREAD.getQuantity(), productInStock2.getQuantity()),
                () -> assertEquals(PRODUCT_IN_STOCK_2_BREAD.getRecommendedQuantity(), productInStock2.getRecommendedQuantity()),
                () -> assertEquals(PRODUCT_2_BREAD.getProductId(), productInStock2.getProduct().getProductId()),
                () -> assertEquals(PRODUCT_2_BREAD.getProductName(),productInStock2.getProduct().getProductName()),
                () -> assertEquals(PRODUCT_2_BREAD.getCalories(), productInStock2.getProduct().getCalories()));
    }

    @Test
    public void whenId_thenQuantity () {
        Integer quantityById1 = productInStockService.getQuantityById(1);
        assertEquals(PRODUCT_IN_STOCK_1_WATER.getQuantity(), quantityById1);
        Integer quantityById2 = productInStockService.getQuantityById(2);
        assertEquals(PRODUCT_IN_STOCK_2_BREAD.getQuantity(), quantityById2);
        Integer quantityById3 = productInStockService.getQuantityById(3);
        assertEquals(PRODUCT_IN_STOCK_3_TOMATO.getQuantity(), quantityById3);
        Integer quantityById4 = productInStockService.getQuantityById(4);
        assertEquals(PRODUCT_IN_STOCK_4_POTATO.getQuantity(), quantityById4);
        Integer quantityById5 = productInStockService.getQuantityById(5);
        assertEquals(PRODUCT_IN_STOCK_5_COFFEE.getQuantity(), quantityById5);
    }

    @Test
    public void whenDuplicateTimestampUpdate_thenAlreadyException() {
        StockUpdateDTO stockUpdateDTO = StockUpdateDTO.builder()
                .productId(1)
                .change(100)
                .timestamp(EXISTED_TIMESTAMP)
                .build();
        assertThrows(ProductStockAlreadyChanged.class,
                () -> productInStockService.updateStock(stockUpdateDTO));
    }

    @Test
    public void whenAbsentId_thenProductNotFoundException() {
        StockUpdateDTO stockUpdateDTO = StockUpdateDTO.builder()
                .productId(6)
                .change(100)
                .build();
        assertThrows(ProductNotFoundException.class,
                () -> productInStockService.updateStock(stockUpdateDTO));
    }

    @Test
    public void whenQuantityNotEnough_thenNotEnoughException() {
        StockUpdateDTO stockUpdateDTO = StockUpdateDTO.builder()
                .productId(PRODUCT_1_WATER.getProductId())
                .change(-PRODUCT_IN_STOCK_1_WATER.getQuantity()-1)
                .build();
        assertThrows(ProductNotEnoughQuantityException.class,
                () -> productInStockService.updateStock(stockUpdateDTO));
    }

    @Test
    public void whenQuantityUpdate_thenOk() {
        Integer change = 10;
        LocalDateTime timestamp = LocalDateTime.now();
        StockUpdateDTO stockUpdateDTO = StockUpdateDTO.builder()
                .productId(PRODUCT_1_WATER.getProductId())
                .change(change)
                .timestamp(timestamp)
                .build();
        ProductInStock productInStock = productInStockService.updateStock(stockUpdateDTO);

        Integer currentQuantity = productInStockService.getQuantityById(PRODUCT_1_WATER.getProductId());
        assertEquals(PRODUCT_IN_STOCK_1_WATER.getQuantity()+change,
                currentQuantity);
        assertEquals(PRODUCT_IN_STOCK_1_WATER.getQuantity()+change,
                productInStock.getQuantity());

        List<StockChange> allByTimestamp = stockChangeRepository.findAllByTimestamp(timestamp);
        assertTrue(!allByTimestamp.isEmpty());
        StockChange stockChange = allByTimestamp.get(0);
        assertEquals(change, stockChange.getChange());
        assertEquals(PRODUCT_1_WATER.getProductId(), stockChange.getProduct().getProductId());

    }

    @Test
    public void whenCheckReplenishment_then2() {
        List<ProductInStock> productInStocks = productInStockService.checkReplenishment();
        assertEquals(2, productInStocks.size());

        ProductInStock productInStock1 = productInStocks.get(0);
        assertAll(() -> assertEquals(PRODUCT_1_WATER.getProductId(), productInStock1.getProduct().getProductId()),
                () -> assertEquals(PRODUCT_IN_STOCK_1_WATER.getQuantity(), productInStock1.getQuantity()),
                () -> assertEquals(PRODUCT_IN_STOCK_1_WATER.getRecommendedQuantity(), productInStock1.getRecommendedQuantity()));

        ProductInStock productInStock2 = productInStocks.get(1);
        assertAll(() -> assertEquals(PRODUCT_IN_STOCK_4_POTATO.getQuantity(), productInStock2.getQuantity()),
                () -> assertEquals(PRODUCT_IN_STOCK_4_POTATO.getRecommendedQuantity(), productInStock2.getRecommendedQuantity()));
    }

    @Test
    public void whenDuplicateTimestampBook_thenAlreadyException() {
        RecipeCookingOrderDTO recipeCookingOrderDTO = RecipeCookingOrderDTO.builder()
                .recipeId(1)
                .timestamp(EXISTED_TIMESTAMP)
                .build();
        assertThrows(ProductStockAlreadyChanged.class,
                () -> productInStockService.bookStock(recipeCookingOrderDTO));
    }


    @Test
    public void whenQuantityNotEnoughBook_thenNotEnoughException() {
        List<ProductAndQuantityDTO> productAndQuantityDTOList = new ArrayList<>();
        productAndQuantityDTOList.add(ProductAndQuantityDTO.builder().productId(1).quantity(100).build());
        RecipeCookingOrderDTO recipeCookingOrderDTO = RecipeCookingOrderDTO.builder()
                .recipeId(1)
                .productAndQuantityDTOList(productAndQuantityDTOList)
                .build();
        assertThrows(ProductNotEnoughQuantityException.class,
                () -> productInStockService.bookStock(recipeCookingOrderDTO));
    }

    @Test
    public void whenChange_thenOk() {
        LocalDateTime timestamp = LocalDateTime.now();
        Integer product1Id = 1;
        Integer product2Id = 2;
        Integer change1quantity = 2;
        Integer change2quantity = 10;
        ProductAndQuantityDTO change1 = ProductAndQuantityDTO.builder().productId(product1Id).quantity(change1quantity).build();
        ProductAndQuantityDTO change2 = ProductAndQuantityDTO.builder().productId(product2Id).quantity(change2quantity).build();
        List<ProductAndQuantityDTO> productAndQuantityDTOList = List.of(change1, change2);
        RecipeCookingOrderDTO recipeCookingOrderDTO = RecipeCookingOrderDTO.builder()
        .recipeId(1).productAndQuantityDTOList(productAndQuantityDTOList).timestamp(timestamp).build();

        productInStockService.bookStock(recipeCookingOrderDTO);

        Integer resultQuantity1 = productInStockService.getQuantityById(product1Id);
        Integer resultQuantity2 = productInStockService.getQuantityById(product2Id);
        assertEquals(resultQuantity1, PRODUCT_IN_STOCK_1_WATER.getQuantity()-change1quantity);
        assertEquals(resultQuantity2, PRODUCT_IN_STOCK_2_BREAD.getQuantity()-change2quantity);

        List<StockChange> allByTimestamp = stockChangeRepository.findAllByTimestamp(timestamp);
        assertEquals(2, allByTimestamp.size());
        StockChange stockChange1 = allByTimestamp.get(0);
        assertAll(() -> assertEquals(product1Id, stockChange1.getProduct().getProductId()),
                () -> assertEquals(-change1quantity, stockChange1.getChange()));
        StockChange stockChange2 = allByTimestamp.get(1);
        assertAll(() -> assertEquals(product2Id, stockChange2.getProduct().getProductId()),
                () -> assertEquals(-change2quantity, stockChange2.getChange()));
    }



}
