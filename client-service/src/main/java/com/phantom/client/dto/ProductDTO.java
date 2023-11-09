package com.phantom.client.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Comparator;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ProductDTO implements Comparable <ProductDTO> {

    private Integer productId;

    @NotEmpty(message = "Name should not be empty")
    @Size(max = 100, message = "Name should not be over 100 characters")
    private String productName;

    @NotNull(message = "Calories should be positive")
    @Min(value = 0, message = "Calories should be positive")
    private Integer calories;

    @Override
    public String toString() {
        return "ProductDTO{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", calories=" + calories +
                '}';
    }

    @Override
    public int compareTo(ProductDTO o) {
        return this.getProductName().compareTo(o.getProductName());
    }
}
