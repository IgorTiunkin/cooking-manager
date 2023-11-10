package com.phantom.inventory.exceptions;

public class ProductStockAlreadyChanged extends RuntimeException{
    public ProductStockAlreadyChanged(String message) {
        super(message);
    }
}
