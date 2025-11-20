package io.github.hprxkbrddd.security_autoconfiguration.core;

/**
 * Token Introspection Response
 * Represents a response structure from Keycloak's
 * <code>/token/introspect</code> endpoint.
 *
 * <p>Used to determine whether the token is active and retrieve its metadata.</p>
 *
 * @param active     whether the token is valid and active
 * @param sub        subject (usually user ID)
 * @param username   username associated with the token
 * @param email      email extracted from token claims
 * @param exp        expiration timestamp (epoch seconds)
 * @param iat        issued-at timestamp (epoch seconds)
 * @param scope      scopes granted to the token
 * @param token_type type of token (e.g., "Bearer")
 * @param client_id  ID of the OAuth2 client that generated the token
 */
public record TokenIntrospectionResponse(
         boolean active,
         String sub,
         String username,
         String email,
         Long exp,
         Long iat,
         String scope,
         String token_type,
         String client_id

) {
}
