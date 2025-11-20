package io.github.hprxkbrddd.security_autoconfiguration.spring.controller;

import io.github.hprxkbrddd.security_autoconfiguration.core.AuthDTO;
import io.github.hprxkbrddd.security_autoconfiguration.core.RegistrationDTO;
import io.github.hprxkbrddd.security_autoconfiguration.core.TokenIntrospectionResponse;
import io.github.hprxkbrddd.security_autoconfiguration.core.TokenResponseDTO;
import io.github.hprxkbrddd.security_autoconfiguration.spring.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Keycloak Controller
 * REST controller exposing authentication and user-management operations
 * provided by {@link KeycloakService}.
 *
 * <p>Base path: <b>/grind/keycloak</b></p>
 *
 * <p><b>Features:</b></p>
 * <ul>
 *     <li>Token retrieval via username/password</li>
 *     <li>Token introspection</li>
 *     <li>User registration</li>
 *     <li>Security context inspection</li>
 *     <li>Protected admin-only endpoint</li>
 * </ul>
 *
 * <p>
 * Loaded only if required conditions are met (via {@link ConditionalOnMissingClass}).
 * </p>
 */
@RequiredArgsConstructor
@RequestMapping("/grind/keycloak")
@RestController
public class KeycloakController {

    /** Service handling all Keycloak-related operations. */
    private final KeycloakService keycloakService;

    /**
     * Obtain Token
     * Issues a Keycloak token using username and password.
     *
     * <p>Endpoint: <code>POST /grind/keycloak/token</code></p>
     *
     * @param dto authentication DTO containing username and password
     * @return {@link TokenResponseDTO} with access/refresh/id tokens
     */
    @PostMapping("/token")
    public ResponseEntity<TokenResponseDTO> getToken(@RequestBody AuthDTO dto) {
        return ResponseEntity.ok(
                keycloakService.getToken(dto.username(), dto.password())
                        .block()
        );
    }

    /**
     * Introspect Token
     * Validates the token and retrieves its metadata.
     *
     * <p>Endpoint: <code>POST /grind/keycloak/introspect</code></p>
     *
     * <p>The token must be provided in the <b>Authorization</b> header:</p>
     * <pre>
     * Authorization: Bearer &lt;token&gt;
     * </pre>
     *
     * @param header Authorization header containing "Bearer &lt;token&gt;"
     * @return introspection response with token metadata
     */
    @PostMapping("/introspect")
    public ResponseEntity<TokenIntrospectionResponse> introspectToken(
            @RequestHeader("Authorization") String header
    ) {
        return ResponseEntity.ok(
                keycloakService.introspectToken(header.substring(7))
                        .block()
        );
    }

    /**
     * Register New User
     * Creates a new Keycloak user with provided credentials and profile data.
     *
     * <p>Endpoint: <code>POST /grind/keycloak/register</code></p>
     *
     * @param dto registration data (username, password, email, etc.)
     * @return server response (often 201 or message body)
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationDTO dto) {
        return keycloakService.register(dto).block();
    }

    /**
     * Inspect Security Context
     * Returns the current authenticated {@link Authentication} object.
     *
     * <p>Endpoint: <code>GET /grind/keycloak/security-context</code></p>
     *
     * <p>Useful for debugging authentication and role mappings.</p>
     *
     * @return authentication object from {@link SecurityContextHolder}
     */
    @GetMapping("/security-context")
    public ResponseEntity<Authentication> inspectSecurityContext() {
        return ResponseEntity.ok(
                SecurityContextHolder.getContext().getAuthentication()
        );
    }

    /**
     * Admin-Only Test Endpoint
     * Demonstrates method-level security using {@link PreAuthorize}.
     *
     * <p>Endpoint: <code>GET /grind/keycloak/admin</code></p>
     *
     * <p>Requires role:</p>
     * <pre>ROLE_ADMIN</pre>
     *
     * @return confirmation message if access is granted
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly() {
        return "Admin access granted!";
    }
}

