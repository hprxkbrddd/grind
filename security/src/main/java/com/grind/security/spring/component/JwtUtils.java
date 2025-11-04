package com.grind.security.spring.component;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@ConditionalOnMissingClass
public class JwtUtils {

    private final JwtDecoder jwtDecoder;

    public Jwt parseJwt(String jwt) {
        return jwtDecoder.decode(jwt);
    }

    public String getSub(String jwt) {
        return parseJwt(jwt).getSubject();
    }

    public boolean isExpired(String jwt) {
        Instant expiration = parseJwt(jwt).getExpiresAt();
        assert expiration != null;
        return expiration.isBefore(Instant.now());
    }
}
