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

    public CompletableFuture <List<RecipeRestDTO>> getAllRecipes() throws ExecutionException, InterruptedException{
        return CompletableFuture.supplyAsync(() ->
                builder.build()
                        .get()
                        .uri("http://api-gateway/api/v1/recipe/all")
                        .retrieve()
                        .bodyToFlux(RecipeRestDTO.class)
                        .collectList()
                        .block()
        );
    }

    public CompletableFuture <RecipeRestDTO> getRecipeById(Integer recipeId) {
        return CompletableFuture.supplyAsync( ()->
                builder.build()
                .get()
                .uri("http://api-gateway/api/v1/recipe/one",
                        uriBuilder -> uriBuilder.queryParam("recipeId", recipeId).build())
                .retrieve()
                .bodyToMono(RecipeRestDTO.class)
                .block()
        );
    }

    public boolean save(RecipeShowDTO recipeShowDTO) {
        return true;//todo save to service
    }



}
