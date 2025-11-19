package io.github.hprxkbrddd.security_autoconfiguration.core;

/**
 * <h1>User Entity DTO</h1>
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
