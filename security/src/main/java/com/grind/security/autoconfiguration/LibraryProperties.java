package com.grind.security.autoconfiguration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security-library")
public class LibraryProperties {

    public final KeycloakProperties keycloak = new KeycloakProperties();
    public final OAuth2Properties oauth2 = new OAuth2Properties();
    
    @ConfigurationProperties(prefix = "keycloak")
    public static class KeycloakProperties {
        public final String adminUsername = "administrator";
        public final String adminPassword = "admin";
        public final Url url = new Url();

        @ConfigurationProperties(prefix = "url")
        public static class Url {
            public final String publicUrl = "http://localhost:8085/realms/grind/protocol/openid-connect";
            public final String adminUrl = "http://localhost:8085/admin/realms/grind";
        }
    }

    @ConfigurationProperties(prefix = "oauth2")
    public static class OAuth2Properties {

        public final Resourceserver resourceserver = new Resourceserver();
        public final Client client = new Client();

        @ConfigurationProperties(prefix = "resourceserver")
        public static class Resourceserver {
            public final Jwt jwt = new Jwt();

            @ConfigurationProperties(prefix = "jwt")
            public static class Jwt {
                public final String issuerUri = "http://localhost:8085/realms/grind";
                public final String jwkSetUri = "http://localhost:8085/realms/grind/protocol/openid-connect/certs";
            }
        }

        @ConfigurationProperties(prefix = "client")
        public static class Client {
            public final Registration registration = new Registration();
            public final Provider provider = new Provider();

            @ConfigurationProperties(prefix = "registration")
            public static class Registration {
               public final Keycloak keycloak = new Keycloak();

                @ConfigurationProperties(prefix = "keycloak")
                public static class Keycloak {
                    public final String clientId = "grind_client";
                    public final String clientSecret = "b5fk9Hdmxn7kFgJDao2SFFkfVoypa60g";
                    public final String authorizationGrantType = "authorization_code";
                    public final String scope = "openid,profile,email";
                    public final String redirectUri = "{baseUrl}/login/oauth2/keycloak";
                }
            }


            @ConfigurationProperties(prefix = "provider")
            public static class Provider {
                Keycloak keycloak = new Keycloak();

                @ConfigurationProperties(prefix = "keycloak")
                public static class Keycloak {
                    public final String issuerUri = "http://localhost:8085/realms/grind";
                    public final String userNameAttribute = "preferred_username";
                }
            }
        }
    }
}
