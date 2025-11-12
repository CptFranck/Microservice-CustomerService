package com.CptFranck.CustomerService.controller;

import com.CptFranck.CustomerService.service.CustomerService;
import com.CptFranck.dto.CustomerDto;
import com.CptFranck.dto.KeycloakUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json", path = "/keycloak-event/new-customer")
    public ResponseEntity<CustomerDto> createCustomerFromKeycloakEvent(@RequestBody KeycloakUserDto request) {
        log.info("Create customer: {}", request);

        CustomerDto customer = customerService.createFromKeycloakUser(
                request.getKeycloakId(),
                request.getUsername(),
                request.getEmail(),
                request.getFirstname(),
                request.getLastname());

        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @PutMapping(consumes = "application/json",produces = "application/json", path = "/keycloak-event/update-customer")
    public ResponseEntity<CustomerDto> updateCustomerFromKeycloakEvent(@RequestBody KeycloakUserDto keycloakUserDto) {
        log.info("Update customer: {}", keycloakUserDto);
        CustomerDto customer = customerService.updateFromKeycloakUser(
                keycloakUserDto.getKeycloakId(),
                keycloakUserDto.getUsername(),
                keycloakUserDto.getEmail(),
                keycloakUserDto.getFirstname(),
                keycloakUserDto.getLastname());

        return ResponseEntity.ok(customer);
    }

    @PutMapping(consumes = "application/json",produces = "application/json", path = "/customers")
    public ResponseEntity<CustomerDto> updateCustomerFromUserRequest(@RequestBody CustomerDto customerDto) {
        log.info("Update customer: {}", customerDto);
            CustomerDto customer = customerService.updateFromKeycloakUser(
                    customerDto.getId(),
                    customerDto.getUsername(),
                    customerDto.getEmail(),
                    customerDto.getFirstname(),
                    customerDto.getLastname());

        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/keycloak-event/delete-customer/{keycloakId}")
    public ResponseEntity<CustomerDto> deleteCustomerFromKeycloakEvent(@PathVariable String keycloakId) {
        log.info("Delete customer: {}", keycloakId);
        CustomerDto customer = customerService.delete(keycloakId);
        return ResponseEntity.ok(customer);
    }
//    @GetMapping("/me")
//    public UserInfoDto getGretting(JwtAuthenticationToken auth) {
//        return new UserInfoDto(
//                auth.getToken().getClaimAsString(StandardClaimNames.PREFERRED_USERNAME),
//                auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
//    }

//    @GetMapping(path = "/customer")
//    public CustomerResponse createBooking(JwtAuthenticationToken auth){
//        return customerService.getOrCreateUser(auth);
//    }
}
