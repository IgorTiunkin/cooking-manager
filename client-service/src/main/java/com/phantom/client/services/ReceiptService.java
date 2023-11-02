package com.phantom.client.services;

import com.phantom.client.dto.ProductDTO;
import com.phantom.client.dto.ReceiptDTO;
import com.phantom.client.exceptions.InventoryServiceCircuitException;
import com.phantom.client.exceptions.InventoryServiceException;
import com.phantom.client.exceptions.InventoryServiceTimeoutException;
import com.phantom.client.exceptions.InventoryServiceTooManyRequestsException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptService {

    private final WebClient.Builder builder;

    public List<ReceiptDTO> getAllReceipts() {
        ProductDTO productDTO1 = ProductDTO.builder()
                .productId(1)
                .productName("water")
                .calories(10)
                .build();
        ProductDTO productDTO2 = ProductDTO.builder()
                .productId(2)
                .productName("bread")
                .calories(200)
                .build();
        TreeMap<ProductDTO, Integer> receiptDTOIntegerMap = new TreeMap<>();
        receiptDTOIntegerMap.put(productDTO1, 2);
        receiptDTOIntegerMap.put(productDTO2, 3);
        ReceiptDTO receiptDTO = ReceiptDTO.builder()
                .receiptId(1)
                .title("Bread with water")
                .actions("Mix.Eat")
                .usedProducts(receiptDTOIntegerMap)
                .build();
        return List.of(receiptDTO, receiptDTO);//todo get from service
    }

    public ReceiptDTO getReceiptById(Integer receiptId) {
        ProductDTO productDTO1 = ProductDTO.builder()
                .productId(1)
                .productName("water")
                .calories(10)
                .build();
        ProductDTO productDTO2 = ProductDTO.builder()
                .productId(2)
                .productName("bread")
                .calories(200)
                .build();
        TreeMap<ProductDTO, Integer> receiptDTOIntegerMap = new TreeMap<>();
        receiptDTOIntegerMap.put(productDTO1, 2);
        receiptDTOIntegerMap.put(productDTO2, 3);
        ReceiptDTO receiptDTO = ReceiptDTO.builder()
                .receiptId(1)
                .title("Bread with water")
                .actions("Mix.Eat")
                .usedProducts(receiptDTOIntegerMap)
                .build();
        return receiptDTO; //todo get from service
    }



    @Retry(name = "inventory", fallbackMethod = "inventoryFail")
    @CircuitBreaker(name = "inventory", fallbackMethod = "inventoryCircuit")
    @TimeLimiter(name = "inventory", fallbackMethod = "inventoryTimeout")
    @RateLimiter(name = "inventory", fallbackMethod = "inventoryTooManyRequests")
    public CompletableFuture <List <ProductDTO>> getAllProducts(){
        return CompletableFuture.supplyAsync( () -> builder.build().get()
                .uri("http://api-gateway/api/v1/product/all")
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .collectSortedList(Comparator.comparing(ProductDTO::getProductName))
                .block());
    }

    public CompletableFuture <List <ProductDTO>> inventoryFail (Exception exception) {
        log.info("Fallback method failedGetProducts activated, {}", exception.getMessage());
        if (exception instanceof WebClientResponseException) {
            throw new InventoryServiceException("Inventory service is unavailable. Please try later.");
        }
        throw new InventoryServiceException(exception.getMessage());
    }

    public CompletableFuture <List <ProductDTO>> inventoryCircuit (CallNotPermittedException exception) {
        log.info("Fallback method circuitInventory activated, {}", exception.getMessage());
        throw new InventoryServiceCircuitException("Inventory service is unavailable. Please try later.");
    }

    public CompletableFuture <List <ProductDTO>> inventoryTimeout (TimeoutException exception) {
        log.info("Fallback method timeoutInventory activated, {}", exception.getMessage());
        throw new InventoryServiceTimeoutException("Inventory service is unavailable. Please try later.");
    }

    public CompletableFuture <List <ProductDTO>> inventoryTooManyRequests (RequestNotPermitted exception) {
        log.info("Fallback method tooManyRequestsToInventory activated, {}", exception.getMessage());
        throw new InventoryServiceTooManyRequestsException("You have done too many requests to inventory. Please try later.");
    }



    public boolean save(ReceiptDTO receiptDTO) {
        return true;//todo save to service
    }
}
