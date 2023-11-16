package com.phantom.recipe.mappers;

import com.phantom.recipe.dto.ProductAndQuantityDTO;
import com.phantom.recipe.dto.ProductDTO;
import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.models.Recipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecipeDTOMapper {

    private final WebClient.Builder webClientBuilder;


    public List<RecipeRestDTO> mapToRecipeRestDTOList(List<Recipe> recipeList) { //todo - convert to new class
        //create set of unique product id - to make only one call
        Set <Integer> recipeIds = new HashSet<>();
        recipeList.forEach(recipe -> recipeIds.addAll(recipe.getProductIDsAndQuantities().keySet()));

        //get data for products
        List<ProductDTO> productDTOS = webClientBuilder.build()
                .get()
                .uri("http://api-gateway/api/v1/product/in",
                        uriBuilder -> uriBuilder.queryParam("recipeIds", recipeIds).build())
                .retrieve()
                .bodyToFlux(ProductDTO.class)
                .collectList()
                .block();

        //create map productid - productDTO
        Map<Integer, ProductDTO> productDTOMap =
                productDTOS.stream().collect(Collectors.toMap(ProductDTO::getProductId, Function.identity()));

        return recipeList.stream().map(entry -> this.convertToRecipeRestDTO(entry, productDTOMap))
                .collect(Collectors.toList());

    }

    public RecipeRestDTO convertToRecipeRestDTO(Recipe recipe, Map<Integer, ProductDTO> productDTOMap ) {
        //get base data from recipe
        RecipeRestDTO recipeRestDTO = new RecipeRestDTO();
        recipeRestDTO.setRecipeId(recipe.getRecipeId());
        recipeRestDTO.setTitle(recipe.getTitle());
        recipeRestDTO.setActions(recipe.getActions());

        //get data from map with product dto
        List<ProductAndQuantityDTO> productAndQuantityDTOList = new ArrayList<>();
        recipe.getProductIDsAndQuantities().entrySet()
                .forEach(entry -> productAndQuantityDTOList.add
                        (new ProductAndQuantityDTO(productDTOMap.get(entry.getKey()), entry.getValue())));
        recipeRestDTO.setProductAndQuantityDTOList(productAndQuantityDTOList);
        return recipeRestDTO;
    }

}
