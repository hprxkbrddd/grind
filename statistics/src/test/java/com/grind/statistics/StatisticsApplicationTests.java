package com.grind.statistics;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.grind.statistics.config.ClickhouseSchemaInitializer;

@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")
class StatisticsApplicationTests {

	@MockitoBean
	ClickhouseSchemaInitializer clickhouseSchemaInitializer;

	@MockitoBean
	JwtDecoder jwtDecoder;

	@Test
	void contextLoads() {
	}

}
