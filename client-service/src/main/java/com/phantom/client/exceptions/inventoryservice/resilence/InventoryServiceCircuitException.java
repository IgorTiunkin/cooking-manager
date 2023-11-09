package com.phantom.client.exceptions.inventoryservice.resilence;

public class InventoryServiceCircuitException extends RuntimeException{
    public InventoryServiceCircuitException(String message) {
        super(message);
    }
}
