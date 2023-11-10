package com.phantom.client.services;

import com.phantom.client.dto.ProductDTO;
import com.phantom.client.dto.RecipeRestDTO;
import com.phantom.client.exceptions.inventoryservice.*;
import com.phantom.client.exceptions.inventoryservice.resilence.InventoryServiceCircuitException;
import com.phantom.client.exceptions.inventoryservice.resilence.InventoryServiceException;
import com.phantom.client.exceptions.inventoryservice.resilence.InventoryServiceTimeoutException;
import com.phantom.client.exceptions.inventoryservice.resilence.InventoryServiceTooManyRequestsException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final WebClient.Builder builder;

    @Retry(name = "inventory", fallbackMethod = "inventoryFail")
    @CircuitBreaker(name = "inventory", fallbackMethod = "inventoryCircuit")
    @TimeLimiter(name = "inventory", fallbackMethod = "inventoryTimeout")
    @RateLimiter(name = "inventory", fallbackMethod = "inventoryTooManyRequests")
    public CompletableFuture<List<ProductDTO>> getAllProducts(){
        return CompletableFuture.supplyAsync( () -> builder.build().get()
                .uri("http://api-gateway/api/v1/product/all")
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .collectSortedList(Comparator.comparing(ProductDTO::getProductName))
                .block());
    }

    public CompletableFuture<ProductDTO> getProductById(Integer productId) {
        return CompletableFuture.supplyAsync( () ->
                builder.build()
                .get()
                .uri("http://api-gateway/api/v1/product/one",
                        uriBuilder -> uriBuilder.queryParam("productId", productId).build())
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        clientResponse -> Mono.error(new ProductNotFoundException("Product not found")))
                .bodyToMono(ProductDTO.class)
                .block()
        );

    }

    public CompletableFuture<ProductDTO> save(ProductDTO productDTO) {
        return CompletableFuture.supplyAsync( ()->
                builder.build()
                .post()
                .uri("http://api-gateway/api/v1/product/save")
                .body(Mono.just(productDTO), ProductDTO.class)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                                clientResponse -> Mono.error(new ProductSaveException("Such product name already present")))
                .bodyToMono(ProductDTO.class)
                .block()
                );
    }

    public CompletableFuture<ProductDTO>delete(Integer productId) {
        return CompletableFuture.supplyAsync( () ->
                builder.build()
                .delete()
                .uri("http://api-gateway/api/v1/product/delete",
                        uriBuilder -> uriBuilder.queryParam("productId", productId).build())
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        clientResponse -> Mono.error(new ProductDeleteException("Delete exception", productId)
                                ))
                .bodyToMono(ProductDTO.class)
                .block()
                );
    }

    public CompletableFuture<ProductDTO> updateProduct(ProductDTO productDTO) {
        return CompletableFuture.supplyAsync(()->
                builder.build()
                .post()
                .uri("http://api-gateway/api/v1/product/update")
                .body(Mono.just(productDTO), ProductDTO.class)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        clientResponse -> Mono.error(new ProductUpdateException("Such product name already present")))
                .bodyToMono(ProductDTO.class)
                .block()
                );
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



}
