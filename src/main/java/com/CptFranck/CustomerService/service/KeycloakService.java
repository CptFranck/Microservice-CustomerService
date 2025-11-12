package com.CptFranck.CustomerService.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakService {

    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloakAdminClient;

    public KeycloakService(Keycloak keycloakAdminClient) {
        this.keycloakAdminClient = keycloakAdminClient;
    }

    public List<UserRepresentation> getAllUsers() {
         return keycloakAdminClient.realm(realm).users().list();
    }
}
