package com.phantom.client.exceptions;

public class InventoryServiceTooManyRequestsException extends RuntimeException{
    public InventoryServiceTooManyRequestsException(String message) {
        super(message);
    }
}
