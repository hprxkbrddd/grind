package com.grind.security.autoconfiguration;

import com.grind.security.spring.controller.KeycloakController;
import com.grind.security.spring.service.KeycloakService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ControllerAutoConfig {

    @Bean
    @ConditionalOnMissingBean(KeycloakController.class)
    public KeycloakController keycloakController(KeycloakService service){
        return new KeycloakController(service);
    }
}
