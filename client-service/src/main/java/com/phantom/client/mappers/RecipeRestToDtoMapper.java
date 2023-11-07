package com.phantom.client.mappers;

import com.phantom.client.dto.ProductDTO;
import com.phantom.client.dto.RecipeDTO;
import com.phantom.client.dto.RecipeRestDTO;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
public class RecipeRestToDtoMapper {


    public List <RecipeDTO> mapToRecipeDto(List<RecipeRestDTO> recipeRestDTOS) {
        return recipeRestDTOS.stream().map(this::convertToRecipeDto).collect(Collectors.toList());

    }

    private RecipeDTO convertToRecipeDto(RecipeRestDTO recipeRestDTO) {
        Map<ProductDTO, Integer> usedProducts = recipeRestDTO.getUsedProducts();
        TreeMap <ProductDTO, Integer> treeUsedProducts =
                new TreeMap<>(Comparator.comparing(ProductDTO::getProductName));
        usedProducts.entrySet().stream().forEach(entry -> treeUsedProducts.put(entry.getKey(), entry.getValue()));
        return RecipeDTO.builder().recipeId(recipeRestDTO.getRecipeId())
                .title(recipeRestDTO.getTitle())
                .actions(recipeRestDTO.getActions())
                .usedProducts(treeUsedProducts)
                .build();

    }
}
