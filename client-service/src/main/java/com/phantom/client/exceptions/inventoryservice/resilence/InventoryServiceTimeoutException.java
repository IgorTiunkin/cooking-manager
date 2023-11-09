package com.phantom.client.exceptions.inventoryservice.resilence;

public class InventoryServiceTimeoutException extends RuntimeException{
    public InventoryServiceTimeoutException(String message) {
        super(message);
    }
}
