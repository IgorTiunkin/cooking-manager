package com.phantom.inventory.dto;

import lombok.*;

import javax.persistence.Column;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ProductDTO {

    private Integer productId;

    private String productName;

    private Integer calories;
}
