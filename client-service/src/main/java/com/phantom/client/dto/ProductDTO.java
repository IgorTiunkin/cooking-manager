package com.phantom.client.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ProductDTO {

    private int productId;

    private String productName;

    private int calories;

    @Override
    public String toString() {
        return  "productName='" + productName + '\'' +
                ", calories=" + calories +
                '}';
    }
}
