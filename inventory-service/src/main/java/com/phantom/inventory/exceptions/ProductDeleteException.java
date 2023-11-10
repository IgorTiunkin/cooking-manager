package com.phantom.inventory.exceptions;

import lombok.Getter;

@Getter
public class ProductDeleteException extends RuntimeException{

    public ProductDeleteException(String message) {
        super(message);
    }
}