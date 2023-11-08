package com.phantom.client.exceptions;

public class SaveFailedException extends RuntimeException{
    public SaveFailedException(String message) {
        super(message);
    }
}
