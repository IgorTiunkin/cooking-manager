package com.phantom.client.exceptions.receiptservice;

public class SaveFailedException extends RuntimeException{
    public SaveFailedException(String message) {
        super(message);
    }
}
