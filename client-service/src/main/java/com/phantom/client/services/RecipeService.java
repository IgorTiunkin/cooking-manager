package com.phantom.client.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.phantom.client.dto.ProductDTO;
import com.phantom.client.dto.RecipeShowDTO;
import com.phantom.client.dto.RecipeRestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {

    private final WebClient.Builder builder;

    public CompletableFuture <List<RecipeRestDTO>> getAllRecipes() throws ExecutionException, InterruptedException, JsonProcessingException {
        CompletableFuture<List<RecipeRestDTO>> listCompletableFuture = CompletableFuture.supplyAsync(() ->
                builder.build()
                        .get()
                        .uri("http://api-gateway/api/v1/recipe/all")
                        .retrieve()
                        .bodyToFlux(RecipeRestDTO.class)
                        .collectList()
                        .block()
        );
        System.out.println(listCompletableFuture.get());
        return listCompletableFuture;
    }

    public RecipeShowDTO getRecipeById(Integer receiptId) {
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
        RecipeShowDTO recipeShowDTO = RecipeShowDTO.builder()
                .recipeId(1)
                .title("Bread with water")
                .actions("Mix.Eat")
                .usedProducts(receiptDTOIntegerMap)
                .build();
        return recipeShowDTO; //todo get from service
    }

    public boolean save(RecipeShowDTO recipeShowDTO) {
        return true;//todo save to service
    }



}
