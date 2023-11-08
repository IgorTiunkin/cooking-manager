package com.phantom.recipe.exceptions;

public class SaveFailedException extends RuntimeException{
    public SaveFailedException(String message) {
        super(message);
    }
}
