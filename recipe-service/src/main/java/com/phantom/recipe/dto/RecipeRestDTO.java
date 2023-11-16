package com.phantom.recipe.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "recipeId")
public class RecipeRestDTO {

    private Integer recipeId;

    private String title;

    private List<ProductAndQuantityDTO> productAndQuantityDTOList = new ArrayList<>();

    private String actions;

    @Override
    public String toString() {
        return "RecipeRestDTO{" +
                "recipeId=" + recipeId +
                ", title='" + title + '\'' +
                ", productAndQuantityDTOList=" + productAndQuantityDTOList +
                ", actions='" + actions + '\'' +
                '}';
    }
}