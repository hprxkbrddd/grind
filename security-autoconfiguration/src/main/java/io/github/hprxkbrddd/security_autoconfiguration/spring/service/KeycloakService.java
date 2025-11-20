package io.github.hprxkbrddd.security_autoconfiguration.spring.service;

import io.github.hprxkbrddd.security_autoconfiguration.autoconfiguration.LibraryProperties;
import io.github.hprxkbrddd.security_autoconfiguration.core.KeycloakException;
import io.github.hprxkbrddd.security_autoconfiguration.core.RegistrationDTO;
import io.github.hprxkbrddd.security_autoconfiguration.core.TokenIntrospectionResponse;
import io.github.hprxkbrddd.security_autoconfiguration.core.TokenResponseDTO;
import jakarta.annotation.PostConstruct;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * Keycloak Service
 * Responsible for communication with a Keycloak server using
 * reactive {@link WebClient} calls.
 *
 * <p><b>Main responsibilities:</b></p>
 * <ul>
 *     <li>Obtaining tokens via Resource Owner Password Credentials flow</li>
 *     <li>Registering new users in Keycloak</li>
 *     <li>Introspecting access tokens</li>
 * </ul>
 *
 * <p>
 * Configuration values (URLs, client credentials, admin user) are read from
 * {@link LibraryProperties}. Web clients are initialized after construction via
 * {@link #init()}.
 * </p>
 */
public class KeycloakService {

    private final String keycloakPublicUrl;
    private final String keycloakAdminUrl;

    private WebClient webClientPublic;
    private WebClient webClientAdmin;

    private final String clientId;
    private final String clientSecret;
    private final String adminUsername;
    private final String adminPassword;

    /**
     * Construct KeycloakService
     * Initializes Keycloak URLs, client credentials and admin credentials
     * using {@link LibraryProperties} default values.
     *
     * <p>
     * Note: {@link WebClient} instances are created later in {@link #init()}.
     * </p>
     */
    public KeycloakService() {
        LibraryProperties props = new LibraryProperties();
        this.keycloakAdminUrl = props.keycloak.url.adminUrl;
        this.keycloakPublicUrl = props.keycloak.url.publicUrl;

        this.clientId = props.oauth2.client.registration.keycloak.clientId;
        this.clientSecret = props.oauth2.client.registration.keycloak.clientSecret;

        this.adminUsername = props.keycloak.adminUsername;
        this.adminPassword = props.keycloak.adminPassword;
    }

    /**
     * Initialize WebClient Instances
     * Called after construction to create and configure two {@link WebClient}
     * instances:
     * <ul>
     *     <li><b>webClientPublic</b> – for public OIDC endpoints (token, introspect)</li>
     *     <li><b>webClientAdmin</b> – for admin endpoints (user management)</li>
     * </ul>
     */
    @PostConstruct
    public void init() {
        this.webClientPublic = WebClient.builder()
                .baseUrl(keycloakPublicUrl)
                .build();
        this.webClientAdmin = WebClient.builder()
                .baseUrl(keycloakAdminUrl)
                .build();
    }

    /**
     * Get Admin Token
     * Obtains a Keycloak access token using admin credentials configured in
     * {@link LibraryProperties}.
     *
     * @return reactive publisher that emits {@link TokenResponseDTO} for admin user
     */
    private Mono<TokenResponseDTO> getAdminToken() {
        return getToken(adminUsername, adminPassword);
    }

    /**
     * Register New User
     * Registers a new user in Keycloak using admin privileges.
     *
     * <p>Steps:</p>
     * <ol>
     *     <li>Obtain admin token</li>
     *     <li>Build {@link UserRepresentation} with credentials and profile data</li>
     *     <li>POST to <code>/users</code> admin endpoint</li>
     * </ol>
     *
     * @param dto registration data (username, password, email, profile, enabled flag)
     * @return reactive publisher with HTTP response status and message
     */
    public Mono<ResponseEntity<String>> register(RegistrationDTO dto) {
        return getAdminToken()
                .flatMap(token -> {
                    UserRepresentation user = new UserRepresentation();
                    user.setUsername(dto.username());
                    user.setEmail(dto.email());
                    user.setFirstName(dto.firstName());
                    user.setLastName(dto.lastName());
                    user.setEnabled(dto.isEnabled());

                    CredentialRepresentation credential = new CredentialRepresentation();
                    credential.setType(CredentialRepresentation.PASSWORD);
                    credential.setValue(dto.password());
                    credential.setTemporary(false);

                    user.setCredentials(List.of(credential));

                    return webClientAdmin.post()
                            .uri("/users")
                            .header("Authorization", "Bearer " + token.access_token())
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(user)
                            .retrieve()
                            .toBodilessEntity()
                            .map(response -> {
                                if (response.getStatusCode().is2xxSuccessful()) {
                                    return ResponseEntity.status(HttpStatus.CREATED)
                                            .body("User registered successfully");
                                }
                                return ResponseEntity.status(response.getStatusCode())
                                        .body("Failed to register user");
                            });
                });

    }

    /**
     * Obtain Token (Password Grant)
     * Performs Resource Owner Password Credentials flow against Keycloak token
     * endpoint to retrieve an access token for the given user credentials.
     *
     * <p>Validates input and throws {@link KeycloakException} if username or password
     * are missing.</p>
     *
     * @param username username of the user
     * @param password raw password of the user
     * @return reactive publisher emitting {@link TokenResponseDTO}
     *
     * @throws KeycloakException if username or password are null/blank
     */
    public Mono<TokenResponseDTO> getToken(String username, String password) {
        System.out.println("get JWT endpoint: call");
        System.out.println("get JWT endpoint: preparing body");
        if (username == null || password == null || username.isBlank() || password.isBlank()) {
            throw new KeycloakException("Username or password are empty");
        }
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", username);
        formData.add("password", password);
        formData.add("scope", "openid");
        System.out.println("get JWT endpoint: body prepared");
        System.out.println("get JWT endpoint: sending HTTP to keycloak server");
        Mono<TokenResponseDTO> resp = webClientPublic.post()
                .uri("/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(TokenResponseDTO.class);
        System.out.println(Objects.requireNonNull(resp.block()).access_token());
        return resp;
    }

    /**
     * Introspect Access Token
     * Uses Keycloak token introspection endpoint to validate token and retrieve
     * its metadata.
     *
     * @param token raw access token (without <code>Bearer </code> prefix)
     * @return reactive publisher emitting {@link TokenIntrospectionResponse}
     *
     * @throws KeycloakException if token is null or blank
     */
    public Mono<TokenIntrospectionResponse> introspectToken(String token) {
        if (token == null || token.isBlank()) {
            throw new KeycloakException("Token is either null or empty");
        }
        return webClientPublic.post()
                .uri("/token/introspect")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue(createFormData(token))
                .retrieve()
                .bodyToMono(TokenIntrospectionResponse.class);
    }

    /**
     * Create Introspection Form Data
     * Builds a URL-encoded form string for Keycloak introspection endpoint,
     * containing token and client credentials.
     *
     * @param token access token to introspect
     * @return URL-encoded form string
     */
    private String createFormData(String token) {
        return "token=" + token +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret;
    }
}
