package com.example.buildrestapi.customer;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CustomerMapper {

    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    Customer customerRequestToCustomer(CustomerRequestDTO customerRequest);

    CustomerResponseDTO customerToCustomerResponse(Customer customer);

    List<CustomerResponseDTO> customerListToCustomerReponseList(List<Customer> customerList);
}
