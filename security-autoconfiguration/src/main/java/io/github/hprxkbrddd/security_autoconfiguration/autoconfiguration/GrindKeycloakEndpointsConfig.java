package io.github.hprxkbrddd.security_autoconfiguration.autoconfiguration;

import io.github.hprxkbrddd.security_autoconfiguration.spring.controller.KeycloakController;
import io.github.hprxkbrddd.security_autoconfiguration.spring.service.KeycloakService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Keycloak-сервиса и контроллера,
 * которая активируется только если grind.auth.token-endpoint-enabled=true.
 * <p>
 * Таким образом, только "auth-сервис" будет реально
 * поднимать REST-эндпоинты /grind/keycloak/**.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
        prefix = "grind.auth",
        name = "token-endpoint-enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class GrindKeycloakEndpointsConfig {

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
     * @return new instance of {@link KeycloakController}
     */
    @Bean
    @ConditionalOnMissingBean(KeycloakController.class)
    public KeycloakController keycloakController(KeycloakService keycloakService) {
        return new KeycloakController(keycloakService);
    }
}
