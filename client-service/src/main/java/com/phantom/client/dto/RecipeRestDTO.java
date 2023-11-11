package com.phantom.client.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeRestDTO {

    private Integer recipeId;

    private String title;

    private List<ProductAndQuantityDTO> productAndQuantityDTOList;

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