package com.CptFranck.CustomerService.config;

import io.micrometer.common.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final String ressourceId;

    private final String principalAttribute;

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;

    public JwtAuthConverter(@Value("${keycloak.auth.converter.ressource-id}") String ressourceId_,
                            @Value("${keycloak.auth.converter.principal-attribute}") String principalAttribute_) {
        this.ressourceId = ressourceId_;
        this.principalAttribute = principalAttribute_;
        this.jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    }

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                Objects.requireNonNull(this.jwtGrantedAuthoritiesConverter.convert(jwt)).stream(),
                extractRessourceRoles(jwt).stream()
            ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(
                jwt,
                authorities,
                getPrinciplesClaimName(jwt)
        );
    }

    private Collection<GrantedAuthority> extractRessourceRoles(Jwt jwt) {
        if(jwt.getClaim("resource_access") == null) return Set.of();

        Map<String, Object> ressourceAccess = jwt.getClaim("resource_access");
        if(ressourceAccess.get(ressourceId) == null) return Set.of();

        Map<String, Object> ressource = (Map<String, Object>) ressourceAccess.get(ressourceId);

        Collection<String> roles = Optional.ofNullable((Collection<String>) ressource.get("roles"))
                .orElse(Collections.emptyList());

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.replace("-", "_")))
                .collect(Collectors.toSet());
    }

    private String getPrinciplesClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if(principalAttribute != null)
            claimName = principalAttribute;
        return jwt.getClaimAsString(claimName);
    }
}
