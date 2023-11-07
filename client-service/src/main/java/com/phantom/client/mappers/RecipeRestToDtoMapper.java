package com.phantom.client.mappers;

import com.phantom.client.dto.ProductAndQuantityDTO;
import com.phantom.client.dto.ProductDTO;
import com.phantom.client.dto.RecipeShowDTO;
import com.phantom.client.dto.RecipeRestDTO;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
public class RecipeRestToDtoMapper {


    public List <RecipeShowDTO> mapToRecipeDto(List<RecipeRestDTO> recipeRestDTOS) {
        return recipeRestDTOS.stream().map(this::convertToRecipeDto).collect(Collectors.toList());

    }

    private RecipeShowDTO convertToRecipeDto(RecipeRestDTO recipeRestDTO) {
        List<ProductAndQuantityDTO> productAndQuantityDTOList = recipeRestDTO.getProductAndQuantityDTOList();
        TreeMap <ProductDTO, Integer> treeUsedProducts =
                new TreeMap<>(Comparator.comparing(ProductDTO::getProductName));
        productAndQuantityDTOList.stream()
                .forEach(entry -> treeUsedProducts.put(
                        ProductDTO.builder()
                        .productId(entry.getProductId())
                        .productName(entry.getProductName())
                        .calories(entry.getCalories())
                        .build()
                        , entry.getQuantity()));
        return RecipeShowDTO.builder().recipeId(recipeRestDTO.getRecipeId())
                .title(recipeRestDTO.getTitle())
                .actions(recipeRestDTO.getActions())
                .usedProducts(treeUsedProducts)
                .build();

    }
}
