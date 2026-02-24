package com.grind.gateway.dto.security;

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
public record RegistrationDTO(
        String username,
        String password,
        String email,
        String firstName,
        String lastName,
        boolean isEnabled
) {
}
