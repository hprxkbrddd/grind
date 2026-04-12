package com.grind.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration",
		"spring.security.oauth2.resourceserver.jwt.issuer-uri=",
		"spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost/jwks",
		"spring.kafka.listener.auto-startup=false"
})
class GatewayApplicationTests {

	@MockitoBean
	ClientRegistrationRepository clientRegistrationRepository;

	@MockitoBean
	JwtDecoder jwtDecoder;

	@Test
	void contextLoads() {
	}

}
