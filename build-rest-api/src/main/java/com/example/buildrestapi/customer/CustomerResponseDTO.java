package com.example.buildrestapi.customer;

import lombok.Data;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String name;
    private String address;
}
