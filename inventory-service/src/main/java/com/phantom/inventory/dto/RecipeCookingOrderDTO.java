package com.phantom.inventory.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeCookingOrderDTO {

    private Integer recipeId;

    private String title;

    private List<ProductAndQuantityDTO> productAndQuantityDTOList;

    private LocalDateTime timestamp;
}
