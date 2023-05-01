package com.example.buildrestapi.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(Long id) {
        super("Could not find resource: id=" + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
