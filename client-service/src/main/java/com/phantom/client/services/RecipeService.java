package com.phantom.client.services;

import com.phantom.client.dto.RecipeRestDTO;
import com.phantom.client.exceptions.SaveFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
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

    public CompletableFuture<RecipeRestDTO> save(RecipeRestDTO recipeRestDTO) {
            return CompletableFuture.supplyAsync(() ->  builder.build()
                    .post()
                    .uri("http://api-gateway/api/v1/recipe/save")
                    .body(Mono.just(recipeRestDTO), RecipeRestDTO.class)
                    .retrieve()
                    .onStatus(
                            HttpStatus.BAD_REQUEST::equals,
                            response -> Mono.error(new SaveFailedException("Already have this title")))
                    .bodyToMono(RecipeRestDTO.class)
                    .block()
            );
    }



}
