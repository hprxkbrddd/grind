package com.grind.security.autoconfiguration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    private Url url = new Url();
    private String adminUsername = "administrator";
    private String adminPassword = "admin";

    @Getter
    @Setter
    public static class Url {
        private String publicUrl = "http://localhost:8085/realms/grind/protocol/openid-connect";
        private String adminUrl = "http://localhost:8085/admin/realms/grind";
    }
}
