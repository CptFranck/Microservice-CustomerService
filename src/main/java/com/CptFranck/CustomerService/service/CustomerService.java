package com.CptFranck.CustomerService.service;

import com.CptFranck.CustomerService.dto.CustomerResponse;
import com.CptFranck.CustomerService.entity.CustomerEntity;
import com.CptFranck.CustomerService.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Service
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse getOrCreateUser(JwtAuthenticationToken auth) {
        final Jwt jwt = auth.getToken();

        String keycloakId = jwt.getClaimAsString("sub");
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");

        Long id = Long.valueOf(keycloakId);

        CustomerEntity customer = customerRepository.findById(id)
                .orElseGet(() -> customerRepository.save(new CustomerEntity(id, username, email, "")));

        return CustomerResponse.builder()
                .customerId(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .build();
    }
}
