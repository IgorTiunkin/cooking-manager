package com.phantom.client.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInStockDTO {

    private Integer stockId;

    private Integer productId;

    private Integer quantity;

    private Integer recommendedQuantity;
}