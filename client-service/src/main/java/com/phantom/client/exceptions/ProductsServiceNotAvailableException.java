package com.phantom.client.exceptions;

public class ProductsServiceNotAvailableException extends RuntimeException {
    public ProductsServiceNotAvailableException(String message) {
        super(message);
    }
}
