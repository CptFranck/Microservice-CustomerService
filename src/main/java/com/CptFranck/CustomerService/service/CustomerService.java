package com.CptFranck.CustomerService.service;

import com.CptFranck.CustomerService.entity.CustomerEntity;
import com.CptFranck.CustomerService.repository.CustomerRepository;
import com.CptFranck.dto.CustomerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerDto createFromKeycloakEvent(String keycloakId, String username, String email, String firstname, String lastname) {
        if (customerRepository.existsById(keycloakId))
            throw new IllegalArgumentException("Customer already exists with id: " + keycloakId);

        CustomerEntity customer = new CustomerEntity(keycloakId, username, email, firstname, lastname, "");
        customer = customerRepository.save(customer);

        return CustomerDto.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .email(customer.getEmail())
                .firstname(customer.getUsername())
                .lastname(customer.getUsername())
                .address(customer.getAddress())
                .build();
    }

    public CustomerDto updateFromKeycloakEvent(String keycloakId, String name, String email, String firstname, String lastname) {
        CustomerEntity customer = customerRepository.findById(keycloakId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + keycloakId));

        customer.setUsername(name);
        customer.setEmail(email);
        customer.setFirstname(firstname);
        customer.setLastname(lastname);

        customer = customerRepository.save(customer);

        return CustomerDto.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .build();
    }

    public CustomerDto delete(String keycloakId) {
        CustomerEntity customer = customerRepository.findById(keycloakId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + keycloakId));
        customerRepository.delete(customer);
        return CustomerDto.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .build();
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
