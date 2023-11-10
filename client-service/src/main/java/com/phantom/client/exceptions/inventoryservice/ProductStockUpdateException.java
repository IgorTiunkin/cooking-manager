package com.phantom.client.exceptions.inventoryservice;

public class ProductStockUpdateException extends RuntimeException{
    public ProductStockUpdateException(String message) {
        super(message);
    }
}
