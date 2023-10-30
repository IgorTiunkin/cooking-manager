package com.phantom.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInStockDTO {

    private Integer stockId;

    private Integer quantity;
}
