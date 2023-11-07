package com.phantom.client.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeRestElementOfListDTO {

    private Integer recipeId;

    private String title;

}
