package com.grind.gateway.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "Keycloak token introspection response")
public record TokenIntrospectionResponse(
        @Schema(description = "Whether the token is active")
        boolean active,
        @Schema(description = "Subject")
        String sub,
        @Schema(description = "Username")
        String username,
        @Schema(description = "Email")
        String email,
        @Schema(description = "Expiration timestamp (epoch seconds)")
        Long exp,
        @Schema(description = "Issued-at timestamp (epoch seconds)")
        Long iat,
        @Schema(description = "Granted scopes")
        String scope,
        @Schema(description = "Token type")
        String token_type,
        @Schema(description = "Client id")
        String client_id

) {
}
