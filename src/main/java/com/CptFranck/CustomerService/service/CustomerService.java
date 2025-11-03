package com.CptFranck.CustomerService.service;

import com.CptFranck.CustomerService.entity.CustomerEntity;
import com.CptFranck.CustomerService.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerEntity create(Long keycloakId, String name, String email) {
        if (customerRepository.existsById(keycloakId))
            throw new IllegalArgumentException("Customer already exists with id: " + keycloakId);

        CustomerEntity customer = new CustomerEntity(keycloakId, name, email, "");

        return customerRepository.save(customer);
    }

    public CustomerEntity update(Long keycloakId, String name, String email, String address) {
        CustomerEntity customer = customerRepository.findById(keycloakId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + keycloakId));

        customer.setName(name);
        customer.setEmail(email);
        customer.setAddress(address);
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long keycloakId) {
        CustomerEntity customer = customerRepository.findById(keycloakId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + keycloakId));
        customerRepository.delete(customer);
    }

//    public CustomerResponse getOrCreateUser(JwtAuthenticationToken auth) {
//        final Jwt jwt = auth.getToken();
//
//        String keycloakId = jwt.getClaimAsString("sub");
//        String username = jwt.getClaimAsString("preferred_username");
//        String email = jwt.getClaimAsString("email");
//
//        Long id = Long.valueOf(keycloakId);
//
//        CustomerEntity customer = customerRepository.findById(id)
//                .orElseGet(() -> customerRepository.save(new CustomerEntity(id, username, email, "")));
//
//        return CustomerResponse.builder()
//                .customerId(customer.getId())
//                .name(customer.getName())
//                .email(customer.getEmail())
//                .address(customer.getAddress())
//                .build();
//    }
}
