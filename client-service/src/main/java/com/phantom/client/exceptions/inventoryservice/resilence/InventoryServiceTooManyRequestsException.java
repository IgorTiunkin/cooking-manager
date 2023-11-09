package com.phantom.client.exceptions.inventoryservice.resilence;

public class InventoryServiceTooManyRequestsException extends RuntimeException{
    public InventoryServiceTooManyRequestsException(String message) {
        super(message);
    }
}
