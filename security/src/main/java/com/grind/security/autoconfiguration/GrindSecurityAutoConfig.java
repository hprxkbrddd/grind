package com.grind.security.autoconfiguration;

import com.grind.security.spring.component.JwtConverter;
import com.grind.security.spring.controller.KeycloakController;
import com.grind.security.spring.exception.SecurityExceptionHandler;
import com.grind.security.spring.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final LibraryProperties props = new LibraryProperties();

    // JWT beans
    // ----------------------------------------
    @Bean
    @ConditionalOnMissingBean(JwtDecoder.class)
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(
                props.oauth2.resourceserver.jwt.issuerUri
        );
    }

    @Autowired
    private JwtConverter jwtConverter;
    // ----------------------------------------

    /**
     * <h1>Spring Security Filter Chain</h1>
     * Configures SecurityFilterChain bean.
     * Sets oauth2 authentication converter. <br>
     * Protects all endpoints of Spring application except
     * <ul>
     *     <li>/grind/keycloak/token</li>
     *     <li>/grind/keycloak/register</li>
     * </ul>
     *
     * @return SecurityFilterChain
     */
    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/grind/keycloak/token",
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
     * <h1>Keycloak Service</h1>
     * Creates bean of service, which is responsible for
     * <ul>
     *      <li>connection to Keycloak</li>
     *      <li>authorization operations</li>
     * </ul>
     *
     * @return KeycloakService
     */
    @Bean
    @ConditionalOnMissingBean(KeycloakService.class)
    public KeycloakService keycloakService() {
        return new KeycloakService();
    }

    /**
     * <h1>Keycloak Controller</h1>
     * Creates bean, whoch exposes KeycloakService functionality through REST
     *
     * @param keycloakService
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(KeycloakController.class)
    public KeycloakController keycloakController(KeycloakService keycloakService) {
        return new KeycloakController(keycloakService);
    }

    @Bean
    @ConditionalOnMissingBean(SecurityExceptionHandler.class)
    public SecurityExceptionHandler securityExceptionHandler() {
        return new SecurityExceptionHandler();
    }
}
