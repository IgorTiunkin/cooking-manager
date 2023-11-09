package com.phantom.client.exceptions.inventoryservice;

public class InventoryServiceTimeoutException extends RuntimeException{
    public InventoryServiceTimeoutException(String message) {
        super(message);
    }
}
