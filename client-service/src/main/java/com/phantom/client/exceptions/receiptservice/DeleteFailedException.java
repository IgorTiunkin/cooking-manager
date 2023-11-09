package com.phantom.client.exceptions.receiptservice;

public class DeleteFailedException extends RuntimeException{
    public DeleteFailedException(String message) {
        super(message);
    }
}
