package com.phantom.inventory.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductsForPrepareDTO {

    private Integer productId;

    private String productName;

    private Integer currentQuantity;

    private Integer neededQuantity;

}
