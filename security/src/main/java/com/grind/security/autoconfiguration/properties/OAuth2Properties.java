package com.grind.security.autoconfiguration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2")
public class OAuth2Properties {

    private Resourceserver resourceserver = new Resourceserver();
    private Client client = new Client();

    @Getter
    @Setter
    public static class Resourceserver {
        private Jwt jwt = new Jwt();

        @Getter
        @Setter
        public static class Jwt {
            private String issuerUri = "http://localhost:8085/realms/grind";
            private String jwkSetUri = "http://localhost:8085/realms/grind/protocol/openid-connect/certs";
        }
    }

    @Getter
    @Setter
    public static class Client {
        private Registration registration = new Registration();
        private Provider provider = new Provider();

        @Getter
        @Setter
        public static class Registration {
            Keycloak keycloak = new Keycloak();

            @Getter
            @Setter
            public static class Keycloak {
                private String clientId = "grind_client";
                private String clientSecret = "b5fk9Hdmxn7kFgJDao2SFFkfVoypa60g";
                private String authorizationGrantType = "authorization_code";
                private String scope = "openid,profile,email";
                private String redirectUri = "{baseUrl}/login/oauth2/keycloak";
            }
        }

        @Getter
        @Setter
        public static class Provider {
            Keycloak keycloak = new Keycloak();

            @Getter
            @Setter
            public static class Keycloak {
                private String issuerUri = "http://localhost:8085/realms/grind";
                private String userNameAttribute = "preferred_username";
            }
        }
    }
}
