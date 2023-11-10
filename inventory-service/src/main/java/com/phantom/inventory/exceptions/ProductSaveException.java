package com.phantom.inventory.exceptions;

public class ProductSaveException extends RuntimeException{
    public ProductSaveException(String message) {
        super(message);
    }
}
