package io.github.hprxkbrddd.security_autoconfiguration.core;

/**
 * User Entity DTO
 * Lightweight representation of a Keycloak user.
 * Used for responses where only minimal user data is required.
 *
 * @param id       unique user identifier (UUID from Keycloak)
 * @param username username of the user
 */
public record UserEntityDTO(
        String id,
        String username
) {
}
