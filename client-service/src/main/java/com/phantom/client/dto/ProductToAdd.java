package com.phantom.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductToAdd {
    private Integer productId;

    @NotNull (message = "Quantity must be positive")
    @Min(value = 0, message = "Quantity must be positive")
    private Integer quantity;
}
