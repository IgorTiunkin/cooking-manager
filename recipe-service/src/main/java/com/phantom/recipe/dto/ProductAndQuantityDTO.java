package com.phantom.recipe.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProductAndQuantityDTO {

    private Integer productId;

    private String productName;

    private Integer calories;

    private Integer quantity;

    public ProductAndQuantityDTO(ProductDTO productDTO, Integer quantity) {
        this.productId = productDTO.getProductId();
        this.productName = productDTO.getProductName();
        this.calories = productDTO.getCalories();
        this.quantity = quantity;
    }
}
