package com.phantom.client.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class RecipeRestDTO {

    private Integer recipeId;

    @NotEmpty(message = "Title should not be empty")
    @Size(max = 100, message = "Title should not be over 100 characters")
    private String title;

    private Map<ProductDTO, Integer> usedProducts = new HashMap<>();

    @NotEmpty(message = "Actions should not be empty")
    @Size(max = 255, message = "Actions should not be over 255 characters")
    private String actions;

    @Override
    public String toString() {
        return "ReceiptDTO{" +
                "title='" + title + '\'' +
                ", usedProducts=" + usedProducts +
                ", actions='" + actions + '\'' +
                '}';
    }
}