package com.grind.security.service;

import com.grind.security.core.TokenIntrospectionResponse;
import com.grind.security.core.TokenResponseDTO;
import com.grind.security.core.KeycloakException;
import com.grind.security.spring.service.KeycloakService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakServiceTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WebClient webClientPublic;

    @Mock
    private WebClient webClientAdmin;

    private KeycloakService keycloakService;

    @BeforeEach
    void setUp() throws Exception {
        keycloakService = new KeycloakService();

        // Устанавливаем значения через reflection
        String clientId = "test-client";
        setField(keycloakService, "clientId", clientId);
        String clientSecret = "test-secret";
        setField(keycloakService, "clientSecret", clientSecret);
        String adminUsername = "admin";
        setField(keycloakService, "adminUsername", adminUsername);
        String adminPassword = "admin-pass";
        setField(keycloakService, "adminPassword", adminPassword);
        setField(keycloakService, "webClientPublic", webClientPublic);
        setField(keycloakService, "webClientAdmin", webClientAdmin);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void getToken_Success() {
        // Arrange
        String username = "testuser";
        String password = "testpass";
        TokenResponseDTO expectedToken = new TokenResponseDTO(
                "access-token-123", 300, 1800, "refresh-token-123",
                "Bearer", "id-token-123", 0, "session-123", "openid"
        );

        when(webClientPublic.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/token")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(MultiValueMap.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(TokenResponseDTO.class)).thenReturn(Mono.just(expectedToken));

        // Act
        Mono<TokenResponseDTO> result = keycloakService.getToken(username, password);

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedToken)
                .verifyComplete();

        verify(webClientPublic).post();
        verify(requestBodyUriSpec).uri("/token");
    }

    @Test
    void getToken_EmptyUsername_ThrowsException() {
        // Arrange
        String username = "";
        String password = "testpass";

        // Act & Assert
        assertThrows(KeycloakException.class, () ->
                keycloakService.getToken(username, password)
        );
    }

    @Test
    void getToken_EmptyPassword_ThrowsException() {
        // Arrange
        String username = "testuser";
        String password = "";

        // Act & Assert
        assertThrows(KeycloakException.class, () ->
                keycloakService.getToken(username, password)
        );
    }

    @Test
    void getToken_NullUsername_ThrowsException() {
        // Arrange
        String username = null;
        String password = "testpass";

        // Act & Assert
        assertThrows(KeycloakException.class, () ->
                keycloakService.getToken(username, password)
        );
    }

    @Test
    void getToken_NullPassword_ThrowsException() {
        // Arrange
        String username = "testuser";
        String password = null;

        // Act & Assert
        assertThrows(KeycloakException.class, () ->
                keycloakService.getToken(username, password)
        );
    }

//    @Test
//    void register_Success() throws Exception {
//        // Arrange
//        RegistrationDTO registrationDTO = new RegistrationDTO(
//                "testuser", "test@example.com", "John", "Doe", "password123", true
//        );
//
//        TokenResponseDTO adminToken = new TokenResponseDTO(
//                "admin-token", 300, 1800, "refresh-admin",
//                "Bearer", "id-admin", 0, "session-admin", "openid"
//        );
//
//        // Mock the complete WebClient chain
//        when(webClientAdmin.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodySpec.header(eq("Authorization"), eq("Bearer " + adminToken.access_token()))).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(any(UserRepresentation.class))).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//
//        // Mock the response - ensure this returns a non-null Mono
//        ResponseEntity<Void> responseEntity = ResponseEntity.status(HttpStatus.CREATED).build();
//        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(responseEntity));
//
//        // Mock admin token retrieval
//        mockTokenRetrieval(adminToken);
//
//        // Act
//        Mono<ResponseEntity<String>> result = keycloakService.register(registrationDTO);
//
//        // Assert
//        StepVerifier.create(result)
//                .expectNextMatches(response ->
//                        response.getStatusCode() == HttpStatus.CREATED
//                )
//                .verifyComplete();
//
//        // Verify user creation parameters
//        ArgumentCaptor<UserRepresentation> userCaptor = ArgumentCaptor.forClass(UserRepresentation.class);
//        verify(requestBodySpec).bodyValue(userCaptor.capture());
//
//        UserRepresentation capturedUser = userCaptor.getValue();
//        assertEquals(registrationDTO.username(), capturedUser.getUsername());
//        assertEquals(registrationDTO.email(), capturedUser.getEmail());
//        assertEquals(registrationDTO.firstName(), capturedUser.getFirstName());
//        assertEquals(registrationDTO.lastName(), capturedUser.getLastName());
//        assertEquals(registrationDTO.isEnabled(), capturedUser.isEnabled());
//
//        List<CredentialRepresentation> credentials = capturedUser.getCredentials();
//        assertEquals(1, credentials.size());
//        assertEquals(CredentialRepresentation.PASSWORD, credentials.get(0).getType());
//        assertEquals(registrationDTO.password(), credentials.get(0).getValue());
//        assertFalse(credentials.get(0).isTemporary());
//    }

//    @Test
//    void register_Failure() throws Exception {
//        // Arrange
//        RegistrationDTO registrationDTO = new RegistrationDTO(
//                "testuser", "test@example.com", "John", "Doe", "password123", true
//        );
//
//        TokenResponseDTO adminToken = new TokenResponseDTO(
//                "admin-token", 300, 1800, "refresh-admin",
//                "Bearer", "id-admin", 0, "session-admin", "openid"
//        );
//
//        // Mock admin token retrieval
//        mockTokenRetrieval(adminToken);
//
//        // Mock user registration failure
//        when(webClientAdmin.post()).thenReturn(requestBodyUriSpec);
//        when(requestBodySpec.header(eq("Authorization"), eq("Bearer " + adminToken.access_token()))).thenReturn(requestBodySpec);
//        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
//        when(requestBodySpec.bodyValue(any(UserRepresentation.class))).thenReturn(requestHeadersSpec);
//        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
//
//        when(responseSpec.toBodilessEntity()).thenReturn(Mono.just(
//                ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
//        ));
//
//        // Act
//        Mono<ResponseEntity<String>> result = keycloakService.register(registrationDTO);
//
//        // Assert
//        StepVerifier.create(result)
//                .expectNextMatches(response ->
//                        response.getStatusCode() == HttpStatus.BAD_REQUEST
//                )
//                .verifyComplete();
//    }

    @Test
    void introspectToken_Success() {
        // Arrange
        String token = "test-token";
        TokenResponseDTO adminToken = new TokenResponseDTO(
                "admin-token", 300, 1800, "refresh-admin",
                "Bearer", "id-admin", 0, "session-admin", "openid"
        );

        TokenIntrospectionResponse introspectResponse = new TokenIntrospectionResponse(
                true,
                "123456789",
                "testuser",
                "test@example.com",
                1762081243L,
                1762011243L,
                "test-scope",
                "Bearer",
                "grind-client");

        // Mock admin token retrieval
        mockTokenRetrieval(adminToken);

        // Mock the complete WebClient chain for token introspection
        when(webClientPublic.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/token/introspect")).thenReturn(requestBodySpec);
        when(requestBodySpec.header(eq("Content-Type"), eq("application/x-www-form-urlencoded"))).thenReturn(requestBodySpec);

        // Use any() or anyString() matcher instead of specific value
        when(requestBodySpec.bodyValue(anyString())).thenReturn(requestHeadersSpec);

        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(TokenIntrospectionResponse.class)).thenReturn(Mono.just(introspectResponse));

        // Act
        Mono<TokenIntrospectionResponse> result = keycloakService.introspectToken(token);

        // Assert
        StepVerifier.create(result)
                .expectNext(introspectResponse)
                .verifyComplete();

        // Verify the correct URI was called
        verify(requestBodyUriSpec).uri("/token/introspect");

        // Optional: Verify the body content was correct
        verify(requestBodySpec).bodyValue(argThat(body -> {
                    if (body instanceof String bodyString) {
                        return bodyString.contains("token=test-token") &&
                                bodyString.contains("client_id=test-client") &&
                                bodyString.contains("client_secret=test-secret");
                    }
                    return false;
                }
        ));
    }

    @Test
    void introspectToken_EmptyToken_ThrowsException() {
        // Arrange
        String token = "";

        // Act & Assert
        assertThrows(KeycloakException.class, () ->
                keycloakService.introspectToken(token)
        );
    }

//    @Test
//    void getAdminToken_Success() throws Exception {
//        // Arrange
//        TokenResponseDTO expectedToken = new TokenResponseDTO(
//                "admin-token", 300, 1800, "refresh-admin",
//                "Bearer", "id-admin", 0, "session-admin", "openid"
//        );
//
//        mockTokenRetrieval(expectedToken);
//
//        // Act
//        Mono<TokenResponseDTO> result = invokePrivateGetAdminToken();
//
//        // Assert
//        StepVerifier.create(result)
//                .expectNext(expectedToken)
//                .verifyComplete();
//
//        // Verify correct credentials were used
//        ArgumentCaptor<MultiValueMap> formDataCaptor = ArgumentCaptor.forClass(MultiValueMap.class);
//        verify(requestBodySpec).bodyValue(formDataCaptor.capture());
//
//        MultiValueMap<String, String> formData = formDataCaptor.getValue();
//        assertEquals("password", formData.getFirst("grant_type"));
//        assertEquals(adminUsername, formData.getFirst("username"));
//        assertEquals(adminPassword, formData.getFirst("password"));
//        assertEquals(clientId, formData.getFirst("client_id"));
//        assertEquals(clientSecret, formData.getFirst("client_secret"));
//    }

    private void mockTokenRetrieval(TokenResponseDTO tokenResponse) {
        when(webClientPublic.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    private Mono<TokenResponseDTO> invokePrivateGetAdminToken() throws Exception {
        var method = KeycloakService.class.getDeclaredMethod("getAdminToken");
        method.setAccessible(true);
        return (Mono<TokenResponseDTO>) method.invoke(keycloakService);
    }
}