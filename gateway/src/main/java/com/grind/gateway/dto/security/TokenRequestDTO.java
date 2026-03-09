package com.grind.gateway.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Token Request DTO
 * Represents credentials used to obtain an access token from Keycloak.
 *
 * @param username Keycloak username
 * @param password Keycloak password
 */
@Schema(description = "Credentials for requesting an access token")
public record TokenRequestDTO(
        @NotBlank
        @Schema(description = "Username", example = "user@example.com")
        String username,
        @NotBlank
        @Schema(description = "Password", example = "secret")
        String password
) {
}
