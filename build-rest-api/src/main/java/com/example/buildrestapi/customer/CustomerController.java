package com.example.buildrestapi.customer;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    public CustomerController(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @PostMapping
    CustomerResponseDTO addCustomer(@RequestBody @Valid CustomerRequestDTO customerRequest) {
        Customer newCustomer = customerRepository
                .save(customerMapper.customerRequestToCustomer(customerRequest));
        return customerMapper.customerToCustomerResponse(newCustomer);
    }

    @PutMapping("/{id}")
    CustomerResponseDTO updateCustomer(@RequestBody @Valid CustomerRequestDTO customerRequest, @PathVariable Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customer.setName(customerRequest.getName());
        customer.setAddress(customerRequest.getAddress());
        return customerMapper.customerToCustomerResponse(customerRepository.save(customer));
    }

    @GetMapping
    List<CustomerResponseDTO> findAll() {
        return customerMapper.customerListToCustomerReponseList(customerRepository.findAll());
    }

    @GetMapping("/{id}")
    CustomerResponseDTO findCustomer(@PathVariable Long id) {
        return customerMapper.customerToCustomerResponse(customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id)));
    }

    @DeleteMapping("/{id}")
    void deleteCustomer(@PathVariable Long id) {
        customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customerRepository.deleteById(id);
    }
}
