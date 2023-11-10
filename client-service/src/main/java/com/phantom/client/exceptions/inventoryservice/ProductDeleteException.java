package com.phantom.client.exceptions.inventoryservice;

import lombok.Getter;

@Getter
public class ProductDeleteException extends RuntimeException{

    private Integer productId;

    public ProductDeleteException(String message, Integer productId) {
        super(message);
        this.productId = productId;
    }
}
