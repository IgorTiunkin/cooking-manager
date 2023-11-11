package com.phantom.client.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipePrepareDTO {

    private Integer recipeId;

    private String title;

    private List <ProductsForPrepareDTO> productsForPrepareDTOS;

    private String actions;


}
