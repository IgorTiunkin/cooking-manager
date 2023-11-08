package com.phantom.recipe.mappers;

import com.phantom.recipe.dto.ProductAndQuantityDTO;
import com.phantom.recipe.dto.ProductDTO;
import com.phantom.recipe.dto.RecipeRestDTO;
import com.phantom.recipe.models.Recipe;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RecipeMapper {

    public Recipe convertToRecipe(RecipeRestDTO recipeRestDTO) {
        List<ProductAndQuantityDTO> productAndQuantityDTOList = recipeRestDTO.getProductAndQuantityDTOList();
        Map<Integer, Integer>  productIDsAndQuantities = new HashMap<>();
        productAndQuantityDTOList
                .forEach(entry ->  productIDsAndQuantities.put(entry.getProductId()
                        ,entry.getQuantity()));
        return Recipe.builder()
                .title(recipeRestDTO.getTitle())
                .actions(recipeRestDTO.getActions())
                .productIDsAndQuantities(productIDsAndQuantities)
                .build();
    }

}
