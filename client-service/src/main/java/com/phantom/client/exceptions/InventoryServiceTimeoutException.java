package com.phantom.client.exceptions;

public class InventoryServiceTimeoutException extends RuntimeException{
    public InventoryServiceTimeoutException(String message) {
        super(message);
    }
}
