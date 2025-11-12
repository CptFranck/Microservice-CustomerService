package com.CptFranck.CustomerService.service;

import com.CptFranck.CustomerService.entity.CustomerEntity;
import com.CptFranck.CustomerService.repository.CustomerRepository;
import com.CptFranck.dto.CustomerDto;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CustomerService {

    private final KeycloakService keycloakService;

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository, KeycloakService keycloakService) {
        this.customerRepository = customerRepository;
        this.keycloakService = keycloakService;
    }

    public CustomerDto createFromKeycloakUser(String keycloakId, String username, String email, String firstname, String lastname) {
        if (customerRepository.existsById(keycloakId))
            throw new IllegalArgumentException("Customer already exists with id: " + keycloakId);

        CustomerEntity customer = new CustomerEntity(keycloakId, username, email, firstname, lastname, "");
        customer = customerRepository.save(customer);

        return CustomerDto.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .email(customer.getEmail())
                .firstname(customer.getFirstname())
                .lastname(customer.getLastname())
                .address(customer.getAddress())
                .build();
    }

    public CustomerDto updateFromKeycloakUser(String keycloakId, String username, String email, String firstname, String lastname) {
        CustomerEntity customer = customerRepository.findById(keycloakId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + keycloakId));

        customer.setUsername(username);
        customer.setEmail(email);
        customer.setFirstname(firstname);
        customer.setLastname(lastname);

        customer = customerRepository.save(customer);

        return CustomerDto.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .email(customer.getEmail())
                .firstname(customer.getFirstname())
                .lastname(customer.getLastname())
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
                .firstname(customer.getFirstname())
                .lastname(customer.getLastname())
                .address(customer.getAddress())
                .build();
    }

    @Scheduled(fixedRateString = "${keycloak.scheduled.mapping.fixedRate}", initialDelayString = "${keycloak.scheduled.mapping.initialDelay}")
    public void UnregisteredKeycloakUser() {
        try {
            List<UserRepresentation> users = keycloakService.getAllUsers();
            Set<String> customersId = customerRepository.findAll().stream()
                    .map(CustomerEntity::getId)
                    .collect(Collectors.toSet());

            for (UserRepresentation user : users) {
                if (!customersId.contains(user.getId())) {
                    createFromKeycloakUser(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName()
                    );
                }
            }
            log.info("✅ Synchronisation Keycloak terminée : {} utilisateurs vérifiés", users.size());
        } catch (Exception e) {
            log.error("❌ Erreur pendant la synchronisation Keycloak", e);
        }
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
