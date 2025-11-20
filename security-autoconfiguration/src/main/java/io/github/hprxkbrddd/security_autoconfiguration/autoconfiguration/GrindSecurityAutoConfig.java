package io.github.hprxkbrddd.security_autoconfiguration.autoconfiguration;

import io.github.hprxkbrddd.security_autoconfiguration.spring.component.JwtConverter;
import io.github.hprxkbrddd.security_autoconfiguration.spring.controller.KeycloakController;
import io.github.hprxkbrddd.security_autoconfiguration.spring.exception.SecurityExceptionHandler;
import io.github.hprxkbrddd.security_autoconfiguration.spring.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnClass(SecurityFilterChain.class)
@EnableConfigurationProperties(LibraryProperties.class)
@RequiredArgsConstructor
public class GrindSecurityAutoConfig {

    private final LibraryProperties props;

    // JWT beans
    // ----------------------------------------

    /**
     * JWT Decoder
     * Creates a {@link JwtDecoder} bean if none is already defined in the application context.
     * <p>
     * Decoder is initialized using issuer URI defined in {@link LibraryProperties}.
     *
     * <p><b>Main responsibilities:</b></p>
     * <ul>
     *     <li>Validate incoming JWT tokens</li>
     *     <li>Decode token payload</li>
     *     <li>Ensure issuer matches configured value</li>
     * </ul>
     *
     * @return configured {@link JwtDecoder}
     */
    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(
                props.oauth2.resourceserver.jwt.issuerUri
        );
    }

    @Bean
    @ConditionalOnMissingBean(JwtConverter.class)
    public JwtConverter jwtConverter() {
        return new JwtConverter(props);
    }
    // ----------------------------------------

    /**
     * Spring Security Filter Chain
     * Configures the default {@link SecurityFilterChain} for the application,
     * unless one is already declared by the user.
     *
     * <p><b>Key features:</b></p>
     * <ul>
     *     <li>Disables CORS and CSRF (can be overridden by user)</li>
     *     <li>Enables stateless session management</li>
     *     <li>Secures all endpoints except:
     *         <ul>
     *             <li><code>/grind/keycloak/token</code></li>
     *             <li><code>/grind/keycloak/register</code></li>
     *             <li><code>/actuator/**</code></li>
     *         </ul>
     *     </li>
     *     <li>Configures OAuth2 Resource Server with JWT support</li>
     *     <li>Uses {@link JwtConverter} to convert decoded tokens into Authentication objects</li>
     * </ul>
     *
     * @param http instance of {@link HttpSecurity} used to build the chain
     *
     * @return fully configured {@link SecurityFilterChain}
     *
     * @throws Exception if Spring Security fails to build the configuration
     */
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            JwtConverter jwtConverter) throws Exception {
        System.out.println(">>> GrindSecurityAutoConfig SecurityFilterChain ACTIVE");
        return http
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/grind/keycloak/**",
                                "/grind/keycloak/register",
                                "/actuator/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)))
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    /**
     * Keycloak Service
     * Creates a {@link KeycloakService} bean if the application has not already provided one.
     *
     * <p><b>Service responsibilities include:</b></p>
     * <ul>
     *     <li>communicating with Keycloak instance</li>
     *     <li>managing token operations</li>
     *     <li>handling user registration / authentication</li>
     * </ul>
     *
     * @return new instance of {@link KeycloakService}
     */
    @Bean
    @ConditionalOnMissingBean(KeycloakService.class)
    public KeycloakService keycloakService() {
        return new KeycloakService();
    }

    /**
     * Keycloak Controller
     * Creates a REST controller for exposing authentication endpoints
     * implemented by {@link KeycloakService}, unless such bean already exists.
     *
     * <p><b>Exposes endpoints for:</b></p>
     * <ul>
     *     <li>retrieving JWT token</li>
     *     <li>registering new users</li>
     * </ul>
     *
     * @param keycloakService service used internally by the controller
     *
     * @return new instance of {@link KeycloakController}
     */
    @Bean
    @ConditionalOnMissingBean(KeycloakController.class)
    public KeycloakController keycloakController(KeycloakService keycloakService) {
        return new KeycloakController(keycloakService);
    }

    /**
     * Security Exception Handler
     * Registers a global {@link SecurityExceptionHandler} bean if one is not already present.
     *
     * <p><b>Responsibilities:</b></p>
     * <ul>
     *     <li>Handle authentication and authorization errors</li>
     *     <li>Convert Spring Security exceptions into user-friendly JSON responses</li>
     * </ul>
     *
     * @return new instance of {@link SecurityExceptionHandler}
     */
    @Bean
    @ConditionalOnMissingBean(SecurityExceptionHandler.class)
    public SecurityExceptionHandler securityExceptionHandler() {
        return new SecurityExceptionHandler();
    }
}
