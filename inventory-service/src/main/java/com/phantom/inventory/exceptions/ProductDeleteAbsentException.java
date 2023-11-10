package com.phantom.inventory.exceptions;

public class ProductDeleteAbsentException extends RuntimeException{
    public ProductDeleteAbsentException(String message) {
        super(message);
    }
}
