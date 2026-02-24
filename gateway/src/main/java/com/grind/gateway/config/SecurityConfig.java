package com.grind.gateway.config;


import com.grind.gateway.component.JwtConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
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
     * @return fully configured {@link SecurityFilterChain}
     * @throws Exception if Spring Security fails to build the configuration
     */
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(
            HttpSecurity http,
            JwtConverter jwtConverter) throws Exception {
        System.out.println(">>> GrindSecurityAutoConfig SecurityFilterChain ACTIVE");
        return http
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/grind/keycloak/**",
                                "/grind/keycloak/register",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/**",
                                "/test/**"
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
