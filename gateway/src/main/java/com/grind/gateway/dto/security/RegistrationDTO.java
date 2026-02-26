package com.grind.gateway.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * User Registration DTO
 * Represents a request payload for creating a new Keycloak user.
 *
 * @param username   unique username for the account
 * @param password   raw password for the account
 * @param email      user’s email address
 * @param firstName  user's first name
 * @param lastName   user's last name
 * @param isEnabled  flag indicating whether the account is active
 */
@Schema(description = "User registration payload")
public record RegistrationDTO(
        @Schema(description = "Username")
        String username,
        @Schema(description = "Password")
        String password,
        @Schema(description = "Email address")
        String email,
        @Schema(description = "First name")
        String firstName,
        @Schema(description = "Last name")
        String lastName,
        @Schema(description = "Whether account is enabled")
        boolean isEnabled
) {
}
