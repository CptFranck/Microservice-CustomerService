package com.CptFranck.CustomerService.controller;

import com.CptFranck.CustomerService.service.CustomerService;
import com.CptFranck.dto.CustomerDto;
import com.CptFranck.dto.KeycloakUserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json", path = "/customers")
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody KeycloakUserDto request) {
        try {
            CustomerDto customer = customerService.create(
                    Long.valueOf(request.getKeycloakId()),
                    request.getUsername(),
                    request.getEmail()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(customer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PutMapping(consumes = "application/json",produces = "application/json", path = "/customers")
    public ResponseEntity<CustomerDto> updateCustomer(@RequestBody CustomerDto customerDto) {
        try {
            CustomerDto customer = customerService.update(
                    customerDto.getId(),
                    customerDto.getUsername(),
                    customerDto.getEmail(),
                    customerDto.getAddress()
            );
            return ResponseEntity.ok(customer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{keycloakId}")
    public ResponseEntity<CustomerDto> deleteCustomer(@PathVariable Long keycloakId) {
        try {
            CustomerDto customer = customerService.delete(keycloakId);
            return ResponseEntity.ok(customer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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
