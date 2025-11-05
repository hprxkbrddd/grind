package com.grind.security.spring.component;

import com.grind.security.autoconfiguration.LibraryProperties;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final LibraryProperties props;

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
        String clientId = props.oauth2.client.registration.keycloak.clientId;
        List<String> roles = resourceAccess.get(clientId).get("roles");
        Set<GrantedAuthority> grantedAuthorities = roles
                .stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
        return new JwtAuthenticationToken(source, grantedAuthorities, source.getSubject());
    }
}
