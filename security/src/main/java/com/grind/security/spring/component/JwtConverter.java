package com.grind.security.spring.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     * <br>
     * P.S. Keycloak has to be configured to have role mapper, so roles from {@code authorities} claim come with 'ROLE_' prefix.
     *
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt source) {
        Map<String, Map<String, List<String>>> resourceAccess = source.getClaim("resource_access");
        List<String> roles = resourceAccess.get(clientId).get("roles");
        Set<GrantedAuthority> grantedAuthorities = roles
                .stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
        return new JwtAuthenticationToken(source, grantedAuthorities, source.getSubject());
    }
}
