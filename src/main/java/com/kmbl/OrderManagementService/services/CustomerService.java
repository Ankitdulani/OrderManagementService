package com.kmbl.OrderManagementService.services;

import com.kmbl.OrderManagementService.models.Customer;
import com.kmbl.OrderManagementService.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    private CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer getCustomerById(String customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }
}
