package com.example.buildrestapi.exception;

public class ResourceAlreadyExistsException extends RuntimeException{

    public ResourceAlreadyExistsException(Long id) {
        super("The resource already exists: id=" + id);
    }

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
