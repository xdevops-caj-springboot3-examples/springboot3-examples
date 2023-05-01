package com.example.buildrestapi.customer;

import com.example.buildrestapi.exception.ResourceNotFoundException;

public class CustomerNotFoundException extends ResourceNotFoundException {
    public CustomerNotFoundException(Long id) {
        super("Could not find customer: id=" + id);
    }
}
