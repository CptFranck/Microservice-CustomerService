package com.CptFranck.CustomerService.controller;

import com.CptFranck.CustomerService.dto.CustomerResponse;
import com.CptFranck.CustomerService.service.CustomerService;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json", path = "/customer")
    public CustomerResponse createBooking(JwtAuthenticationToken auth ){
        return customerService.getOrCreateUser(auth);
    }
}
