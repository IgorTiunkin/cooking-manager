package com.phantom.recipe.mappers;

import com.phantom.recipe.dto.ProductDTO;
import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.dto.RecipeRestElementOfListDTO;
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


    public List<RecipeRestElementOfListDTO> mapToRecipeRestListDTOList(List<Recipe> recipeList) {
        Set <Integer> recipeIds = new HashSet<>();
        recipeList.forEach(recipe -> recipeIds.addAll(recipe.getProductIDsAndQuantities().keySet()));

        return recipeList.stream().map(this::convertToRecipeRestDTO)
                .collect(Collectors.toList());

    }

    private RecipeRestElementOfListDTO convertToRecipeRestDTO(Recipe recipe) {
        RecipeRestElementOfListDTO recipeRestElementOfListDTO = new RecipeRestElementOfListDTO();
        recipeRestElementOfListDTO.setRecipeId(recipe.getRecipeId());
        recipeRestElementOfListDTO.setTitle(recipe.getTitle());
        return recipeRestElementOfListDTO;
    }

}
