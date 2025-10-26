package com.grind.security.controller;

import com.grind.security.dto.AuthDTO;
import com.grind.security.dto.TokenIntrospectionResponse;
import com.grind.security.dto.TokenResponseDTO;
import com.grind.security.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/grind/keycloak")
public class KeycloakController {
    private final KeycloakService keycloakService;

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDTO> getToken(@RequestBody AuthDTO dto){
        return ResponseEntity.ok(
                keycloakService.getToken(dto.username(), dto.password())
                        .block()
        );
    }

    @PostMapping("/introspect")
    public ResponseEntity<TokenIntrospectionResponse> introspectToken(@RequestHeader("Authorization") String header){
        return ResponseEntity.ok(
                keycloakService.introspectToken(header.substring(7))
                        .block()
        );
    }

    @GetMapping("/security-context")
    public ResponseEntity<Authentication> inspectSecurityContext(){
        return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminOnly() {
        return "Admin access granted!";
    }
}
