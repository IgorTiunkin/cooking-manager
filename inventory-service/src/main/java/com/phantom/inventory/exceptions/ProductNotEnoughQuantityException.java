package com.phantom.inventory.exceptions;

public class ProductNotEnoughQuantityException extends RuntimeException{
    public ProductNotEnoughQuantityException(String message) {
        super(message);
    }
}
