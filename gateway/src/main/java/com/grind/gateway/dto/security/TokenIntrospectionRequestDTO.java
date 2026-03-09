package com.grind.gateway.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Token Introspection Request DTO
 * Encapsulates a token that should be validated by Keycloak.
 *
 * @param token Raw JWT access token
 */
@Schema(description = "Payload for Keycloak token introspection")
public record TokenIntrospectionRequestDTO(
        @NotBlank
        @Schema(description = "Access token", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token
) {
}
