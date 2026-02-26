package com.grind.gateway.dto.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Token Response DTO
 * Represents a standard Keycloak OAuth2 token response obtained from:
 * <ul>
 *     <li>/protocol/openid-connect/token</li>
 * </ul>
 *
 * <p>Contains access token, refresh token, ID token, expiration metadata, policy, etc.</p>
 *
 * @param access_token        generated access token (JWT)
 * @param expires_in          number of seconds until access token expires
 * @param refresh_expires_in  number of seconds until refresh token expires
 * @param refresh_token       refresh token for obtaining new access tokens
 * @param token_type          type of token (usually "Bearer")
 * @param id_token            OpenID Connect ID token
 * @param not_before_policy   NBP value used for token invalidation logic
 * @param session_state       session identifier inside Keycloak
 * @param scope               space-delimited list of granted scopes
 */
@Schema(description = "Keycloak token response")
public record TokenResponseDTO(
        @Schema(description = "Access token (JWT)")
        String access_token,
        @Schema(description = "Access token expiry (seconds)")
        Integer expires_in,
        @Schema(description = "Refresh token expiry (seconds)")
        Integer refresh_expires_in,
        @Schema(description = "Refresh token")
        String refresh_token,
        @Schema(description = "Token type")
        String token_type,
        @Schema(description = "ID token")
        String id_token,
        @JsonProperty("not-before-policy")
        @Schema(description = "Not-before policy")
        Integer not_before_policy,
        @Schema(description = "Session state")
        String session_state,
        @Schema(description = "Granted scopes")
        String scope
) {
}
