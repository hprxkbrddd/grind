package io.github.hprxkbrddd.security_autoconfiguration.spring.exception;

import io.github.hprxkbrddd.security_autoconfiguration.core.KeycloakException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <h1>Security Exception Handler</h1>
 * Global exception handler for security-related errors thrown during Keycloak
 * authentication or communication.
 *
 * <p>
 * Annotated with {@link RestControllerAdvice}, which makes it applicable across
 * all REST controllers in the application.
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Convert {@link KeycloakException} into HTTP 400 responses</li>
 *     <li>Provide consistent, readable error messages</li>
 *     <li>Prevent leaking internal stack traces to the client</li>
 * </ul>
 */
@RestControllerAdvice
public class SecurityExceptionHandler {

    /**
     * <h2>Handle KeycloakException</h2>
     * Converts a {@link KeycloakException} into an HTTP 400 Bad Request response.
     *
     * <p>Triggered when any part of the authentication/authorization flow throws
     * {@link KeycloakException}.</p>
     *
     * @param ex exception instance containing the error details
     * @return HTTP 400 response with human-readable message
     */
    @ExceptionHandler(KeycloakException.class)
    public ResponseEntity<String> handleKeycloakException(KeycloakException ex) {
        return ResponseEntity
                .badRequest()
                .body("Keycloak exception: " + ex.getMessage());
    }
}
