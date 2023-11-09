package com.phantom.client.exceptions.inventoryservice;

public class InventoryServiceTooManyRequestsException extends RuntimeException{
    public InventoryServiceTooManyRequestsException(String message) {
        super(message);
    }
}
