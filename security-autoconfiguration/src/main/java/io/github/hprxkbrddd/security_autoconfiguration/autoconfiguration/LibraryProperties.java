package io.github.hprxkbrddd.security_autoconfiguration.autoconfiguration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Security Library Properties
 * Root configuration class for properties used by Grind Security Auto Configuration.
 * <p>
 * Loaded automatically via Spring Boot's {@link ConfigurationProperties} mechanism.
 * <br>
 * All properties use the prefix <b>security-library</b>.
 * <p>
 * Contains nested configuration groups for:
 * <ul>
 *     <li>Keycloak settings</li>
 *     <li>OAuth2 Resource Server and Client settings</li>
 * </ul>
 */
@ConfigurationProperties(prefix = "security-library")
public class LibraryProperties {

    /** Keycloak-related settings. */
    public final KeycloakProperties keycloak = new KeycloakProperties();

    /** OAuth2 server/client settings. */
    public final OAuth2Properties oauth2 = new OAuth2Properties();


    /**
     * Keycloak Properties
     * Contains configuration for Keycloak authentication server.
     *
     * <p>Prefix: <b>security-library.keycloak</b></p>
     */
    @ConfigurationProperties(prefix = "keycloak")
    public static class KeycloakProperties {

        /** Default administrator username used for Keycloak API interaction. */
        public final String adminUsername = "administrator";

        /** Default administrator password used for Keycloak API interaction. */
        public final String adminPassword = "admin";

        /** Nested URL configuration for Keycloak endpoints. */
        public final Url url = new Url();


        /**
         * Keycloak URL Properties
         * Contains endpoint URLs for Keycloak Realm and AdminAPI.
         *
         * <p>Prefix: <b>security-library.keycloak.url</b></p>
         */
        @ConfigurationProperties(prefix = "url")
        public static class Url {

            /**
             * Public OpenID Connect endpoint for clients.
             * <p>Used for JWT issuing, token retrieval, and user authentication.</p>
             */
            public final String publicUrl =
                    "http://localhost:8085/realms/grind/protocol/openid-connect";

            /**
             * Administration endpoint for managing users, roles, and realm settings.
             */
            public final String adminUrl =
                    "http://localhost:8085/admin/realms/grind";
        }
    }


    /**
     * OAuth2 Configuration
     * Contains properties for Spring Security OAuth2:
     * <ul>
     *     <li>resource server (JWT validation)</li>
     *     <li>client registration</li>
     * </ul>
     *
     * <p>Prefix: <b>security-library.oauth2</b></p>
     */
    @ConfigurationProperties(prefix = "oauth2")
    public static class OAuth2Properties {

        /** Resource Server configuration (JWT validation). */
        public final Resourceserver resourceserver = new Resourceserver();

        /** OAuth2 Client configuration (registration + provider). */
        public final Client client = new Client();


        /**
         * Resource Server Properties
         * Settings related to JWT validation when acting as OAuth2 Resource Server.
         *
         * <p>Prefix: <b>security-library.oauth2.resourceserver</b></p>
         */
        @ConfigurationProperties(prefix = "resourceserver")
        public static class Resourceserver {

            /** JWT-related validation properties. */
            public final Jwt jwt = new Jwt();


            /**
             * JWT Properties
             * Defines issuer and JWK endpoints used to validate incoming tokens.
             *
             * <p>Prefix: <b>security-library.oauth2.resourceserver.jwt</b></p>
             */
            @ConfigurationProperties(prefix = "jwt")
            public static class Jwt {

                /**
                 * Issuer URI for Keycloak tokens.
                 * <p>Used by Spring Security to validate <code>iss</code> field in JWT.</p>
                 */
                public final String issuerUri = "http://localhost:8085/realms/grind";

                /**
                 * JWK Set URI for retrieving public keys used to verify JWT signatures.
                 */
                public final String jwkSetUri =
                        "http://localhost:8085/realms/grind/protocol/openid-connect/certs";
            }
        }


        /**
         * OAuth2 Client Properties
         * Settings for Spring Security OAuth2 Client configuration.
         *
         * <p>Prefix: <b>security-library.oauth2.client</b></p>
         */
        @ConfigurationProperties(prefix = "client")
        public static class Client {

            /** Client registration settings. */
            public final Registration registration = new Registration();

            /** Provider settings (Keycloak issuer metadata). */
            public final Provider provider = new Provider();


            /**
             * Client Registration
             * Contains client credentials, scopes, redirect URIs, etc.
             *
             * <p>Prefix: <b>security-library.oauth2.client.registration</b></p>
             */
            @ConfigurationProperties(prefix = "registration")
            public static class Registration {

                /** Keycloak client registration settings. */
                public final Keycloak keycloak = new Keycloak();


                /**
                 * Keycloak Client Registration
                 * Defines properties required for OAuth2 client to authenticate with Keycloak.
                 *
                 * <p>Prefix: <b>security-library.oauth2.client.registration.keycloak</b></p>
                 */
                @ConfigurationProperties(prefix = "keycloak")
                public static class Keycloak {

                    /** OAuth2 client ID registered in Keycloak. */
                    public final String clientId = "grind_client";

                    /** Secret used for client authentication. */
                    public final String clientSecret =
                            "b5fk9Hdmxn7kFgJDao2SFFkfVoypa60g";

                    /** Grant type used for login. */
                    public final String authorizationGrantType = "authorization_code";

                    /** Scopes requested during authorization. */
                    public final String scope = "openid,profile,email";

                    /** Redirect URI after login. */
                    public final String redirectUri =
                            "{baseUrl}/login/oauth2/keycloak";
                }
            }


            /**
             * OAuth2 Provider Properties
             * Defines where the metadata for OAuth2/OpenID provider is loaded from.
             *
             * <p>Prefix: <b>security-library.oauth2.client.provider</b></p>
             */
            @ConfigurationProperties(prefix = "provider")
            public static class Provider {

                /** Provider metadata for Keycloak instance. */
                Keycloak keycloak = new Keycloak();


                /**
                 * Keycloak Provider Metadata
                 * Defines issuer information and identity claim mapping.
                 *
                 * <p>Prefix: <b>security-library.oauth2.client.provider.keycloak</b></p>
                 */
                @ConfigurationProperties(prefix = "keycloak")
                public static class Keycloak {

                    /** Issuer URI used to fetch OpenID provider metadata. */
                    public final String issuerUri = "http://localhost:8085/realms/grind";

                    /** JWT claim name used as username in Spring Security. */
                    public final String userNameAttribute = "preferred_username";
                }
            }
        }
    }
}
