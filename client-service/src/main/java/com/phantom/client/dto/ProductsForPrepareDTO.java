package com.phantom.client.dto;

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
