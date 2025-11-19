package io.github.hprxkbrddd.security_autoconfiguration.core;

/**
 * <h1>Keycloak Exception</h1>
 * Runtime exception used for wrapping and propagating errors
 * that occur during interaction with the Keycloak server.
 *
 * <p>Thrown when:</p>
 * <ul>
 *     <li>Keycloak returns an error response</li>
 *     <li>Connection or configuration issues occur</li>
 *     <li>Token parsing / registration errors happen</li>
 * </ul>
 */
public class KeycloakException extends RuntimeException {
    /**
     * Creates a new exception with the given error message.
     *
     * @param message description of the Keycloak-related error
     */
    public KeycloakException(String message) {
        super(message);
    }
}
