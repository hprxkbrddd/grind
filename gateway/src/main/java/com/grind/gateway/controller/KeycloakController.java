package com.grind.gateway.controller;

import com.grind.gateway.dto.security.RegistrationDTO;
import com.grind.gateway.dto.security.TokenIntrospectionRequestDTO;
import com.grind.gateway.dto.security.TokenIntrospectionResponse;
import com.grind.gateway.dto.security.TokenRequestDTO;
import com.grind.gateway.dto.security.TokenResponseDTO;
import com.grind.gateway.service.keycloak.KeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/grind/keycloak")
@RequiredArgsConstructor
@Tag(name = "Keycloak API")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Keycloak error")
})
public class KeycloakController {

    private final KeycloakService keycloakService;

    @PostMapping("/token")
    @Operation(summary = "Obtain Keycloak access token")
    public Mono<TokenResponseDTO> getToken(@Valid @RequestBody TokenRequestDTO request) {
        return keycloakService.getToken(request.username(), request.password());
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new Keycloak user")
    public Mono<ResponseEntity<String>> register(@Valid @RequestBody RegistrationDTO request) {
        return keycloakService.register(request);
    }

    @PostMapping("/token/introspect")
    @Operation(summary = "Introspect Keycloak token")
    public Mono<TokenIntrospectionResponse> introspectToken(
            @Valid @RequestBody TokenIntrospectionRequestDTO request
    ) {
        return keycloakService.introspectToken(request.token());
    }
}
