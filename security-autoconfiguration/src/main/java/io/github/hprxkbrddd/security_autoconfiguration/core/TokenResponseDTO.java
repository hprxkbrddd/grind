package io.github.hprxkbrddd.security_autoconfiguration.core;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public record TokenResponseDTO(
        String access_token,
        Integer expires_in,
        Integer refresh_expires_in,
        String refresh_token,
        String token_type,
        String id_token,
        @JsonProperty("not-before-policy")
        Integer not_before_policy,
        String session_state,
        String scope
) {
}
