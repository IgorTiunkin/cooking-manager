package com.phantom.recipe.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ProductDTO implements Comparable <ProductDTO> {

    private Integer productId;

    private String productName;

    private Integer calories;

    @Override
    public String toString() {
        return  "productName='" + productName + '\'' +
                ", calories=" + calories +
                '}';
    }

    @Override
    public int compareTo(ProductDTO o) {
        return this.getProductName().compareTo(o.getProductName());
    }
}
