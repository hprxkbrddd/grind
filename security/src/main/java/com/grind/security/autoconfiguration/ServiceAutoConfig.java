package com.grind.security.autoconfiguration;

import com.grind.security.spring.service.KeycloakService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@AutoConfiguration
public class ServiceAutoConfig {

    @Bean
    public KeycloakService keycloakService(JwtDecoder jwtDecoder){
        return new KeycloakService(jwtDecoder);
    }
}
