package com.phantom.client.exceptions.inventoryservice;

public class ProductDeleteAbsentException extends RuntimeException{
    public ProductDeleteAbsentException(String message) {
        super(message);
    }
}
