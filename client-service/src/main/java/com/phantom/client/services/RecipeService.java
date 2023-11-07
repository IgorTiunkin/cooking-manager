package com.phantom.client.services;

import com.phantom.client.dto.ProductDTO;
import com.phantom.client.dto.RecipeDTO;
import com.phantom.client.dto.RecipeRestDTO;
import com.phantom.client.dto.RecipeRestElementOfListDTO;
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
public class RecipeService {

    private final WebClient.Builder builder;

    public CompletableFuture <List<RecipeRestElementOfListDTO>> getAllRecipes() {
        return  CompletableFuture.supplyAsync(() ->
                builder.build()
                        .get()
                        .uri("http://api-gateway/api/v1/recipe/all")
                        .retrieve()
                        .bodyToFlux(RecipeRestElementOfListDTO.class)
                        .collectList()
                        .block()
        );

    }

    public RecipeDTO getRecipeById(Integer receiptId) {
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
        RecipeDTO recipeDTO = RecipeDTO.builder()
                .recipeId(1)
                .title("Bread with water")
                .actions("Mix.Eat")
                .usedProducts(receiptDTOIntegerMap)
                .build();
        return recipeDTO; //todo get from service
    }

    public boolean save(RecipeDTO recipeDTO) {
        return true;//todo save to service
    }



}
