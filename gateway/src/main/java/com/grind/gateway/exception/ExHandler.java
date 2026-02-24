package com.grind.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class ExHandler {
    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<Object> handleTimeoutException(
            TimeoutException ex
    ){
        return ResponseEntity
                .status(HttpStatus.GATEWAY_TIMEOUT)
                .body(ex);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(
            IllegalStateException ex
    ){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex);
    }

    /**
     * Handle KeycloakException
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
