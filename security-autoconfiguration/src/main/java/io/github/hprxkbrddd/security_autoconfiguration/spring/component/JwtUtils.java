package io.github.hprxkbrddd.security_autoconfiguration.spring.component;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * <h1>JWT Utility Component</h1>
 * Helper component providing convenient methods for working with JWT tokens.
 * <p>
 * Uses Spring Security's {@link JwtDecoder} to decode and inspect tokens.
 * Registered only if no other matching bean/class is present (via
 * {@link ConditionalOnMissingClass}).
 *
 * <p><b>Features include:</b></p>
 * <ul>
 *     <li>Decoding raw JWT strings into {@link Jwt} objects</li>
 *     <li>Extracting subject (<code>sub</code> claim)</li>
 *     <li>Checking token expiration</li>
 * </ul>
 */
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
