package com.grind.security.spring.exception;

import com.grind.security.core.KeycloakException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SecurityExceptionHandler {

    @ExceptionHandler(KeycloakException.class)
    public ResponseEntity<String> handleKeycloakException(
            KeycloakException ex
    ){
        return ResponseEntity.badRequest().body("Keycloak exception: "+ex.getMessage());
    }
}
