package io.github.hprxkbrddd.security_autoconfiguration.core;

/**
 * <h1>Authentication DTO</h1>
 * Simple data transfer object used to pass user credentials (username, password)
 * to authentication endpoints (e.g., Keycloak login).
 *
 * @param username name of the user
 * @param password raw user password
 */
public record AuthDTO(String username, String password) {
}
