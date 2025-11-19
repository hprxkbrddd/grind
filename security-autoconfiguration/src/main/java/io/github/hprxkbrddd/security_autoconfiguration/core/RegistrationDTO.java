package io.github.hprxkbrddd.security_autoconfiguration.core;

/**
 * <h1>User Registration DTO</h1>
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
